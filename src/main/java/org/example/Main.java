package org.example;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        // Check for valid arguments
        if (args.length < 2 || (!"-index".equals(args[0]) && !"-search".equals(args[0]))) {
            System.out.println("Usage for indexing: java -jar target/docsearch-1.0-SNAPSHOT.jar -index -directory <path to docs>");
            System.out.println("Usage for searching: java -jar target/docsearch-1.0-SNAPSHOT.jar -search -query <keyword>");
            return;
        }

        String mode = args[0];
        Directory index = FSDirectory.open(Paths.get("indexDirectory")); // Default index directory

        if ("-index".equals(mode)) {
            if (args.length < 3 || !"-directory".equals(args[1])) {
                System.out.println("Error: Missing -directory argument for indexing mode.");
                System.out.println("Usage: java -jar target/docsearch-1.0-SNAPSHOT.jar -index -directory <path to docs>");
                return;
            }
            String directoryPath = args[2];
            File folder = new File(directoryPath);

            if (!folder.exists() || !folder.isDirectory()) {
                System.out.println("The provided path is not a valid directory: " + directoryPath);
                return;
            }

            // Initialize the analyzer and index the documents
            Analyzer analyzer = new RomanianTextNormalizerAnalyzer();
            Indexer indexer = new Indexer();
            indexer.indexDocuments(index, analyzer, folder);
            System.out.println("Indexing completed.");

        } else if ("-search".equals(mode)) {
            if (args.length < 3 || !"-query".equals(args[1])) {
                System.out.println("Error: Missing -query argument for search mode.");
                System.out.println("Usage: java -jar target/docsearch-1.0-SNAPSHOT.jar -search -query <keyword>");
                return;
            }
            String queryStr = args[2];

            // Initialize the analyzer and search the index
            Searcher searcher = new Searcher();
            searcher.searchDocuments(index, queryStr);
        }
    }
}


/*

package org.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String indexPath = "indexDirectory"; //Directory"; // Replace with your actual index directory path

        try {
            System.out.println("Opening index at: " + indexPath);
            IndexViewer.viewIndex(indexPath);
        } catch (IOException e) {
            System.err.println("Error reading the index: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

 */