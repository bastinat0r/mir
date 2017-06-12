package imageRetrieval;

import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by sebastian on 6/10/17.
 */
public class ImageRetrievalSimple implements EvalInterface {
    List<ImageHandle> imageHandles;

    public ImageRetrievalSimple(List<ImageHandle> imageHandles) {
        this.imageHandles = imageHandles;
    }

    @Override
    public boolean eval(String input) {
        File file = new File(input);
        if(! file.exists()) {
            System.out.println("Please enter an image file here");
            return true;
        }
        ImageHandle imageHandle = null;
        try {
            Tika tika = new Tika();
            String filetype = tika.detect(file);
            // check if file is txt/html/...
            switch (filetype) {
                case "image/jpeg": {
                    imageHandle = new ImageHandle(file, filetype);
                    break;
                }
                default: {
                    System.out.println(filetype + " is not supported yet.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(imageHandle == null) {
            System.out.println("Please enter an image file here");
            return true;
        }

        imageHandles.sort(new ImageComperator(imageHandle));
        for (int i = 0; i < 5; i++) {
            ImageHandle imageHandle1 = imageHandles.get(i);
            imageHandle1.display_similarity(imageHandle);
            System.out.println(i + ": " + imageHandle.file.getAbsolutePath());
            System.out.println("Histogram-Similarity: " + imageHandle1.getFeatures().cosine_similarity(imageHandle.getFeatures()));
            System.out.println("Matrix-Distance: " + imageHandle1.getFeatures().matrixDistance(imageHandle.getFeatures()));
        }


        return true;
    }

}
