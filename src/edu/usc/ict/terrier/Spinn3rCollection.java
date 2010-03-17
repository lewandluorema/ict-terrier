package edu.usc.ict.terrier;

import org.terrier.indexing.Collection;
import org.terrier.indexing.Document;

public class Spinn3rCollection implements Collection {
    private String[] filenames;

    public Spinn3rCollection(String[] filenames) { 
	this.filenames = filenames;
	java.lang.System.err.println("Nevermind the TRECCollection, this is Spinn3rCollection!"); 
    }

    public boolean endOfCollection() { return false; }
    public Document getDocument() { return new Spinn3rDocument(); }
    public boolean nextDocument() { return false; }
    public void reset() {}
    public void close() {}

    // Arrrgh.  Why should I implement a deprecated method?
    @Deprecated public String getDocid() { return null; }
}
