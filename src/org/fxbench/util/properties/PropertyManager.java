package org.fxbench.util.properties;

import java.awt.Color;
import java.awt.Font;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import org.fxbench.BenchApp;
import org.fxbench.entity.TOffer;
import org.fxbench.entity.TPriceBar.Interval;
import org.fxbench.util.ResourceManager;
import org.fxbench.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Viorel
 */
public class PropertyManager extends Observable
{    
    private static PropertyManager INSTANCE;
    
    private ResourceManager resManager;
    protected PropertySheet serverPropSheet;
    protected List<PropertySheet> connectionPropSheets;
    protected PropertySheet proxyPropSheet;
    protected Map<String, PropertySheet> appPropSheets;
    protected Map<String, PropertySheet> panelPropSheets;
    protected Map<String, PropertySheet> dialogPropSheets;	
//    protected PropertySheet priceBarPropheet;
    protected Map<String, List<PropertySheet>> chartPropSheets;	//<no+symbol+interval, list<PropertySheet>>
//    protected List<PropertySheet> overlayPropSheets;
//    protected List<PropertySheet> indicatorPropSheets;
    
    protected Map<String, PropertySheet> propSheetPool;	//<xpath, PropertySheet>
    protected Map<String, PropertySheet> defaultPropSheetPool;	//<xpath, PropertySheet>
    
    protected Document propDoc;
//    protected Element loginUserNode;
    protected boolean isDefault;

