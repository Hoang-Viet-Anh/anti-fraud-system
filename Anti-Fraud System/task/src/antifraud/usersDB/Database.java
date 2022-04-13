package antifraud.usersDB;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class Database {
    // JDBC driver name and database URL
    @Value("${spring.datasource.driverClassName}")
    private String jdbcDriver;
    @Value("${spring.datasource.url}")
    private String dbUrl;

    //  Database credentials
    @Value("${spring.datasource.username}")
    private String user;
    @Value("${spring.datasource.password}")
    private String pass;

    private boolean isExist = false;

    public Database() {
    }

    private Connection connection;

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, user, pass);
    }

    public void connect() throws SQLException, ClassNotFoundException{
        Class.forName(jdbcDriver);
        connection = getConnection();
        if (!isExist) {
            PreparedStatement preparedStatement = connection.prepareStatement(StatementsSQL.CREATE_TABLE);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            isExist = true;
        }
    }

    public User find(String value) throws SQLException, ClassNotFoundException {
        connect();
        PreparedStatement preparedStatement = connection.prepareStatement(StatementsSQL.GET_USER);
        preparedStatement.setString(1, value);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            User user = new User();
            user.setId(resultSet.getLong("id"));
            user.setName(resultSet.getString("name"));
            user.setUsername(resultSet.getString("username"));
            user.setPassword(resultSet.getString("password"));
            user.setRole(Role.valueOf(resultSet.getString("role")));
            user.setLock(resultSet.getBoolean("isUnlocked"));
            closeConnection();
            preparedStatement.close();
            resultSet.close();
            return user;
        } else {
            resultSet.close();
            closeConnection();
            preparedStatement.close();
            return null;
        }
    }

    synchronized public void addUser(User user) throws SQLException, ClassNotFoundException {
        connect();
        PreparedStatement preparedStatement = connection.prepareStatement(StatementsSQL.ADD_USER);
        preparedStatement.setString(1, user.getName());
        preparedStatement.setString(2, user.getUsername());
        preparedStatement.setString(3, user.getPassword());
        preparedStatement.setString(4, hasUsers() ?
                Role.MERCHANT.toString() : Role.ADMINISTRATOR.toString());
        preparedStatement.setBoolean(5, !hasUsers());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        closeConnection();
    }

    public List<User> getUsers() throws SQLException, ClassNotFoundException {
        List<User> list = new ArrayList<>();
        connect();
        PreparedStatement preparedStatement = connection.prepareStatement(StatementsSQL.GET_TABLE);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            User user = new User();
            user.setId(resultSet.getLong("id"));
            user.setName(resultSet.getString("name"));
            user.setUsername(resultSet.getString("username"));
            user.setPassword(resultSet.getString("password"));
            user.setRole(Role.valueOf(resultSet.getString("role")));
            user.setLock(resultSet.getBoolean("isUnlocked"));
            list.add(user);
        }
        preparedStatement.close();
        resultSet.close();
        return list;
    }

    synchronized public boolean deleteUser(String username) throws SQLException, ClassNotFoundException {
        if (find(username) != null) {
            connect();
            PreparedStatement preparedStatement = connection.prepareStatement(StatementsSQL.DELETE_USER);
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            closeConnection();
            return true;
        } else {
            return false;
        }
    }

    public boolean hasUsers() throws SQLException, ClassNotFoundException {
        connect();
        PreparedStatement preparedStatement = connection.prepareStatement(StatementsSQL.GET_TABLE);
        ResultSet resultSet = preparedStatement.executeQuery();
        boolean hasUsers = resultSet.next();
        resultSet.close();
        preparedStatement.close();
        closeConnection();
        return hasUsers;
    }

    public User changeRole(String username, String role) throws SQLException,
            ClassNotFoundException, ResponseStatusException {
        User user = find(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else if (!role.equals(Role.SUPPORT.toString()) &&
                    !role.equals(Role.MERCHANT.toString())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (user.getRole().toString().equals(role)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } else {
            connect();
            PreparedStatement preparedStatement = connection.prepareStatement(StatementsSQL.CHANGE_ROLE);
            preparedStatement.setString(1, role.toString());
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            closeConnection();
            user = find(username);
            return user;
        }
    }

    public boolean lockUser(String username, String action) throws SQLException, ClassNotFoundException {
        User user = find(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else if (user.getRole().equals(Role.ADMINISTRATOR)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else {
            connect();
            PreparedStatement preparedStatement = connection.prepareStatement(StatementsSQL.CHANGE_LOCK);
            preparedStatement.setBoolean(1, action.toUpperCase().equals("UNLOCK"));
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            closeConnection();
            return true;
        }
    }

    public void closeConnection() throws SQLException {
        connection.close();
    }
}
