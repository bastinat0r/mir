package imageRetrieval;

import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
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

        imageHandles.sort(new imageComperator(imageHandle));
        imageHandle.display_similarity(imageHandles.get(0));
        imageHandle.display_similarity(imageHandles.get(1));
        imageHandle.display_similarity(imageHandles.get(2));
        imageHandle.display_similarity(imageHandles.get(imageHandles.size() - 3));
        imageHandle.display_similarity(imageHandles.get(imageHandles.size() - 2));
        imageHandle.display_similarity(imageHandles.get(imageHandles.size() - 1));


        return true;
    }
    private class imageComperator implements Comparator<ImageHandle> {
        ImageHandle reference;

        public imageComperator(ImageHandle reference) {
            this.reference = reference;
        }

        @Override
        public int compare(ImageHandle imageHandle, ImageHandle t1) {
            if (reference.similarity(imageHandle) > reference.similarity(t1))
                return 1;
            else
                return -1;
        }
    }
}
