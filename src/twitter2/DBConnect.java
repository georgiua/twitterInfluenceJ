package twitter2;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
    String server = "127.0.0.1";
    String dbName = "twitter";
    int port = 3306;
    String username = "root";
    String password = "root";
    private Connection conn;
    
    
    public void connect() {
        String url = "jdbc:mysql://" + server + ":" + 
					port + "/" + dbName;
        try {
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connection OK");
        }   catch(SQLException e) {
            System.out.println("Connection not OK");
            e.printStackTrace();
        }
    }
    
    public Connection getConnection() {
        return conn;
    }
    
    public void disconnect() throws SQLException {
        conn.close();
    }
}
