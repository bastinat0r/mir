import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

import java.io.IOException;

/**
 * Created by sebastian on 5/2/17.
 */
public class SearchIndex implements EvalInterface{

    Directory index;

    public SearchIndex(Directory index) {
        this.index = index;
    }

    /**
     * evaluates the input and prints the result
     * @param input query given by the user
     * @return true if the repl should continue after this evaluation phase
     */
    @Override
    public boolean eval(String input) {
        System.out.println("\nSearching for \"" + input + "\"");
        try {
            IndexReader indexReader = DirectoryReader.open(index);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            Query query = new QueryParser("body", new EnglishAnalyzer()).parse(input);

            TopDocs docs = indexSearcher.search(query, 10);
            System.out.println("Found " + docs.totalHits + " documents.");
            for(ScoreDoc scoreDoc : docs.scoreDocs) {
                Document document = indexSearcher.doc(scoreDoc.doc);
                System.out.println(document.get("path"));
                if(document.get("title") != null) {
                    System.out.println(document.get("title"));
                }
            }
            indexReader.close();
        } catch (IOException e) {
            System.err.println("Error reading index");
            e.printStackTrace();
        } catch (ParseException e) {
            System.err.println("Error parsing query");
            e.printStackTrace();
        }
        return true;
    }
}
