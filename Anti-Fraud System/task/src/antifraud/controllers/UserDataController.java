package antifraud.controllers;

import antifraud.UserRepository;
import antifraud.usersDB.User;
import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
public class UserDataController {
    @Autowired
    UserRepository userRepo;

    @GetMapping("/api/auth/list")
    public ResponseEntity<String> getUsersData() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        JsonArray array = new JsonArray();
        try {
            List<User> list = userRepo.getUserList();
            list.forEach(a -> {
                array.add(JsonParser.parseString(a.getJsonUser()));
            });
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(gson.toJson(array), HttpStatus.OK);
    }

    @DeleteMapping("/api/auth/user/{username}")
    public ResponseEntity<String> deteleUser(@PathVariable String username) {
        try {
            if (userRepo.deleteUser(username)) {
                Gson gson = new GsonBuilder()
                        .setPrettyPrinting()
                        .create();
                JsonObject object = new JsonObject();
                object.addProperty("username", username);
                object.addProperty("status", "Deleted successfully!");
                return new ResponseEntity<>(gson.toJson(object), HttpStatus.OK);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
