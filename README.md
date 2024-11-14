
# Document Search and Indexing System

This project is a document indexing and search system developed using Apache Lucene, which is designed to support efficient searching across a collection of documents. The system leverages a custom analyzer for Romanian language support, enabling accurate text normalization and stemming of Romanian content.

## Features

- **Document Indexing**: Supports indexing of `.txt`, `.pdf`, and `.docx` files.
- **Romanian Language Support**: Uses a custom `RomanianTextNormalizerAnalyzer` to handle Romanian-specific text normalization, including diacritic removal and recursive stemming.
- **Text Search**: Allows for keyword-based search across indexed documents, returning relevant results based on a Boolean query search.
- **Efficient Search**: Limits results to top 5 most relevant documents for each search query.

## Project Structure

The project consists of the following main components:

- **`Indexer`**: Handles the indexing of documents in a specified directory.
- **`Searcher`**: Supports keyword-based search queries on the indexed documents.
- **`RomanianTextNormalizerAnalyzer`**: A custom analyzer that normalizes Romanian text and removes diacritics, punctuation, and applies recursive stemming for improved search accuracy.

## Installation

### Prerequisites

- Java (JDK 21.0.1)
- Apache Maven
- Apache Lucene
- Apache PDFBox
- Apache POI

### Setup
 **Build the project**:
   Compile and package the project using Maven:
   ```bash
   mvn clean package
   ```

## Usage

The application provides two main functionalities: indexing and searching. Run the application with the following command structure:

```bash
java -jar target/docsearch-1.0-SNAPSHOT.jar <mode> <options>
```

### Indexing Documents

To index documents, use the `-index` mode along with the directory containing documents:

```bash
java -jar target/docsearch-1.0-SNAPSHOT.jar -index -directory <path-to-documents>
```

Example:
```bash
java -jar target/docsearch-1.0-SNAPSHOT.jar -index -directory /path/to/docs
```

### Searching Documents

To search within indexed documents, use the `-search` mode with a specified keyword query:

```bash
java -jar target/docsearch-1.0-SNAPSHOT.jar -search -query <keyword>
```

Example:
```bash
java -jar target/docsearch-1.0-SNAPSHOT.jar -search -query "example search term"
```

## Custom Romanian Text Normalization

The `RomanianTextNormalizerAnalyzer` class performs several important preprocessing steps for Romanian language text:
- **Diacritic Removal**: Converts characters with diacritics to their base forms.
- **Punctuation Removal**: Strips out all punctuation.
- **Lowercasing**: Converts all text to lowercase.
- **Recursive Stemming**: Uses Snowball stemming for Romanian, applying recursive stemming to normalize words to their root forms.

### Stop Words

A list of Romanian stop words is provided in `stopwords-ro.txt`, which the analyzer uses to remove common words that do not contribute to meaningful search results.

## Contribution

The key contributions to this project include:
- **Implementation of the custom RomanianTextNormalizerAnalyzer**: This includes recursive stemming, diacritic, and punctuation removal.
- **Indexing logic in the `Indexer` class**: Developed to handle multiple file types.
- **Document search functionality in the `Searcher` class**: Uses Boolean queries to retrieve and rank document matches.
- **Error Handling**: Added input validation and error messages for incorrect arguments.
