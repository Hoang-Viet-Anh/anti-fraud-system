package antifraud;

import antifraud.usersDB.Database;
import antifraud.usersDB.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Component
public class UserRepository {
    @Autowired
    private Database database;

    public User findUserByUsername(String username) throws SQLException, ClassNotFoundException {
        return database.find(username);
    }

    public void save(User user) throws SQLException, ClassNotFoundException {
        database.addUser(user);
    }

    public List<User> getUserList() throws SQLException, ClassNotFoundException {
        return database.getUsers();
    }

    public boolean deleteUser(String username) throws SQLException, ClassNotFoundException {
        return database.deleteUser(username);
    }

}
