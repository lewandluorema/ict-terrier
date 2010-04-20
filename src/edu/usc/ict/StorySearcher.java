package edu.usc.ict;

import org.apache.commons.cli.*;
import org.terrier.indexing.*;
import org.terrier.matching.ResultSet;
import org.terrier.querying.Manager;
import org.terrier.querying.SearchRequest;
import org.terrier.structures.Index;
import java.io.*;
import javax.xml.stream.*;

public class StorySearcher {
    private Manager queryingManager;

    public StorySearcher(String path, String prefix) {
	Index index = Index.createIndex(path, prefix);
	queryingManager = new Manager(index);
    }

    public String process(String query) {
	StringWriter sw = new StringWriter();
	XMLStreamWriter writer = null;

	try {
	    writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
	    writer.writeStartDocument("UTF-8", "1.0");
	    
	    if (query != null && query.length() > 0) {
		SearchRequest srq = queryingManager.newSearchRequest("queryID0", query);
		srq.addMatchingModel("Matching", "PL2");
		srq.setControl("decorate", "on");
		queryingManager.runPreProcessing(srq);
		queryingManager.runMatching(srq);
		queryingManager.runPostProcessing(srq);
		queryingManager.runPostFilters(srq);
		ResultSet rs = srq.getResultSet();

		writer.writeStartElement("items");
		writer.writeAttribute("size", Integer.toString(rs.getResultSize()));
		writer.writeAttribute("exactSize", Integer.toString(rs.getExactResultSize()));

		int[] docids = rs.getDocids();
		double[] scores = rs.getScores();
		String[] urls = rs.getMetaItems("url");
		String[] titles = rs.getMetaItems("title");
		String[] texts = rs.getMetaItems("text");

		for (int i = 0; i < docids.length; ++i) {
		    writer.writeStartElement("item");

		    try {
			// docno
			writer.writeStartElement("docno");
			writer.writeCharacters(Integer.toString(docids[i]));
		    } finally { writer.writeEndElement(); }

		    try {
			// score
			writer.writeStartElement("score");
			writer.writeCharacters(Double.toString(scores[i]));
		    } finally { writer.writeEndElement(); }

		    // url
		    if (urls != null) {
			try {
			    writer.writeStartElement("url");
			    writer.writeCData(urls[i].replace("]]>", "").replace("\0", ""));
			} finally { writer.writeEndElement(); }
		    }

		    // title
		    if (titles != null) {
			try {
			    writer.writeStartElement("title");
			    writer.writeCData(titles[i].replace("]]>", "").replace("\0", ""));
			} finally { writer.writeEndElement(); }
		    }

		    // text
		    if (texts != null) {
			try {
			    writer.writeStartElement("text");
			    writer.writeCData(texts[i].replace("]]>", "").replace("\0", ""));
			} finally { writer.writeEndElement(); }
		    }

		    writer.writeEndElement();
		}

		writer.writeEndElement();
	    }
	    else {
		writer.writeEmptyElement("items");
	    }
	}
	catch (XMLStreamException e) { e.printStackTrace(); }
	finally { 
	    try { 
		writer.writeEndDocument();
		writer.flush(); 
		writer.close(); 
	    }
	    catch (XMLStreamException e) {}
	}

	return sw.toString();
    }
}
