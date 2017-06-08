package imageRetrieval;

import java.io.File;

/**
 * Created by sebastian on 6/8/17.
 */
public class ImageHandle {
    String filetype;
    File file;

    public ImageHandle(File file, String filetype) {
        this.filetype = filetype;
        this.file = file;
    }
}
