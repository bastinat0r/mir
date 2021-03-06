package imageRetrieval;

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
 * Class used to extract ImageHandle(s) from all files with matching file-type in a given directory.
 *
 * Created by sebastian on 5/2/17.
 */
public class DocumentFinder {
    Path path;

    public DocumentFinder(Path path) {
        this.path = path;
    }

    /**
     * Get all the documents in the given path with image/* as their content-type
     *
     * @return List of all the documents in the given folder
     */
    List<ImageHandle> getDocuments() {
        DocumentVisitor documentVisitor = new DocumentVisitor();
        try {
            Files.walkFileTree(path, documentVisitor);
        } catch (IOException ex) {
            System.err.println("Could not index Files");
            ex.printStackTrace();

        }
        return documentVisitor.documentHandles;
    }

    /**
     * FileVisitor for usage with Files.walkFileTree
     * The Visit File function adds text/plain and text/html files to the list of document handles
     */
    class DocumentVisitor implements FileVisitor<Path>{
        List<ImageHandle> documentHandles;
        Tika tika;

        public DocumentVisitor() {
            documentHandles = new ArrayList<>();
            tika = new Tika();
        }

        @Override
        public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        /**
         * visitFile handler for Files.walkFileTree
         *
         * @param path current file
         * @param basicFileAttributes attr
         * @return continue ...
         * @throws IOException
         */
        @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
            try {
                // path.toFile().exists != Files.exists(path) ???
                if (Files.isDirectory(path) || !path.toFile().exists()) {
                    return FileVisitResult.CONTINUE;
                }
                String filetype = tika.detect(path.toFile());
                switch (filetype) {
                    case "text/plain":
                        break;
                    case "image/jpeg": {
                        documentHandles.add(new ImageHandle(path.toFile(), filetype));
                        System.out.println("jpg: " + path.getFileName());
                        break;
                    }
                    case "image/png": {
                        documentHandles.add(new ImageHandle(path.toFile(), filetype));
                        System.out.println("png: " + path.getFileName());
                        break;
                    }
                    case "image/bmp": {
                        documentHandles.add(new ImageHandle(path.toFile(), filetype));
                        System.out.println("png: " + path.getFileName());
                        break;
                    }
                    case "image/gif": {
                        documentHandles.add(new ImageHandle(path.toFile(), filetype));
                        System.out.println("png: " + path.getFileName());
                        break;
                    }
                    case "image/tiff": {
                        System.out.println("Not supported yet.");
                        break;
                    }
                    default: {
                        System.out.println(filetype);
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
