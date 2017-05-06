package lugle;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.ParsingReader;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * Created by sebastian on 5/5/17.
 */
public class DirectoryBuilder {
    Analyzer analyzer;
    List<DocumentHandle> documentHandles;

    /**
     * Class used to create a LUCENE index
     *
     * @param analyzer to be used
     * @param documentHandles the documentHandles created by lugle.DocumentFinder
     */
    public DirectoryBuilder(Analyzer analyzer, List<DocumentHandle> documentHandles) {
        this.analyzer = analyzer;
        this.documentHandles = documentHandles;
    }

    /**
     * creates the index
     *
     * @return Lucene Directory with the created index
     */
    public Directory create() {
        Directory index = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        try {
            IndexWriter writer = new IndexWriter(index, config);
            for (DocumentHandle documentHandle : documentHandles) {
                addDocumentToIndex(writer, documentHandle);
            }
            writer.close();
        } catch (IOException e) {
            System.err.println("Error writing index");
        }

        return index;
    }

    /**
     * writes a document to the index
     *
     * @param writer indexwriter to be used
     * @throws IOException
     */
    private void addDocumentToIndex(IndexWriter writer, DocumentHandle documentHandle) throws IOException {
        switch (documentHandle.contontType) {
            case "text/plain": {
                Reader reader = new FileReader(documentHandle.file);
                // textfield with file content will be searched
                documentHandle.document.add(new TextField("body", reader));
                break;
            }
            default: {
                String title;
                Metadata metadata = new Metadata();
                AutoDetectParser autoDetectParser = new AutoDetectParser();
                ParseContext parseContext = new ParseContext();
                Reader reader = new ParsingReader(autoDetectParser, new FileInputStream(documentHandle.file), metadata, parseContext);
                title = metadata.get(TikaCoreProperties.TITLE);
                documentHandle.document.add(new TextField("body", reader));
                if(title != null) {
                    documentHandle.document.add(new TextField("title", title, Field.Store.YES));
                }
            }
        }
        writer.addDocument(documentHandle.document);
    }
}
