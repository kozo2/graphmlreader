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
	public void testRead() throws Exception {
		GraphMLReader reader1 = new GraphMLReader("src/test/resources/testGraph1.xml");
		
		assertNotNull(reader1);
		
		reader1.read();
		assertEquals(11, reader1.getNodeIndicesArray().length);
		assertEquals(12, reader1.getEdgeIndicesArray().length);
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
