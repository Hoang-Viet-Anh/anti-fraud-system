package antifraud;

import com.google.gson.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {

    @PostMapping("/api/antifraud/transaction")
    public ResponseEntity<String> isValid(@RequestBody String json) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        JsonObject jsonObject = JsonParser
                .parseString(json)
                .getAsJsonObject();
        JsonObject object1 = new JsonObject();
        String response = "";
        long amount;

        try {
            amount = jsonObject.has("amount") ?
                    jsonObject.getAsJsonPrimitive("amount")
                            .getAsLong() : -1;
        } catch (Exception exc) {
            amount = -1;
        }

        if (amount > 0) {
            if (amount <= 200) {
                response = "ALLOWED";
            } else if (amount <= 1500) {
                response = "MANUAL_PROCESSING";
            } else {
                response = "PROHIBITED";
            }
            object1.addProperty("result", response);
        }


        return object1.has("result") ?
                new ResponseEntity<>(gson.toJson(object1), HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
