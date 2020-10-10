package org.fxbench.util.properties;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fxbench.util.Utils;
import org.fxbench.util.XMLUtil;
import org.fxbench.util.properties.type.BooleanPropertyType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author joshua.taylor
 */
public class PropertySheet implements Cloneable
{
	public static final String PROPERTY_NODE = "property";
	public static final String USER_NODE = "user";
	public static final String ATTR_TITLE = "title";
	public static final String ATTR_LABEL = "label";
	public static final String ATTR_TYPE = "type";
	public static final String ATTR_VALUE = "value";
	public static final String ATTR_DATA = "data";
	public static final String ATTR_NAME = "name";
	public static final String ATTR_VISIBLE = "visible";
	public static final String ATTR_XPATH = "xpath";
	public static final String ATTR_XPATH_IN = "in";
	public static final String ATTR_XPATH_OUT = "out";
	public static final String DELIM = ",";
	
    private String name;
    private String title;
    private String xpath;
    private boolean visible;
    private Map<String, Property> propertyMap;
    private List<Property> propertyList;
    
    public PropertySheet() {
    	propertyMap = new HashMap<String, Property>();
    	propertyList = new ArrayList<Property>();
    }
    
    public PropertySheet(String name, String title) {
    	this.name = name;
    	this.title = title;
    	propertyMap = new HashMap<String, Property>();
    	propertyList = new ArrayList<Property>();
    }
    
    public int size() {
    	return propertyList.size();
    }

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPath() {
		return xpath;
	}

