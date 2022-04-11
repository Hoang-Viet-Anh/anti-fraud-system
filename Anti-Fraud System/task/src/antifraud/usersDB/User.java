package antifraud.usersDB;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class User {
    private long id;
    private String name;
    private String username;
    private String password;

    public User() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username.toLowerCase();
    }

    public void setUsername(String username) {
        this.username = username.toLowerCase();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJsonUser() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        object.addProperty("name", name);
        object.addProperty("username", username);
        return gson.toJson(object);
    }

    public static User parseJson(String json) {
        JsonObject object = JsonParser.parseString(json).getAsJsonObject();
        User user = new User();
        user.setName(object.getAsJsonPrimitive("name").getAsString());
        user.setUsername(object.getAsJsonPrimitive("username").getAsString());
        user.setPassword(object.getAsJsonPrimitive("password").getAsString());
        return user;
    }

}
