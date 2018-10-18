package twitter2;

import GaussianMM.*;
import java.util.Map;

public class PostProcessingGaussian {
        
    public void PostProcessingGaussian(int NoClusters, int FeaturesNumber, Map<String, double[]> FeatureMap) {
        
        GaussianMixtureModel gm = new GaussianMixtureModel();
        gm.GaussianMixtureModel(NoClusters, FeaturesNumber, FeatureMap);
    }
    
}
