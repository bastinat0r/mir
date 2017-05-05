import org.apache.tika.Tika;
import org.apache.tika.detect.Detector;
import org.apache.tika.detect.MagicDetector;
import org.apache.tika.detect.NameDetector;
import org.apache.tika.detect.TextDetector;
import org.apache.tika.detect.TypeDetector;
import org.apache.tika.metadata.Metadata;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

/**
 * Created by sebastian on 5/2/17.
 */
public class FileFinder {
    Path path;

    public FileFinder(Path path) {
        this.path = path;
    }

    ArrayList<File> getTextFiles() {
        DocumentVisitor documentVisitor = new DocumentVisitor();
        try {
            Files.walkFileTree(path, documentVisitor);
        } catch (IOException ex) {
            System.err.println("Could not index Text-Files");

        }
        return documentVisitor.files;
    }

    class DocumentVisitor implements FileVisitor<Path>{
        ArrayList<File> files;
        Tika tika;

        public DocumentVisitor() {
            files = new ArrayList<>();
            tika = new Tika();
        }

        @Override
        public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
            try {
                if (Files.isDirectory(path) || !Files.exists(path)) {
                    return FileVisitResult.CONTINUE;
                }
                String filetype = tika.detect(path.toFile());
                System.out.println(path.toAbsolutePath() + " = " + filetype);
                // check if file is txt
                switch (filetype) {
                    case "text/plain": {
                        files.add(path.toFile());
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
