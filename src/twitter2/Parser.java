package twitter2;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import twitter4j.IDs;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.User;
import twitter4j.UserMentionEntity;

public class Parser {

    Date StartingDate;
    Date FinishingDate;
    List<Status> tweetHelpList = new ArrayList();
    Map<String, List<Status>> UserTweetMap = new HashMap<>();
    List listPostID = new ArrayList();
    List listUserName = new ArrayList();
    List listUserID = new ArrayList();
    List listCreationDate = new ArrayList();
    List listCreationDate1 = new ArrayList();
    
    // STANDARD METRICS
    Map<String, Integer> OT1 = new HashMap<>();
    Map<String, Integer> OT2 = new HashMap<>();
    Map<String, Double> OT3 = new HashMap<>();
    Map<String, Integer> OT4 = new HashMap<>();
    Map<String, Integer> CT1 = new HashMap<>();
    Map<String, Integer> CT2 = new HashMap<>();
    Map<String, Integer> RT1 = new HashMap<>();
    Map<String, Integer> RT2 = new HashMap<>();
    Map<String, Integer> RT3 = new HashMap<>();
    Map<String, Integer> M1 = new HashMap<>();
    Map<String, Integer> M2 = new HashMap<>();
    Map<String, Integer> M3 = new HashMap<>();
    Map<String, Integer> M4 = new HashMap<>();
    Map<String, Integer> G1 = new HashMap<>();
    Map<String, Integer> G2 = new HashMap<>();
    Map<String, Integer> G3 = new HashMap<>();
    Map<String, Integer> G4 = new HashMap<>();
    
    // NEW METRICS
    Map<String, Float> Freq = new HashMap<>();
    Map<String, Integer> Morning = new HashMap<>();
    Map<String, Integer> Noon = new HashMap<>();
    Map<String, Integer> Evening = new HashMap<>();
    Map<String, Integer> Night = new HashMap<>();
    
    // FEATURE LIST
    Map<String, Double> TS = new HashMap<>();  //topical signal
    Map<String, Double> SS = new HashMap<>(); //signal strength
    Map<String, Double> nonCS = new HashMap<>(); // non-chat signal
    Map<String, Double> RI = new HashMap<>(); // retweet impact
    Map<String, Double> MI = new HashMap<>(); // mention impact
    Map<String, Double> ID1 = new HashMap<>(); // information diffusion
    Map<String, Double> NS = new HashMap<>(); // network score
    
    Logger logger = Logger.getLogger("logger2");
    User u = null;
    DBConnect dbconn;
    Connection conn;
    DBReadWriter rw;
    
    public Parser() {
        dbconn = new DBConnect();
        dbconn.connect();
        conn = dbconn.getConnection();
        rw = new DBReadWriter();
    }

    public Map<String, List<Status>> parseTweetList(List tweetList) {
        
        Map<String, List<Status>> tempMap;
        
        tweetHelpList.addAll(tweetList);
        for (int i = 0; i < tweetList.size(); i++) {
            listUserName.add(tweetHelpList.get(i).getUser().getScreenName());
        }
        
        StartingDate = tweetHelpList.get(0).getCreatedAt();
        FinishingDate = tweetHelpList.get(tweetHelpList.size() - 1).getCreatedAt();
        tempMap = parseTweetList(listUserName, tweetHelpList);
        return tempMap;
    }

    public Map<String, List<Status>> parseTweetList(List<String> listUserName, List<Status> tweetHelpList) {

        for (int i = 0; i < listUserName.size(); i++) {
            List<Status> localList = new ArrayList();
            for (int j = 0; j < tweetHelpList.size(); j++) {
                if (listUserName.get(i).equals(tweetHelpList.get(j).getUser().getScreenName())) {
                    localList.add(tweetHelpList.get(j));
                }
            }
            UserTweetMap.put(listUserName.get(i), localList);
        }
        return UserTweetMap;
    }

