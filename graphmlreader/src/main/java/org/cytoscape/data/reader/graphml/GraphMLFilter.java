package org.cytoscape.data.reader.graphml;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import cytoscape.data.ImportHandler;
import cytoscape.data.readers.GraphReader;
import cytoscape.util.CyFileFilter;

public class GraphMLFilter extends CyFileFilter {
	
	private static final String GRAPHML_NAMESPACE = "http://graphml.graphdrawing.org/xmlns";

	/**
	 * GraphML Files are Graphs.
	 */
	private static String fileNature = ImportHandler.GRAPH_NATURE;
	
	/**
	 * File Extensions.
	 */
	private static String[] fileExtensions = { "xml", "graphml" };
	
	/**
	 * Filter Description.
	 */
	private static String description = "GraphML files";
	
	/**
	 * Constructor.
	 */
	public GraphMLFilter() {
		super(fileExtensions, description, fileNature);
	}
	
	/**
	 * Gets the appropriate GraphReader object.
	 * 
	 * @param fileName File Name.
	 * @return GraphReader Object.
	 */
	public GraphReader getReader(String fileName) {
		return new GraphMLReader(fileName);
	}
	
	public GraphReader getReader(URL url, URLConnection conn) {
		return new GraphMLReader(url);
	}
	
	public boolean accept(File file) {
		String fileName = file.getName();
		boolean firstPass = false;
		
		//  First test:  file must end with one of the registered file extensions.
		for (int i = 0; i < fileExtensions.length; i++) {
			if (fileName.endsWith(fileExtensions[i])) {
				firstPass = true;
			}
		}

		if (firstPass) {
			//  Second test:  file header must contain the KGML declaration
			try {
				final String header = getHeader(file);

				if (header.indexOf(GRAPHML_NAMESPACE) > 0) {
					return true;
				}
			} catch (IOException e) {
			}
		}

		return false;
	}
	
	public boolean accept(URL url, String contentType) {
		String fileName = url.toString();
		boolean firstPass = false;
		
		//  First test:  file must end with one of the registered file extensions.
		for (int i = 0; i < fileExtensions.length; i++) {
			if (fileName.endsWith(fileExtensions[i])) {
				firstPass = true;
			}
		}

		if (firstPass) {
			//  Second test:  file header must contain the KGML declaration
			try {
				final String header = getHeader(url);
				
				if (header.contains("graphml")) {
					return true;
				}
			} catch (IOException e) {
			}
		}

		return false;

	}
}
