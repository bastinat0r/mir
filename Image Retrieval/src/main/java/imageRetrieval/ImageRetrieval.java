package imageRetrieval;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

/**
 * Created by sebastian on 6/6/17.
 */
public class ImageRetrieval {
    // extract Metadata using DocumentFinder
    private static List<ImageHandle> setup(String args[]) {
        String pathString = args.length > 0 ? args[0] : "/home/sebastian/Nextcloud/Photos/2017/";
        DocumentFinder documentFinder = new DocumentFinder(Paths.get( pathString) );
        List<ImageHandle> imageHandles = documentFinder.getDocuments();
        return imageHandles;
    }

    public static void main(String args[]) {
        List<ImageHandle> imageHandles = setup(args);
        Repl repl = new Repl(new Scanner(System.in), new ImageRetrievalSimple(imageHandles));
        if(args.length > 1)
        repl.evaluator.eval(args[1]);
        if(args.length > 2)
            repl.evaluator.eval(args[2]);
        repl.run();
    }
}
