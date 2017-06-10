package imageRetrieval;

import java.util.List;

import static java.lang.Math.sqrt;

/**
 * Created by sebastian on 6/10/17.
 */
public class Features {
    public List<Double> values;

    public double cosine_similarity(Features other) {
        if(values.size() != other.values.size()) {
            System.out.println("Feature Vectors didn't mactch in size! Something went wrong.");
            return 0;
        }
        double sum = 0;
        double other_sum = 0;
        double result = 0;
        for (int i = 0; i < values.size(); i++) {
            result += values.get(i) * other.values.get(i);
            sum += values.get(i) * values.get(i);
            other_sum += other.values.get(i) * other.values.get(i);
        }
        return result / (sqrt(sum) * sqrt(other_sum));
    }

    public Features(List<Double> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        String str = "";
        for(Double value: values) {
            str += value + " ";
        }
        return str;
    }
}
