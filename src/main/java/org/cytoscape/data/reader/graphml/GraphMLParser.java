/**
 * A SAX Parser for GraphML data file.
 * @author Kozo.Nishida
 *
 */

package org.cytoscape.data.reader.graphml;

import static org.cytoscape.data.reader.graphml.GraphMLToken.DATA;
import static org.cytoscape.data.reader.graphml.GraphMLToken.DIRECTED;
import static org.cytoscape.data.reader.graphml.GraphMLToken.EDGE;
import static org.cytoscape.data.reader.graphml.GraphMLToken.EDGEDEFAULT;
import static org.cytoscape.data.reader.graphml.GraphMLToken.GRAPH;
import static org.cytoscape.data.reader.graphml.GraphMLToken.ID;
import static org.cytoscape.data.reader.graphml.GraphMLToken.KEY;
import static org.cytoscape.data.reader.graphml.GraphMLToken.NODE;
import static org.cytoscape.data.reader.graphml.GraphMLToken.SOURCE;
import static org.cytoscape.data.reader.graphml.GraphMLToken.STRING;
import static org.cytoscape.data.reader.graphml.GraphMLToken.TARGET;

import java.util.HashMap;
import java.util.Map;

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
	
	private CyNetwork network;	

	/**
	 * Main constructor for our parser. Initialize any local arrays. Note that
	 * this parser is designed to be as memory efficient as possible. As a
	 * result, a minimum number of local data structures
	 */
	GraphMLParser(final TaskMonitor tm, final CyNetworkFactory networkFactory) {
		this.tm = tm;
		this.networkFactory = networkFactory;

		nodeidMap = new HashMap<String, CyTableEntry>();
		datatypeMap = new HashMap<String, String>();
	}

	/********************************************************************
	 * Handler routines. The following routines are called directly from the SAX
	 * parser.
	 *******************************************************************/

	@Override
	public void startDocument() {
		// Create CyNetwork Object at the beginning.
		network = networkFactory.getInstance();
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
			// parse directed or undirected
			String edef = atts.getValue(EDGEDEFAULT.getTag());
			directed = DIRECTED.getTag().equalsIgnoreCase(edef);
			this.networkName = atts.getValue(ID.getTag());
		} else if (qName.equals(KEY.getTag())) {
			if (atts.getValue(GraphMLToken.FOR.getTag()).equals(GraphMLToken.NODE.getTag())) {
				datatypeMap.put(atts.getValue(GraphMLToken.ID.getTag()), atts.getValue(GraphMLToken.ATTRTYPE.getTag()));
			} else if (atts.getValue(GraphMLToken.FOR.getTag()).equals(GraphMLToken.EDGE.getTag())) {
				datatypeMap.put(atts.getValue(GraphMLToken.ID.getTag()), atts.getValue(GraphMLToken.ATTRTYPE.getTag()));
			} else if (atts.getValue(GraphMLToken.FOR.getTag()).equals(GraphMLToken.ALL.getTag())) {
				datatypeMap.put(atts.getValue(GraphMLToken.ID.getTag()), atts.getValue(GraphMLToken.ATTRTYPE.getTag()));
			}
		} else if (qName.equals(NODE.getTag())) {
			// Parse node entry.
			currentObjectTarget = NODE.getTag();
			currentAttributeID = atts.getValue(ID.getTag());
			
			currentObject = nodeidMap.get(currentAttributeID);
			if (currentObject == null) {
				currentObject = network.addNode();
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
			currentObject = network.addEdge(sourceNode, targetNode, directed);
			currentObject.getCyRow().set(CyTableEntry.NAME, currentEdgeSource + " (-) " + currentEdgeTarget);
			currentObject.getCyRow().set(CyEdge.INTERACTION,"-");
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
				if (currentAttributeType != null) {
					if (currentAttributeType.equals(STRING.getTag()))
						this.currentObject.getCyRow().set(currentAttributeKey, currentAttributeData);
					else if (currentAttributeType.equals(GraphMLToken.DOUBLE.getTag()))
						this.currentObject.getCyRow().set(currentAttributeKey, Double.parseDouble(currentAttributeData));
				}
			}
		}
//			else if (currentObjectTarget.equals(GraphMLToken.EDGE.getTag())) {
//				if (currentAttributeType != null) {
//					if (currentAttributeType.equals(GraphMLToken.STRING.getTag())) {
//						// debug
//						// System.out.println(currentAttributeData);
//						edgeAttributes.setAttribute(currentObject.getIdentifier(), currentAttributeKey,
//								currentAttributeData);
//					}
//					if (currentAttributeType.equals(GraphMLToken.DOUBLE.getTag())) {
//						// debug
//						// System.out.println(currentAttributeData);
//						edgeAttributes.setAttribute(currentObject.getIdentifier(), currentAttributeKey,
//								Double.parseDouble(currentAttributeData));
//					}
//				}
//			}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (currentQname != DATA.getTag())
			currentObjectTarget = null;
//		currentAttributeType = null;
//		currentNode = null;
//		currentEdge = null;
	}

}
