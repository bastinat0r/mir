package lugle;

import org.apache.tika.Tika;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sebastian on 5/2/17.
 */
public class DocumentFinder {
    Path path;

    public DocumentFinder(Path path) {
        this.path = path;
    }

    /**
     * Get all the documents in the given path with either text/plain or text/html as their content-type
     * @return List of all the documents in the given folder
     */
    List<DocumentHandle> getDocuments() {
        DocumentVisitor documentVisitor = new DocumentVisitor();
        try {
            Files.walkFileTree(path, documentVisitor);
        } catch (IOException ex) {
            System.err.println("Could not index Text-Files");
            ex.printStackTrace();

        }
        return documentVisitor.documentHandles;
    }

    /**
     * FileVisitor for usage with Files.walkFileTree
     * The Visit File function adds text/plain and text/html files to the list of document handles
     */
    class DocumentVisitor implements FileVisitor<Path>{
        List<DocumentHandle> documentHandles;
        Tika tika;

        public DocumentVisitor() {
            documentHandles = new ArrayList<>();
            tika = new Tika();
        }

        @Override
        public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
            try {
                // path.toFile().exists != Files.exists(path) ???
                if (Files.isDirectory(path) || !path.toFile().exists()) {
                    return FileVisitResult.CONTINUE;
                }
                String filetype = tika.detect(path.toFile());
                // check if file is txt
                switch (filetype) {
                    case "text/plain": {
                        documentHandles.add(new DocumentHandle(path.toFile(), filetype));
                        break;
                    }
                    case "text/html": {
                        documentHandles.add(new DocumentHandle(path.toFile(), filetype));
                        break;
                    }
                    case "application/pdf": {
                        documentHandles.add(new DocumentHandle(path.toFile(), filetype));
                        break;
                    }
                }
            } catch (java.lang.NoSuchMethodError e) {
                e.printStackTrace();
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path path, IOException e) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path path, IOException e) throws IOException {
            return FileVisitResult.CONTINUE;
        }
    }


}
