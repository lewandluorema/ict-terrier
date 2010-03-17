package edu.usc.ict.terrier;

import java.io.*;
import java.util.*;
import org.terrier.indexing.Document;

public class Spinn3rDocument implements Document {
    private String content;
    private ListIterator<String> iteratorOfTerms;
    private Map<String, String> properties = new HashMap<String, String>();
    private Set<String> fields = new HashSet<String>();

    public Spinn3rDocument(String content) {
	this.content = content;
	iteratorOfTerms = Arrays.asList(content.split("\\s+")).listIterator();
	properties.put("docno", "default_docno");
    }

    public boolean endOfDocument() { return !iteratorOfTerms.hasNext(); }
    public Map<String,String> getAllProperties() { return properties; }
    public Set<String> getFields() { return fields; }
    public String getNextTerm() { return Integer.toString(iteratorOfTerms.next().length()); }
    public String getProperty(String name) { return properties.get(name); }
    public Reader getReader() { return new StringReader(content); }
}
