/**
 * A SAX Parser for GraphML data file.
 * @author kozo.nishida
 *
 */

package org.cytoscape.data.reader.graphml;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.data.CyAttributes;
import cytoscape.generated.Network;
import cytoscape.logger.CyLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class GraphMLParser extends DefaultHandler {
	
	private static CyLogger logger = CyLogger.getLogger(GraphMLParser.class);
	
	private String networkName = null;
	
	/* Internal lists of the created nodes and edges */
	private List<CyNode> nodeList = null;
	private List<CyEdge> edgeList = null;
	
	/* Map of XML ID's to nodes */
	private HashMap<String,CyNode> nodeidMap = null;
	
	/* Map of data type to nodes and edges */
	private HashMap<String, String> nodeDatatypeMap = null;
	private HashMap<String, String> edgeDatatypeMap = null; 
	
	private CyNode currentNode = null;
	private CyEdge currentEdge = null;
	
	/* Attribute values */
	private String currentAttributeID = null;
	private String currentAttributeName = null;
	private String currentAttributeData = null;
	private String currentAttributeType = null;
	private String currentEdgeSource = null;
	private String currentEdgeTarget = null;
	private String currentObjectTarget = null;
	
	private CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
	private CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
	
	/* Edge handle list */
	private List<String> handleList = null;
	
	/* node, edge, data parsing */
	private boolean directed = false;

	
	/********************************************************************
	 * Routines to handle keys
	 *******************************************************************/
	
	/**
	 * Main constructor for our parser. Initialize any local arrays. Note that this
	 * parser is designed to be as memory efficient as possible. As a result, a minimum
	 * number of local data structures
	 */
	GraphMLParser() {
		nodeList = new ArrayList<CyNode>();
		edgeList = new ArrayList<CyEdge>();
		nodeidMap = new HashMap();
		nodeDatatypeMap = new HashMap();
		edgeDatatypeMap = new HashMap();
	}
	
	/********************************************************************
	 * Interface routines.  These routines are called by the GraphMLReader
	 * to get the resulting data.
	 *******************************************************************/	

	int[] getNodeIndicesArray() {
		int[] array = new int[nodeList.size()];
		for (int i = 0; i < nodeList.size(); i++) {
			array[i] = nodeList.get(i).getRootGraphIndex();
		}
		return array;
	}
	
	int[] getEdgeIndicesArray() {
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
	 * Handler routines.  The following routines are called directly from
	 * the SAX parser.
	 *******************************************************************/
	
	public void startDocument(){

	}
	
	public void endDocument() throws SAXException{
		
	}
	
	public void startElement(String namespace, String localName, String qName, Attributes atts) throws SAXException {
		if (qName.equals("graph")) {
			// parse directednes default
			String edef = atts.getValue("edgedefault");
			directed = "directed".equalsIgnoreCase(edef);
		}
		else if (qName.equals("key")) {
			if(atts.getValue("for").equals("node")) {
				nodeDatatypeMap.put(atts.getValue("id"), atts.getValue("attr.type"));
			}
			else if (atts.getValue("for").equals("edge")) {
				edgeDatatypeMap.put(atts.getValue("id"), atts.getValue("attr.type"));
			}
		}
		else if (qName.equals("node")) {
			currentObjectTarget = "node";
			currentAttributeID = atts.getValue("id");
			CyNode node = Cytoscape.getCyNode(currentAttributeID, true);
			nodeList.add(node);
			nodeidMap.put(currentAttributeID, node);
		}
		else if (qName.equals("edge")) {
			currentObjectTarget = "edge";
			currentEdgeSource = atts.getValue("source");
			currentEdgeTarget = atts.getValue("target");
			CyNode sourceNode = nodeidMap.get(currentEdgeSource);
			CyNode targetNode = nodeidMap.get(currentEdgeTarget);
		}
		else if (qName.equals("data")) {
			currentAttributeName = atts.getValue("key");
		}
	}
	
	public void characters(char[] ch, int start, int length) {
		currentAttributeData = new String(ch, start, length);
		if (currentObjectTarget.equals("node")){
			if (currentAttributeType.equals("string")) {
				nodeAttributes.setAttribute(currentAttributeID, currentAttributeName, currentAttributeData);	
			}
			else if (currentAttributeType.equals("double")) {
				nodeAttributes.setAttribute(currentAttributeID, currentAttributeName, Double.parseDouble(currentAttributeData));
			}
		}
		else if (currentObjectTarget.equals("edge")) {
			if (currentAttributeType.equals("string")) {
				
			}
		}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		currentObjectTarget = null;
	}
	
}
