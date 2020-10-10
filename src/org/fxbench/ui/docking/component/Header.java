package org.fxbench.ui.docking.component;

import java.beans.PropertyChangeListener;

/**
 * An interface for a header of an object. The header has a position.
 * 
 * @author Heidi Rakels.
 */
public interface Header
{

	/**
	 * Gets the position of this header. 
	 * Possible values are constants defined by the class {@link org.fxbench.ui.docking.dock.Position}, i.e.:
	 * <ul>
	 * <li>{@link org.fxbench.ui.docking.dock.Position#LEFT},</li> 
	 * <li>{@link org.fxbench.ui.docking.dock.Position#RIGHT},</li> 
	 * <li>{@link org.fxbench.ui.docking.dock.Position#TOP},</li> 
	 * <li>{@link org.fxbench.ui.docking.dock.Position#BOTTOM}.</li> 
	 * </ul>
	 * 
	 * @return					The position of this header.
	 */
	public int getPosition();

	/**
	 * Gets the position of this header. 
	 * Possible values are constants defined by the class {@link org.fxbench.ui.docking.dock.Position}, i.e.:
	 * <ul>
	 * <li>{@link org.fxbench.ui.docking.dock.Position#LEFT},</li> 
	 * <li>{@link org.fxbench.ui.docking.dock.Position#RIGHT},</li> 
	 * <li>{@link org.fxbench.ui.docking.dock.Position#TOP},</li> 
	 * <li>{@link org.fxbench.ui.docking.dock.Position#BOTTOM}.</li> 
	 * </ul>
	 * 
	 * @param position  		The position of this header.
	 */
	public void setPosition(int position);
	
	/**
	 * Adds a java.beans.PropertyChangeListener. 
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
	 * Disposes the header.
	 */
	public void dispose();
}
