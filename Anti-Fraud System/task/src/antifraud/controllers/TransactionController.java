package antifraud.controllers;

import antifraud.database.ip.IpRepository;
import antifraud.database.stolenCard.StolenCardRepository;
import antifraud.database.transaction.TransactionData;
import antifraud.database.transaction.TransactionResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.*;

@RestController
public class TransactionController {
    @Autowired
    private IpRepository ipRepo;

    @Autowired
    private StolenCardRepository cardRepo;

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @PostMapping("/api/antifraud/transaction")
    public ResponseEntity<String> isValid(@Valid @RequestBody TransactionData transaction) {
        if (!transaction.ipIsValid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "IP address has the wrong format.");
        } else if (!transaction.numberIsValid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Card number has the wrong format.");
        } else if (!transaction.amountIsValid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount has the wrong value.");
        }

        TransactionResult amountResult = transaction.amountResult();
        TransactionResult cardResult = !cardRepo.existsByNumber(transaction.getNumber()) ?
                TransactionResult.ALLOWED : TransactionResult.PROHIBITED;
        TransactionResult ipResult = !ipRepo.existsByIp(transaction.getIp()) ?
                TransactionResult.ALLOWED : TransactionResult.PROHIBITED;

        Map<String, TransactionResult> resultList = new LinkedHashMap<>();
        resultList.put("amount", amountResult);
        resultList.put("card-number", cardResult);
        resultList.put("ip", ipResult);
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

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("result", result);
        jsonObject.addProperty("info", info);

        return new ResponseEntity<>(gson.toJson(jsonObject), HttpStatus.OK);
    }

}
