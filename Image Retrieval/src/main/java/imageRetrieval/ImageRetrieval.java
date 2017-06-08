package imageRetrieval;

import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Created by sebastian on 6/6/17.
 */
public class ImageRetrieval {
    // extract Metadata using DocumentFinder
    private static void setup(String args[]) {
        String pathString = args.length > 0 ? args[0] : "/home/sebastian/Nextcloud/Photos/2017";
        DocumentFinder documentFinder = new DocumentFinder(Paths.get( pathString) );
        documentFinder.getDocuments();
    }
    public static void main(String args[]) {
        setup(args);
        Repl repl = new Repl(new Scanner(System.in), new PrintInput());
        repl.run();
    }
}