    public PropertyManager() {
    	try {
        	resManager = ResourceManager.getManager("org.fxbench.util.properties.resources.Resources");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(BenchApp.getInst().getMainFrame(),
                                          "Resource manager not created!",
                                          "fxBench",
                                          JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return;
        }
//    	ConnectionPropSheets = new ArrayList<PropertySheet>();
//    	panelPropSheets = new HashMap<String, PropertySheet>();
//    	overlayPropSheets = new ArrayList<PropertySheet>();
//    	indicatorPropSheets = new ArrayList<PropertySheet>();
    	propSheetPool = new HashMap<String, PropertySheet>();
    	defaultPropSheetPool = new HashMap<String, PropertySheet>();
    }
        
    public void loadFromXml(InputStream inputStream) {
    	propDoc = XMLUtil.loadXMLDocument(inputStream);
    	if (propDoc == null) {
    		propDoc = XMLUtil.newXMLDocument();
    		serverPropSheet = HostManager.getInstance().getServerPropSheetsA(BenchApp.getHost());	    	 
    		connectionPropSheets = HostManager.getInstance().getConnectionPropSheetsA(BenchApp.getHost());
	    	proxyPropSheet = HostManager.getInstance().getProxyPropSheetsA(BenchApp.getHost());
    	} else {
    		loadHostProperties();
    	}
    	setPropSheetPool();
    	setDefaultPropSheetPool();
    	isDefault = true;
    }
    
    public void loadLoginUserProperties(String loginUser) {
    	Element loginUserNode = XMLUtil.getRoot(getRoot(), PropertySheet.ATTR_TITLE, loginUser);		
		if (loginUserNode != null) {
			loadPreferenceProperties(loginUserNode);
			loadChartProperties(loginUserNode);	
		}
		if (appPropSheets == null || appPropSheets.size() == 0) {
			appPropSheets = TemplateManager.getInstance().getAppPropSheetsA();
		}
		if (panelPropSheets == null || panelPropSheets.size() == 0) {
			panelPropSheets = TemplateManager.getInstance().getPanelPropSheetsA();
		}
		if (dialogPropSheets == null || dialogPropSheets.size() == 0) {
			dialogPropSheets = TemplateManager.getInstance().getDialogPropSheetsA();
		}
		if (chartPropSheets == null) {
			chartPropSheets = new HashMap<String, List<PropertySheet>>();
		}
		if (chartPropSheets.size() == 0) {
			if (BenchApp.getInst().getTradeDesk().getOffers().size() > 0) {
				TOffer offer = (TOffer)BenchApp.getInst().getTradeDesk().getOffers().get(0);
				newChartProperty(offer.getSymbol(), Interval.m5.toString());
			}
		}
		
		setPropSheetPool();
    }
    
    public ResourceManager getResourceManager() {
    	return resManager;
    }
    
    private Element getRoot() {
    	return XMLUtil.getRoot(propDoc);
    }
    
    private void setPropSheetPool() {
        if (serverPropSheet != null) {
        	propSheetPool.put(serverPropSheet.getPath(), serverPropSheet);
        }
        if (connectionPropSheets != null) {
        	for (PropertySheet propSheet : connectionPropSheets) {
        		propSheetPool.put(propSheet.getPath(), propSheet);
        	}
        }
        if (proxyPropSheet != null) {
        	propSheetPool.put(proxyPropSheet.getPath(), proxyPropSheet);
        }
        if (appPropSheets != null) {
        	for(Entry<String, PropertySheet> entry : appPropSheets.entrySet()){
        		propSheetPool.put(entry.getValue().getPath(), entry.getValue());
    		}
        }
        if (panelPropSheets != null) {
        	for(Entry<String, PropertySheet> entry : panelPropSheets.entrySet()){
        		propSheetPool.put(entry.getValue().getPath(), entry.getValue());
    		}
        }
        if (dialogPropSheets != null) {
        	for(Entry<String, PropertySheet> entry : dialogPropSheets.entrySet()){
        		propSheetPool.put(entry.getValue().getPath(), entry.getValue());
    		}
        }
    }
    
    private void setDefaultPropSheetPool() {
    	//host
    	PropertySheet serverPropSheet = HostManager.getInstance().getServerPropSheets(BenchApp.getHost());
    	defaultPropSheetPool.put(serverPropSheet.getPath(), serverPropSheet);
    	PropertySheet proxyPropSheet = HostManager.getInstance().getProxyPropSheets(BenchApp.getHost());
    	if (proxyPropSheet != null) {
    		defaultPropSheetPool.put(proxyPropSheet.getPath(), proxyPropSheet);
    	}
    	
    	//preference
    	Map<String, PropertySheet> appPropSheets = TemplateManager.getInstance().getAppPropSheets();
    	for(Entry<String, PropertySheet> entry : appPropSheets.entrySet()){
    		defaultPropSheetPool.put(entry.getValue().getPath(), entry.getValue());
		}  
    	Map<String, PropertySheet> panelPropSheets = TemplateManager.getInstance().getPanelPropSheets();
    	for(Entry<String, PropertySheet> entry : panelPropSheets.entrySet()){
    		defaultPropSheetPool.put(entry.getValue().getPath(), entry.getValue());
		}  
    	
    	//overlay
    	List<PropertySheet> overlayPropSheets = TemplateManager.getInstance().getOverlayPropSheets();
    	for (PropertySheet propSheet : overlayPropSheets) {
    		defaultPropSheetPool.put(propSheet.getPath() + propSheet.getTitle(), propSheet);
    	}
    	
    	//indicator
    	List<PropertySheet> indicatorPropSheets = TemplateManager.getInstance().getIndicatorPropSheets();
    	for (PropertySheet propSheet : indicatorPropSheets) {
    		defaultPropSheetPool.put(propSheet.getPath() + propSheet.getTitle(), propSheet);
    	}
    }
    
    public void saveToXml(OutputStream output, String userName) {
    	Element rootNode = getRoot();
    	if (rootNode == null) {
    		rootNode = propDoc.createElement(BenchApp.getHost());
    		rootNode.setAttribute(PropertySheet.ATTR_XPATH, PropertySheet.ATTR_XPATH_OUT);
    		propDoc.appendChild(rootNode);
    	}
    	
    	// host node
    	Element hostNode = XMLUtil.getRoot(rootNode, HostManager.HOST_NODE);
    	if (hostNode == null) {
    		rootNode.appendChild(makeHostNode());
    	} else {
    		rootNode.replaceChild(makeHostNode(), hostNode);
    	}
    	
    	// user node
    	if (userName != null && userName.length() > 0) {
    		Element newUserNode = propDoc.createElement(PropertySheet.USER_NODE);
    		newUserNode.setAttribute(PropertySheet.ATTR_TITLE, userName);
    		newUserNode.setAttribute(PropertySheet.ATTR_XPATH, PropertySheet.ATTR_XPATH_OUT);
	    	newUserNode.appendChild(makePreferencesNode());
//	    	newUserNode.appendChild(makePriceBarNode());
	    	newUserNode.appendChild(makeChartsNode());
	    	
	    	Element loginUserNode = XMLUtil.getRoot(rootNode, PropertySheet.ATTR_TITLE, userName);
    		if (loginUserNode == null) {
    			rootNode.appendChild(newUserNode);
        	} else {
    			rootNode.replaceChild(newUserNode, loginUserNode);
        	}
    	}
    	
    	XMLUtil.saveXMLDocument(propDoc, output);
    }
    
    private void loadHostProperties() {
    	Element hostNode = XMLUtil.getNode(getRoot(), HostManager.HOST_NODE);
    	if (hostNode != null) {
	    	serverPropSheet = PropertySheet.loadPropertySheet(hostNode, HostManager.SERVER_NODE);
	    	connectionPropSheets = PropertySheet.loadPropertySheets(
	    		XMLUtil.getNode(hostNode, HostManager.CONNECTION_NODE));	    
	    	proxyPropSheet = PropertySheet.loadPropertySheet(hostNode, HostManager.PROXY_NODE);
    	}
    }
    
    private Element makeHostNode() {
    	Element hostNode = propDoc.createElement(HostManager.HOST_NODE);
    	hostNode.setAttribute(PropertySheet.ATTR_TITLE, BenchApp.getHost());
    	
    	Element serverNode = serverPropSheet.toElement(propDoc);
    	Element connectionNode = propDoc.createElement(HostManager.CONNECTION_NODE);
    	for (PropertySheet prop : connectionPropSheets) {
    		connectionNode.appendChild(prop.toElement(propDoc));
    	}
    	serverNode.appendChild(connectionNode);
    	hostNode.appendChild(serverNode);
    	
    	if (proxyPropSheet != null) {
    		hostNode.appendChild(proxyPropSheet.toElement(propDoc));
    	}
    	return hostNode;
    }
    
    private void loadPreferenceProperties(Element loginUserNode) {
    	Element preferencesNode = XMLUtil.getNode(loginUserNode, TemplateManager.PREFERENCES_NODE);
    	if (preferencesNode != null) {
    		panelPropSheets = new HashMap<String, PropertySheet>();
    		dialogPropSheets = new HashMap<String, PropertySheet>();
    		appPropSheets = new HashMap<String, PropertySheet>();
    		Element[] prefChildList = XMLUtil.getChildNodes(preferencesNode);
    		for (int i = 0; i < prefChildList.length; i++) {
    			String tagName = prefChildList[i].getTagName();
    			if (tagName.equals(TemplateManager.PANELS_NODE)) {
    				List<PropertySheet> panelPropSheetList = PropertySheet.loadPropertySheets(prefChildList[i]);
    				for (PropertySheet propSheet : panelPropSheetList) {
    					panelPropSheets.put(propSheet.getName(), propSheet);
    				}			    		
    			} else if (tagName.equals(TemplateManager.DIALOGS_NODE)) {
    				List<PropertySheet> panelPropSheetList = PropertySheet.loadPropertySheets(prefChildList[i]);
    				for (PropertySheet propSheet : panelPropSheetList) {
    					dialogPropSheets.put(propSheet.getName(), propSheet);
    				}
    			} else {
    				appPropSheets.put(tagName, PropertySheet.valueOf(prefChildList[i]));
    			}
    		}
    	}
    }
    
    private Element makePreferencesNode() {
    	Element preferencesNode = propDoc.createElement(TemplateManager.PREFERENCES_NODE);
    	
    	for(Entry<String, PropertySheet> entry : appPropSheets.entrySet()){
    		preferencesNode.appendChild(entry.getValue().toElement(propDoc));
		}
    	
    	Element panelsNode = propDoc.createElement(TemplateManager.PANELS_NODE);
    	for(Entry<String, PropertySheet> entry : panelPropSheets.entrySet()){
    		panelsNode.appendChild(entry.getValue().toElement(propDoc));
		}    	
    	preferencesNode.appendChild(panelsNode);
    	
    	Element dialogsNode = propDoc.createElement(TemplateManager.DIALOGS_NODE);
    	for(Entry<String, PropertySheet> entry : dialogPropSheets.entrySet()){
    		dialogsNode.appendChild(entry.getValue().toElement(propDoc));
		}    	
    	preferencesNode.appendChild(dialogsNode);
    	
    	return preferencesNode;
    }
    
//    private void loadPriceBarProperty() {
//    	priceBarPropheet = PropertySheet.loadPropertySheet(loginUserNode, TemplateManager.PRICEBAR_NODE);
//    }
//    
//    private Element makePriceBarNode() {
//    	return priceBarPropheet.toElement(propDoc);
//    }
    
    private void loadChartProperties(Element loginUserNode) {
    	chartPropSheets = new HashMap<String, List<PropertySheet>>();
    	Element chartsNode = XMLUtil.getRoot(loginUserNode, TemplateManager.CHARTS_NODE);
    	Element[] chartNodeList = XMLUtil.getChildNodes(chartsNode);
		for (int i = 0; i < chartNodeList.length; i++) {
			List<PropertySheet> chart = new ArrayList<PropertySheet>();
			Element[] indicatorNodeList = XMLUtil.getChildNodes(chartNodeList[i]);
			for (int j = 0; j < indicatorNodeList.length; j++) {
				chart.add(PropertySheet.valueOf(indicatorNodeList[j]));
			}
			chartPropSheets.put(chartNodeList[i].getAttribute(PropertySheet.ATTR_TITLE), chart);
		}    		
    }
    
	private Element makeChartsNode() {
		Element chartsNode = null;
		if (chartPropSheets.size() > 0) {
			chartsNode = propDoc.createElement(TemplateManager.CHARTS_NODE);
			for(Entry<String, List<PropertySheet>> entry : chartPropSheets.entrySet()){
				Element chartNode = propDoc.createElement(TemplateManager.CHART_NODE);
				chartNode.setAttribute(PropertySheet.ATTR_TITLE, entry.getKey());
				for (PropertySheet propSheet : entry.getValue()) {
					chartNode.appendChild(propSheet.toElement(propDoc));
				}
				chartsNode.appendChild(chartNode);
			}
		}
		return chartsNode;
	}
	
	public ChartSchema newChartProperty(String symbol, String period) {
		List<PropertySheet> chartPropList = new ArrayList<PropertySheet>();
		chartPropList.add(TemplateManager.getInstance().getPriceBarPropSheetA());
		ChartSchema chartSchema = new ChartSchema(symbol, period);
		chartPropSheets.put(chartSchema.toString(), chartPropList);
		return chartSchema;
	}
    
//    private void loadOverlayProperties() {
//    	Element overlaysNode = XMLUtil.getNode(loginUserNode, TemplateManager.OVERLAYS_NODE);
//    	if (overlaysNode != null) {
//    		overlayPropSheets = PropertySheet.loadPropertySheets(overlaysNode, TemplateManager.OVERLAY_NODE);
//    	}
//    }
//    
//    private Element makeOverlaysNode() {
//    	Element overlaysNode = propDoc.createElement(TemplateManager.OVERLAYS_NODE);
//    	if (overlayPropSheets != null) {
//	    	for (PropertySheet prop : overlayPropSheets) {
//	    		overlaysNode.appendChild(prop.toElement(propDoc));
//	    	}
//    	}
//    	
//    	return overlaysNode;
//    }
    
//    private void loadIndicatorProperties() {
//    	Element indicatorsNode = XMLUtil.getNode(loginUserNode, TemplateManager.INDICATORS_NODE);
//    	if (indicatorsNode != null) {
//    		indicatorPropSheets = PropertySheet.loadPropertySheets(indicatorsNode, TemplateManager.INDICATOR_NODE);
//    	}
//    }
//    
//    private Element makeIndicatorsNode() {
//    	Element indicatorsNode = propDoc.createElement(TemplateManager.INDICATORS_NODE);
//    	if (indicatorPropSheets != null) {
//	    	for (PropertySheet prop : indicatorPropSheets) {
//	    		indicatorsNode.appendChild(prop.toElement(propDoc));
//	    	}
//    	}
//    	
//    	return indicatorsNode;
//    }
    
	public static PropertyManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new PropertyManager();
		}
		return INSTANCE;
	}

	public PropertySheet getServerPropSheet() {
		return serverPropSheet;
	}

	public void setServerPropSheet(PropertySheet serverPropSheet) {
		this.serverPropSheet = serverPropSheet;
	}

	public List<PropertySheet> getConnectionPropSheets() {
		return connectionPropSheets;
	}

	public void setConnectionPropSheets(List<PropertySheet> connectionPropSheets) {
		this.connectionPropSheets = connectionPropSheets;
	}

	public PropertySheet getProxyPropSheet() {
		return proxyPropSheet;
	}

	public void setProxyPropSheet(PropertySheet proxyPropSheet) {
		this.proxyPropSheet = proxyPropSheet;
	}
	
	public Map<String, PropertySheet> getAppPropSheets() {
		return appPropSheets;
	}
	
	public void setAppPropSheets(Map<String, PropertySheet> appPropSheets) {
		this.appPropSheets = appPropSheets;
	}
	
	public Map<String, PropertySheet> getPanelPropSheets() {
		return panelPropSheets;
	}

	public void setPanelPropSheets(Map<String, PropertySheet> panelPropSheets) {
		this.panelPropSheets = panelPropSheets;
	}
	
	public PropertySheet getHelpDialogPropSheet() {
		return dialogPropSheets.get(TemplateManager.DIALOG_HELP_NODE);
	}
	
	public void setDialogPropSheets(Map<String, PropertySheet> dialogPropSheets) {
		this.dialogPropSheets = dialogPropSheets;
	}
	
	public Map<String, List<PropertySheet>> getChartPropSheets() {
		return chartPropSheets;
	}
	
	public void setChartPropSheets(Map<String, List<PropertySheet>> chartPropSheets) {
		this.chartPropSheets = chartPropSheets;
	}

	public List<PropertySheet> getChartProperty(String chartSchema) {
		return chartPropSheets.get(chartSchema);
	}
	
	public ChartSchema replaceChartSchema(String chartSchema, String symbol, String interval) {
		List<PropertySheet> propSheetList = chartPropSheets.get(chartSchema);
		if (propSheetList != null) {
			chartPropSheets.remove(chartSchema);
			ChartSchema oldSchema = ChartSchema.valueOf(chartSchema);
			ChartSchema newSchema = new ChartSchema(oldSchema.getNo(), symbol, interval);
			chartPropSheets.put(newSchema.toString(), propSheetList);
			return newSchema;
		} else {
			return null;
		}
	}
	
	public void addChartPropSheet(String chartSchema, PropertySheet propSheet) {
		List<PropertySheet> propSheetList = chartPropSheets.get(chartSchema);
		if (propSheetList != null) {
			propSheetList.add(propSheet);
		}
	}
	
	public void removeChartPropSheet(String chartSchema) {
		chartPropSheets.remove(chartSchema);
	}
	
	public void removeChartPropSheet(String chartSchema, PropertySheet propSheet) {
		List<PropertySheet> propSheetList = chartPropSheets.get(chartSchema);
		if (propSheetList != null) {
			propSheetList.remove(propSheet);
		}
	}

	public PropertySheet getPropSheet(String xpath) {
		return propSheetPool.get(xpath);
	}
	
	public PropertySheet getDefaultPropSheet(String xpath) {
		return defaultPropSheetPool.get(xpath);
	}
	
	public PropertySheet getDefaultPropSheet(PropertySheet propSheet) {
		String xpath = propSheet.getPath();
		if (propSheet.getName().equals(TemplateManager.OVERLAY_NODE) ||
			propSheet.getName().equals(TemplateManager.INDICATOR_NODE)) {
			xpath += propSheet.getTitle();
		}
		return defaultPropSheetPool.get(xpath);
	}
	
