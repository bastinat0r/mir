package lugle;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.store.Directory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Created by sebastian on 4/26/17.
 */
public class Lugle {
    /**
     * Indexing all the text/plain and text/html files given in argument list (folders will be traversed)
     * @param args argument list for program execution
     * @return Lucene-Directory that can be searched
     */
    public static Directory setup(String args[]){
        String pathString = args.length > 0 ? args[0] : ".";
        Path path = Paths.get(pathString);
        DocumentFinder finder = new DocumentFinder(path);
        DirectoryBuilder directoryBuilder = new DirectoryBuilder(new EnglishAnalyzer(), finder.getDocuments());
        return directoryBuilder.create();
    }

    /**
     * Main Function
     *
     * the program will execute the setup function where the files are indexed, afterwards it will be searchable by entering queries.
     *
     * @param args command line args
     */
    public static void main(String args[]) {
        Directory index = setup(args);
        Repl repl = new Repl(new Scanner(System.in), new SearchIndex(index));
        repl.run();
    }
}
