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
    private Role role;
    private boolean lock = false;

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
        lock = role.equals(Role.ADMINISTRATOR);
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public String getJsonUser() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        object.addProperty("name", name);
        object.addProperty("username", username);
        object.addProperty("role", role.toString());
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
