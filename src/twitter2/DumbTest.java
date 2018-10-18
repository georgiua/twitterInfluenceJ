/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter2;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import twitter4j.Status;

/**
 *
 * @author alex
 */
public class DumbTest {
    
    public static void main(String args[]) throws SQLException {
        DBConnect dbconn = new DBConnect();
        dbconn.connect();
        Connection conn = dbconn.getConnection();
        DBReadWriter rw = new DBReadWriter();
        List<Status> tweets = rw.readTweets(conn);
        
        for(Status s:tweets) {
            System.out.println(s);
        }
        System.out.println(tweets.size());
    }
    
}
