package edu.usc.ict.terrier;

import org.terrier.indexing.Collection;
import org.terrier.indexing.Document;
import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class Spinn3rCollection implements Collection {
    private InputStream in;
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

	in = new SequenceInputStream(streams.elements());
	nextDocument();
    }

    public boolean endOfCollection() { return endOfStream; }

    public Document getDocument() { 
	return new Spinn3rDocument(new java.io.StringReader("Hey you"), new HashMap<String, String>()); 
    }

    public boolean nextDocument() { 
	return false; 
    }

    public void reset() { try { in.reset(); } catch (IOException e) { e.printStackTrace(); } }
    public void close() {}

    // Arrrgh.  Why should I implement a deprecated method?
    @Deprecated public String getDocid() { return null; }
}
