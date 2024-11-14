package org.example;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Searcher {

    private final RomanianTextNormalizerAnalyzer analyzer;

    public Searcher() throws IOException {
        this.analyzer = new RomanianTextNormalizerAnalyzer();
    }

    public void searchDocuments(Directory index, String queryStr) throws IOException {
        // Normalize the search query using RomanianTextNormalizerAnalyzer
        String normalizedQuery = analyzer.normalizeText(queryStr);

        // Tokenize and process each term in the normalized query
        String[] queryTokens = normalizedQuery.split("\\s+");
        List<String> processedTokens = new ArrayList<>();

        for (String token : queryTokens) {
            if (!token.isEmpty()) {
                processedTokens.add(token);
            }
        }

        // Build the query with processed tokens
        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
        for (String token : processedTokens) {
            TermQuery termQuery = new TermQuery(new Term("content", token)); // Use "content" field to match the index
            queryBuilder.add(termQuery, BooleanClause.Occur.SHOULD);
        }

        // Perform the search
        try (IndexReader reader = DirectoryReader.open(index)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs docs = searcher.search(queryBuilder.build(), 5); // Retrieve only the top 5 results
            ScoreDoc[] hits = docs.scoreDocs;


            // Display file name and score for each hit, limited to top 5
            for (ScoreDoc hit : hits) {
                int docId = hit.doc;
                Document d = searcher.doc(docId);
                String filePath = d.get("path");
                float score = hit.score;

                System.out.println(new File(filePath).getName());// + " | Score: " + score);
            }
        }
    }
}



