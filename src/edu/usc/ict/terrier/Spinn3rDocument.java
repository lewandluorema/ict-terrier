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
    
    private final static Pattern docnoPattern = Pattern.compile("<link>(.*?)</link>");
    private final static Pattern sentencePattern = Pattern.compile("<dsentence>(.*?)</dsentence>");
    private String content;

    public Spinn3rDocument(String content) {
	reader = new StringReader(content);
	StringBuffer text = new StringBuffer();
	text.append("stuff\n");

	try {
	    // NOTE: Work with the `reader' inside
	    BufferedReader reader = new BufferedReader(new StringReader(content));
	    String line;
	    while ((line = reader.readLine()) != null) {
		if (line.startsWith("<link>")) {
		    Matcher m = docnoPattern.matcher(line);
		    if (m.matches()) 
			properties.put("docno", line.substring(m.start(1), Math.min(m.end(1), m.start(1) + 60)));

		    System.err.println(line.substring(m.start(1), m.end(1)));
		}
		//--------------------------------------------------
		// else if (line.startsWith("<dsentence>")) {
		//     Matcher m = sentencePattern.matcher(line);
		//     if (m.matches()) 
		// 	text.append(line.substring(m.start(1), m.end(1)) + "\n\n");
		// }
		//-------------------------------------------------- 
	    }
	}
	catch (IOException e) { e.printStackTrace(); }

	// Set things up
	iteratorOfTerms = Arrays.asList(text.toString().split("\\s+")).listIterator();
    }

    public boolean endOfDocument() { return !iteratorOfTerms.hasNext(); }
    public Map<String,String> getAllProperties() { return properties; }
    public Set<String> getFields() { return fields; }

    public String getNextTerm() { 
	String term = iteratorOfTerms.next(); 
	return term.length() < 20? term: "";
    }

    public String getProperty(String name) { return properties.get(name); }
    public Reader getReader() { return null; }
}
