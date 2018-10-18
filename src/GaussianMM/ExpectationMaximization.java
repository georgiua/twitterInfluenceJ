package GaussianMM;

import jMEF.*;
import java.util.Vector;

public class ExpectationMaximization {
    
    private static int MAX_ITERATIONS = 30;
    
    // Initializes a mixture model from clusters of points
    // The parameters estimated corresponds to univariate Gaussian distributions
    public static MixtureModel initialize(Vector<PVector>[] clusters) {
        
        MixtureModel mm = new MixtureModel(clusters.length);
        mm.EF = new UnivariateGaussian();
        
        // Amount of points
        int nb = 0;
        for (int i = 0; i < clusters.length; i++) {
            nb += clusters[i].size();
        }
        
        // Loop on the clusters
        for (int i = 0; i < clusters.length; i++) {
            // Weight
            mm.weight[i] = ((double) clusters[i].size()) / nb;
            
            // Mean
            double mean = 0;
            for (int j = 0; j < clusters[i].size(); j++) {
                mean += clusters[i].get(j).array[0];
            }
            mean /= clusters[i].size();
            
            // Variance
            double var = 0;
            for (int j = 0; j < clusters[i].size(); j++) {
                var += (clusters[i].get(j).array[0] - mean) * (clusters[i].get(j).array[0] - mean);
            }
            var /= clusters[i].size();
            
            // Parameters
            PVector param = new PVector(2);
            param.array[0] = mean;
            param.array[1] = var;
            mm.param[i] = param;
        }
        
        return mm;
    }
    
    // Performs the Expectation-Maximization algorithm
    public static MixtureModel run(PVector[] points, MixtureModel f) {
        
        MixtureModel fout = f.clone();
        
        // Variables
        int k = fout.size;
        int n = points.length;
        int row, col;
        int iterations = 0;
        double[][] p = new double[n][k];
        
        // Initial log likelihood
        double logLikelihoodNew = logLikelihood(points, fout);
        double logLikelihoodThreshold = Math.abs(logLikelihoodNew) * 0.01;
        double logLikelihoodOld;
        
        //System.out.printf("%2d : %12.6f\n", iterations, logLikelihoodNew);
        
        do {
            logLikelihoodOld = logLikelihoodNew;
            
            // E-step: computation of matrix P (fast version, we don't compute 1/f(x) for all P[i][j]) 
            for (row = 0; row < n; row++) {
                double sum = 0;
                for (col = 0; col < k; col++) {
                    double tmp = fout.weight[col] * fout.EF.density(points[row], (PVector) fout.param[col]);
                    p[row][col] = tmp;
                    sum += tmp;
                }
                for (col = 0; col < k; col++) {
                    p[row][col] /= sum;
                }
            }
            
            // M-step: computation of new Gaussians and the new weights
            for (col = 0; col < k; col++) {
                double sum = 0;
                double mu = 0;
                double sigma = 0;
                
                // First step of the computation of new mu
                for (row = 0; row < n; row++) {
                    double w = p[row][col];
                    sum += w;
                    mu += points[row].array[0] * w;
                }
                mu /= sum;
                
                // Computation of new sigma
                for (row = 0; row < n; row++) {
                    double diff = points[row].array[0] - mu;
                    sigma += p[row][col] * diff * diff;
                }
                sigma /= sum;
                
                // Set new mu and sigma to the PVectorMatrix
                PVector param = new PVector(2);
                param.array[0] = mu;
                param.array[1] = sigma;
                fout.param[col] = param;
                fout.weight[col] = sum / n;
            }
            
            // Update of iterations and log likelihood value
            iterations++;
            logLikelihoodNew = logLikelihood(points, fout);
            
            //System.out.printf("%2d : %12.6f\n", iterations, logLikelihoodNew);
        } while (Math.abs(logLikelihoodNew - logLikelihoodOld) > logLikelihoodThreshold && iterations < MAX_ITERATIONS);
        
        return fout;
    }
    
    // Computes the log likelihood
    private static double logLikelihood(PVector[] points, MixtureModel f) {
        double value = 0;
        for (int i = 0; i < points.length; i++) {
            value += Math.log(f.density(points[i]));
        }
        return value;
    }
    
}
