package org.fxbench.ui.docking.dockable;

import java.awt.Component;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.Icon;

import org.fxbench.ui.docking.dock.LeafDock;
import org.fxbench.ui.docking.event.DockableEvent;
import org.fxbench.ui.docking.event.DockingListener;

/**
 * <p>
 * A dockable is an object that can be moved around and docked in a {@link org.fxbench.ui.docking.dock.Dock}. 
 * It contains a graphical component as content. This content should be set once and never be changed.
 * </p>
 * <p>
 * Information on using dockables is in 
 * <a href="http://www.javadocking.com/developerguide/dockable.html" target="_blank">How to Use Dockables</a> in 
 * <i>The Sanaware Developer Guide</i>.
 * Information on adding, moving, and removing dockables is in 
 * <a href="http://www.javadocking.com/developerguide/adddockable.html" target="_blank">How to Add, Move, and Remove Dockables</a>.
 * </p>
 * <p>
 * The purpose of the docks and dockables is to organize and move the graphical 
 * content components of the application. Docks are in a fixed position.
 * They can receive dockables. The dockables are moved with their content from dock to dock.
 * </p>
 * <p>
 * All the dockables in the application should have a different ID. Implementations of this class should
 * overwrite {@link java.lang.Object#equals(java.lang.Object)}. Dockables are equal if their ID is equal.
 * </p>
 * 
 * @author Heidi Rakels.
 */
public interface Dockable
{

	// Interface methods.

	/**
	 * Gets the ID of the dockable. 
	 * <br>
	 * <b>WARNING:</b> All the dockables used in an application should have a different ID.
	 * 
	 * @return 							The ID of the dockable.
	 */
	public String getID();
	
	/**
	 * Sets the dock in which the dockable is docked. 
	 * 
	 * @param 	dock 					The dock in which the dockable is docked. 
	 * 									This can be null, if the dockable is not docked.
	 */
	public void setDock(LeafDock dock);

	/**
	 * Gets the dock in which the dockable is docked.
	 * 
	 * @return 							The dock in which the dockable is docked. 
	 * 									This can be null, if the dockable is not docked.
	 */
	public LeafDock getDock();
	
	/**
	 * Gets the content of the dockable. This is a graphical component. The dockable is a wrapper around
	 * this content component. The dockable is used to move this content from dock to dock.
	 * 
	 * @return 							The content of the dockable.
	 */
	public Component getContent();

	/**
	 * Gets the title of the dockable.
	 * 
	 * @return 							The title of the dockable.
	 */
	public String getTitle();
	
	/**
	 * Gets the description of the dockable.
	 * 
	 * @return 							The description of the dockable.
	 */
	public String getDescription();

	/**
	 * Gets the icon of the dockable.
	 * 
	 * @return 							The icon of the dockable.
	 */
	public Icon getIcon();
	
	/**
	 * <p>
	 * Gets the modes how this dockable can be docked. The integer should be a combination of constants defined by {@link DockingMode}.
	 * </p>
	 * <p>
	 * Combinations of the different modes are made by using the bitwise or-operator.
	 * </p>
	 * 
	 * @return 							The possible docking modes of the dockable. This integer should be combination of constants
	 * 									defined by {@link DockingMode}.
	 */
	public int getDockingModes();
	
	/**
	 * Sets the mode how the dockable is docked in its current dock or how it was docked the last time it was 
	 * in a dock.
	 * 
	 * @param 	dockingMode 			The mode how the dockable is docked in its current dock or how it was docked the last time it was 
	 * 									in a dock. This integer should be a constant defined by {@link DockingMode}.
	 */
	public void setLastDockingMode(int dockingMode);
	
	/**
	 * Gets the mode how the dockable is docked in its current dock or how it was docked the last time it was 
	 * in a dock.
	 * 
	 * @return 							The mode how the dockable is docked in its current dock or how it was docked the last time it was 
	 * 									in a dock. This integer should be a constant defined by {@link DockingMode}.
	 */
	public int getLastDockingMode();
	
	/**
	 * Returns whether the dockable will have a header when it is docked alone. With the header the dockable can be dragged.
	 * The header can contain also the title, the icon, etc.
	 * 
	 * @return 							True if the dockable will have a header when it is docked alone, false otherwise.
	 */
	public boolean isWithHeader();
	
	/**
	 * Gets the state of the dockable.
	 * 
	 * @return							The current state of the dockable. This should be a constant defined by {@link DockableState}.
	 */
	public int getState();
	
	/**
	 * Tries to set the new state of the dockable. No checking is done, if the state is allowed by
	 * {@link #getPossibleStates()}.
	 * 
	 * @param 	state					The new state of the dockable. This should be a constant defined by {@link DockableState}.
	 * @param	visualizer				The object that currently shows the content of the dockable. 
	 * 									Can be null, i.e. when the state of the dockable is {@link DockableState#CLOSED}.
	 */
	public void setState(int state, Object visualizer);
	
	/**
	 * Gets the possible states of the dockable. This can be a combination of constants defined by {@link DockableState}.
	 * A combination is made by the bitwise or-operation on the integer constants.
	 * 
	 * @return							The possible states of the dockable. 
	 */
	public int getPossibleStates();
	
	/**
	 * Gets the object that currently shows the content of the dockable. 
	 * Can be null, i.e. when the state of the dockable is {@link DockableState#CLOSED}.
	 * 
	 * @return							The object that currently shows the content of the dockable. 
	 */
	public Object getVisualizer();
	
	/**
	 * <p>
	 * Gets a matrix with the actions for the dockable. 
	 * </p>
	 * <p>
	 * These actions can be displayed in a tool bar in the header of the dockable.
	 * They can also be displayed in a popup menu, that appears when right clicking on the header of a dockable.
	 * </p>
	 * <p>
	 * The actions of the different rows are divided in the tool bar or in the popup menu.
	 * </p>
	 * <p>
	 * In some headers i.e. in tabs, there is not enough space to display all the actions. In that case
	 * only the first row of actions is displayed.
	 * </p>
	 * 
	 * @return							The actions that are associated with the dockable. Can be null.
	 */
	public Action[][] getActions();

	/**
	 * Adds a java.beans.PropertyChangeListener. The listener is registered for the <code>dock</code> property.
	 * 
	 * @param 	listener 				The property change listener to be added.
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener);
	
	/**
	 * Removes a java.beans.PropertyChangeListener. This removes a listener.
	 * 
	 * @param 	listener 				The property change listener to be removed
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener);
	
	/**
	 * Adds a listener for docking events of this dockable.
	 * 
	 * @param 	listener 				A docking listener that will be notified, when this dockable is added, moved, or removed.
	 */
	public void addDockingListener(DockingListener listener);
	
	/**
	 * Removes a listener for docking events of this dockable.
	 * 
	 * @param 	listener 				A docking listener to remove.
	 */
	public void removeDockingListener(DockingListener listener);
	
	/**
	 * Notifies all listeners that have registered interest for notification on this event type.
	 * 
	 * @param	dockableEvent			Gives more information about the origin dock, the destination dock,
	 * 									and the object whose docking state changed.
	 */
	public void fireDockingWillChange(DockableEvent dockableEvent);
	
	/**
	 * Notifies all listeners that have registered interest for notification on this event type.
	 * 
	 * @param	dockableEvent			Gives more information about the origin dock, the destination dock,
	 * 									and the object whose docking state changed.
	 */
	public void fireDockingChanged(DockableEvent dockableEvent);
	
}
