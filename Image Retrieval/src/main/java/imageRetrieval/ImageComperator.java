package imageRetrieval;

import java.util.Comparator;

/**
 * Created by sebastian on 6/12/17.
 */
class ImageComperator implements Comparator<ImageHandle> {
    ImageHandle reference;

    public ImageComperator(ImageHandle reference) {
        this.reference = reference;
    }

    @Override
    public int compare(ImageHandle imageHandle, ImageHandle t1) {
        if (reference.similarity(imageHandle) < reference.similarity(t1))
            return 1;
        else
            return -1;
    }
}
