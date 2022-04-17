package antifraud.database.ip;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import javax.persistence.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Table(name = "ip")
public class IP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "ip", nullable = false, unique = true)
    private String ip;

    public IP() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String toJson() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        object.addProperty("ip", ip);
        return gson.toJson(object);
    }

    public boolean isValid() {
        Pattern pattern = Pattern.compile("(\\d{1,3}).(\\d{1,3}).(\\d{1,3}).(\\d{1,3})");
        Matcher matcher = pattern.matcher(ip);
        if (matcher.matches()) {
            for (int i = 1; i <= 4; i++) {
                int value = Integer.parseInt(matcher.group(i));
                if (value < 0 || value > 255) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
