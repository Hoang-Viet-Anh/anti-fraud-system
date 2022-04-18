package antifraud.controllers;

import antifraud.database.ip.IpRepository;
import antifraud.database.stolenCard.StolenCardRepository;
import antifraud.database.transaction.Feedback;
import antifraud.database.transaction.TransactionData;
import antifraud.database.transaction.TransactionRepository;
import antifraud.database.transaction.TransactionResult;
import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.*;

@RestController
public class TransactionController {
    @Autowired
    private IpRepository ipRepo;

    @Autowired
    private StolenCardRepository cardRepo;

    @Autowired
    private TransactionRepository transactionRepo;

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @PostMapping("/api/antifraud/transaction")
    ResponseEntity<String> isValid(@Valid @RequestBody TransactionData transaction) {
        if (!transaction.ipIsValid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "IP address has the wrong format.");
        } else if (!transaction.numberIsValid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Card number has the wrong format.");
        } else if (!transaction.amountIsValid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount has the wrong value.");
        }
        try {
            if (!transaction.dateIsValid()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
            transactionRepo.save(transaction);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Timestamp from = new Timestamp(transaction.getDateData().getTime() - 3600000);
        Timestamp to = new Timestamp(transaction.getDateData().getTime());

        List<TransactionData> listData = transactionRepo.findByNumberAndDateDataBetween(transaction.getNumber(), from, to);

        TransactionResult amountResult = transaction.amountResult();
        TransactionResult cardResult = !cardRepo.existsByNumber(transaction.getNumber()) ?
                TransactionResult.ALLOWED : TransactionResult.PROHIBITED;
        TransactionResult ipResult = !ipRepo.existsByIp(transaction.getIp()) ?
                TransactionResult.ALLOWED : TransactionResult.PROHIBITED;
        Set<String> correlation = new HashSet<>();
        listData.forEach(a -> correlation.add(a.getIp()));
        TransactionResult ipCorrelation = correlation.size() < 3 ?
                TransactionResult.ALLOWED : correlation.size() == 3 ?
                TransactionResult.MANUAL_PROCESSING : TransactionResult.PROHIBITED;
        correlation.clear();
        listData.forEach(a -> correlation.add(a.getRegion().toString()));
        TransactionResult regCorrelation = correlation.size() < 3 ?
                TransactionResult.ALLOWED : correlation.size() == 3 ?
                TransactionResult.MANUAL_PROCESSING : TransactionResult.PROHIBITED;

        Map<String, TransactionResult> resultList = new LinkedHashMap<>();
        resultList.put("amount", amountResult);
        resultList.put("card-number", cardResult);
        resultList.put("ip", ipResult);
        resultList.put("ip-correlation", ipCorrelation);
        resultList.put("region-correlation", regCorrelation);
        List<String> infoList = new ArrayList<>();
        infoList.add("none");
        String result = TransactionResult.ALLOWED.toString();
        String info;

        for (Map.Entry<String, TransactionResult> entry : resultList.entrySet()) {
            if (TransactionResult.valueOf(result).getNumber() < entry.getValue().getNumber()) {
                result = entry.getValue().toString();
                infoList.clear();
                infoList.add(entry.getKey());
            } else if (TransactionResult.valueOf(result) != TransactionResult.ALLOWED &&
                    TransactionResult.valueOf(result).getNumber() == entry.getValue().getNumber()) {
                infoList.add(entry.getKey());
            }
        }
        Iterator<String> iterator = infoList.iterator();
        info = iterator.next();
        while (iterator.hasNext()) {
            info = info.concat(", " + iterator.next());
        }
        transaction.setResult(TransactionResult.valueOf(result));
        transactionRepo.save(transaction);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("result", result);
        jsonObject.addProperty("info", info);

        return new ResponseEntity<>(gson.toJson(jsonObject), HttpStatus.OK);
    }

    @PutMapping("/api/antifraud/transaction")
    ResponseEntity<String> addFeedback(@Valid @RequestBody Feedback feedback) {
        TransactionData transaction = transactionRepo.findById(feedback.getTransactionId()).get(0);
        if (!transactionRepo.existsById(feedback.getTransactionId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction is not found in history.");
        } else if (transactionRepo.existsByIdAndFeedbackIsNotNull(feedback.getTransactionId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "The feedback for this transaction is already in the database.");
        } else if (transaction.getResult().equals(feedback.getFeedback())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        } else {
            transaction.setFeedback(feedback.getFeedback());
            transaction.setDate(transaction.getDateData().toString());
            transaction = transactionRepo.save(transaction);
            TransactionResult feedbackResult = feedback.getFeedback();
            TransactionResult validatyResult = transaction.getResult();
            if (feedbackResult.equals(TransactionResult.ALLOWED)) {
                if (validatyResult.equals(TransactionResult.MANUAL_PROCESSING)) {
                    TransactionData.increaseAllowLimit(transaction.getAmount());
                } else if (validatyResult.equals(TransactionResult.PROHIBITED)) {
                    TransactionData.increaseAllowLimit(transaction.getAmount());
                    TransactionData.increaseProhibitedLimit(transaction.getAmount());
                }
            } else if (feedbackResult.equals(TransactionResult.MANUAL_PROCESSING)) {
                if (validatyResult.equals(TransactionResult.ALLOWED)) {
                    TransactionData.decreaseAllowLimit(transaction.getAmount());
                } else if (validatyResult.equals(TransactionResult.PROHIBITED)) {
                    TransactionData.increaseProhibitedLimit(transaction.getAmount());
                }
            } else if (feedbackResult.equals(TransactionResult.PROHIBITED)) {
                if (validatyResult.equals(TransactionResult.ALLOWED)) {
                    TransactionData.decreaseAllowLimit(transaction.getAmount());
                    TransactionData.decreaseProhibitedLimit(transaction.getAmount());
                } else if (validatyResult.equals(TransactionResult.MANUAL_PROCESSING)) {
                    TransactionData.decreaseProhibitedLimit(transaction.getAmount());
                }
            }

            return new ResponseEntity<>(gson.toJson(transaction.toJson()), HttpStatus.OK);
        }
    }

    @GetMapping("/api/antifraud/history")
    ResponseEntity<String> getHistory() {
        JsonArray array = new JsonArray();
        List<TransactionData> list = new ArrayList<>();
        transactionRepo.findAll().forEach(list::add);
        list.forEach(a -> {
            array.add(a.toJson());
        });
        return new ResponseEntity<>(gson.toJson(array), HttpStatus.OK);
    }

    @GetMapping("/api/antifraud/history/{number}")
    ResponseEntity<String> getNumberHistory(@PathVariable String number) {
        TransactionData data = new TransactionData();
        data.setNumber(number);
        if (!data.numberIsValid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Card number doesn't follow the right format");
        } else if (!transactionRepo.existsByNumber(number)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Transaction for a specified card number are not found.");
        }

        JsonArray array = new JsonArray();
        List<TransactionData> list = new ArrayList<>(transactionRepo.findByNumber(number));
        list.forEach(a -> {
            array.add(a.toJson());
        });
        return new ResponseEntity<>(gson.toJson(array), HttpStatus.OK);
    }
}
