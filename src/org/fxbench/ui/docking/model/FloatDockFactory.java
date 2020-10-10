package org.fxbench.ui.docking.model;

import java.awt.Window;
import java.util.Properties;

import org.fxbench.ui.docking.dock.FloatDock;

/**
 * This class creates {@link org.fxbench.ui.docking.dock.FloatDock}s.
 * 
 * @author Heidi Rakels.
 */
public interface FloatDockFactory
{

	/**
	 * Create a float dock with the given window as owner.
	 * 
	 * @param 	owner		The owner window of the float dock.
	 * @return				The created float dock.
	 */
	public FloatDock createFloatDock(Window owner);
	
	/**
	 * Saves the properties of this float dock factory in the given properties object. The property names for this float dock factory
	 * should start with the given prefix.
	 * 
	 * @param 	prefix 		The prefix for the property names.
	 * @param 	properties 	The properties object to which the properties should be added.
	 */
	public void saveProperties(String prefix, Properties properties);
	
	/**
	 * Sets the properties for this float dock factory. The properties can be found in the given properties object
	 * and the property names for this float dock factory start with the given prefix. 
	 * 
	 * @param 	prefix 		The prefix of the names of the properties that have been intended for this float dock factory.
	 * @param 	properties 	The properties object that contains the properties for this float dock factory. It can contain also 
	 * 						properties for other objects, but they will have another prefix.
	 */
	public void loadProperties(String prefix, Properties properties);

}
