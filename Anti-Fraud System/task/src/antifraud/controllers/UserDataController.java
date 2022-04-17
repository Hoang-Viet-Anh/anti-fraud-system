package antifraud.controllers;

import antifraud.database.user.UserRepository;
import antifraud.database.user.Role;
import antifraud.database.user.User;
import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserDataController {
    @Autowired
    private UserRepository userRepo;

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @GetMapping("/api/auth/list")
    ResponseEntity<String> getUsersData() {
        JsonArray array = new JsonArray();
        List<User> list = new ArrayList<>();
        userRepo.findAll().forEach(list::add);
        list.forEach(a -> {
            array.add(JsonParser.parseString(a.getJsonUser()));
        });
        return new ResponseEntity<>(gson.toJson(array), HttpStatus.OK);
    }

    @DeleteMapping("/api/auth/user/{username}")
    ResponseEntity<String> deteleUser(@PathVariable String username) {
        if (!userRepo.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            userRepo.deleteByUsername(username);
            JsonObject object = new JsonObject();
            object.addProperty("username", username);
            object.addProperty("status", "Deleted successfully!");
            return new ResponseEntity<>(gson.toJson(object), HttpStatus.OK);
        }
    }

    @PutMapping("/api/auth/role")
    ResponseEntity<String> changeRole(@RequestBody String json) {
        JsonObject object = JsonParser.parseString(json).getAsJsonObject();
        String username = object.getAsJsonPrimitive("username").getAsString();
        String role = object.getAsJsonPrimitive("role")
                                        .getAsString()
                                        .toUpperCase();
        if (!userRepo.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else if (!role.equals(Role.SUPPORT.toString()) &&
                !role.equals(Role.MERCHANT.toString())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (userRepo.findByUsername(username)
                .get(0)
                .getRole()
                .toString()
                .equals(role)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } else {
            User user = userRepo.findByUsername(username).get(0);
            user.setRole(Role.valueOf(role));
            user = userRepo.save(user);
            return new ResponseEntity<>(user.getJsonUser(), HttpStatus.OK);
        }
    }

    @PutMapping("/api/auth/access")
    public ResponseEntity<String> lockUser(@RequestBody String json) {
        JsonObject object = JsonParser.parseString(json).getAsJsonObject();
        String username = object.getAsJsonPrimitive("username").getAsString();
        String action = object.getAsJsonPrimitive("operation").getAsString();
        if (!userRepo.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else if (userRepo.findByUsername(username)
                .get(0)
                .getRole()
                .equals(Role.ADMINISTRATOR)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else {
            User user = userRepo.findByUsername(username).get(0);
            user.setLock(action.toUpperCase().equals("UNLOCK"));
            userRepo.save(user);
            object = new JsonObject();
            object.addProperty("status",
                    String.format("User %s %sed!", username, action.toLowerCase()));
            return new ResponseEntity<>(gson.toJson(object), HttpStatus.OK);
        }
    }
}
