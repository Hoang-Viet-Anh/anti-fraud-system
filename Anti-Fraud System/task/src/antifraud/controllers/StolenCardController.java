package antifraud.controllers;

import antifraud.database.stolenCard.StolenCard;
import antifraud.database.stolenCard.StolenCardRepository;
import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
public class StolenCardController {
    @Autowired
    StolenCardRepository cardRepo;

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @PostMapping("/api/antifraud/stolencard")
    ResponseEntity<String> addStolenCard(@RequestBody StolenCard card) {
        if (cardRepo.existsByNumber(card.getNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The card number is already in the database.");
        } else if (!card.isValid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Card number has the wrong format.");
        } else {
            card = cardRepo.save(card);
            return new ResponseEntity<>(card.toJson(), HttpStatus.OK);
        }
    }

    @DeleteMapping("/api/antifraud/stolencard/{number}")
    ResponseEntity<String> deleteStolenCard(@PathVariable String number) {
        StolenCard card = new StolenCard();
        card.setNumber(number);

        if (!card.isValid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Card number has the wrong format.");
        } else if (!cardRepo.existsByNumber(number)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Card number is not found in the database.");
        } else {
            cardRepo.deleteByNumber(number);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status",
                    String.format("Card %s successfully removed!", number));
            return new ResponseEntity<>(gson.toJson(jsonObject), HttpStatus.OK);
        }
    }

    @GetMapping("/api/antifraud/stolencard")
    ResponseEntity<String> getStolenCardList() {
        JsonArray array = new JsonArray();
        List<StolenCard> list = new ArrayList<>();
        cardRepo.findAll().forEach(list::add);
        list.forEach(a -> {
            array.add(JsonParser.parseString(a.toJson()));
        });
        return new ResponseEntity<>(gson.toJson(array), HttpStatus.OK);
    }
}
