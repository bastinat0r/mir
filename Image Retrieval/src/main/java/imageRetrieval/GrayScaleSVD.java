package imageRetrieval;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by sebastian on 6/11/17.
 */
public class GrayScaleSVD {
    Matrix Uk, Sk, Vtk;

    public GrayScaleSVD(Matrix u, Matrix s, Matrix vt, int rank) {
        Uk = matrixSubset(u, 200, rank);
        Sk = matrixSubset(s, rank, rank);
        Vtk = matrixSubset(vt, rank, 200);
    }

    public GrayScaleSVD(SingularValueDecomposition svd, int rank) {
        Uk = matrixSubset(svd.getU(), 200, rank);
        Sk = matrixSubset(svd.getS(), rank, rank);
        Vtk = matrixSubset(svd.getV().transpose(), rank, 200);
    }

    public GrayScaleSVD(GrayScaleSVD other, int rank) {
        new GrayScaleSVD(other.Uk, other.Sk, other.Vtk, rank);
    }

    public Matrix getMatrix() {
        return Uk.times(Sk).times(Vtk);
    }

    /* This is the method to get only a specified number of
    * rows/columns from a matrix */
    private Matrix matrixSubset(Matrix orig, int m, int k) {
        Matrix newMatrix = new Matrix(m, k);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < k; j++) {
                newMatrix.set(i, j, orig.get(i, j));
            }
        }
        return newMatrix;
    }

    public static void displayMatrix( Matrix m)
    {
        BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 200; x++) {
            for (int y = 0; y < 200; y++) {
                float a = (float) m.get(x, y);
                if(a < 0)
                    a = 0;
                if(a > 1.0)
                    a = 1.0f;

                Color newColor = new Color(a,a,a);
                image.setRGB(x,y,newColor.getRGB());

            }
        }
        ImageIcon icon = new ImageIcon(image);
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(200, 200);
        JLabel lbl = new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }


}
