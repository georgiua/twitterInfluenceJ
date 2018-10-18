/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter2;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author alex
 */
public class TwitterAccess {
    
    private static final String ACCESS_TOKEN = "837544873-fOwhCf9tLfpsq8Gottts9OBgAyKYoW0u3xrLFaFu";
    private static final String ACCESS_TOKEN_SECRET = "3ZwDv34qV3CPIjwd8AxmfkNs0veBfpMcgTHn7kQXkZk";
    private static final String CONSUMER_KEY = "IcVVucKw1bDA9wwPw40J0g";
    private static final String CONSUMER_SECRET = "USqA9KdP0Z2lt4AuT65KLMwL67Q37y3svQjDL1B7OM";
    Twitter twitter;
   
    public TwitterAccess() {

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthAccessToken(ACCESS_TOKEN);
        builder.setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
        builder.setOAuthConsumerKey(CONSUMER_KEY);
        builder.setOAuthConsumerSecret(CONSUMER_SECRET);
        builder.setJSONStoreEnabled(true);
        //OAuthAuthorization auth = new OAuthAuthorization(builder.build());
        TwitterFactory tf = new TwitterFactory(builder.build());
        twitter = tf.getInstance();
    }
    
    public Twitter getTwitter() {
        return twitter;
    }
}
