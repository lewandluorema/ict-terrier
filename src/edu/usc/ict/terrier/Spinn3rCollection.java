package edu.usc.ict.terrier;

import org.terrier.indexing.Collection;
import org.terrier.indexing.Document;
import java.util.*;

public class Spinn3rCollection implements Collection {
    private ListIterator<String> listOfFiles;
    private String openedFile;

    public Spinn3rCollection(String[] filenames) { 
	listOfFiles = Arrays.asList(filenames).listIterator();
	openedFile = null;
    }

    public boolean endOfCollection() { 
	return openedFile == null && !listOfFiles.hasNext();
    }

    public Document getDocument() { 
	return new Spinn3rDocument(new java.io.StringReader("Hey you"), new HashMap<String, String>()); 
    }

    public boolean nextDocument() { 
	return false; 
    }

    public void reset() {}
    public void close() {}

    // Arrrgh.  Why should I implement a deprecated method?
    @Deprecated public String getDocid() { return null; }
}
