package edu.usc.ict.terrier;

import org.terrier.indexing.Collection;
import org.terrier.indexing.Document;
import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class Spinn3rCollection implements Collection {
    private InputStream in;
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

	in = new SequenceInputStream(streams.elements());
	nextDocument();
    }

    public boolean endOfCollection() { return endOfStream; }
    public Document getDocument() { return document; }

    public boolean nextDocument() { 
	BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	StringBuffer buffer = new StringBuffer();

	try {
	    String lastLine = reader.readLine();
	    while (lastLine != null && !lastLine.equals("<item>")) 
		lastLine = reader.readLine();

	    if (lastLine == null) {
		endOfStream = true;
		return false;
	    }

	    buffer.append(lastLine + "\n");

	    while (lastLine != null && !lastLine.equals("</item>")) {
		lastLine = reader.readLine();
		buffer.append(lastLine + "\n");
	    }

	    document = new Spinn3rDocument(buffer.toString());
	    return true; 
	}
	catch (IOException e) { e.printStackTrace(); }
	return false;
    }

    public void reset() { try { in.reset(); } catch (IOException e) { e.printStackTrace(); } }
    public void close() {}

    // Arrrgh.  Why should I implement a deprecated method?
    @Deprecated public String getDocid() { return null; }
}
