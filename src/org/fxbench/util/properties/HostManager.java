package org.fxbench.util.properties;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fxbench.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Viorel
 */
public class HostManager
{    
    public static final String HOSTS_NODE	= "hosts";
    public static final String HOST_NODE	= "host";
    public static final String SERVER_NODE	= "server";
    public static final String CONNECTION_NODE	= "connection";
    public static final String PROXY_NODE	= "proxy";
    private static final String XML_PATH = "org/fxbench/util/properties/template/";
    private static final String XML_NAME = "hosts.xml";
    private static HostManager INSTANCE;
    
	private String[] hosts;	//HostName list
	private String connectedHostName;	//name of connected host
	private Map<String, PropertySheet> serverPropSheets;	//<HostName, ServerPropertySheet>
	private Map<String, List<PropertySheet>> connectionPropSheets; //<HostName, ServerModePropertySheetList>
	private Map<String, PropertySheet> proxyPropSheets; //<HostName, ProxyPropertySheet>
	
    public HostManager() {
//    	connectedHostName = System.getProperty(CONNECTED_HOST_NAME);
    	serverPropSheets = new HashMap<String, PropertySheet>();
    	connectionPropSheets = new HashMap<String, List<PropertySheet>>();
    	proxyPropSheets = new HashMap<String, PropertySheet>();
    	loadFromXml();
    }
    
    public void loadFromXml() {
    	ClassLoader classLoader = HostManager.class.getClassLoader();
        InputStream inputStream = null;
        
        try {
	        inputStream = classLoader.getResourceAsStream(XML_PATH + XML_NAME);
	        if (inputStream != null) {
	        	Document doc = XMLUtil.loadXMLDocument(inputStream);
	        	Element hostsNode = XMLUtil.getNode(doc, HOSTS_NODE);
	        	Element[] hostList = XMLUtil.getChildNodes(hostsNode);
	        	if (hostList.length > 0) {
		        	hosts = new String[hostList.length];
		        	for (int i=0; i < hostList.length; i++) {
		        	    hosts[i] = hostList[i].getAttribute(PropertySheet.ATTR_TITLE);
		        	    
		        	    PropertySheet serverProperty = PropertySheet.loadPropertySheet(hostList[i], SERVER_NODE);
		        	    if (serverProperty == null) {
		        	    	continue;
		        	    }
		        	    serverPropSheets.put(hosts[i], serverProperty);
		        	    
		        	    List<PropertySheet> modePropList = PropertySheet.loadPropertySheets(XMLUtil.getNode(hostList[i], CONNECTION_NODE));
		        	    if (modePropList.size() == 0) {
		        	    	continue;
		        	    }
		        	    connectionPropSheets.put(hosts[i], modePropList);
		        	    
		        	    PropertySheet proxyProperty = PropertySheet.loadPropertySheet(hostList[i], PROXY_NODE);
		        	    if (proxyProperty != null) {
		        	    	proxyPropSheets.put(hosts[i], proxyProperty);
		        	    }
		        	}
	        	}
	        }

        } catch (Exception ex) {
        }
    }
    
//    private void delPathPrefix() {
//    	String prefix = HOSTS_NODE + ".";
//    	for(Entry<String, PropertySheet> entry : serverPropSheets.entrySet()){
//    		entry.getValue().getPath().replaceFirst(prefix, "");
//		}
//    }
    
	public static HostManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new HostManager();
		}
		return INSTANCE;
	}

	public String[] getHosts() {
		return hosts;
	}

	public String getConnectedHostName() {
		return connectedHostName;
	}
	
	public void setConnectedHostName(String hostName) {
		this.connectedHostName = hostName;
//		System.setProperty(CONNECTED_HOST_NAME, hostName);
	}
	
	public PropertySheet getServerPropSheets(String hostName) {
		if (hostName == null || hostName.length() == 0) {
			return serverPropSheets.get(connectedHostName);
		} else {
			return serverPropSheets.get(hostName);
		}
	}
	
	public PropertySheet getServerPropSheetsA(String hostName) {
		PropertySheet propertySheet = getServerPropSheets(hostName);
		return propertySheet == null ? null : propertySheet.clone();
	}

	public List<PropertySheet> getConnectionPropSheets(String hostName) {
		if (hostName == null || hostName.length() == 0) {
			return connectionPropSheets.get(connectedHostName);
		} else {
			return connectionPropSheets.get(hostName);
		}
	}
	
	public List<PropertySheet> getConnectionPropSheetsA(String hostName) {
		List<PropertySheet> propertySheetList = getConnectionPropSheets(hostName);
		return propertySheetList == null ? null : TemplateManager.cloneArrayList(propertySheetList);
	}

	public PropertySheet getProxyPropSheets(String hostName) {
		if (hostName == null || hostName.length() == 0) {
			return proxyPropSheets.get(connectedHostName);
		} else {
			return proxyPropSheets.get(hostName);
		}
	}
	
	public PropertySheet getProxyPropSheetsA(String hostName) {
		PropertySheet propertySheet = getProxyPropSheets(hostName);
		return propertySheet == null ? null : propertySheet.clone();
	}
}
