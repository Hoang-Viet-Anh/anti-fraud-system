package antifraud.controllers;

import antifraud.database.ip.IP;
import antifraud.database.ip.IpRepository;
import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
public class SuspiciosIpController {
    @Autowired
    private IpRepository ipRepo;

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @PostMapping("/api/antifraud/suspicious-ip")
    ResponseEntity<String> addIP(@RequestBody IP ip) {
        if (ipRepo.existsByIp(ip.getIp())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This IP is already exists.");
        } else if (!ip.isValid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This IP address has the wrong format.");
        } else {
            ip = ipRepo.save(ip);
            return new ResponseEntity<>(ip.toJson(), HttpStatus.OK);
        }
    }

    @DeleteMapping("/api/antifraud/suspicious-ip/{ip}")
    ResponseEntity<String> deleteIP(@PathVariable String ip) {
        IP object = new IP();
        object.setIp(ip);
        if (!object.isValid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "IP has the wrong format.");
        } else if (!ipRepo.existsByIp(ip)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "IP is not found in the database.");
        } else {
            ipRepo.deleteByIp(ip);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status",
                    String.format("IP %s successfully removed!", ip));
            return new ResponseEntity<>(gson.toJson(jsonObject), HttpStatus.OK);
        }
    }

    @GetMapping("/api/antifraud/suspicious-ip")
    ResponseEntity<String> getIPlist() {
        JsonArray array = new JsonArray();
        List<IP> list = new ArrayList<>();
        ipRepo.findAll().forEach(list::add);
        list.forEach(a -> {
            array.add(JsonParser.parseString(a.toJson()));
        });
        return new ResponseEntity<>(gson.toJson(array), HttpStatus.OK);
    }
}
