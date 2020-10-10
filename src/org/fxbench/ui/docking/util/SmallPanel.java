package org.fxbench.ui.docking.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.fxbench.ui.docking.dockable.DraggableContent;
import org.fxbench.ui.docking.drag.DragListener;

public class SmallPanel extends JPanel implements DraggableContent
{

	// Fields.

	private JLabel label;
	
	// Constructors.

	public SmallPanel()
	{
		this("");
	}
	
	public SmallPanel(String text)
	{
		super(new FlowLayout());
		
		setMinimumSize(new Dimension(80,80));
		setPreferredSize(new Dimension(150,150));
		setBackground(Color.white);
		setBorder(BorderFactory.createLineBorder(Color.lightGray));
		
		label = new JLabel(text);
		label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		add(label);
	}

	// Public methods.

	public void setText(String text)
	{
		label.setText(text);
	}

	// Implementations of DraggableComponent.

	public void addDragListener(DragListener dragListener)
	{
		addMouseListener(dragListener);
		addMouseMotionListener(dragListener);
		label.addMouseListener(dragListener);
		label.addMouseMotionListener(dragListener);
	}
}
