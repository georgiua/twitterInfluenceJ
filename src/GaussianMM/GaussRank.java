package GaussianMM;

import java.util.HashMap;
import java.util.Map;

public class GaussRank {

    public Map<String, Double> RankScore = new HashMap<String, Double>();

    public void GaussRank() {
    }

    public void GaussRank(Map<String, double[]> FeatureMap, int NoFeatures) {

        double TotalRank = 0.;
        double[] mean = new double[NoFeatures];
        double[] sttdev = new double[NoFeatures];
        MedianStandardDeviation msd = new MedianStandardDeviation();
        double temp = 0.;
        double[] tempArray = new double[FeatureMap.keySet().size()];
        double[] tempRank = new double[NoFeatures];
        for (int i = 0; i < NoFeatures; i++) {
            int counter = 0;
            for (String key : FeatureMap.keySet()) {
                temp = FeatureMap.get(key)[i];
                tempArray[counter] = temp;
                counter++;
            }
            mean[i] = msd.Median(tempArray);
            sttdev[i] = msd.StandardDeviation(tempArray);
        }

        for (String key : FeatureMap.keySet()) {
            for (int i = 0; i < NoFeatures; i++) {
                tempRank[i] = Rank(FeatureMap.get(key)[i], mean[i], sttdev[i]);
            }
            TotalRank = TotalRank(tempRank);
            RankScore.put(key, (double) TotalRank);
        }

        System.out.println(RankScore);
    }

    private double Rank(double feature, double mean, double sttdev) {

        double gaussrank = 0.;
        double random = 0.;
        GaussianManual gs = new GaussianManual();
        random = gs.Phi(feature, mean, sttdev);
        gaussrank = random;

        return gaussrank;
    }

    private double TotalRank(double[] gaussrank) {

        double totalgaussrank = 1.;

        for (int i = 0; i < gaussrank.length; i++) {
            totalgaussrank = totalgaussrank * gaussrank[i];
        }

        return totalgaussrank;
    }

    private double integralFunction() {

        double integral = 1.;
//        integrate();
        
        return integral;
    }

    private static double f(double x) {
        return Math.exp(-x * x / 2) / Math.sqrt(2 * Math.PI);
    }

    private static double integrate(double a, double b) {
        
        int N = 10000;                    // precision parameter
        double h = (b - a) / (N - 1);     // step size

        // 1/3 terms
        double sum = 1.0 / 3.0 * (f(a) + f(b));

        // 4/3 terms
        for (int i = 1; i < N - 1; i += 2) {
            double x = a + h * i;
            sum += 4.0 / 3.0 * f(x);
        }

        // 2/3 terms
        for (int i = 2; i < N - 1; i += 2) {
            double x = a + h * i;
            sum += 2.0 / 3.0 * f(x);
        }

        return sum * h;
    }
}