    public void extractMetrics(Map<String, String> Followers, Map<String, String> Friends) throws SQLException, TwitterException {
        
        logger.setLevel(Level.INFO);

        // number of original tweets
        List<Status> temp = new ArrayList();
        String users;
        int counter;
        CharSequence s = "RT ";

        // number of original tweets OT1
        for (String key : UserTweetMap.keySet()) {
            counter = 0;
            
            temp = UserTweetMap.get(key);
            for (int i = 1; i < temp.size(); i++) {
                if (!(temp.get(i).getText().contains(s))) {
                    counter++;
                }
            }
            OT1.put(key, counter);
        }
        System.out.println("OT1 " + OT1);
        logger.info("OT1\n\n" + OT1 + "\n----------------------\n");

        // number of links shared OT2
        for (String key : UserTweetMap.keySet()) {
            counter = 0;
            temp = UserTweetMap.get(key);
            for (int i = 1; i < temp.size(); i++) {
                counter = counter + temp.get(i).getHashtagEntities().length;
            }
            OT2.put(key, counter);
        }
        System.out.println("OT2 " + OT2);
        logger.info("OT2\n\n" + OT2 + "\n----------------------\n");
        
//         self similarity score OT3
        for (String key : UserTweetMap.keySet()) {
            int distance = 0;
            double finalDistance = 0;
            int temp2 = 0;
            temp = UserTweetMap.get(key);
            for (int i = 1; i < temp.size() - 1; i++) {
                for (int j = i + 1; j < temp.size() - 1; j++) {
                    distance = LevenshteinDistance.computeLevenshteinDistance(temp.get(j).getText(), temp.get(i).getText());
                    temp2 = temp2 + distance;
                }
            }
            if (temp.size() == 1) {
                finalDistance = distance;
            } else {
                finalDistance = (2 * temp2) / (temp.size() * (temp.size() - 1));
            }
            OT3.put(key, finalDistance);
        }
        System.out.println("OT3 " + OT3);
        logger.info("OT3\n\n" + OT3 + "\n----------------------\n");

        // number of hashtags OT4
        for (String key : UserTweetMap.keySet()) {
            counter = 0;
            temp = UserTweetMap.get(key);
            for (int i = 1; i < temp.size(); i++) {
                counter = counter + temp.get(i).getHashtagEntities().length;
            }
            OT4.put(key, counter);
        }
        System.out.println("OT4 " + OT4);
        logger.info("OT4\n\n" + OT4 + "\n----------------------\n");

        // number of conversational tweets CT1
        for (String key : UserTweetMap.keySet()) {
            counter = 0;
            temp = UserTweetMap.get(key);
            for (int i = 1; i < temp.size(); i++) {
                if (temp.get(i).getInReplyToStatusId() != 0) {
                    counter++;
                }
            }
            CT1.put(key, counter);
        }
        System.out.println("CT1 " + CT1);
        logger.info("CT1\n\n" + CT1 + "\n----------------------\n");

        // number of conversational tweets started by user CT2
        // initialization of CT2
        for (String key : UserTweetMap.keySet()) {
            CT2.put(key, 0);
        }
        
        for (String key : UserTweetMap.keySet()) {
            counter = 0;
            temp = UserTweetMap.get(key);
            String replyName = null;
            for (int i = 1; i < temp.size(); i++) {
                if (temp.get(i).getInReplyToScreenName() != null) {
                    replyName = temp.get(i).getInReplyToScreenName();
                }
                if (replyName != null) {
                    if(CT2.containsKey(replyName)) {
                        counter = CT2.get(replyName);
                        counter++; // <3
                    }
                    CT2.put(key, counter);
                }
            }
        }
        System.out.println("CT2 " + CT2);
        logger.info("CT2\n\n" + CT2 + "\n----------------------\n");
        
        // number of rts of others' tweets RT1
        for (String key : UserTweetMap.keySet()) {
            counter = 0;
            temp = UserTweetMap.get(key);
            for (int i = 1; i < temp.size(); i++) {
                if ((temp.get(i).getText().contains(s))) {
                    counter++;
                }
            }
            RT1.put(key, counter);
        }
        System.out.println("RT1 " + RT1);
        logger.info("RT1\n\n" + RT1 + "\n----------------------\n");

        // number of OT1 rtd by others RT2
        for (String key : UserTweetMap.keySet()) {
            counter = 0;
            long id = 0;
            temp = UserTweetMap.get(key);
            for (int i = 1; i < temp.size(); i++) {
                if (!(temp.get(i).getText().contains(s))) {
                    if(temp.get(i).getRetweetCount()!=0) {
                        counter++;
                    }
                }
            }
            RT2.put(key, counter);
        }
        System.out.println("RT2 " + RT2);
        logger.info("RT2\n\n" + RT2 + "\n----------------------\n");

        // number of unique users who rtd user's tweets RT3
        List<String> tempListUsername = new ArrayList();
        for (String key : UserTweetMap.keySet()) {
            for (Map.Entry<String, List<Status>> tempMap : UserTweetMap.entrySet()) {
                List<Status> value = tempMap.getValue();
                for (Status tempTweet : value) {
                    if (tempTweet.getText().contains(s)) {
                        for (UserMentionEntity entity : tempTweet.getUserMentionEntities()) {
                            if (entity.getScreenName().equals(key)) {
                                if (!tempListUsername.contains(tempTweet.getUser().getScreenName())) {
                                    tempListUsername.add(tempTweet.getUser().getScreenName());
                                }
                            }
                        }
                    }
                }
            }
            RT3.put(key, tempListUsername.size());
            tempListUsername.clear();
        }
        System.out.println("RT3 " + RT3);
        logger.info("RT3\n\n" + RT3 + "\n----------------------\n");

        // number of mentions of other users M1
        for (String key : UserTweetMap.keySet()) {
            counter = 0;
            temp = UserTweetMap.get(key);
            for (int i = 1; i < temp.size(); i++) {
                counter = counter + temp.get(i).getUserMentionEntities().length;
            }
            M1.put(key, counter);
        }
        System.out.println("M1 " + M1);
        logger.info("M1\n\n" + M1 + "\n----------------------\n");

        // number of unique mentions by user M2
        for (String key : UserTweetMap.keySet()) {
            temp = UserTweetMap.get(key);
            for (int i = 1; i < temp.size(); i++) {
                for (UserMentionEntity entity : temp.get(i).getUserMentionEntities()) {
                    if (!tempListUsername.contains(entity.getScreenName())) {
                        tempListUsername.add(entity.getScreenName());
                    }
                }
            }
            M2.put(key, tempListUsername.size());
            tempListUsername.clear();
        }
        System.out.println("M2 " + M2);
        logger.info("M2\n\n" + M2 + "\n----------------------\n");

        // number of mentions by others M3 + compute M4
        for (String key : UserTweetMap.keySet()) {
            counter = 0;
            for (Map.Entry<String, List<Status>> tempMap : UserTweetMap.entrySet()) {
                List<Status> value = tempMap.getValue();
                for (Status tempTweet : value) {
                    for (UserMentionEntity entity : tempTweet.getUserMentionEntities()) {
                        if (entity.getScreenName().equals(key)) {
                            counter++;
                            if (!tempListUsername.contains(tempTweet.getUser().getScreenName())) {
                                tempListUsername.add(tempTweet.getUser().getScreenName());
                            }
                        }
                    }
                }
            }
            M3.put(key, counter);
            M4.put(key, tempListUsername.size());
            tempListUsername.clear();
        }
        System.out.println("M3 " + M3);
        System.out.println("M4 " + M4);
        logger.info("M3\n\n" + M3 + "\n----------------------\n");
        logger.info("M4\n\n" + M4 + "\n----------------------\n");
        
        List<String> idList = rw.readIdList(conn);
        // number of topically active followers G1
        for (String key : UserTweetMap.keySet()) {
            counter = 0;
            users = null;
            users = Followers.get(key);
            IDs followerIds = TwitterObjectFactory.createIDs(users);
            long[] ids = followerIds.getIDs();
            for(int i =0; i<ids.length;i++) {
                String idString = String.valueOf(ids[i]);
                if(idList.contains(idString)) {
                    counter++;
                } else
                    continue;
            }
            
            G1.put(key, counter);
        }
        System.out.println("G1 " + G1);
        logger.info("G1\n\n" + G1 + "\n----------------------\n");

        // number of topically active friends G2
        for (String key : UserTweetMap.keySet()) {
            counter = 0;
            users = null;
            users = Friends.get(key);
            IDs friendsIds = TwitterObjectFactory.createIDs(users);
            long[] ids = friendsIds.getIDs();
            for(int i =0; i<ids.length;i++) {
                String idString = String.valueOf(ids[i]);
                if(idList.contains(idString)) {
                    counter++;
                } else
                    continue;
            }
            
            G2.put(key, counter);
        }
        System.out.println("G2 " + G2);
        logger.info("G2\n\n" + G2 + "\n----------------------\n");

        // number of followers tweeting after author G3
        for (String key : UserTweetMap.keySet()) {
            counter = 0;
            users = null;
            users = Followers.get(key);
            IDs followersIds = TwitterObjectFactory.createIDs(users);
            long[] ids = followersIds.getIDs();
            if (users != null) {
                for (int i=0; i<ids.length;i++) {
                    String idString = String.valueOf(ids[i]);
                    String username = rw.readUsernameById(idString, conn);
                    if (UserTweetMap.keySet().contains(username)) {
                        if (!(UserTweetMap.get(username).isEmpty())) {
                            if ((UserTweetMap.get(key).get(1).getCreatedAt().after(UserTweetMap.get(username).get(1).getCreatedAt()))) {
                                counter++;
                            }
                        }
                    }
                }
            }
            else
                continue;
            G3.put(key, counter);
        }
        System.out.println("G3 " + G3);
        logger.info("G3\n\n" + G3 + "\n----------------------\n");
        
        // number of friends before author G4
        for (String key : UserTweetMap.keySet()) {
            counter = 0;
            users = null;
            users = Friends.get(key);
            IDs friendsIds = TwitterObjectFactory.createIDs(users);
            long[] ids = friendsIds.getIDs();
            if (users != null) {
                for (int i=0; i<ids.length;i++) {
                    String idString = String.valueOf(ids[i]);
                    String username = rw.readUsernameById(idString, conn);
                    if (UserTweetMap.keySet().contains(username)) {
                        if (!(UserTweetMap.get(username).isEmpty())) {
                            if ((UserTweetMap.get(key).get(1).getCreatedAt().after(UserTweetMap.get(username).get(1).getCreatedAt()))) {
                                counter++;
                            }
                        }
                    }
                }
            }
            else
                continue;
            G4.put(key, counter);
        }
        System.out.println("G4 " + G4);
        logger.info("G4\n\n" + G4 + "\n----------------------\n");
        
        // frequency of posting - new metric
        int duration = (int) ((FinishingDate.getTime() - StartingDate.getTime()) / 1000 * 60);
        float frequency = 0;
        for (String key : UserTweetMap.keySet()) {
            if(duration !=0)
                frequency = UserTweetMap.get(key).size() / duration;
            Freq.put(key, frequency);
        }
        System.out.println("Freq " + Freq);

        // timezones - new metric
        Calendar calendar = Calendar.getInstance();
        
        for (String key : UserTweetMap.keySet()) {
            int morningcount = 0;
            int nooncount = 0;
            int eveningcount = 0;
            int nightcount = 0;
            temp = UserTweetMap.get(key);
            for (int i = 0; i < temp.size(); i++) {
                Date tempDate = temp.get(i).getCreatedAt();
                if (tempDate != null) {
                    calendar.setTime(tempDate);
                    if (calendar.get(Calendar.HOUR_OF_DAY) <= 6) {
                        morningcount++;
                    } else if ((calendar.get(Calendar.HOUR_OF_DAY) > 6) && (calendar.get(Calendar.HOUR_OF_DAY) <= 12)) {
                        nooncount++;
                    } else if ((calendar.get(Calendar.HOUR_OF_DAY) > 12) && (calendar.get(Calendar.HOUR_OF_DAY) <= 18)) {
                        eveningcount++;
                    } else {
                        nightcount++;
                    }
                } else {
                    continue;
                }
            }
            Morning.put(key, morningcount);
            Noon.put(key, nooncount);
            Evening.put(key, eveningcount);
            Night.put(key, nightcount);
        }
        System.out.println("Morning " + Morning);
        System.out.println("Noon " + Noon);
        System.out.println("Evening " + Evening);
        System.out.println("Night " + Night);
    }

