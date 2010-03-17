package edu.usc.ict.terrier;

import java.io.Reader;
import java.util.*;
import org.terrier.indexing.Document;

public class Spinn3rDocument implements Document {
    private Reader reader;
    private Map<String, String> properties;
    private Set<String> fields = new HashSet<String>();

    public Spinn3rDocument(Reader reader, Map<String, String> properties) {
	this.reader = reader;
	this.properties = properties;
    }

    public boolean endOfDocument() { return true; }
    public Map<String,String> getAllProperties() { return properties; }
    public Set<String> getFields() { return fields; }
    public String getNextTerm() { return null; }
    public String getProperty(String name) { return properties.get(name); }
    public Reader getReader() { return reader; }
}
