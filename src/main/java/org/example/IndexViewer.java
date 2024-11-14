package org.example;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.nio.file.Paths;

public class IndexViewer {

    public static void viewIndex(String indexPath) throws IOException {
        Directory index = FSDirectory.open(Paths.get(indexPath));
        try (IndexReader reader = DirectoryReader.open(index)) {
            System.out.println("Total number of live documents in the index: " + reader.numDocs());

            for (int i = 0; i < reader.maxDoc(); i++) {
                Document doc = reader.document(i);
                if (doc != null) { // Only access documents that are live
                    System.out.println("Document " + i + ":");
                    doc.getFields().forEach(field ->
                            System.out.println("  Field \"" + field.name() + "\": " + doc.get(field.name()))
                    );

                    // If you want to see term frequencies for each document
                    Terms terms = reader.getTermVector(i, "contents");
                    if (terms != null) {
                        System.out.println("  Terms in 'contents' field:");
                        TermsEnum termsEnum = terms.iterator();
                        BytesRef term;
                        while ((term = termsEnum.next()) != null) {
                            System.out.println("    Term: " + term.utf8ToString() + ", Frequency: " + termsEnum.totalTermFreq());
                        }
                    }
                    System.out.println("------------------------------------------------");
                }
            }
        }
    }
}