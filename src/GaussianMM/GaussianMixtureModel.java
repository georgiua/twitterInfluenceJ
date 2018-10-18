package GaussianMM;

import jMEF.*;
import java.util.Map;
import java.util.Vector;

public class GaussianMixtureModel {
    
    public void GaussianMixtureModel(int NoClusters, int FeaturesNumber, Map<String, double[]> FeatureMap) {
        
        int elements = FeatureMap.entrySet().size();
        int m = 1000;
        int count = 0;
        
        // Initial mixture model
        MixtureModel mm = new MixtureModel(elements);
        mm.EF = new UnivariateGaussian();
        for (Object value : FeatureMap.values()) {
            if (count < elements) {
                double[] temp = (double[]) value;
                PVector param = new PVector(FeaturesNumber);
                param.array = temp;
                mm.param[count] = param;
                mm.weight[count] = count + 1;
                count++;
            }
        }
        
        mm.normalizeWeights();
        System.out.println("Initial mixture model \n" + mm + "\n");
        
        // Draw points from initial mixture model and compute clusters
        PVector[] points = mm.drawRandomPoints(m);
        Vector<PVector>[] clusters = KMeans.run(points, NoClusters);
        
        // Classical Expectation-Maximization (EM) algorithm
        MixtureModel mmc;
        mmc = ExpectationMaximization.initialize(clusters);
        mmc = ExpectationMaximization.run(points, mmc);
        System.out.println("Mixture model estimated using classical EM \n" + mmc + "\n");
        
        
        
    }
}