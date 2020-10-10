package org.fxbench.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.fxbench.util.properties.PropertySheet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Viorel
 */
public final class XMLUtil
{
	public static void createXMLDocument(String path) {
		createXMLDocument(path, "root");
	}

	public static void createXMLDocument(String path, String root) {
		createXMLDocument(new File(path), root);
	}

    public static void createXMLDocument(File file) {
    	createXMLDocument(file, "root");
    }

	public static void createXMLDocument(File file, String rootName) {
		if (!file.exists()) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.newDocument();
                Element root = document.createElement(rootName);
                document.appendChild(root);
                saveXMLDocument(document, file);
            } catch (Exception ex) { 
            }
        }
	}

	public static void emptyXMLDocument(String path) {
		emptyXMLDocument(new File(path));
	}
	
    public static void emptyXMLDocument(File file) {
        if (file.exists()) {
            Document document = loadXMLDocument(file);
            if (document != null) {
            	Element[] childs = getChildNodes(document);
            	for (int i = 0; i < childs.length; i++) {
            		NodeList nodeList = childs[i].getChildNodes();
                    for (int j = 0; j < nodeList.getLength(); j++) {
                    	childs[i].removeChild(nodeList.item(j));
                    }
                }
            }
        }
    }

	public static void saveXMLDocument(Document document, String path) {
		saveXMLDocument(document, new File(path));
	}
	
	public static void saveXMLDocument(Document document, File file) {
		try {
			saveXMLDocument(document, new FileOutputStream(file));
		} catch( Exception e) {
		}
	}
	
    public static void saveXMLDocument(Document document, OutputStream output) {
//        FileOutputStream output = null;
        try {
//            output = new FileOutputStream(file);
            Source source = new DOMSource(document);
            Result result = new StreamResult(output);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(source, result);
        } catch (Exception ex) {
            try {
            	output.close();
            } catch (IOException io) {
            }
        } finally {
            try { 
            	output.close();
            } catch (IOException io) {
            }
        }
    }

	public static Document loadXMLDocument(String path) { 
		return loadXMLDocument(new File(path));
	}
	
	public static Document loadXMLDocument(File file) {
		try {
			return loadXMLDocument(new FileInputStream(file));
		}  catch (Exception ex) {
            return null;
        }
	}

    public static Document loadXMLDocument(InputStream inputStream) {
    	try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inputStream);
			return document;
    	}  catch (Exception ex) {
            return null;
        }    	
    }
    
    public static Document newXMLDocument() {
    	try {
	    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        Document document = builder.newDocument();
	        return document;
    	}  catch (Exception ex) {
            return null;
        }
    }

    public static Element getRoot(Node parent) {
		if (parent == null) {
			return null;
		}
        Element[] nodeList = getChildNodes(parent);
        return nodeList.length > 0 ? nodeList[0] : null;
	}
    
    public static Element getRoot(Node parent, String nodeName) {
		if (parent == null) {
			return null;
		}
		Element root = null;
        Element[] nodeList = getChildNodes(parent);
        for (int i = 0; i < nodeList.length; i++) {
        	if (nodeName.equals(nodeList[i].getNodeName())) {
        		root = nodeList[i];
        		break;
        	}
        }
        return root;
	}
    
    public static Element getRoot(Node parent, String attrName, String attrVal) {
		if (parent == null) {
			return null;
		}
		Element root = null;
        Element[] nodeList = getChildNodes(parent);
        for (int i = 0; i < nodeList.length; i++) {
        	if (attrVal.equals(nodeList[i].getAttribute(attrName))) {
        		root = nodeList[i];
        		break;
        	}
        }
        return root;
	}
	
	public static Element getNode(Node parent, String nodeName) {
		if (parent == null) {
			return null;
		}
		
        NodeList nodeList = null;
        if (parent instanceof Document) {
        	nodeList = ((Document)parent).getElementsByTagName(nodeName);
        } else if (parent instanceof Element) {
        	nodeList = ((Element)parent).getElementsByTagName(nodeName);
        } else {
        	return null;
        }
        return nodeList.getLength() > 0 ? (Element)nodeList.item(0) : null;
	}
	
	public static Node removeNode(Node parent, String nodeName) {
		if (parent == null) {
			return null;
		}
		Element child = getNode(parent, nodeName);
		return parent.removeChild(child);
	}
	
	public static Element[] getChildNodes(Node parent) {
		if (parent == null) {
//			throw new IllegalArgumentException("Null element");
			return null;
		}
		
		List<Element> childNodes = new ArrayList<Element>();
		NodeList nodeList = parent.getChildNodes();
    	for (int i = 0; i < nodeList.getLength(); i++) {
    		if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
    			childNodes.add((Element)nodeList.item(i));
    		}
    	}
    	Element[] elements = new Element[childNodes.size()];
    	return childNodes.toArray(elements);
	}

	public static String getPath(Node current) {
		if (current == null) {
			return null;
		}
		
		String path = current.getNodeName();
		Node parent = current.getParentNode();
    	while (parent != null) {
    		if (parent.getNodeType() == Node.ELEMENT_NODE) {
    			if (((Element)parent).getAttribute(PropertySheet.ATTR_XPATH).equals(PropertySheet.ATTR_XPATH_OUT)) {
    			} else {
    				path = parent.getNodeName() + "." + path;
    			}
    		}
    		parent = parent.getParentNode();
    	}
    	
		return path;
	}
}
