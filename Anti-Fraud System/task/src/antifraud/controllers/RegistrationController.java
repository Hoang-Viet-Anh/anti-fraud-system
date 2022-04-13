package antifraud.controllers;

import antifraud.UserRepository;
import antifraud.usersDB.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class RegistrationController {
    @Autowired
    UserRepository userRepo;
    @Autowired
    PasswordEncoder encoder;

    @PostMapping("/api/auth/user")
    public ResponseEntity<String> register(@RequestBody User user) {

        try {
            user.setPassword(encoder.encode(user.getPassword()));
            if (userRepo.findUserByUsername(user.getUsername()) == null) {
                userRepo.save(user);
                User addedUser = userRepo.findUserByUsername(user.getUsername());
                return new ResponseEntity<>(addedUser.getJsonUser(), HttpStatus.CREATED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
}
