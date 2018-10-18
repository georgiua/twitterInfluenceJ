package twitter2;

import java.lang.Double;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;

public class PostProcessingMain {
    
    
    public static void main(String args[]) throws SQLException {
        Map<String, double[]> featureMap = new HashMap<>();
        DBConnect dbconn = new DBConnect();
        dbconn.connect();
        Connection conn = dbconn.getConnection();
        DBReadWriter rw = new DBReadWriter();
        Map<String, String> stringMap = rw.readFeaturesByTopic("#blacklivesmatter", conn);
        for(String key: stringMap.keySet()) {
            String feature = stringMap.get(key);
            String[] values = feature.split(";");
            int size = values.length;
            Double valuesToDouble[] = new Double[size];
            for(int i=0; i<values.length;i++)
                valuesToDouble[i] = (Double.parseDouble(values[i]));
            for(int j=0; j<valuesToDouble.length;j++)  {
                if(Double.isNaN(valuesToDouble[j]))
                    valuesToDouble[j] = 0.0;
                else if(Double.isInfinite(valuesToDouble[j]))
                    valuesToDouble[j] = Double.MIN_VALUE;
            }
            featureMap.put(key, ArrayUtils.toPrimitive(valuesToDouble));
        }
        System.out.println("Starting Gaussian ============================\n\n");
        PostProcessingGaussian pg = new PostProcessingGaussian();
        pg.PostProcessingGaussian(2, 7, featureMap);
        System.out.println("Starting Retrieved Lists ============================\n\n");
        PostProcessingRetrievedLists rl = new PostProcessingRetrievedLists();
        rl.PostProcessingRetrievedLists(3, featureMap);
        
    }
    
}
