package RetrievedLists;

import java.util.HashMap;
import java.util.Map;

public class FusionLists {
    
    public FusionLists() {
    }
    
    public void FLists(Map<String, double[]> Ranking) {
        
        RankAggregation<String> rag = new RankAggregation<String>();
        Map<String, Map<String, Double>> scores = new HashMap<String, Map<String, Double>>();
        
        for (String key : Ranking.keySet()) {
            scores.put(key, new HashMap<String, Double>());
            String temp = null;
            for(int i = 0; i < Ranking.get(key).length; i++) {
                temp = "Metric ".concat(String.valueOf(i));
                scores.get(key).put(temp, Ranking.get(key)[i]);
            }
        }
        System.out.println("scores " + scores);
        
//        Map<String, Double>[] aux = rag.processMap(scores);

//        Map<String, Double> resultSUM = rag.combSUM(scores);
//        System.out.println("resultSUM " + resultSUM);
//        for (String key : resultSUM.keySet()) {
//            System.out.println("resultSUM " + key + " -> " + resultSUM.get(key));
//        }
        
//        Map<String, Double> resultMNZ = rag.combMNZ(scores);
//        System.out.println("resultMNZ " + resultMNZ);
//        for (String key : resultMNZ.keySet()) {
//            System.out.println("resultMNZ " + key + " -> " + resultMNZ.get(key));
//        }
        
        Map<String, Double> resultRR = rag.reciprocalRankFusion(scores);
        System.out.println("resultRR " + resultRR);
//        for (String key : resultRR.keySet()) {
//            System.out.println("resultRR " + key + " -> " + resultRR.get(key));
//        }
        
//        Map<String, Double> resultBorda = rag.bordaFusion(scores);
//        System.out.println("resultBorda " + resultBorda);
//        for (String key : resultBorda.keySet()) {
//            System.out.println("resultBorda " + key + " -> " + resultBorda.get(key));
//        }
    }
    
}
