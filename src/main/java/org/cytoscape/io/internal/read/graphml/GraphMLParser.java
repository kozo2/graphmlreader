/**
 * A SAX Parser for GraphML data file.
 * @author Kozo.Nishida
 *
 */

package org.cytoscape.io.internal.read.graphml;

import static org.cytoscape.io.internal.read.graphml.GraphMLToken.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.work.TaskMonitor;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GraphMLParser extends DefaultHandler {

	private String networkName = null;

	/* Map of XML ID's to nodes */
	private Map<String, CyTableEntry> nodeidMap = null;

	/* Map of data type to nodes or edges */
	private Map<String, String> datatypeMap = null;

	private CyTableEntry currentObject = null;

	// Attribute values
	private String currentAttributeID = null;
	private String currentAttributeKey = null;
	private String currentAttributeData = null;
	private String currentAttributeType = null;
	private String currentEdgeSource = null;
	private String currentEdgeTarget = null;
	private String currentObjectTarget = null;
	private String currentQname = null;

	private final TaskMonitor tm;
	private final CyNetworkFactory networkFactory;

	private boolean directed = true;

	private final Set<CyNetwork> cyNetworks;

	// Current CyNetwork. GraphML can have multiple networks in a file.
	private CyNetwork currentNetwork;

	/**
	 * Main constructor for our parser. Initialize any local arrays. Note that
	 * this parser is designed to be as memory efficient as possible. As a
	 * result, a minimum number of local data structures
	 */
	GraphMLParser(final TaskMonitor tm, final CyNetworkFactory networkFactory) {
		this.tm = tm;
		this.networkFactory = networkFactory;
		cyNetworks = new HashSet<CyNetwork>();

		nodeidMap = new HashMap<String, CyTableEntry>();
		datatypeMap = new HashMap<String, String>();
	}

	CyNetwork[] getCyNetworks() {
		return cyNetworks.toArray(new CyNetwork[0]);
	}

	/********************************************************************
	 * Handler routines. The following routines are called directly from the SAX
	 * parser.
	 *******************************************************************/

	@Override
	public void startDocument() {
	}

	@Override
	public void endDocument() throws SAXException {

		// Clear
		nodeidMap.clear();
		datatypeMap.clear();
		nodeidMap = null;
		datatypeMap = null;
	}

	@Override
	public void startElement(String namespace, String localName, String qName, Attributes atts) throws SAXException {
		currentQname = qName;
		if (qName.equals(GRAPH.getTag())) {
			// Create CyNetwork Object at the beginning.
			currentNetwork = networkFactory.getInstance();
			cyNetworks.add(currentNetwork);
			// parse directed or undirected
			String edef = atts.getValue(EDGEDEFAULT.getTag());
			directed = DIRECTED.getTag().equalsIgnoreCase(edef);
			this.networkName = atts.getValue(ID.getTag());
		} else if (qName.equals(KEY.getTag())) {
			if (atts.getValue(GraphMLToken.FOR.getTag()).equals(GraphMLToken.NODE.getTag())) {
				datatypeMap.put(atts.getValue(GraphMLToken.ID.getTag()), atts.getValue(ATTRTYPE.getTag()));
			} else if (atts.getValue(GraphMLToken.FOR.getTag()).equals(GraphMLToken.EDGE.getTag())) {
				datatypeMap.put(atts.getValue(GraphMLToken.ID.getTag()), atts.getValue(ATTRTYPE.getTag()));
			} else if (atts.getValue(GraphMLToken.FOR.getTag()).equals(GraphMLToken.ALL.getTag())) {
				datatypeMap.put(atts.getValue(GraphMLToken.ID.getTag()), atts.getValue(ATTRTYPE.getTag()));
			}
		} else if (qName.equals(NODE.getTag())) {
			// Parse node entry.
			currentObjectTarget = NODE.getTag();
			currentAttributeID = atts.getValue(ID.getTag());

			currentObject = nodeidMap.get(currentAttributeID);
			if (currentObject == null) {
				currentObject = currentNetwork.addNode();
				currentObject.getCyRow().set(CyTableEntry.NAME, currentAttributeID);
				nodeidMap.put(currentAttributeID, currentObject);
			}
		} else if (qName.equals(EDGE.getTag())) {
			// Parse edge entry
			currentObjectTarget = EDGE.getTag();
			currentEdgeSource = atts.getValue(SOURCE.getTag());
			currentEdgeTarget = atts.getValue(TARGET.getTag());
			final CyNode sourceNode = (CyNode) nodeidMap.get(currentEdgeSource);
			final CyNode targetNode = (CyNode) nodeidMap.get(currentEdgeTarget);
			currentObject = currentNetwork.addEdge(sourceNode, targetNode, directed);
			currentObject.getCyRow().set(CyTableEntry.NAME, currentEdgeSource + " (-) " + currentEdgeTarget);
			currentObject.getCyRow().set(CyEdge.INTERACTION, "-");
		} else if (qName.equals(DATA.getTag())) {
			currentAttributeKey = atts.getValue(KEY.getTag());
			currentAttributeType = datatypeMap.get(currentAttributeKey);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		currentAttributeData = new String(ch, start, length);

		if (currentObjectTarget != null) {
			if (currentObjectTarget.equals(NODE.getTag()) || currentObjectTarget.equals(EDGE.getTag())) {
				if (currentAttributeType != null && currentAttributeData.trim().length() != 0) {
					final CyColumn column = currentObject.getCyRow().getTable().getColumn(currentAttributeKey);					
					if (currentAttributeType.equals(STRING.getTag())) {
						if (column == null)
							currentObject.getCyRow().getTable().createColumn(currentAttributeKey, String.class, false);
						this.currentObject.getCyRow().set(currentAttributeKey, currentAttributeData);
					} else if (currentAttributeType.equals(DOUBLE.getTag())) {
						if (column == null)
							currentObject.getCyRow().getTable().createColumn(currentAttributeKey, Double.class, false);
						this.currentObject.getCyRow()
								.set(currentAttributeKey, Double.parseDouble(currentAttributeData));
					}
				}
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (currentQname != DATA.getTag())
			currentObjectTarget = null;
	}

}
