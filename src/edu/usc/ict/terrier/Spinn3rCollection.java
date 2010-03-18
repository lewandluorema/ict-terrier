package edu.usc.ict.terrier;

import org.terrier.indexing.Collection;
import org.terrier.indexing.Document;
import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class Spinn3rCollection implements Collection {
    private InputStream in;
    private BufferedReader reader;
    private Document document;
    private boolean endOfStream = false;

    public Spinn3rCollection(String[] filenames) { 
	Vector<InputStream> streams = new Vector<InputStream>();
	for (String filename: filenames) {
	    try {
		streams.add(filename.endsWith(".gz")? 
		    new GZIPInputStream(new FileInputStream(filename)): new FileInputStream(filename));
	    }
	    catch (FileNotFoundException e) {}
	    catch (IOException e) { e.printStackTrace(); }
	}

	try {
	    in = new SequenceInputStream(streams.elements());
	    reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
	}
	catch (Exception e) { e.printStackTrace(); }

	nextDocument();
    }

    public boolean endOfCollection() { return endOfStream; }
    public Document getDocument() { return document; }

    //--------------------------------------------------
    // The birth of mess
    //-------------------------------------------------- 
    public boolean nextDocument() { 
	StringBuffer buffer = new StringBuffer();

	try {
	    String line;
	    while ((line = reader.readLine()) != null && !line.startsWith("<item>")) ;

	    if (line == null) {
		System.err.println("* here");
		//--------------------------------------------------
		// endOfStream = true;
		//-------------------------------------------------- 
		return false;
	    }

	    buffer.append(line + "\n");
	    while ((line = reader.readLine()) != null && !line.startsWith("</item>"))
		buffer.append(line + "\n");

	    if (line == null) {
		System.err.println("* there");
		//--------------------------------------------------
		// endOfStream = true;
		//-------------------------------------------------- 
		return false;
	    }

	    buffer.append(line + "\n");

	    document = new Spinn3rDocument(buffer.toString());
	    return true; 
	}
	catch (IOException e) { e.printStackTrace(); }
	return false;
    }

    public void reset() { 
	// try { in.reset(); } catch (IOException e) { e.printStackTrace(); } 
    }

    public void close() {}

    // Arrrgh.  Why should I implement a deprecated method?
    @Deprecated public String getDocid() { return null; }
}
