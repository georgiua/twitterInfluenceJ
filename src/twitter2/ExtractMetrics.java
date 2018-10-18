package twitter2;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;

public class ExtractMetrics {
    
    public static void main(String args[]) throws SQLException, TwitterException {
        String topic = "#bigdata";
        Map<String,String> Followers = new HashMap<>();
        Map<String,String> Friends = new HashMap<>();
        DBConnect dbconn = new DBConnect();
        dbconn.connect();
        Connection conn = dbconn.getConnection();
        DBReadWriter rw = new DBReadWriter();
        List<Status> tweetList = new ArrayList<>();
        tweetList = rw.readTweetsByTopic(topic, conn);
        List<String> userList = rw.readUserListByTopic(topic, conn);
        Parser parser = new Parser();
        parser.parseTweetList(tweetList);
        for(String user: userList) {
            Followers.put(user, rw.readFollowersByUsername(user, conn));
            Friends.put(user, rw.readFriendsByUsername(user, conn));
        }
        parser.extractMetrics(Followers, Friends);
        parser.extractFeatures(tweetList.size());
        Map<String, double[]> FeatureMap = parser.getFeatures();
        Map<String, double[]> FeatureMapNew = parser.getFeaturesNew();
        for(String user: FeatureMap.keySet()) {
            String features = "";
            double[] temp = FeatureMap.get(user);
            for(int i=0;i<temp.length;i++) {
                features = features + temp[i] + ";";
            }
            rw.writeFeatures(user, features, topic, conn);
            temp = FeatureMapNew.get(user);
            features = "";
            for(int i=0;i<temp.length;i++) {
                features = features + temp[i] + ";";
            }
            rw.writeFeaturesNew(user, FeatureMapNew.get(user).toString(), topic, conn);
        }
           
    }
}
