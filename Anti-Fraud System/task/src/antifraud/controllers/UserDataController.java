package antifraud.controllers;

import antifraud.UserRepository;
import antifraud.usersDB.Role;
import antifraud.usersDB.User;
import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
public class UserDataController {
    @Autowired
    UserRepository userRepo;

    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @GetMapping("/api/auth/list")
    public ResponseEntity<String> getUsersData() {
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

    @PutMapping("/api/auth/role")
    public ResponseEntity<String> changeRole(@RequestBody String json) {
        JsonObject object = JsonParser.parseString(json).getAsJsonObject();
        String username = object.getAsJsonPrimitive("username").getAsString();
        String role = object.getAsJsonPrimitive("role")
                                        .getAsString()
                                        .toUpperCase();
        try {
            User user = userRepo.changeRole(username, role);
            return new ResponseEntity<>(user.getJsonUser(), HttpStatus.OK);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/api/auth/access")
    public ResponseEntity<String> lockUser(@RequestBody String json) {
        JsonObject object = JsonParser.parseString(json).getAsJsonObject();
        String username = object.getAsJsonPrimitive("username").getAsString();
        String action = object.getAsJsonPrimitive("operation").getAsString();
        try {
            if(userRepo.lockUser(username, action)) {
                object = new JsonObject();
                object.addProperty("status",
                        String.format("User %s %sed!", username, action.toLowerCase()));
                return new ResponseEntity<>(gson.toJson(object), HttpStatus.OK);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
