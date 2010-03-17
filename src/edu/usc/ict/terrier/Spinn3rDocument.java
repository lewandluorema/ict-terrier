package edu.usc.ict.terrier;

import java.io.*;
import java.util.*;
import org.terrier.indexing.Document;

public class Spinn3rDocument implements Document {
    public Spinn3rDocument() {}

    private Map<String, String> properties = new HashMap<String,String>();
    private Set<String> fields = new HashSet<String>();

    public boolean endOfDocument() { return true; }
    public Map<String,String> getAllProperties() { return properties; }
    public Set<String> getFields() { return fields; }
    public String getNextTerm() { return null; }
    public String getProperty(String name) { return null; }
    public Reader getReader() { return new StringReader("hey you"); }
}
