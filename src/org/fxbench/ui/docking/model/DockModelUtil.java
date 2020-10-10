package org.fxbench.ui.docking.model;

import java.awt.Window;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.fxbench.ui.docking.dock.Dock;
import org.fxbench.ui.docking.dock.HidableFloatDock;

/**
 * This class contains a collection of static utility methods for dock models.
 * 
 * @author Heidi Rakels.
 */
public class DockModelUtil 
{

	public static Set getVisibleFloatDocks(DockModel dockModel, Window ownerWindow) 
	{
		Set keys = dockModel.getFloatDockKeys(ownerWindow);
		Set visibleDocks = new HashSet();
		Iterator iterator = keys.iterator();
		while(iterator.hasNext()) 
		{
			String key = (String)iterator.next();
			Dock dock = dockModel.getRootDock(key);
			if (dock instanceof HidableFloatDock) 
			{
				if (!((HidableFloatDock)dock).isHidden())
				{
					visibleDocks.add(dock);
				}
			}
			else 
			{
				visibleDocks.add(dock);
			}
		}
		return visibleDocks;
	}
	
}
