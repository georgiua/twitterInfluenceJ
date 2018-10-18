package twitter2;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Place;
import twitter4j.RateLimitStatus;
import twitter4j.Scopes;
import twitter4j.Status;
import twitter4j.SymbolEntity;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;

public class DBReadWriter {
    
    public void writeTweets (String tweet, String user, String topic, Connection conn) {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO tweets (tweet, user, topic) VALUES (?,?,?)");
            stmt.setString(1, tweet);
            stmt.setString(2, user);
            stmt.setString(3, topic);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void writeFollowers(String user, String toWrite, Connection conn) {
        
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO followers (user, followers) VALUES (?,?)");
            stmt.setString(1, user);
            stmt.setString(2, toWrite);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void writeFriends(String user, String toWrite, Connection conn) {
        
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO friends (user, friends) VALUES (?,?)");
            stmt.setString(1, user);
            stmt.setString(2, toWrite);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    
    }
    
    public void writeIdByScreenname(String user, long id, Connection conn) {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO ids (user, id) VALUES (?,?)");
            stmt.setString(1, user);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
     public void writeFeatures(String user, String toWrite, String topic, Connection conn) {
        
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO features (user, features, topic) VALUES (?,?,?)");
            stmt.setString(1, user);
            stmt.setString(2, toWrite);
            stmt.setString(3, topic);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    
    }
     
    public void writeFeaturesNew(String user, String toWrite, String topic, Connection conn) {
        
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO featuresNew (user, features, topic) VALUES (?,?,?)");
            stmt.setString(1, user);
            stmt.setString(2, toWrite);
            stmt.setString(3, topic);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    
    }
    
    public List<Status> readTweets(Connection conn) throws SQLException {
        List<Status> tweetList = new ArrayList<>();
        Statement stmt;
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT tweet FROM tweets");
        while(rs.next()) {
            try {
                String tweet = rs.getString("tweet");
                tweetList.add(TwitterObjectFactory.createStatus(tweet));
            } catch (TwitterException ex) {
                ex.printStackTrace();
            }
        }
        rs.close();
        return tweetList;
    }
    
    public List<Status> readTweetsByTopic(String topic, Connection conn) throws SQLException {
        List<Status> tweetList = new ArrayList<>();
        PreparedStatement stmt;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement("SELECT tweet FROM tweets WHERE topic=?");
            stmt.setString(1, topic);
            rs = stmt.executeQuery();
        } catch(Exception e) {
            e.printStackTrace();
        }
        while(rs.next()) {
            try {
                String tweet = rs.getString("tweet");
                tweetList.add(TwitterObjectFactory.createStatus(tweet));
            } catch (TwitterException ex) {
                ex.printStackTrace();
            }
        }
        rs.close();
        return tweetList;
    }
    
    
    public List<String> readUserList(Connection conn) throws SQLException {
        Statement stmt;
        List<String> userList = new ArrayList<>();
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT DISTINCT user FROM tweets");
        while(rs.next()) {
            userList.add(rs.getString("user"));
        }
        rs.close();
        return userList;
    }
    
    public List<String> readUserListByTopic(String topic, Connection conn) throws SQLException {
        PreparedStatement stmt;
        List<String> userList = new ArrayList<>();
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement("SELECT DISTINCT user FROM tweets WHERE topic=?");
            stmt.setString(1, topic);
            rs = stmt.executeQuery();
        } catch(Exception e) {
            e.printStackTrace();
        }
        while(rs.next()) {
            userList.add(rs.getString("user"));
        }
        rs.close();
        return userList;
    }
    
    @Deprecated
    public List<String> readUserListFollowers(Connection conn) throws SQLException {
        Statement stmt;
        List<String> userList = new ArrayList<>();
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT DISTINCT user FROM followers");
        while(rs.next()) {
            userList.add(rs.getString("user"));
        }
        rs.close();
        return userList;
    }
    
    public void deleteTweetsByUser(String user, Connection conn) {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("DELETE FROM tweets WHERE user=?");
            stmt.setString(1, user);
            stmt.executeUpdate();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public String readFollowersByUsername(String user, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement("SELECT followers FROM followers WHERE user=?");
            stmt.setString(1, user);
            rs = stmt.executeQuery();
            } catch(Exception e) {
                e.printStackTrace();
            }
        if(rs.next()) {
            String returnString = rs.getString(1);
            rs.close();
            return returnString;
        }
        else 
            return null;
    }
    
    public String readFriendsByUsername(String user, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement("SELECT friends FROM friends WHERE user=?");
            stmt.setString(1, user);
            rs = stmt.executeQuery();
        } catch(Exception e) {
            e.printStackTrace();
        }
        if(rs.next()) {
            String returnString = rs.getString(1);
            rs.close();
            return returnString;
        }
        else 
            return null;
    }
    
    public String readIdByUsername(String user, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement("SELECT id FROM ids WHERE user=?");
            stmt.setString(1, user);
            rs = stmt.executeQuery();
        } catch(Exception e) {
            e.printStackTrace();
        }
       if(rs.next()) {
            String returnString = rs.getString(1);
            rs.close();
            return returnString;
        }
        else 
            return null;
    }
    
    public String readUsernameById(String id, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement("SELECT user FROM ids WHERE id=?");
            stmt.setString(1, id);
            rs = stmt.executeQuery();
        } catch(Exception e) {
            e.printStackTrace();
        }
       if(rs.next()) {
            String returnString = rs.getString(1);
            rs.close();
            return returnString;
        }
        else 
            return null;
       
    }
    
    public List<String> readIdList(Connection conn) throws SQLException {
        Statement stmt = null;
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT id FROM ids");
        List<String> idList = new ArrayList<>();
        
        while(rs.next()) {
            idList.add(rs.getString("id"));
        }
        rs.close();
        return idList;
    }
    
    public Map<String, String> readFeaturesByTopic(String topic, Connection conn) throws SQLException {
        Map<String, String> featureMap = new HashMap<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement("SELECT user,features FROM features WHERE topic=?");
            stmt.setString(1, topic);
            rs = stmt.executeQuery();
        } catch(Exception e) {
            e.printStackTrace();
        }
        while(rs.next()) {
            featureMap.put(rs.getString(1), rs.getString(2));
        }
        return featureMap;
    }
    
    public Map<String, String> readFeaturesNewByTopic(String topic, Connection conn) throws SQLException {
        Map<String, String> featureMap = new HashMap<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement("SELECT user,features FROM featuresNew WHERE topic=?");
            stmt.setString(1, topic);
            rs = stmt.executeQuery();
        } catch(Exception e) {
            e.printStackTrace();
        }
        while(rs.next()) {
            featureMap.put(rs.getString(1), rs.getString(2));
        }
        return featureMap;
    }
}
