package org.cytoscape.data.reader.graphml;

import org.cytoscape.data.reader.graphml.util.GraphMLDragAndDropManager;
import org.cytoscape.data.writer.graphml.ExportAsGraphMLAction;

import cytoscape.Cytoscape;
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

		// Setup drop target
		GraphMLDragAndDropManager.getManager().activateTarget();

		// Add Export menu
		final ExportAsGraphMLAction action = new ExportAsGraphMLAction();
		Cytoscape.getDesktop().getCyMenus().addAction(action);
	}

}
