package GaussianMM;

public class MedianStandardDeviation {

    public MedianStandardDeviation() {
    }

    public double Median(double[] numbers) {

        double mean = 0;
        double sum = 0;
        
        for (int i = 0; i < numbers.length; i++) {
            sum = sum + numbers[i];
        }
        mean = sum / numbers.length;
        
        return mean;
    }

    public double StandardDeviation(double[] numbers) {

        double mean = 0;
        double standardDeviation = 0;
        
        mean = Median(numbers);
        double sum = 0;
        double temp1 = 0;
        double temp2 = 0;
        
        for (int i = 0; i < numbers.length; i++) {
            temp1 = numbers[i] - mean;
            temp2 = temp1 * temp1;
            sum = sum + temp2;
        }
        
        standardDeviation = Math.sqrt(sum / (numbers.length - 1));

        return standardDeviation;
    }
}
