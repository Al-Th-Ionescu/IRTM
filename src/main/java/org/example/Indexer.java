
package org.example;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class Indexer {

    public void indexDocuments(Directory index, Analyzer analyzer, File folder) throws IOException {
        // Configure IndexWriter with the provided analyzer
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(OpenMode.CREATE);  // Create a new index each time

        try (IndexWriter writer = new IndexWriter(index, config)) {
            for (File file : Objects.requireNonNull(folder.listFiles())) {
                if (file.isFile()) {
                    String content = readFileContent(file);
                    //System.out.println(content);
                    if (!content.isEmpty()) {
                        // Add document to index with analyzer applied to content
                        addDocument(writer, file.getPath(), content);
                       // System.out.println("Indexing (" + file.getName() + ") completed with analyzer applied!");
                    }
                }
            }
           // System.out.println("Indexing of all documents in the directory completed.");
        }
    }


    private String readFileContent(File file) throws IOException {
        if (file.getName().endsWith(".docx")) {
            try (FileInputStream fis = new FileInputStream(file);
                 XWPFDocument docx = new XWPFDocument(fis)) {
                StringBuilder sb = new StringBuilder();
                for (XWPFParagraph para : docx.getParagraphs()) {
                    sb.append(para.getText()).append("\n");
                }
                return sb.toString();
            }
        } else if (file.getName().endsWith(".pdf")) {
            try (PDDocument pdf = PDDocument.load(file)) {
                return new PDFTextStripper().getText(pdf);
            }
        } else if (file.getName().endsWith(".txt")) {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }
            return sb.toString();
        }
        return ""; // Handle other file types if needed
    }

    private void addDocument(IndexWriter writer, String path, String contents) throws IOException {
        Document doc = new Document();
        doc.add(new StringField("path", path, Field.Store.YES));
        doc.add(new TextField("content", contents, Field.Store.YES)); // Store raw content as-is
        writer.addDocument(doc);
    }
}
