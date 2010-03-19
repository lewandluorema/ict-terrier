package edu.usc.ict.terrier;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.terrier.indexing.Document;

public class Spinn3rDocument implements Document {
    private Reader reader;
    private ListIterator<String> iteratorOfTerms;
    private Map<String, String> properties = new HashMap<String, String>();
    private Set<String> fields = new HashSet<String>();
    
    private String content;

    private static int docno = 0;

    public Spinn3rDocument(String content) {
	reader = new StringReader(content);
	StringBuffer buffer = new StringBuffer();

	String title = "N/A";
	String url = "N/A";
	String text = "N/A";

	try {
	    // NOTE: Work with the `reader' inside
	    BufferedReader reader = new BufferedReader(new StringReader(content));
	    String line;
	    while ((line = reader.readLine()) != null) {
		if (line.startsWith("<link>") && line.endsWith("</link>")) {
		    url = line.substring(6, line.indexOf("</link>"));
		}
		else if (line.startsWith("<title>") && line.endsWith("</title>")) {
		    title = line.substring(7, line.indexOf("</title>"));
		}
		else if (line.startsWith("<dsentence>") && line.endsWith("</dsentence>")) {
		    buffer.append(line.substring(11, line.indexOf("</dsentence>")) + " \n");
		}
	    }
	}
	catch (IOException e) { e.printStackTrace(); }

	// Set things up
	text = buffer.toString();
	iteratorOfTerms = Arrays.asList(text.toLowerCase().split("\\s+")).listIterator();
	properties.put("docno", Integer.toString(++docno));
	properties.put("title", title.length() > 256? title.substring(0, 256): title);
	properties.put("url", url.length() > 256? url.substring(0, 256): url);
	properties.put("text", text.length() > 512? text.substring(0, 512): text);
    }

    public boolean endOfDocument() { return !iteratorOfTerms.hasNext(); }
    public Map<String,String> getAllProperties() { return properties; }
    public Set<String> getFields() { return fields; }

    public String getNextTerm() { 
	String term = iteratorOfTerms.next(); 
	return term.length() < 20? term: null;
    }

    public String getProperty(String name) { return properties.get(name); }
    public Reader getReader() { return null; }
}
