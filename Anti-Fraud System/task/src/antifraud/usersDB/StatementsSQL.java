package antifraud.usersDB;

public class StatementsSQL {
     public static final String GET_USER = "SELECT * FROM user WHERE username = ?";
     public static final String ADD_USER = "INSERT INTO user (name, username, password, role, isUnlocked) VALUES (?, ?, ?, ?, ?)";
     public static final String GET_TABLE = "SELECT * FROM user";
     public static final String DELETE_USER = "DELETE FROM user WHERE username = ?";
     public static final String CHANGE_ROLE = "UPDATE user SET role = ? WHERE username = ?";
     public static final String CHANGE_LOCK = "UPDATE user SET isUnlocked = ? WHERE username = ?";
     public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS user (\n" +
             "  id INT PRIMARY KEY AUTO_INCREMENT,\n" +
             "  name VARCHAR(64),\n" +
             "  username VARCHAR(64) NOT NULL,\n" +
             "  password VARCHAR(128) NOT NULL,\n" +
             "  role VARCHAR(64) NOT NULL,\n" +
             "  isUnlocked BIT NOT NULL\n" +
             ");\n";

     public StatementsSQL() {
     }
}