//	public void resetPanelPropSheets() {
//		panelPropSheets = TemplateManager.getInstance().getPanelPropSheetsA();
//		for(Entry<String, PropertySheet> entry : panelPropSheets.entrySet()){
//    		propSheetPool.put(entry.getValue().getPath(), entry.getValue());
//		}
//	}
	
	public Object getVal(String propertyPath, Object oldValue) {
		if (oldValue instanceof String) {
            return getStrVal(propertyPath);
        } else if (oldValue instanceof Integer) {
            return getIntVal(propertyPath);
        } else if (oldValue instanceof Double) {
            return getDblVal(propertyPath);
        } else if (oldValue instanceof Float) {
            return getFloatVal(propertyPath);
        } else if (oldValue instanceof Boolean) {
            return getBoolVal(propertyPath);
        } else if (oldValue instanceof Date) {
            return getDateVal(propertyPath);
        } else if (oldValue instanceof Color) {
            return getColorVal(propertyPath);
        } else if (oldValue instanceof Font) {
            return getFontVal(propertyPath);
        } else {
        	return oldValue;
        }
	}
	
	public Object getVal(String propertyPath) {
		PathSplit pathSplit = new PathSplit(propertyPath);
		PropertySheet propertySheet = getPropSheet(pathSplit.getXPath());
		return propertySheet == null ? null : propertySheet.getVal(pathSplit.getPropName());
	}
	
	public String getStrVal(String propertyPath) {
		PathSplit pathSplit = new PathSplit(propertyPath);
		PropertySheet propertySheet = getPropSheet(pathSplit.getXPath());
		return propertySheet == null ? null : propertySheet.getStrVal(pathSplit.getPropName());
	}
	
	public int getIntVal(String propertyPath) {
		PathSplit pathSplit = new PathSplit(propertyPath);
		PropertySheet propertySheet = getPropSheet(pathSplit.getXPath());
		return propertySheet == null ? 0 : propertySheet.getIntVal(pathSplit.getPropName());
	}
	
	public double getDblVal(String propertyPath) {
		PathSplit pathSplit = new PathSplit(propertyPath);
		PropertySheet propertySheet = getPropSheet(pathSplit.getXPath());
		return propertySheet == null ? 0 : propertySheet.getDblVal(pathSplit.getPropName());
	}
	
	public float getFloatVal(String propertyPath) {
		PathSplit pathSplit = new PathSplit(propertyPath);
		PropertySheet propertySheet = getPropSheet(pathSplit.getXPath());
		return propertySheet == null ? 0 : propertySheet.getFloatVal(pathSplit.getPropName());
	}
	
	public boolean getBoolVal(String propertyPath) {
		PathSplit pathSplit = new PathSplit(propertyPath);
		PropertySheet propertySheet = getPropSheet(pathSplit.getXPath());
		return propertySheet == null ? false : propertySheet.getBoolVal(pathSplit.getPropName());
	}
	
	public Date getDateVal(String propertyPath) {
		PathSplit pathSplit = new PathSplit(propertyPath);
		PropertySheet propertySheet = getPropSheet(pathSplit.getXPath());
		return propertySheet == null ? null : propertySheet.getDateVal(pathSplit.getPropName());
	}
	
	public Color getColorVal(String propertyPath) {
		PathSplit pathSplit = new PathSplit(propertyPath);
		PropertySheet propertySheet = getPropSheet(pathSplit.getXPath());
		return propertySheet == null ? null : propertySheet.getColorVal(pathSplit.getPropName());
	}
	
	public Font getFontVal(String propertyPath) {
		PathSplit pathSplit = new PathSplit(propertyPath);
		PropertySheet propertySheet = getPropSheet(pathSplit.getXPath());
		return propertySheet == null ? null : propertySheet.getFontVal(pathSplit.getPropName());
	}
	
	public void setProperty(String propertyPath, String val) {
		PathSplit pathSplit = new PathSplit(propertyPath);
		PropertySheet propertySheet = getPropSheet(pathSplit.getXPath());
		if (propertySheet != null) {
			propertySheet.setProperty(pathSplit.getPropName(), val);
			notifyObservers(propertyPath);
			isDefault = false;
		}
	}
	
	public void setProperty(String propertyPath, int val) {
		PathSplit pathSplit = new PathSplit(propertyPath);
		PropertySheet propertySheet = getPropSheet(pathSplit.getXPath());
		if (propertySheet != null) {
			propertySheet.setProperty(pathSplit.getPropName(), Integer.valueOf(val));
			notifyObservers(propertyPath);
			isDefault = false;
		}
	}
	
	public void setProperty(String propertyPath, double val) {
		PathSplit pathSplit = new PathSplit(propertyPath);
		PropertySheet propertySheet = getPropSheet(pathSplit.getXPath());
		if (propertySheet != null) {
			propertySheet.setProperty(pathSplit.getPropName(), Double.valueOf(val));
			notifyObservers(propertyPath);
			isDefault = false;
		}
	}
	
	public void setProperty(String propertyPath, float val) {
		PathSplit pathSplit = new PathSplit(propertyPath);
		PropertySheet propertySheet = getPropSheet(pathSplit.getXPath());
		if (propertySheet != null) {
			propertySheet.setProperty(pathSplit.getPropName(), Float.valueOf(val));
			notifyObservers(propertyPath);
			isDefault = false;
		}
	}
	
	public void setProperty(String propertyPath, boolean val) {
		PathSplit pathSplit = new PathSplit(propertyPath);
		PropertySheet propertySheet = getPropSheet(pathSplit.getXPath());
		if (propertySheet != null) {
			propertySheet.setProperty(pathSplit.getPropName(), Boolean.valueOf(val));
			notifyObservers(propertyPath);
			isDefault = false;
		}
	}
	
	public void setProperty(String propertyPath, Date val) {
		PathSplit pathSplit = new PathSplit(propertyPath);
		PropertySheet propertySheet = getPropSheet(pathSplit.getXPath());
		if (propertySheet != null) {
			propertySheet.setProperty(pathSplit.getPropName(), val);
			notifyObservers(propertyPath);
			isDefault = false;
		}
	}
	
	public void setProperty(String propertyPath, Color val) {
		PathSplit pathSplit = new PathSplit(propertyPath);
		PropertySheet propertySheet = getPropSheet(pathSplit.getXPath());
		if (propertySheet != null) {
			propertySheet.setProperty(pathSplit.getPropName(), val);
			notifyObservers(propertyPath);
			isDefault = false;
		}
	}
	
	public void setProperty(String propertyPath, Font val) {
		PathSplit pathSplit = new PathSplit(propertyPath);
		PropertySheet propertySheet = getPropSheet(pathSplit.getXPath());
		if (propertySheet != null) {
			propertySheet.setProperty(pathSplit.getPropName(), val);
			notifyObservers(propertyPath);
			isDefault = false;
		}
	}
	
	public void setProperty(String propertyPath, Object val) {
		PathSplit pathSplit = new PathSplit(propertyPath);
		PropertySheet propertySheet = getPropSheet(pathSplit.getXPath());
		if (propertySheet != null) {
			propertySheet.setProperty(pathSplit.getPropName(), val);
			notifyObservers(propertyPath);
			isDefault = false;
		}
	}
	
	public boolean isDefault() {
        return isDefault;
    }
	public void resetToDefault() {
		isDefault = true;
    }
	
//	public Element getNode(String nodeName) {
//		if (loginUserNode == null) {
//			return null;
//		} else {
//			return XMLUtil.getNode(loginUserNode, nodeName);
//		}
//	}
	
//	public void preferencesUpdated(Vector changings) {
//        for (Object change : changings) {
//            PrefProperty property = (PrefProperty) change;
//            setProperty(property.getPropertyID(), property.getValue());
//        }
//    }
	
	private class PathSplit {
		private String xpath;
		private String propName;
		public PathSplit(String propertyPath) {
			//example:  propertyPath:host.server.mode.demo 
			//  ==>  xpath:host.server.mode   propName:demo
			int pos = propertyPath.lastIndexOf('.');
			if (pos >= 0) {
				xpath = propertyPath.substring(0, pos);
				propName = propertyPath.substring(pos + 1);
			}
		}
		public String getXPath() {
			return xpath;
		}
		public String getPropName() {
			return propName;
		}
	}
}
