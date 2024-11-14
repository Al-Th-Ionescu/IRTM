package org.example;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.tartarus.snowball.ext.RomanianStemmer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class RomanianTextNormalizerAnalyzer extends Analyzer {
    private final CharArraySet stopWords;

    public RomanianTextNormalizerAnalyzer() throws IOException {
        this.stopWords = loadStopWords("src/main/java/org/example/stopwords-ro.txt");
    }

    // Text normalization that removes diacritics, punctuation, converts to lowercase, and applies recursive stemming.
    public String normalizeText(String text) throws IOException {
        text = removePunctuation(text).toLowerCase();
        text = removeDiacritics(text);

        StringBuilder normalizedContent = new StringBuilder();
        try (TokenStream tokenStream = tokenStream("content", text)) {
            CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                String originalToken = attr.toString();
                String stemmedToken = recursiveStem(originalToken);
                normalizedContent.append(stemmedToken).append(" ");
            }
            tokenStream.end();
        }

        return normalizedContent.toString().trim();
    }

    // Recursive stemming helper method
    private String recursiveStem(String word) {
        RomanianStemmer stemmer = new RomanianStemmer();
        String previousStem = word;
        String currentStem;

        do {
            stemmer.setCurrent(previousStem);
            stemmer.stem();
            currentStem = stemmer.getCurrent();
            if (currentStem.equals(previousStem)) {
                break;
            }
            previousStem = currentStem;
        } while (true);

        return currentStem;
    }

    // Removes diacritics from Romanian text
    private static String removeDiacritics(String text) {
        String normalizedText = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern diacriticPattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return diacriticPattern.matcher(normalizedText).replaceAll("");
    }

    // Removes punctuation from text
    private static String removePunctuation(String text) {
        return text.replaceAll("\\p{Punct}", " ");
    }

    // Loads stop words from a file, removing diacritics
    public static CharArraySet loadStopWords(String filePath) throws IOException {
        Set<String> stopWordsSet = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stopWordsSet.add(removeDiacritics(line.trim().toLowerCase()));
            }
        }
        return new CharArraySet(stopWordsSet, true);
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        WhitespaceTokenizer tokenizer = new WhitespaceTokenizer();

        // Chain filters: Remove stop words, convert to lowercase, remove diacritics, remove punctuation, apply stemming, and recursive stemming.
        TokenStream filter = new StopFilter(tokenizer, this.stopWords);
        filter = new LowerCaseFilter(filter); // Convert to lowercase
        filter = new DiacriticRemovalFilter(filter); // Remove diacritics
        filter = new PunctuationFilter(filter); // Remove punctuation
        filter = new SnowballFilter(filter, new RomanianStemmer()); // Apply initial stemming
        filter = new RecursiveStemmingFilter(filter); // Apply recursive stemming

        return new TokenStreamComponents(tokenizer, filter);
    }

    // Custom filter to remove diacritics
    private static class DiacriticRemovalFilter extends TokenFilter {
        private final CharTermAttribute termAttr = addAttribute(CharTermAttribute.class);

        protected DiacriticRemovalFilter(TokenStream input) {
            super(input);
        }

        @Override
        public final boolean incrementToken() throws IOException {
            if (input.incrementToken()) {
                String text = removeDiacritics(termAttr.toString());
                termAttr.setEmpty().append(text);
                return true;
            }
            return false;
        }

        private static String removeDiacritics(String text) {
            String normalizedText = Normalizer.normalize(text, Normalizer.Form.NFD);
            return normalizedText.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        }
    }

    // Custom filter to remove punctuation
    private static class PunctuationFilter extends TokenFilter {
        private final CharTermAttribute termAttr = addAttribute(CharTermAttribute.class);

        protected PunctuationFilter(TokenStream input) {
            super(input);
        }

        @Override
        public final boolean incrementToken() throws IOException {
            if (input.incrementToken()) {
                String text = termAttr.toString().replaceAll("\\p{Punct}", "");
                termAttr.setEmpty().append(text);
                return true;
            }
            return false;
        }
    }

    // Custom filter to apply recursive stemming
    private static class RecursiveStemmingFilter extends TokenFilter {
        private final CharTermAttribute termAttr = addAttribute(CharTermAttribute.class);
        private final RomanianStemmer stemmer = new RomanianStemmer();

        protected RecursiveStemmingFilter(TokenStream input) {
            super(input);
        }

        @Override
        public final boolean incrementToken() throws IOException {
            if (input.incrementToken()) {
                String term = termAttr.toString();
                termAttr.setEmpty().append(applyRecursiveStemming(term));
                return true;
            }
            return false;
        }

        // Recursive stemming logic
        private String applyRecursiveStemming(String word) {
            String previousStem = word;
            String currentStem;

            do {
                stemmer.setCurrent(previousStem);
                stemmer.stem();
                currentStem = stemmer.getCurrent();
                if (currentStem.equals(previousStem)) {
                    break;
                }
                previousStem = currentStem;
            } while (true);

            return currentStem;
        }
    }
}

