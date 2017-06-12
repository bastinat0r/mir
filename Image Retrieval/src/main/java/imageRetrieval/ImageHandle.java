package imageRetrieval;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Container for one Image file
 *
 * @member file : fileHandle
 * @member features : features that have been extracted or read from files
 * @member version : used to decide if previously saved features are still valid
 * Created by sebastian on 6/8/17.
 */
public class ImageHandle {
    String filetype;
    File file;
    Features features = null;
    public static int version = 3;


    /**
     * (re) compute the features for this image
     */
    public void computeFeatures() {
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        ArrayList<Double> histogramValues = getHistogram(bufferedImage);

        Matrix matrix = getGrayScaleMatrix(bufferedImage);
        SingularValueDecomposition svd = new SingularValueDecomposition(matrix);

        Features.FeaturesBuilder featuresBuilder = new Features.FeaturesBuilder(this.version);
        featuresBuilder.histogram = histogramValues;
        featuresBuilder.grayScaleSVD = new GrayScaleSVD(svd, 10); // rank 10 may be a little over the top ...
        this.features = featuresBuilder.create();
    }

    /**
     * Compute a Matrix containing Gray-Scale values from an image
     *
     * @param bufferedImage input-image
     * @return gray-scale values
     */
    public Matrix getGrayScaleMatrix(BufferedImage bufferedImage) {
        BufferedImage imageSmall = null;
        try {
            imageSmall = Thumbnails.of(bufferedImage).size(200,200).asBufferedImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedImage imageGray = new BufferedImage( imageSmall.getWidth(), imageSmall.getHeight(), BufferedImage.TYPE_BYTE_GRAY );
        Graphics g = imageGray.getGraphics();
        g.drawImage( imageSmall, 0, 0, null );
        g.dispose();
        double [][] grayScale = new double[200][200];
        for (int y = 0; y < imageGray.getHeight(); y++) {
            for (int x = 0; x < imageGray.getWidth(); x++) {
                int gray= imageGray.getRGB(x, y)& 0xFF;
                grayScale[x][y] = (double) gray / 256;
            }
        }
        return new Matrix(grayScale);
    }

    /**
     * Compute a histogram from the given image
     *
     * @param bufferedImage input image
     * @return Array List containing the histogram values
     */
    private ArrayList<Double> getHistogram(BufferedImage bufferedImage) {
        ArrayList<Double> histogramValues = new ArrayList<Double>(30);
        for (int i = 0; i < 30; i++) {
            histogramValues.add(0.00);
        }
        int pixel[];
        float[] hsv = {0,0,0};
        int featureVectorIndex;
        for (int y = 0; y < bufferedImage.getHeight(); y++) {
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                // compute bins
                pixel = bufferedImage.getRaster().getPixel(x, y , new int[3]);
                Color.RGBtoHSB(pixel[0], pixel[1], pixel[2], hsv);
                featureVectorIndex = (int) (hsv[0] * 10);
                if(featureVectorIndex >= 10)
                    featureVectorIndex = 9;
                histogramValues.set(featureVectorIndex, histogramValues.get(featureVectorIndex) + 1);
                featureVectorIndex = (int) (hsv[1] * 10 + 10);
                if(featureVectorIndex >= 20)
                    featureVectorIndex = 19;
                histogramValues.set(featureVectorIndex, histogramValues.get(featureVectorIndex) + 1);
                featureVectorIndex = (int) (hsv[2] * 10 + 20);
                if(featureVectorIndex >= 30)
                    featureVectorIndex = 29;
                histogramValues.set(featureVectorIndex, histogramValues.get(featureVectorIndex) + 1);
            }
        }
        return histogramValues;
    }


    /**
     * Compute a combined similarity measure
     *
     * @param other the image handle of the image compared to
     * @return numeric value, that is bigger for more similar images
     */
    public double similarity(ImageHandle other) {
        double d1 = this.getFeatures().matrixDistance(other.getFeatures());
        double s1 = this.getFeatures().cosine_similarity_excluding_brightness(other.getFeatures());
        return 1 / (d1  + 1) * s1;
    }


    public Features getFeatures() {
        return features;
    }

    /**
     * Display two images side-by-side and the similarity value in the title
     *
     * @param other image that compared to THIS
     */
    public void display_similarity(ImageHandle other) {
        System.out.println("Histogram-Similarity: " + getFeatures().cosine_similarity(other.getFeatures()));
        System.out.println("Histogram-Distance: " + getFeatures().euler_distance(other.getFeatures()));
        System.out.println("Histogram-Similarity (without brightness): " +
                getFeatures().cosine_similarity_excluding_brightness(other.getFeatures()));
        System.out.println("Histogram-Distance (without brightness): " +
                getFeatures().euler_distance_excluding_brightness(other.getFeatures()));
        System.out.println("Matrix-Distance: " + getFeatures().matrixDistance(other.getFeatures()));
        try {
            BufferedImage img = ImageIO.read(file);
            BufferedImage otherImg = ImageIO.read(other.file);
            Image scaledImg = img.getScaledInstance(500, -1, Image.SCALE_SMOOTH);
            Image otherScaledImg = otherImg.getScaledInstance(500, -1, Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(scaledImg);
            ImageIcon otherIcon = new ImageIcon(otherScaledImg);
            JFrame frame = new JFrame();
            frame.setLayout(new FlowLayout());
            int maxHeight = img.getHeight() / img.getWidth() > otherImg.getHeight() / otherImg.getWidth() ? 500 * img.getHeight() / img.getWidth() : 500 * otherImg.getHeight() / otherImg.getWidth();
            frame.setSize(1200, maxHeight);
            JLabel lbl = new JLabel();
            lbl.setIcon(icon);
            JLabel lbl2 = new JLabel();
            lbl.setIcon(icon);
            lbl2.setIcon(otherIcon);
            frame.setTitle("Sim = " + similarity(other));
            frame.add(lbl);
            frame.add(lbl2);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * display the given image
     */
    public void display()
    {
        try {
            BufferedImage img = ImageIO.read(file);
            Image scaledImg = img.getScaledInstance(500, -1, Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(scaledImg);
            JFrame frame = new JFrame();
            frame.setLayout(new FlowLayout());
            frame.setSize(500, 500 * img.getHeight() / img.getWidth());
            JLabel lbl = new JLabel();
            lbl.setIcon(icon);
            frame.add(lbl);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get an Image-Handle from given image-file
     * - tries to read the "image.jpg.features" file to get the features that where previously computed
     * - if the features file can not be read / contains old features, the features will be computed
     *
     * @param file image file
     * @param filetype filetype
     */
    public ImageHandle(File file, String filetype) {
        this.filetype = filetype;
        this.file = file;
        File featuresFile = new File(file.getAbsoluteFile() + ".features");
        if(featuresFile.exists() ) {
            // deserialise old features
            Features.FeaturesBuilder featuresBuilder = new Features.FeaturesBuilder(0);
            features = featuresBuilder.create(featuresFile);
        }
        if(features == null || features.version < this.version) {
            // recompute features
            System.out.println("Saving features version " + version + "  for " + file.getName());
            this.computeFeatures();

            // serialise features
            this.features.save(featuresFile);
        }
    }

}
