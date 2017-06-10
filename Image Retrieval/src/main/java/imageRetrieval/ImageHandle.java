package imageRetrieval;

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


    public Features extractFeatures() {
        if(features != null) {
            return features;
        }
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        ArrayList<Double> featureValues = new ArrayList<Double>(30);
        for (int i = 0; i < 30; i++) {
            featureValues.add(0.00);
        }
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                // compute bins
                Color color = new Color(bufferedImage.getRGB(x, y));
                float[] hsv = {0,0,0};
                int featureVectorIndex;
                Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
                featureVectorIndex = (int) (hsv[0] * 10);
                if(featureVectorIndex >= 10)
                    featureVectorIndex = 9;
                featureValues.set(featureVectorIndex, featureValues.get(featureVectorIndex) + 1);
                featureVectorIndex = (int) (hsv[1] * 10 + 10);
                if(featureVectorIndex >= 20)
                    featureVectorIndex = 19;
                featureValues.set(featureVectorIndex, featureValues.get(featureVectorIndex) + 1);
                featureVectorIndex = (int) (hsv[2] * 10 + 20);
                if(featureVectorIndex >= 30)
                    featureVectorIndex = 29;
                featureValues.set(featureVectorIndex, featureValues.get(featureVectorIndex) + 1);
            }
        }

        this.features = new Features(featureValues);
        return new Features(featureValues);
    }

    public double similarity(ImageHandle other) {
        return this.extractFeatures().cosine_similarity(other.extractFeatures());
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
    }

}
