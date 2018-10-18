
package twitter2;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author alex
 */
public class TestDBConnection {
    
    public static void main(String args[]) throws SQLException {
        DBConnect dbconn = new DBConnect();
        dbconn.connect();
        Connection conn = dbconn.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT DISTINCT topic FROM tweets");
        while(rs.next())
            System.out.println(rs.getString(1));
    }
}
