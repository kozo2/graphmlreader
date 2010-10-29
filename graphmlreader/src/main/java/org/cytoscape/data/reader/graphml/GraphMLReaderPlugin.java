package org.cytoscape.data.reader.graphml;

import cytoscape.data.ImportHandler;
import cytoscape.plugin.CytoscapePlugin;

/**
 * GraphML Reader Main class
 * 
 * @author Kozo.Nishida
 *
 */
public class GraphMLReaderPlugin extends CytoscapePlugin {
	
	public GraphMLReaderPlugin() {
		final ImportHandler importHandler = new ImportHandler();
		importHandler.addFilter(new GraphMLFilter());
	}
	
}
