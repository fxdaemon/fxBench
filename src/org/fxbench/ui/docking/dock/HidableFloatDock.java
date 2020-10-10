package org.fxbench.ui.docking.dock;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.fxbench.ui.docking.dock.factory.DockFactory;
import org.fxbench.ui.docking.dock.factory.SplitDockFactory;
import org.fxbench.ui.docking.dockable.Dockable;
import org.fxbench.ui.docking.util.PropertiesUtil;

/**
 * <p>
 * This is a FloatDock that can be hidden. When the dock is hidden, all its child docks are not visible.
 * </p>
 * 
 * @author Heidi Rakels.
 */
public class HidableFloatDock extends FloatDock 
{

	/** The name of the <code>hidden</code> property. */
	private static final String 	PROPERTY_HIDDEN = "hidden";

	/** True when the float dock is hidden, false otherwise. */
	private boolean 				hidden 	= false;
	/** The support for handling the property changes. */
	private PropertyChangeSupport 	propertyChangeSupport 		= new PropertyChangeSupport(this);

	/**
	 * Constructs a hidable float dock with no owner and a {@link SplitDockFactory}
	 * as factory for the child docks.
	 */
	public HidableFloatDock()
	{
		super();
	}
	
	/**
	 * Constructs a hidable float dock with the given window as owner for the child dock windows 
	 * and a {@link SplitDockFactory} as factory for creating child docks.	
	 * 
	 * @param	owner				The window that owns the floating windows created by this dock.
	 */
	public HidableFloatDock(Window owner)
	{
		super(owner);
	}

	/**
	 * Constructs a hidable float dock with the given window as owner for the child dock windows 
	 * and the given factory for the child docks.
	 * 
	 * @param	owner				The window that owns the floating windows created by this dock.
	 * @param	childDockFactory	The factory for creating child docks.	
	 */
	public HidableFloatDock(Window owner, DockFactory childDockFactory)
	{
		super(owner, childDockFactory);
	}
	
	// Getters / Setters.
	
	/**
	 * Determines if the float dock is hidden.
	 * 
	 * @return 						True, when the float dock is hidden, false otherwise.
	 */
	public boolean isHidden() 
	{
		return hidden;
	}

	public void setHidden(boolean hidden) {
		boolean oldValue = this.hidden;
		this.hidden = hidden;
		hide(hidden);
		propertyChangeSupport.firePropertyChange("hidden", oldValue, hidden);

	}
	
	// Overwritten methods.
	
	public void addChildDock(Dock dock, Point location, Dimension size) 
	{
		super.addChildDock(dock, location, size);
		Window window = (Window)getChildDockWindows().get(dock);
		if (window != null) 
		{
			if (hidden) 
			{
				window.setVisible(!hidden);
			}
		}
	}

	
	
	// Private methods.
	
	public void loadProperties(String prefix, Properties properties, Map newChildDocks, Map dockablesMap, Window owner) throws IOException 
	{
		hidden = PropertiesUtil.getBoolean(properties, prefix + PROPERTY_HIDDEN, hidden);
		super.loadProperties(prefix, properties, newChildDocks, dockablesMap, owner);
	}

	public void saveProperties(String prefix, Properties properties, Map childDockIds) 
	{
		PropertiesUtil.setBoolean(properties, prefix + PROPERTY_HIDDEN, hidden);
		super.saveProperties(prefix, properties, childDockIds);
	}

	private void hide(boolean hidden) 
	{

		Map childDockWindows = getChildDockWindows();
		Iterator iterator = getChildDockWindows().values().iterator();
		while (iterator.hasNext()) 
		{
			Window window = (Window)iterator.next();
			window.setVisible(!hidden);
		}
		
	}

}
