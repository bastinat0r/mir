import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;

import java.io.File;
import java.io.IOException;

/**
 * Created by sebastian on 5/5/17.
 */
public class DocumentHandle {
    File file;
    String contontType;
    Document document;

    public DocumentHandle(File file, String contontType) {
        this.file = file;
        this.contontType = contontType;
        document = new Document();
        try {
            document.add(new StringField("path", this.file.getCanonicalPath(), Field.Store.YES));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
