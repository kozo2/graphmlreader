package org.cytoscape.data.reader.graphml;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
		
		assertEquals(2, reader.getNodeIndicesArray().length);
		assertEquals(1, reader.getEdgeIndicesArray().length);
	}
	
	@Test
	public void testGetNodeIndicesArray() {
	}

	@Test
	public void testGraphMLReaderString() {
	}

	@Test
	public void testGraphMLReaderURL() {
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
	public void testGetEdgeIndiceArray() {
	}

	@Test
	public void testGetNetworkName() {
	}

}
