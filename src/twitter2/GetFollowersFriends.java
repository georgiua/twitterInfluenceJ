package twitter2;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import twitter4j.IDs;
import twitter4j.PagableResponseList;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.TwitterResponse;
import twitter4j.User;

public class GetFollowersFriends {
    
    public static void main(String args[]) throws SQLException, TwitterException, InterruptedException {
        Map<String, ResponseList> Followers = new HashMap<>();
        Twitter twitter;
        PagableResponseList<User> FollowersList = null;
        PagableResponseList<User> FriendsList = null;
        
        TwitterAccess ta = new TwitterAccess();
        twitter = ta.getTwitter();
        DBConnect dbconn = new DBConnect();
        dbconn.connect();
        Connection conn = dbconn.getConnection();
        DBReadWriter rw = new DBReadWriter();
        List<String> userList = rw.readUserList(conn);
        List<String> userListFollower = rw.readUserListFollowers(conn);
        for(String user: userList) {
            if(!userListFollower.contains(user)) {
                try {
                long followerCursor = -1;
                IDs followerIds;
                followerIds = twitter.getFollowersIDs(user, followerCursor, 5000);
                 
                rw.writeFollowers(user, TwitterObjectFactory.getRawJSON(followerIds), conn);
                
                long friendCursor = -1;
                IDs friendsIds;
                friendsIds = twitter.getFriendsIDs(user, friendCursor, 5000);
                rw.writeFriends(user, TwitterObjectFactory.getRawJSON(friendsIds), conn);
                Thread.sleep(61000);
                } catch(Exception e) {
                    rw.deleteTweetsByUser(user, conn);
                    e.printStackTrace();
                }
                
            }
        }
    }
}
