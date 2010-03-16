package edu.usc.ict.terrier;

import java.util.Map;
import java.util.Set;
import java.io.Reader;

public abstract class Spinn3rDocument implements org.terrier.indexing.Document {
    public boolean endOfDocument() { return true; }
//--------------------------------------------------
//     public Map<String,String> getAllProperties() { return new Map<String,String>(); }
//     public Set<String> getFields() { return new Set<String>(); }
//-------------------------------------------------- 
    public String getNextTerm() { return new String(); }
    public String getProperty(String name) { return new String(); }
//--------------------------------------------------
//     public Reader getReader() { return new Reader(); }
//-------------------------------------------------- 
}
