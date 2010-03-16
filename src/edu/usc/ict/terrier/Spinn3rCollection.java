package edu.usc.ict.terrier;

public abstract class Spinn3rCollection implements org.terrier.indexing.Collection {
    public boolean endOfCollection() { return true; }
    public org.terrier.indexing.Document getDocument() { return new Spinn3rDocument(); }
    public boolean nextDocument() { return true; }
    public void reset() {}
}
