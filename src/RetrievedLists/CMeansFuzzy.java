package RetrievedLists;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import jminhep.cluster.*;

public class CMeansFuzzy {
    
    public Map<String, double[]> Ranking = new HashMap<>();
    
    public CMeansFuzzy() {
    }
    
    public void CMeans(int clusters, Map<String, double[]> FeatureMap) {
        
//        Map<Integer, Map<String, double[]>> clusterResult = new HashMap<>();
        Vector<String> temp = new Vector<>();
        
        DataHolder data = new DataHolder("Twitter");
        for (Map.Entry entry1 : FeatureMap.entrySet()) {
//            System.out.println(entry1.getKey() + " " + entry1.getValue());
            String temp1 = (String) entry1.getKey();
            temp.add(temp1);
            data.add(new DataPoint((double[]) entry1.getValue()));
        }
        
        System.out.println("Number of Features = " + data.getDimention());
        System.out.println("Number of Users = " + data.getSize());
        
        Partition pat = new Partition(data);
        
        // set No clusters, precision, fuzziness (dummy if non-applicable),
        // max number of iterations
        pat.set(clusters, 0.001, 1.7, 1000);

        // probability for membership (only for Fuzzy algorithm)
        pat.setProbab(0.68);

        // define types of cluster analysis
        int[] mode = new int[clusters-1];
//        mode[0] = 111;
//        mode[1] = 112;
//        mode[2] = 113;
//        mode[3] = 114;
//        mode[4] = 121;
//        mode[5] = 122;
        mode[0] = 131;
        mode[1] = 132;
        
        // run over all clustering modes 
        for (int i = 0; i < mode.length; i++) {
            pat.run(mode[i]);
            System.out.println("\nalgorithm: " + pat.getName());
            System.out.println("Compactness: " + pat.getCompactness());
            System.out.println("No of final clusters: " + pat.getNclusters());
            DataHolder Centers = pat.getCenters();
            System.out.println("Positions of centers: ");
            Centers.print();
            
            // show cluster association
            for (int m = 0; m < data.getSize(); m++) {
                DataPoint dp = data.getRaw(m);
                int k = dp.getClusterNumber();
                System.out.println("User = " + m + " in cluster = " + k);
            }
        }
        
        System.out.println(data.getArrayList());
        
        DataHolder centers = new DataHolder();
        centers = pat.getCenters();
        
        for (int k = 0; k < data.getSize(); k++) {
            DataPoint dp = data.getRaw(k);
            double distance = 0;
            double[] Ranks = new double[clusters];
            
            for(int l = 0; l < clusters; l++) {
                if(dp.getClusterNumber() == l) {
                    distance = Distance(data.getElement(k), centers.getElement(l));
                }
                else
                    distance = 0.;
                Ranks[l] = distance;
            }
            Ranking.put(temp.get(k), Ranks);
        }
        
//        for (String key : Ranking.keySet()) {
//            System.out.println(key + "\n");
//            for (int l = 0; l < Ranking.get(key).length; l++)
//                System.out.printf("\n%f\n", Ranking.get(key)[l]);
//        }
        
//        return Ranking;
    }
    
    private double Distance(double[] element, double[] center) {
        
        double tempSum = 0;
        for (int i = 0; i < element.length; i++) {
            tempSum = tempSum + element[i]*element[i] - center[i]*center[i];
        }
        
        return Math.sqrt(Math.abs(tempSum));
    }
    
}
