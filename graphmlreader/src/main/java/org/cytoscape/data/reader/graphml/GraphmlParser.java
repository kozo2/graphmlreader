/*
 * @author kozo.nishida
 */

package org.cytoscape.data.reader.graphml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.generated.Network;
import cytoscape.logger.CyLogger;

//interface Handler {
//	public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException;
//}

enum Tokens {
	NONE("none"),
	ID("id"),
	GRAPH("graph"),
	DIRECTED("directed"),
	UNDIRECTED("undirected"),
	KEY("key"),
	FOR("for"),
	ALL("all"),
	ATTRNAME("attr.name"),
	ATTRTYPE("attr.type"),
	DEFAULT("default"),
	NODE("node"),
	EDGE("edge"),
	SOURCE("source"),
	TARGET("target"),
	DATA("data"),
	TYPE("type");
	
	private String name;
	private Tokens(String str) { name = str; }
	public String toString() { return name; }
};

enum ObjectType {
	INT("int"),
	INTEGER("integer"),
	LONG("long"),
	FLOAT("float"),
	DOUBLE("double"),
	REAL("real"),
	BOOLEAN("boolean"),
	STRING("string"),
	DATE("date");
	
	private String name;
	private ObjectType (String s) { name = s; }
	public String toString() { return name; }
};

public class GraphmlParser extends DefaultHandler {
	private static CyLogger logger = CyLogger.getLogger(GraphmlParser.class);
	
	private String networkName = null;
	
	/* Internal lists of the created nodes and edges */
	private List<CyNode> nodeList = null;
	private List<CyEdge> edgeList = null;
	
	/* Map of XML ID's to nodes */
	private HashMap<String,CyNode> idMap = null;
	
	private CyNode currentNode = null;
	private CyEdge currentEdge = null;
	
	/* Attribute values */
	private String currentAttributeID = null;
	private CyAttributes currentAttributes = null;
	private String objectTarget = null;
	
	/* Edge handle list */
	private List<String> handleList = null;

	
	/********************************************************************
	 * Routines to handle keys
	 *******************************************************************/
	

	
	/**
	 * Main constructor for our parser. Initialize any local arrays. Note that this
	 * parser is designed to be as memory efficient as possible. As a result, a minimum
	 * number of local data structures
	 */
	GraphmlParser() {
		nodeList = new ArrayList<CyNode>();
		edgeList = new ArrayList<CyEdge>();
		idMap = new HashMap();
		datatypeMap = new HashMap();
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
	
	/**
	 * startElement is callled whenever the SAX parser sees a start tag. We
	 * user this as the way to fire our state table.
	 * 
	 * @param namespace the URL of the namespace (full spec)
	 * @param localName the tag itself, stripped of all namespace stuff
	 * @param qName the tag with the namespace prefix
	 * @param atts the Attributes list from the tag
	 */
	
	public void startElement(String namespace, String localName, String qName, Attributes atts) throws SAXException {
		ParseState nextState = handleState(startParseTable, parseState, localName, atts);
		
		stateStack.push(parse)
	}
	
	
	/********************************************************************
	 * Element handling routines.  The following routines are the methods
	 * called by the state mechine.
	 *******************************************************************/
	
	class HandleNode implements Handler {
		public ParseState handle(String tag, Attributes atts, ParseState current)
				throws SAXException {
			String id = atts.getValue("id");
			currentNode = createUniqueNode(id);
			return current;
		}
	}
	
//	class HandleNodeAttribute implements Handler {
//		public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {
//			if (atts == null) {
//				return current;
//			}
//			attState = current;
//			String name = atts.get
//		}
//	}
	
	/********************************************************************
	 * Utility routines.  The following routines are utilities that are
	 * used for internal purposes.
	 *******************************************************************/	
	
//	private ParseState handleAttribute(Attributes atts, CyAttributes cyAtts, String id) throws SAXException {
//		String name = atts.getValue("")
//	}
	
	private CyNode createUniqueNode (String id) throws SAXException {
		CyNode node = Cytoscape.getCyNode(id, true);
		nodeList.add(node);
		idMap.put(id, node);
		return node;
	}
	
}
