package org.fxbench.ui.docking.drag.painter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JWindow;

import org.fxbench.ui.docking.DockingManager;
import org.fxbench.ui.docking.dock.Dock;
import org.fxbench.ui.docking.dock.FloatDock;
import org.fxbench.ui.docking.dockable.Dockable;

/**
 * A dockable drag painter that only paints the dockable when the new dock is a {@link FloatDock}.
 * The dockable is painted on a window.
 * The given {@link RectanglePainter} paints the dockable on this window.
 * 
 * @author Heidi Rakels.
 */
public class OldWindowDockableDragPainter implements DockableDragPainter
{

	/** The transparent window on which the dockable is painted. */
	private JWindow 				window;
	private JPanel 					panel;
	/** The position where the window will be placed. */
	private Point 					windowLocation 			= new Point();
	/** On this component the dragged dockable is shown. */
	private Component 				dragComponent;
	/** The factory that creates the drag component. */
	private DragComponentFactory 	dragComponentFactory;

	
	/**
	 * Constructs a Swing dockable drag painter that uses the given drag component factory.
	 * 
	 * @param dragComponentFactory	The factory that creates the component that represents the dragged dockable.
	 */
	public OldWindowDockableDragPainter(DragComponentFactory dragComponentFactory)
	{

		initializeWindow();

		this.dragComponentFactory = dragComponentFactory;
	}
	
	/**
	 * Constructs a Swing dockable drag painter that uses a {@link org.fxbench.ui.docking.drag.painter.RectangleDragComponentFactory}
	 * as drag component factory.
	 * 
	 * @param rectanglePainter		The rectangle painter used by the default drag component factory
	 * 								to paint the dragged dockable on the component.
	 */
	public OldWindowDockableDragPainter(RectanglePainter rectanglePainter)
	{
		
		initializeWindow();

		dragComponentFactory = new RectangleDragComponentFactory(rectanglePainter);
	}


	public void clear()
	{
		window.setVisible(false);
	}

	public void paintDockableDrag(Dockable dockable, Dock dock, Rectangle rectangle, Point locationInDestinationDock)
	{

		if (dock instanceof FloatDock)
		{

			// Get the corresponding rectangle in the root pane.
			windowLocation.move(rectangle.x, rectangle.y);
			//SwingUtilities.convertPointToScreen(windowLocation, (Component)dock);
	
			window.setSize(new Dimension(rectangle.width, rectangle.height));
			window.setLocation(windowLocation);
			
			// Create the drag rectangle.
			//TODO optimize
			dragComponent = dragComponentFactory.createDragComponent(dockable, dock, rectangle); 
			panel.removeAll();
			panel.add(dragComponent);
			if (!window.isVisible())
			{
				window.setVisible(true);
			}
		}
		else
		{
			window.setVisible(false);
		}

	}

	// Private metods.


	private void initializeWindow()
	{
		
		panel = new JPanel(new BorderLayout());
		panel.setOpaque(true);
		//panel.setBackground(Color.white);
		window = new JWindow();
		window.setBackground(new Color(0,0,0,255));
		window.setCursor(DockingManager.getCanDockCursor());
		window.getContentPane().add(panel);
		
	}
}
