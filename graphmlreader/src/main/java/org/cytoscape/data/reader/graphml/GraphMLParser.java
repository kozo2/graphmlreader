/**
 * A SAX Parser for GraphML data file.
 * @author Kozo.Nishida
 *
 */

package org.cytoscape.data.reader.graphml;

import cytoscape.Cytoscape;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.logger.CyLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class GraphMLParser extends DefaultHandler {

	// private static CyLogger logger = CyLogger.getLogger(GraphMLParser.class);

	private String networkName = null;

	/* Internal lists of the created nodes and edges */
	private List<CyNode> nodeList = null;
	private List<CyEdge> edgeList = null;

	/* Map of XML ID's to nodes */
	private Map<String, CyNode> nodeidMap = null;

	/* Map of data type to nodes or edges */
	private Map<String, String> datatypeMap = null;

	private CyNode currentNode = null;
	private CyEdge currentEdge = null;

	/* Attribute values */
	private String currentAttributeID = null;
	private String currentAttributeKey = null;
	private String currentAttributeData = null;
	private String currentAttributeType = null;
	private String currentEdgeSource = null;
	private String currentEdgeTarget = null;
	private String currentObjectTarget = null;
	private String currentQname = null;

	private CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
	private CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();

	/* node, edge, data parsing */
	private boolean directed = false;
	
	/********************************************************************
	 * Routines to handle keys
	 *******************************************************************/

	/**
	 * Main constructor for our parser. Initialize any local arrays. Note that
	 * this parser is designed to be as memory efficient as possible. As a
	 * result, a minimum number of local data structures
	 */
	GraphMLParser() {
		nodeList = new ArrayList<CyNode>();
		edgeList = new ArrayList<CyEdge>();
		nodeidMap = new HashMap<String, CyNode>();
		datatypeMap = new HashMap<String, String>();
	}

	/********************************************************************
	 * Interface routines. These routines are called by the GraphMLReader to get
	 * the resulting data.
	 *******************************************************************/

	int[] getNodeIndicesArray() {
		
		System.out.println("Got nodes: " + nodeList.size());
		
		int[] array = new int[nodeList.size()];
		
		for (int i = 0; i < nodeList.size(); i++) {
			array[i] = nodeList.get(i).getRootGraphIndex();
		}
		return array;
	}

	int[] getEdgeIndicesArray() {
		
		System.out.println("Got edges: " + edgeList.size());
		
		int[] array = new int[edgeList.size()];
		for (int i = 0; i < edgeList.size(); i++) {
			array[i] = edgeList.get(i).getRootGraphIndex();
		}
		return array;
	}

	String getNetworkName() {
		return networkName;
	}

	/********************************************************************
	 * Handler routines. The following routines are called directly from the SAX
	 * parser.
	 *******************************************************************/

	public void startDocument() {

	}

	public void endDocument() throws SAXException {

	}

	public void startElement(String namespace, String localName, String qName,
			Attributes atts) throws SAXException {
		if (qName.equals(GraphMLToken.GRAPH.getTag())) {
			currentQname = GraphMLToken.GRAPH.getTag();
			// parse directed or undirected
			String edef = atts.getValue(GraphMLToken.EDGEDEFAULT.getTag());
			directed = GraphMLToken.DIRECTED.getTag().equalsIgnoreCase(edef);
			
			this.networkName = atts.getValue(GraphMLToken.ID.getTag());
			
		} else if (qName.equals(GraphMLToken.KEY.getTag())) {
			currentQname = GraphMLToken.KEY.getTag();
			if (atts.getValue(GraphMLToken.FOR.getTag()).equals(GraphMLToken.NODE.getTag())) {
				datatypeMap
						.put(atts.getValue(GraphMLToken.ID.getTag()), atts.getValue(GraphMLToken.ATTRTYPE.getTag()));
			} else if (atts.getValue(GraphMLToken.FOR.getTag()).equals(GraphMLToken.EDGE.getTag())) {
				datatypeMap
						.put(atts.getValue(GraphMLToken.ID.getTag()), atts.getValue(GraphMLToken.ATTRTYPE.getTag()));
			} else if (atts.getValue(GraphMLToken.FOR.getTag()).equals(GraphMLToken.ALL.getTag())) {
				datatypeMap
						.put(atts.getValue(GraphMLToken.ID.getTag()), atts.getValue(GraphMLToken.ATTRTYPE.getTag()));
			}
		} else if (qName.equals(GraphMLToken.NODE.getTag())) {
			currentQname = GraphMLToken.NODE.getTag();
			// Parse node entry.
			currentObjectTarget = GraphMLToken.NODE.getTag();
			currentAttributeID = atts.getValue(GraphMLToken.ID.getTag());
			currentNode = Cytoscape.getCyNode(currentAttributeID, true);
			nodeList.add(currentNode);
			nodeidMap.put(currentAttributeID, currentNode);
		} else if (qName.equals(GraphMLToken.EDGE.getTag())) {
			currentQname = GraphMLToken.EDGE.getTag();
			// Parse edge entry
			currentObjectTarget = GraphMLToken.EDGE.getTag();
			currentEdgeSource = atts.getValue(GraphMLToken.SOURCE.getTag());
			currentEdgeTarget = atts.getValue(GraphMLToken.TARGET.getTag());
			CyNode sourceNode = nodeidMap.get(currentEdgeSource);
			CyNode targetNode = nodeidMap.get(currentEdgeTarget);
			currentEdge = Cytoscape.getCyEdge(sourceNode, targetNode,
					Semantics.INTERACTION, "pp", true);
			edgeList.add(currentEdge);
		} else if (qName.equals(GraphMLToken.DATA.getTag())) {
			currentQname = GraphMLToken.DATA.getTag();
			currentAttributeKey = atts.getValue(GraphMLToken.KEY.getTag());
			currentAttributeType = datatypeMap.get(currentAttributeKey);
		}
	}

	public void characters(char[] ch, int start, int length) {
		currentAttributeData = new String(ch, start, length);
		
		if (currentObjectTarget != null) {
			if (currentObjectTarget.equals(GraphMLToken.NODE.getTag())) {
				if (currentAttributeType != null) {
					if (currentAttributeType.equals(GraphMLToken.STRING.getTag())) {
						// debug
						//System.out.println(currentAttributeData);
						nodeAttributes.setAttribute(currentAttributeID,
								currentAttributeKey, currentAttributeData);
					} else if (currentAttributeType.equals(GraphMLToken.DOUBLE.getTag())) {
						// debug
						//System.out.println(currentAttributeData);
						nodeAttributes.setAttribute(currentAttributeID,
								currentAttributeKey,
								Double.parseDouble(currentAttributeData));
					}
				}
			}
			else if (currentObjectTarget.equals(GraphMLToken.EDGE.getTag())) {
				if (currentAttributeType != null) {
					if (currentAttributeType.equals(GraphMLToken.STRING.getTag())) {
						// debug
						//System.out.println(currentAttributeData);
						edgeAttributes.setAttribute(currentEdge.getIdentifier(),
								currentAttributeKey, currentAttributeData);
					}
					if (currentAttributeType.equals(GraphMLToken.DOUBLE.getTag())) {
						// debug
						//System.out.println(currentAttributeData);
						edgeAttributes.setAttribute(currentEdge.getIdentifier(),
								currentAttributeKey, Double.parseDouble(currentAttributeData));
					}					
				}
			}
		}

	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (currentQname != GraphMLToken.DATA.getTag()) {
			currentObjectTarget = null;			
		}
		currentAttributeType = null;
	}

}
