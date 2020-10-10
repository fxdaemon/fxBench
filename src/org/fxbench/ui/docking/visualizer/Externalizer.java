package org.fxbench.ui.docking.visualizer;

import java.awt.Point;

import org.fxbench.ui.docking.dockable.Dockable;

public interface Externalizer extends Visualizer 
{

	public void moveExternalizedDockable(Dockable dockable, Point position, Point dockableOffset);
	
}