	public void setPath(String path) {
		this.xpath = path;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public Map<String, Property> getPropertyMap() {
		return propertyMap;
	}

	public void setPropertyMap(Map<String, Property> propertyMap) {
		this.propertyMap = propertyMap;
	}
	
	public List<Property> getPropertyList() {
		return propertyList;
	}

	public void setPropertyList(List<Property> propertyList) {
		this.propertyList = propertyList;
	}
	
	public Property getProperty(String id) {
		return propertyMap.get(id);
	}
	
	public Property getProperty(int index) {
		return index >= 0 && index < propertyList.size() ? propertyList.get(index) : null; 
	}
	
	public Object getVal(String id) {
		Property prop = getProperty(id);
		return prop == null ? null : prop.getValue();
	}
	
	public String getStrVal(String id) {
		Property prop = getProperty(id);
		return prop == null ? null : prop.strValue();
	}
	
	public int getIntVal(String id) {
		Property prop = getProperty(id);
		return prop == null ? 0 : prop.intValue();
	}
	
	public double getDblVal(String id) {
		Property prop = getProperty(id);
		return prop == null ? 0 : prop.dblValue();
	}
	
	public float getFloatVal(String id) {
		Property prop = getProperty(id);
		return prop == null ? 0 : prop.floatValue();
	}
	
	public boolean getBoolVal(String id) {
		Property prop = getProperty(id);
		return prop == null ? false : prop.boolValue();
	}
	
	public Date getDateVal(String id) {
		Property prop = getProperty(id);
		return prop == null ? null : prop.dateValue();
	}
	
	public Color getColorVal(String id) {
		Property prop = getProperty(id);
		return prop == null ? null : prop.colorValue();
	}
	
	public Font getFontVal(String id) {
		Property prop = getProperty(id);
		return prop == null ? null : prop.fontValue();
	}
	
	public void addProperty(Property prop) {
		propertyMap.put(prop.getId(), prop);
		propertyList.add(prop);
	}
	
	public void setProperty(Property prop) {
		propertyMap.put(prop.getId(), prop);
		int index = indexOf(prop);
		if (index >= 0) {
			propertyList.set(index, prop);
		}
	}
	
	public void setProperty(String id, Object val) {
		Property prop = getProperty(id);
		if (prop != null) {
			prop.setValue(val);
		}
	}
	
	public void removeProperty(String id, Property prop) {
		propertyMap.remove(id);
		int index = indexOf(prop);
		if (index >= 0) {
			propertyList.remove(index);
		}
	}
	
	public int indexOf(Property prop) {
    	int index = -1;
    	for (int i = 0; i < propertyList.size(); i++) {
        	if (propertyList.get(i).getId().equals(prop.getId())){
        		index = i;
        		break;
        	}
        }
        return index;
    }
	
	public void copyValue(PropertySheet srcPropSheet) {
		if (srcPropSheet == null) {
			return;
		}
//		for (Property prop : propertyList) {
//			Property src = srcPropSheet.getProperty(prop.getId());
//			if (src != null) {
//				prop.setValue(src.getValue());
//			}
//		}		
		for (int i = 0; i < propertyList.size(); i++) {
			propertyList.get(i).setValue(srcPropSheet.getProperty(i).getValue());
		}
	}
	
	public Element toElement(Document document) {
		Element sheetTag = document.createElement(name);
		sheetTag.setAttribute(ATTR_TITLE, title);
		sheetTag.setAttribute(ATTR_VISIBLE, BooleanPropertyType.toString(visible));
		for (Property prop : propertyList) {
			Element propTag = document.createElement(PROPERTY_NODE);
			propTag.setAttribute(ATTR_LABEL, prop.getLabel());
			propTag.setAttribute(ATTR_TYPE, prop.getType().getTypeName());
			propTag.setAttribute(ATTR_VALUE, prop.toString());
			propTag.setAttribute(ATTR_NAME, prop.getName());
			if (prop.getType().getDataList() != null) {
				propTag.setAttribute(
						ATTR_DATA, Utils.mergeArrayToString(prop.getType().getDataList(), DELIM));
			}
			propTag.setAttribute(ATTR_VISIBLE, BooleanPropertyType.toString(prop.isVisible()));
			sheetTag.appendChild(propTag);
		}
		return sheetTag;
	}
	
	public PropertySheet clone() {
		try { 
			PropertySheet propertySheet = (PropertySheet)super.clone();
			propertySheet.propertyList = new ArrayList<Property>();
			for (Property prop : this.propertyList) {
				propertySheet.propertyList.add(prop.clone());
			}
		    propertySheet.propertyMap = new HashMap<String, Property>();
		    for (Property prop : propertySheet.propertyList) {
		    	propertySheet.propertyMap.put(prop.getId(), prop);
		    }
			return propertySheet;
		} catch (CloneNotSupportedException e) {  
			return null;
		}  
	}

	public static PropertySheet valueOf(Element sheetTag) {
		PropertySheet propertySheet = new PropertySheet();
		propertySheet.name = sheetTag.getTagName();
		propertySheet.title = sheetTag.getAttribute(ATTR_TITLE);
		propertySheet.xpath = XMLUtil.getPath(sheetTag);
		propertySheet.visible = BooleanPropertyType.getBoolean(sheetTag.getAttribute(ATTR_VISIBLE));
//		NodeList propNodeList = sheetTag.getElementsByTagName(PROPERTY_NODE_NAME);		
//		for (int i = 0; i < propNodeList.getLength(); i++) {
//			Element propNode = (Element)propNodeList.item(i);
//			Property prop = Property.valueOf(
//				propNode.getAttribute(ATTR_NAME), propNode.getAttribute(ATTR_LABEL),
//				propNode.getAttribute(ATTR_TYPE), propNode.getAttribute(ATTR_VALUE));
//			if (prop != null) {
//				propertySheet.propertyMap.put(prop.getId(), prop);
//				propertySheet.propertyList.add(prop);
//			}
//		}
		Element[] nodeList = XMLUtil.getChildNodes(sheetTag);
		for (int i = 0; i < nodeList.length; i++) {
			if (nodeList[i].getTagName().equals(PROPERTY_NODE)) {
				Property prop = Property.valueOf(
					nodeList[i].getAttribute(ATTR_NAME), nodeList[i].getAttribute(ATTR_LABEL),
					nodeList[i].getAttribute(ATTR_TYPE), nodeList[i].getAttribute(ATTR_VALUE));				
				if (prop != null) {
					String data = nodeList[i].getAttribute(ATTR_DATA);
					if (data != null) {
						prop.setData(data.split(DELIM));
					}
					prop.setVisible(BooleanPropertyType.getBoolean(nodeList[i].getAttribute(ATTR_VISIBLE)));
					propertySheet.propertyMap.put(prop.getId(), prop);
					propertySheet.propertyList.add(prop);
				}
			}
		}
		
		return propertySheet;
	}
	
	public static PropertySheet loadPropertySheet(Element parentNode, String sheetName) {
    	NodeList nodeList = parentNode.getElementsByTagName(sheetName);
    	return nodeList.getLength() > 0 ? valueOf((Element)nodeList.item(0)) : null;
//		Element[] elements = XMLUtil.getChildNodes(parentNode);
//		return elements.length > 0 ? valueOf(elements[0]) : null;
    }
	
	public static List<PropertySheet> loadPropertySheets(Element parentNode, String sheetName) {
    	List<PropertySheet> propertySheetList = new ArrayList<PropertySheet>();
    	NodeList nodeList = parentNode.getElementsByTagName(sheetName);
    	for (int i = 0; i < nodeList.getLength(); i++) {
			Element node = (Element)nodeList.item(i);
			PropertySheet prop = valueOf(node);
//			if (prop.size() > 0) {
				propertySheetList.add(prop);
//			}
    	}
//    	Element[] elements = XMLUtil.getChildNodes(parentNode);
//    	for (int i = 0; i < elements.length; i++) {
//    		PropertySheet prop = valueOf(elements[i]);
//    		propertySheetList.add(prop);
//    	}
    	return propertySheetList;
    }
	
	public static List<PropertySheet> loadPropertySheets(Element parentNode) {
    	List<PropertySheet> propertySheetList = new ArrayList<PropertySheet>();
    	Element[] elements = XMLUtil.getChildNodes(parentNode);
    	for (int i = 0; i < elements.length; i++) {
    		PropertySheet prop = valueOf(elements[i]);
    		propertySheetList.add(prop);
    	}
    	return propertySheetList;
    }
    
}
