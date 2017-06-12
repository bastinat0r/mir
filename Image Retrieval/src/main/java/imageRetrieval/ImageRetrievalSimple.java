package imageRetrieval;

import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Handles the input and computes the most similar images
 *
 * Created by sebastian on 6/10/17.
 */
public class ImageRetrievalSimple implements EvalInterface {
    /**
     * List of imageHandles that will be used to find the best fitting images
     */
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
            switch (filetype) {
                case "image/jpeg": {
                    imageHandle = new ImageHandle(file, filetype);
                    break;
                }
                case "image/png": {
                    imageHandle = new ImageHandle(file, filetype);
                    break;
                }
                case "image/bmp": {
                    imageHandle = new ImageHandle(file, filetype);
                    break;
                }
                case "image/gif": {
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
        try {
            for (int i = 0; i < 5; i++) {
                ImageHandle imageHandle1 = imageHandles.get(i);
                System.out.println(i + ": " + imageHandle1.file.getAbsolutePath());
                imageHandle1.display_similarity(imageHandle);
            }
            ImageHandle imageHandle1 = imageHandles.get(imageHandles.size() - 1);
            System.out.println("worst image: " + imageHandle1.file.getAbsolutePath());
            imageHandle1.display_similarity(imageHandle);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Index is to small, all images have been displayed");
        }

        return true;
    }

}
