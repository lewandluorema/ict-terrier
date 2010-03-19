package edu.usc.ict;

import org.apache.log4j.Logger;
import org.apache.commons.cli.*;
import java.io.*;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.*;

public class StoryServer {
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
		formatter.printHelp("StoryServer [options..]", options);
		return;
	    }

	    if (!cmd.hasOption("index")) throw new MissingOptionException("Missing option --index");

	    // NOTE: Prefix defaults "ict"
	    StoryServer server = new StoryServer(cmd.getOptionValue("index", "index.unnamed"), "ict");
	}
	catch (ParseException e) { System.err.println("common-cli: " + e.getMessage()); }
	catch (Exception e) { e.printStackTrace(); }
    }

    public StoryServer(String path, String prefix) {
	InetSocketAddress addr = new InetSocketAddress(8080);

	try {
	    HttpServer httpServer = HttpServer.create(addr, 0);
	    httpServer.createContext("/search", new SearchHandler());
	    httpServer.createContext("/stats", new StatsHandler());

	    httpServer.start();
	}
	catch (IOException e) { e.printStackTrace(); }
    }
}

class SearchHandler implements HttpHandler {
    public void handle(HttpExchange exchange) {
	int rCode = 200;

	Headers headers = exchange.getResponseHeaders();
	headers.add("Content-Type", "text/html; charset=ISO-8859-1");

	String message = "<html><head><title>It works!</title></head><body><h3>It works!</h3><p>" 
	    + exchange.getRequestURI().toString() + "</p></body></html>";

	try { 
	    exchange.sendResponseHeaders(rCode, 0); 

	    OutputStream out = exchange.getResponseBody();
	    PrintWriter writer = new PrintWriter(out);
	    writer.print(message);
	}
	catch (IOException e) { }
	finally { exchange.close(); }
    }
}

class StatsHandler implements HttpHandler {
    public void handle(HttpExchange exchange) {
	try { exchange.sendResponseHeaders(200, 0l); }
	catch (IOException e) {}
	finally { exchange.close(); }
    }
}

