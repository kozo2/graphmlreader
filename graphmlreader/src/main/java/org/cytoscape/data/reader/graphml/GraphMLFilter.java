package org.cytoscape.data.reader.graphml;

import java.net.URL;
import java.net.URLConnection;

import cytoscape.data.ImportHandler;
import cytoscape.data.readers.GraphReader;
import cytoscape.util.CyFileFilter;

public class GraphMLFilter extends CyFileFilter {

	/**
	 * GraphML Files are Graphs.
	 */
	private static String fileNature = ImportHandler.GRAPH_NATURE;
	
	/**
	 * File Extensions.
	 */
	private static String[] fileExtensions = { "graphml" };
	
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
}
