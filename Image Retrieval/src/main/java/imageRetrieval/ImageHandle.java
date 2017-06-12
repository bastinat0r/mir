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
 * Created by sebastian on 6/8/17.
 */
public class ImageHandle {
    String filetype;
    File file;
    Features features = null;
    public static int version = 3;


    public void extractFeatures() {
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




    public double similarity(ImageHandle other) {
        double s1 = this.getFeatures().matrixDistance(other.getFeatures());
        double s2 = this.getFeatures().cosine_similarity(other.getFeatures());
        return s2 / s1;
    }


    public Features getFeatures() {
        return features;
    }

    public void display_similarity(ImageHandle other) {
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
            this.extractFeatures();

            // serialise features
            this.features.save(featuresFile);
        }
    }

}
