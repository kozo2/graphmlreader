package org.cytoscape.data.reader.graphml;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

public class GraphMLReaderTest {
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRead1() throws Exception {
		GraphMLReader reader = new GraphMLReader("src/test/resources/testGraph1.xml");
		
		assertNotNull(reader);
		
		reader.read();
		assertEquals(11, reader.getNodeIndicesArray().length);
		assertEquals(12, reader.getEdgeIndicesArray().length);
	}
	
	@Test
	public void testRead2() throws Exception {
		GraphMLReader reader = new GraphMLReader("src/test/resources/simpleWithAttributes.xml");
		
		assertNotNull(reader);
		
		reader.read();
		assertEquals(6, reader.getNodeIndicesArray().length);
		assertEquals(7, reader.getEdgeIndicesArray().length);
	}

	@Test
	public void testRead3() throws Exception {
		GraphMLReader reader = new GraphMLReader("src/test/resources/atted.graphml");
		assertNotNull(reader);
		reader.read();
		
		//CyNetwork net = Cytoscape.createNetwork("dummy");
		CyAttributes nodeAttr = Cytoscape.getNodeAttributes();
		CyAttributes edgeAttr = Cytoscape.getEdgeAttributes();
		
		assertEquals("AtbZIP52", nodeAttr.getAttribute("At1g06850", "symbol"));
		assertEquals("bZIP", nodeAttr.getAttribute("At1g06850", "TF_family"));
		
		assertEquals("correlation", edgeAttr.getAttribute("At5g48880 (pp) At1g65060", "label"));
		assertEquals(5.20, edgeAttr.getAttribute("At5g48880 (pp) At1g65060", "mr_all"));
	}
	
	@Test
	public void testGetNodeIndicesArray() throws Exception{
		GraphMLReader reader = new GraphMLReader("src/test/resources/atted.graphml");
		assertNotNull(reader);
		reader.read();
		assertEquals(41, reader.getNodeIndicesArray().length);
	}

	@Test
	public void testGraphMLReaderString() throws Exception{
		GraphMLReader reader = new GraphMLReader("src/test/resources/atted.graphml");
		assertNotNull(reader);
	}

	@Test
	public void testGraphMLReaderURL() throws Exception {
		GraphMLReader reader = new GraphMLReader("http://graphmlreader.googlecode.com/svn/trunk/graphmlreader/src/test/resources/atted.graphml");
		assertNotNull(reader);
	}

	@Test
	public void testGraphMLReaderInputStream() {
	}

	@Test
	public void testGraphMLReaderInputStreamString() {
	}

	@Test
	public void testGraphMLReaderStringTaskMonitor() {
	}

	@Test
	public void testSetTaskMonitorTaskMonitor() {
	}

	@Test
	public void testGetEdgeIndiceArray() throws Exception {
		GraphMLReader reader = new GraphMLReader("src/test/resources/atted.graphml");
		assertNotNull(reader);
		reader.read();
		assertEquals(118, reader.getEdgeIndicesArray().length);
	}

	@Test
	public void testGetNetworkName() throws Exception {
		GraphMLReader reader = new GraphMLReader("src/test/resources/atted.graphml");
		assertNotNull(reader);
		reader.read();
		assertEquals("1107222336-129-07298_NetworkDrawer", reader.getNetworkName());
	}

}
