package antifraud.usersDB;

public class StatementsSQL {
     public static final String GET_USER = "SELECT * FROM User WHERE username = ?";
     public static final String ADD_USER = "INSERT INTO User (name, username, password) VALUES (?, ?, ?)";
     public static final String GET_TABLE = "SELECT * FROM User";
     public static final String DELETE_USER ="DELETE FROM User WHERE username = ?";
     public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS user (\n" +
             "  id INT PRIMARY KEY AUTO_INCREMENT,\n" +
             "  name VARCHAR(64),\n" +
             "  username VARCHAR(64) NOT NULL,\n" +
             "  password VARCHAR(128) NOT NULL\n" +
             ");\n";
}
