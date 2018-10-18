package twitter2;

import RetrievedLists.*;
import java.util.Map;

public class PostProcessingRetrievedLists {
    
    public void PostProcessingRetrievedLists(int NoClusters, Map<String, double[]> FeatureMap) {
        
        CMeansFuzzy cm = new CMeansFuzzy();
        cm.CMeans(NoClusters, FeatureMap);
        
        FusionLists fl = new FusionLists();
        fl.FLists(cm.Ranking);
        
    }
    
}
