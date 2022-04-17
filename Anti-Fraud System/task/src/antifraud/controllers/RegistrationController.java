package antifraud.controllers;

import antifraud.database.user.UserRepository;
import antifraud.database.user.Role;
import antifraud.database.user.User;
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
            if (!userRepo.existsByUsername(user.getUsername())) {
                if (userRepo.count() == 0) {
                    user.setLock(true);
                    user.setRole(Role.ADMINISTRATOR);
                } else {
                    user.setRole(Role.MERCHANT);
                    user.setLock(false);
                }
                User addedUser = userRepo.save(user);
                return new ResponseEntity<>(addedUser.getJsonUser(), HttpStatus.CREATED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
}
