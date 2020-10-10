package org.fxbench.ui.docking.visualizer;

import java.awt.Window;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.fxbench.ui.docking.dockable.Dockable;

/**
 * <p>
 * This is an interface for an object that can visualize {@link org.fxbench.ui.docking.dockable.Dockable}s 
 * in a special state.
 * </p>
 * <p>
 * The special state is a constant defined by {@link org.fxbench.ui.docking.dockable.DockableState},
 * but not {@link org.fxbench.ui.docking.dockable.DockableState#NORMAL} or 
 * {@link org.fxbench.ui.docking.dockable.DockableState#CLOSED}.
 * </p>
 * <p>
 * Information on using visualizers is in 
 * <a href="http://www.javadocking.com/developerguide/visualizer.html" target="_blank">
 * How to Use Visualizers (Minimizers and Maximizers)</a> in 
 * <i>The Sanaware Developer Guide</i>.
 * </p>
 * <p>
 * A visualizer will usually be used to maximize ({@link org.fxbench.ui.docking.dockable.DockableState#MAXIMIZED}) or 
 * minimize({@link org.fxbench.ui.docking.dockable.DockableState#MINIMIZED}) a dockable.
 * </p>
 * <p>
 * When a dockable is visualized, it can be removed from its {@link org.fxbench.ui.docking.dock.LeafDock},
 * but this is not obligatory. It can also be hidden in its dock, if the dock implements
 * the interface {@link org.fxbench.ui.docking.dock.DockableHider}.
 * </p>
 * <p>
 * 
 * @author Heidi Rakels.
 */
public interface Visualizer
{

	// Interface methods.
	
	/**
	 * Determines if a dockable can be visualized by this visualizer. 
	 * 
	 * @param 	dockableToVisualize		The dockable whose content has to be visualized. May not be nul.
	 * @return							True if the dockable can be visualized, lse otherwise.
	 */
	public boolean canVisualizeDockable(Dockable dockableToVisualize);

	/**
	 * Adds a dockable to this visualizer. 
	 * 
	 * @param 	dockableToVisualize		The dockable whose content has to be visualized. May not be nul.
	 */
	public void visualizeDockable(Dockable dockableToVisualize);
	
	/**
	 * Removes the visualized dockables from this visualizer.
	 *
	 * @throws	IllegalArgumentException	If the dockable is not visualized in this visualizer.
	 */
	public void removeVisualizedDockable(Dockable dockableToRemove);
	
	/**
	 * Gets the number of visualized dockables of this visualizer.
	 * 
	 * @return 		The number of visualized dockables of this visualizer.
	 */
	public int getVisualizedDockableCount();

	
	/**
	 * Gets the visualized dockable with the specified index.
	 * 
	 * @return 		The visualized dockable with the specified index.
	 * @throws 		IndexOutOfBoundsException 	If the index is out of range (index < 0 || index >= getvisualizedDockableCount()).
	 */
	public Dockable getVisualizedDockable(int index) throws IndexOutOfBoundsException;
	
	/**
	 * Determines the state of the dockables that are visualized by this visualizer.
	 * 
	 * @return		The state of the dockables that are visualized by this visualizer.
	 * 				The special state is a constant defined by {@link org.fxbench.ui.docking.dockable.DockableState},
	 * 				but not {@link org.fxbench.ui.docking.dockable.DockableState#NORMAL} or 
	 * 				{@link org.fxbench.ui.docking.dockable.DockableState#CLOSED}.
	 */
	public int getState();
	
	/**
	 * <p>
	 * Saves the properties of this visualizer in the given properties object. 
	 * </p>
	 * <p>
	 * The property names for this visualizer should start with the given prefix.
	 * </p>
	 * 
	 * @param prefix 		The prefix for the property names.
	 * @param properties 	The properties object to which the properties should be added.
	 */
	public void saveProperties(String prefix, Properties properties);
	
	/**
	 * <p>
	 * Loads the properties for this visualizer. The properties can be found in the given properties object.
	 * The property names for this visualizer start with the given prefix. 
	 * </p>
	 * <p>
	 * The dockables can be added to this visualizer. This is not obligatory.
	 * They can be found in the given dockables mapping.
	 * </p>
	 * <p>
	 * This method should be called after the empty constructor to create the content of the visualizer. 
	 * Don't call this method for a visualizer that already has a content.
	 * </p>
	 * 
	 * @param 	prefix 			The prefix of the names of the properties that have been intended for this visualizer.
	 * @param 	properties 		The properties object that contains the properties for this visualizer. It can contain also 
	 * 							properties for other objects, but they will have another prefix.
	 * @param 	dockablesMap 	A mapping that contains the available dockables.
	 * 							<ul>
	 * 							<li>map key: the ID of the dockable (java.lang.String).</li>
	 * 							<li>map value: the dockable ({@link Dockable}).</li>
	 * 							</ul>
	 * @param 	owner 			The owner window of the visualizer in the dock model ({@link org.fxbench.ui.docking.model.DockModel}).
	 * @throws	IOException		If an error occures while decoding the properties.
	 */
	public void loadProperties(String prefix, Properties properties, Map dockablesMap, Window owner) throws IOException;

}
