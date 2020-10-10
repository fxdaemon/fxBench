package org.fxbench.ui.docking.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import org.fxbench.ui.docking.dock.LeafDock;
import org.fxbench.ui.docking.dock.Position;
import org.fxbench.ui.docking.drag.DragListener;

/**
 * <p>
 * The default header for dragging all the dockables that are docked in a given dock.
 * </p>
 * <p> 
 * The dockables can be dragged by dragging this component.
 * </p>
 * 
 * @author Heidi Rakels.
 */
public class DefaultDockHeader extends JPanel implements DockHeader
{
	
	// Static fields.
	
	/** This is the minimum width when the orientation is vertical.
	 * It is the minimum height when the orientation is horizontal. */
	private static final int		HEADER_SIZE		= 10;
	/** The color of the handle. */
	private static Color 			color 			= Color.white;
	/** The color of the shadow of the handle. */
	private static Color 			shadow 			= Color.lightGray;

	// Fields.

	/** The dockables of this dock will be dragged when the handle is dragged. */
	private LeafDock 				dock;
	/** The drag listener that drags the dockables of this dock. */
	private DragListener 			dragListener;
	/** The position of this handle. */
	private int						position;

	// Constructors.

	/**
	 * Constructs a header for the given dock.
	 * 
	 * @param	dock			The dock of the header.
	 * @param	position		The position of this header. 
	 * 							Possible values are constants defined by the class {@link com.javadocking.dock.Position}, i.e.:
	 * 							<ul>
	 * 							<li>{@link com.javadocking.dock.Position#LEFT},</li> 
	 * 							<li>{@link com.javadocking.dock.Position#RIGHT},</li> 
	 * 							<li>{@link com.javadocking.dock.Position#TOP},</li> 
	 * 							<li>{@link com.javadocking.dock.Position#BOTTOM}.</li> 
	 * 							</ul>
	 */
	public DefaultDockHeader(LeafDock dock, int position)
	{
		super(new BorderLayout());
		this.setPosition(position);
		this.dock = dock;
	}
	
	// Implementations of DockHeader.

	public void setDragListener(DragListener dragListener)
	{
		this.dragListener = dragListener;
		this.addMouseMotionListener(dragListener);
		this.addMouseListener(dragListener);
	}
	
	public DragListener getDragListener()
	{
		return dragListener;
	}

	public void dispose()
	{
	}

	public LeafDock getDock()
	{
		return dock;
	}

	public int getPosition() {

		return position;
	}

	public void setPosition(int position) {
		
		this.position = position;
		if ((position == Position.TOP) || (position == Position.BOTTOM))
		{
			this.setMinimumSize(new Dimension(HEADER_SIZE, HEADER_SIZE));	
			this.setMaximumSize(new Dimension(Short.MAX_VALUE, HEADER_SIZE));	
			this.setPreferredSize(new Dimension(HEADER_SIZE, HEADER_SIZE));	
		}
		else
		{
			this.setMinimumSize(new Dimension(HEADER_SIZE, HEADER_SIZE));	
			this.setMaximumSize(new Dimension(HEADER_SIZE, Short.MAX_VALUE));	
			this.setPreferredSize(new Dimension(HEADER_SIZE, HEADER_SIZE));				
		}
		
	}

	// Overwritten methods.

	public void paint(Graphics graphics) 
	{
		super.paint(graphics);
		graphics.setColor(Color.darkGray);
		
		int lineOffset = 5;
		int headerOffset = 3;
		if ((position == Position.TOP) || (position == Position.BOTTOM)) 
		{
			
			// Draw a horizontal handle.
			int width = getSize().width;
			
			// Draw the light line.
			graphics.setColor(color);
			graphics.drawLine(lineOffset,headerOffset, width - lineOffset, headerOffset);
			graphics.drawLine(lineOffset,headerOffset + 1, width - lineOffset, headerOffset + 1);
			
			// Draw the shadow.
			graphics.setColor(shadow);
			graphics.drawLine(width - lineOffset + 1,headerOffset, width - lineOffset + 1, headerOffset + 2);
			graphics.drawLine(lineOffset,headerOffset + 2, width - lineOffset, headerOffset + 2);

		}
		else 
		{
			
			// Draw a vertical handle.
			int height = getSize().height;
			
			// Draw the light line.
			graphics.setColor(color);
			graphics.drawLine(headerOffset,lineOffset,headerOffset, height - lineOffset);
			graphics.drawLine(headerOffset + 1, lineOffset, headerOffset + 1, height - lineOffset);

			// Draw the shadow.
			graphics.setColor(shadow);
			graphics.drawLine(headerOffset,height - lineOffset + 1 ,headerOffset + 2, height - lineOffset + 1);
			graphics.drawLine(headerOffset + 2, lineOffset, headerOffset + 2, height - lineOffset);
		}
	}


	/**
	 * Gets the color of the shadow of the handle.
	 * 
	 * @return			The color of the shadow of the handle.
	 */
	public static Color getShadow()
	{
		return shadow;
	}

	/**
	 * Sets the color of the shadow of the handle.
	 * 
	 * @param shadow	The color of the shadow of the handle.
	 */
	public static void setShadow(Color shadow)
	{
		DefaultDockHeader.shadow = shadow;
	}

	/**
	 * Gets the color of the handle.
	 * 
	 * @return			The color of the handle.
	 */
	public static Color getColor()
	{
		return color;
	}

	/**
	 * Sets the color of the handle.
	 * 
	 * @param color		The color of the handle.
	 */
	public static void setColor(Color color)
	{
		DefaultDockHeader.color = color;
	}

}
