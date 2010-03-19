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
    public static void main(String[] args) {
	//--------------------------------------------------
	// CLI stuff
	//-------------------------------------------------- 
	Option[] optionArray = {
	    OptionBuilder.withLongOpt("help").withDescription("Show this help screen").create(),
	    OptionBuilder.withLongOpt("verbose").withDescription("Show verbose output").create(),
	    OptionBuilder.withLongOpt("index").withArgName("path").hasArgs(1).withDescription("Path to the index").create(),
	};

	Options options = new Options();
	for (Option o: optionArray) options.addOption(o);

	CommandLineParser parser = new GnuParser();

	try {
	    CommandLine cmd = parser.parse(options, args);
	    String[] filenames = cmd.getArgs();

	    if (cmd.hasOption("help")) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("StorySearcher [options..] file..", options);
		return;
	    }

	    if (!cmd.hasOption("index")) throw new MissingOptionException("Missing option --index");

	    StorySearcher searcher = new StorySearcher(cmd.getOptionValue("index"), "ict");

	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    String query;
	    while ((query = br.readLine()) != null) 
		System.out.println(searcher.process(query));
	}
	catch (ParseException e) { System.err.println("common-cli: " + e.getMessage()); }
	catch (Exception e) { e.printStackTrace(); }
    }

    private Manager queryingManager;

    public StorySearcher(String path, String prefix) {
	System.setProperty("terrier.home", ".");
	System.setProperty("terrier.var", ".");

	Index index = Index.createIndex(path, prefix);
	queryingManager = new Manager(index);
    }

    public String process(String query) {
	StringWriter sw = new StringWriter();
	XMLStreamWriter writer = null;

	try {
	    writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
	    writer.writeStartDocument("UTF-8", "1.0");
	    writer.writeStartElement("items");

	    if (query != null && query.length() > 0) {
		SearchRequest srq = queryingManager.newSearchRequest("queryID0", query);
		srq.addMatchingModel("Matching", "PL2");
		srq.setControl("decorate", "on");
		queryingManager.runPreProcessing(srq);
		queryingManager.runMatching(srq);
		queryingManager.runPostProcessing(srq);
		queryingManager.runPostFilters(srq);
		ResultSet rs = srq.getResultSet();

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
			    writer.writeCData(urls[i].replace('\0', ' '));
			} finally { writer.writeEndElement(); }
		    }

		    // title
		    if (titles != null) {
			try {
			    writer.writeStartElement("title");
			    writer.writeCData(titles[i].replace('\0', ' '));
			} finally { writer.writeEndElement(); }
		    }

		    // text
		    if (texts != null) {
			try {
			    writer.writeStartElement("text");
			    writer.writeCData(texts[i]);
			} finally { writer.writeEndElement(); }
		    }

		    writer.writeEndElement();
		}
	    }

	    writer.writeEndElement();
	}
	catch (XMLStreamException e) { e.printStackTrace(); }
	finally { 

	    try { 
		writer.writeEndDocument();
		writer.flush(); 
		writer.close(); 
	    }
	    catch (XMLStreamException e) {}
	    finally {}
	}

	return sw.toString();
    }
}
