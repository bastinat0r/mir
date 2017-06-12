package imageRetrieval;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static java.lang.Math.sqrt;

/**
 * Created by sebastian on 6/10/17.
 */
public class Features {
    public List<Double> histogram;
    public int version = 0;
    public GrayScaleSVD grayScaleSVD;

    public double cosine_similarity(Features other) {
        if(histogram.size() != other.histogram.size()) {
            System.out.println("Feature Vectors didn't match in size! Something went wrong.");
            return 0;
        }
        double sum = 0;
        double other_sum = 0;
        double result = 0;
        for (int i = 0; i < histogram.size(); i++) {
            result += histogram.get(i) * other.histogram.get(i);
            sum += histogram.get(i) * histogram.get(i);
            other_sum += other.histogram.get(i) * other.histogram.get(i);
        }
        return result / (sqrt(sum) * sqrt(other_sum));
    }

    public double matrixDistance(Features other){
        return this.grayScaleSVD.getMatrix().minus(other.grayScaleSVD.getMatrix()).normF();
    }

    public Features(List<Double> histogram, int version, GrayScaleSVD grayScaleSVD) {
        this.histogram = histogram;
        this.version = version;
        this.grayScaleSVD = grayScaleSVD;
    }

    @Override
    public String toString() {
        String str = "";
        for(Double value: histogram) {
            str += value + " ";
        }
        return str;
    }

    public void save(File file) {
        XStream xStream = new XStream();
        String xml = xStream.toXML(this);

        if(! file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        try (FileWriter out = new FileWriter(file, false)){
                                                     // false to overwrite.
            out.write(xml);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class FeaturesBuilder {
        public List<Double> histogram;
        public int version = 0;
        public GrayScaleSVD grayScaleSVD;

        public FeaturesBuilder(int version) {
            this.version = version;
        }

        public Features create() {
            return new Features(histogram, version, grayScaleSVD);
        }

        public Features create(File file) {
            try {
                String xml = new String(Files.readAllBytes(file.toPath()));
                XStream xStream = new XStream();

                Features features = (Features) xStream.fromXML(xml);
                System.out.println("Read features from: " + file.getName());
                return features;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (StreamException e) {
                System.out.println("Couldn't read features: " + file.getName());
                return null;
            }
        }
    }
}
