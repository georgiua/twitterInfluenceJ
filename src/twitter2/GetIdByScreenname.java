/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter2;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class GetIdByScreenname {
    
    public static void main(String args[]) throws SQLException, TwitterException, InterruptedException {
        Twitter twitter;
        TwitterAccess ta = new TwitterAccess();
        twitter = ta.getTwitter();
        DBConnect dbconn = new DBConnect();
        dbconn.connect();
        Connection conn = dbconn.getConnection();
        DBReadWriter rw = new DBReadWriter();
        List<String> userList = rw.readUserList(conn);
        long id;
        for(String user:userList) {
            id = twitter.showUser(user).getId();
            rw.writeIdByScreenname(user, id, conn);
            System.out.println(user + " " + id);
            Thread.sleep(6000);
        }
        
    }
     
}