    public void extractFeatures(int tweetNum) {

        logger.setLevel(Level.INFO);
        double lambda = 0.05;

        for (String key : OT1.keySet()) {
            double temp = 0;
            temp = (OT1.get(key) + CT1.get(key) + RT1.get(key)) / tweetNum;
            TS.put(key, temp);
        }

        for (String key : OT1.keySet()) {
            double temp = 0;
            if ((OT1.get(key) + RT1.get(key)) != 0)
                temp = OT1.get(key) / (OT1.get(key) + RT1.get(key));
            SS.put(key, temp);
        }

        for (String key : OT1.keySet()) {
            double temp = 0;
            if ((OT1.get(key) + CT1.get(key)) != 0)
                temp = (OT1.get(key) / (OT1.get(key) + CT1.get(key))) + lambda * (CT1.get(key) - CT2.get(key)) / (CT1.get(key) + 1);
            nonCS.put(key, temp);
        }

        for (String key : RT2.keySet()) {
            double temp = 0;
            if (RT3.get(key) != 0)
                temp = RT2.get(key) * (java.lang.Math.log(RT3.get(key)));
            else
                temp = 0;
            RI.put(key, temp);
        }

        for (String key : M3.keySet()) {
            double temp = 0;
            if (M1.get(key) != null && M2.get(key) != null && M3.get(key) != null && M4.get(key) != null) {
                if ((M4.get(key) != 0) && M2.get(key) != 0)
                    temp = M3.get(key) * java.lang.Math.log(M4.get(key)) - M1.get(key) * java.lang.Math.log(M2.get(key));
                else
                    temp = 0;
            }
            MI.put(key, temp);
        }

        for (String key : G3.keySet()) {
            double temp = 0;
            if (G1.get(key) != null && G2.get(key) != null && G3.get(key) != null && G4.get(key) != null)
                temp = java.lang.Math.log((G3.get(key) + 1) / (G1.get(key) + 1)) - java.lang.Math.log((G4.get(key) + 1) / (G2.get(key) + 1));
            ID1.put(key, temp);
        }

        for (String key : RT2.keySet()) {
            double temp = 0;
            if (G1.get(key) != null && G2.get(key) != null)
                temp = java.lang.Math.log(G1.get(key) + 1) - java.lang.Math.log(G2.get(key) + 1);
            NS.put(key, temp);
        }
    }
    
