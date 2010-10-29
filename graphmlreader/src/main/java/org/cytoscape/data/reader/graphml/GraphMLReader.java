/*
 * @author kozo.nishida
 */

package org.cytoscape.data.reader.graphml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.ParserAdapter;

import cytoscape.CytoscapeInit;
import cytoscape.data.readers.AbstractGraphReader;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;
import cytoscape.util.FileUtil;
import cytoscape.util.PercentUtil;

public class GraphMLReader extends AbstractGraphReader
{
	private URL targetURL;
	
	private int[] nodeIdx;
	private int[] edgeIdx;
	
	// GraphML file name to be loaded.
	private String networkName = null;
	private InputStream networkStream;
	private GraphMLParser parser;
	
	private Properties prop = CytoscapeInit.getProperties();
	private String vsbSwitch = prop.getProperty("visualStyleBuilder");
	
	// For exception handling
	private TaskMonitor taskMonitor;
	private PercentUtil percentUtil;
	private CyLogger logger = null;
	
	/**
	 * Constructor.<br>
	 * This is for local GraphML file.
	 * 
	 * @param fineName File name of local GraphML file.
	 * @throws FileNotFoundException
	 * 
	 */
	public GraphMLReader(final String fileName) {
		super(fileName);
		System.out.println("Debug: GraphML File name = " + fileName);
		try {
			this.targetURL = (new File(fileName)).toURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public GraphMLReader(final URL url) {
		super(url.toString());
		System.out.println("Debug: KGML URL name = " + fileName);
		this.targetURL = url;
	}
	
	/**
	 * Constructor.<br>
	 * This is usually used for remote file loading.
	 * 
	 * @param is 
	 *            Input stream of GraphML file,
	 *            
	 */
	public GraphMLReader(InputStream is) {
		super("InputStream");
		this.networkStream = is;
		initialize();
	}
	
	/**
	 * Constructor.<br>
	 * This is usually used for remote file loading.
	 * 
	 * @param is
	 *            Input stream of GraphML file,
	 *
	 */
	public GraphMLReader(InputStream is, String name) {
		super(name);
		
		this.networkStream = is;
		initialize();
	}
	
    /**
     * Creates a new GraphMLReader object.
     * 
     * @param fileName  File name of local GraphML file.
     * @param monitor DOCUMENT ME!
     */
    public GraphMLReader(final String fileName, final TaskMonitor monitor) {
		super(fileName);
		this.taskMonitor = monitor;
		percentUtil = new PercentUtil(3);
		networkStream = FileUtil.getInputStream(fileName, monitor);
		initialize();
	}
    
    /**
     * Sets the task monitor we want to use
     * 
     * @param monitor the TaskMonitor to use
     */
    public void setTaskMonitor(TaskMonitor monitor) {
    	this.taskMonitor = monitor;
    	percentUtil = new PercentUtil(3);
    }
    
    private void initialize() {
    	logger = CyLogger.getLogger(GraphMLReader.class);
    }
    
    /**
     *  DOCUMENT ME!
     *  
     * @throws IOException DOCUMENT ME!
     */
    public void read() throws IOException {
    	try {
    		this.readGraphml();
    	} catch (SAXException e) {
    		if (taskMonitor != null) {
				taskMonitor.setException(e, e.getMessage());
			}
			throw new IOException(e.getMessage());
		}
    }
    
    /**
     * Actual method to read GraphML documents.
     * 
     * @throws IOException
     * @throws SAXException
     */
    private void readGraphml() throws SAXException, IOException {
    	try {
			try {
				try {
					/*
					 * Read the file and map the entire XML document into data
					 * structure.
					 */
					if (taskMonitor != null) {
						taskMonitor.setPercentCompleted(-1);
						taskMonitor.setStatus("Reading GraphML data...");
					}
					
					//Get out parser
					SAXParserFactory spf = SAXParserFactory.newInstance();
					SAXParser sp = spf.newSAXParser();
					ParserAdapter pa = new ParserAdapter(sp.getParser());
					parser = new GraphMLParser();

//					pa.setContentHandler(parser);
//					pa.setErrorHandler(parser);
//					pa.parse(new InputSource(networkStream));
					
					
				} catch (Exception e) {
					// TODO: handle exception
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
    }
    
    public int[] getEdgeIndiceArray() {
    	return edgeIdx;
    }
    
    public String getNetworkName() {
    	return networkName;
    }
    
    public int[] getNodeIndicesArray() {
    	return nodeIdx;
    }
    


}
