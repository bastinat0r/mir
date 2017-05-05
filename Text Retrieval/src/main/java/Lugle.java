import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by sebastian on 4/26/17.
 */
public class Lugle {
    public static Directory setup(String args[]){
        String pathString = args.length > 0 ? args[0] : ".";
        Path path = Paths.get(pathString);
        FileFinder finder = new FileFinder(path);
        ArrayList<File> textFiles = finder.getTextFiles();
        for(File file : textFiles) {
            try {
                System.out.println(file.getCanonicalPath().toString());
            } catch (IOException e) {
                System.err.println("Error Printing Paths");
            }
        }
        Analyzer analyzer = new EnglishAnalyzer();
        Directory index = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        try {
            IndexWriter writer = new IndexWriter(index, config);
            for (File textFile : textFiles) {
                Document document = new Document();
                Reader reader = new FileReader(textFile);
                // textfield with file content will be searched
                document.add(new TextField("body", reader));
                // string field with path will not be searched
                document.add(new StringField("path", textFile.getCanonicalPath(), Field.Store.YES));
                writer.addDocument(document);
            }
            writer.close();
        } catch (IOException e) {
            System.err.println("Error writing index");
        }

        return index;
    }

    public static void main(String args[]) {
        Directory index = setup(args);
        Repl repl = new Repl(new Scanner(System.in), new SearchIndex(index));
        repl.run();
    }
}