    // METHOD FOR PACKAGING AND RETURNING FEATURES PER USER - PAPER VERSION
    public Map<String, double[]> getFeatures() {
        
        Map<String, double[]> FeatureMap = new HashMap<String, double[]>();
        double[] tempList = null;
        
        for(String key: UserTweetMap.keySet()) {
            tempList = new double[] {TS.get(key), SS.get(key), nonCS.get(key), RI.get(key), MI.get(key), ID1.get(key),
            NS.get(key)};
//            tempList.add(TS.get(key));
//            tempList.add(SS.get(key));
//            tempList.add(nonCS.get(key));
//            tempList.add(RI.get(key));
//            tempList.add(MI.get(key));
//            tempList.add(ID1.get(key));
//            tempList.add(NS.get(key));
            FeatureMap.put(key, tempList);
        }
        return FeatureMap;
    }
    
    public Map<String, double[]> getFeaturesNew() {
        
        Map<String, double[]> FeatureMap = new HashMap<String, double[]>();
        double[] tempList = null;
        
        for(String key: UserTweetMap.keySet()) {
            tempList = new double[] {TS.get(key), SS.get(key), nonCS.get(key), RI.get(key), MI.get(key), ID1.get(key),
            NS.get(key), Freq.get(key).doubleValue(), Morning.get(key).doubleValue(), Noon.get(key).doubleValue(),
            Evening.get(key).doubleValue(), Night.get(key).doubleValue()};
            
            FeatureMap.put(key, tempList);
        }
        return FeatureMap;
    }
    
}
