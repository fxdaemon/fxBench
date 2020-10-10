/*
* Copyright 2020 FXDaemon
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.fxbench.chart;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Line2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.fxbench.BenchApp;
import org.fxbench.ui.panel.ChartPanel;
import org.fxbench.util.Utils;

public abstract class Canvas extends JPanel 
	implements MouseListener, MouseMotionListener, MouseWheelListener
{
	protected final static Color BOUNDS_COLOR = Color.DARK_GRAY;
	protected final static int CHART_AREA_KBN = 0;
	protected final static int AXIS_X_AREA_KBN = 1;
	protected final static int AXIS_Y_AREA_KBN = 2;
	
	private static Cursor dragHandCursor;
	static {	
		try {
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Image image = toolkit.getImage(BenchApp.getInst().getResourceManager().getResource("ID_DRAG_HAND_CURSOR"));
			dragHandCursor = toolkit.createCustomCursor(image, new Point(0, 0), "DRAG_HAND_CURSOR");
	    } catch (Exception e) {
	    	dragHandCursor = Cursor.getDefaultCursor();
	    }
	}
	
	protected ChartPanel chartPanel;
	protected JSplitPane splitPane;
	
	protected boolean autoScale;	//Flag that automatically adjusts the vertical display range of the chart to match the height of the panel
	protected double axisYScale;	//Y-axis scale (pixels / 1point)
	protected Range rangeY;			//Vertical display range of chart
	protected double zoomYFactor;	//Factors that zoom in the Y-axis direction
	protected Point mousePressedPos;//Position when mouse is pressed
	protected int mousePressedArea;	//Area when mouse is pressed
	
    protected int leftMargin;
    protected int topMargin;
    protected int rightMargin;
    protected int bottomMargin;
    protected int axisXHeight;		//Height of area to draw X-axis scale
    protected int axisYWidth;		//Width of area to draw Y-axis scale
    
    protected Point crossPoint;		//Mouse movement coordinates
    protected Color crossColor;		//The color of the crosshairs displayed when the mouse is moved
    protected Marker crossXMarker;	//Label showing the X coordinate of mouse movement (bottom side of panel)
    protected Marker crossYMarker;	//Label indicating the Y coordinate of mouse movement (right side of the panel)
    protected Marker rateMarker;	//Label that immediately reflects rate fluctuations (on the right side of the panel)
    protected String axisYValFormat;//Format to display the Y coordinate of mouse movement
  
    public Canvas(ChartPanel panel) {
    	super(new BorderLayout());
    	
        this.chartPanel = panel;
        autoScale = true;
        axisYScale = 0;
        
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this, null);
    	splitPane.setDividerSize(0);
    	splitPane.setDividerLocation(0);
    	splitPane.addPropertyChangeListener(
    		    new PropertyChangeListener() { 
    		        public void propertyChange(PropertyChangeEvent e) { 
    		            if (e.getPropertyName().equals(JSplitPane.DIVIDER_LOCATION_PROPERTY)) {
		            		IndicatorPane indicatorPane = chartPanel.getIndicatorPane(
		            				(JSplitPane)splitPane.getRightComponent());
		            		if (indicatorPane != null) {
		            			indicatorPane.saveSplitDiveiderLocation(e.getNewValue());
		            		}
    		            }
    		        }
    		    }
    		);
        
        crossPoint = new Point(-1, -1); 
        crossColor = chartPanel.getCrossColor();
        crossXMarker = new Marker(
        		chartPanel.getMarkerFont(), chartPanel.getCrossMarkerForeground(), chartPanel.getCrossMarkerBackground());
        crossXMarker.setAlign(Marker.ALIGN_H_CENTER, Marker.ALIGN_V_TOP);
        crossYMarker = new Marker(
        		chartPanel.getMarkerFont(), chartPanel.getCrossMarkerForeground(), chartPanel.getCrossMarkerBackground());
        crossYMarker.setVAlign(Marker.ALIGN_V_CENTER);
        rateMarker = new Marker(
        		chartPanel.getMarkerFont(), chartPanel.getRateMarkerForeground(), chartPanel.getRateMarkerBackground());
        rateMarker.setVAlign(Marker.ALIGN_V_CENTER);
        
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }
    
    public ChartPanel getChartPanel() {
		return chartPanel;
	}

	public void setChartPanel(ChartPanel chartPanel) {
		this.chartPanel = chartPanel;
	}
	
	public JSplitPane getSplitPane() {
    	return splitPane;
    }

	public boolean isAutoScale() {
		return autoScale;
	}
	
	public void setAutoScale(boolean auto) {
		this.autoScale = auto;
	}
	
	public Point getOriginPoint() {
		return new Point(leftMargin, topMargin);
	}
	
	@Override
    public Rectangle getBounds() {
        return new Rectangle(
        		leftMargin,
        		topMargin,
        		getWidth() - (leftMargin + rightMargin), 
        		getHeight() - (topMargin + bottomMargin));
    }
	
	public Dimension getDimension() {
		return new Dimension(
				getWidth() - (leftMargin + rightMargin), 
        		getHeight() - (topMargin + bottomMargin));
	}
	
	public Rectangle getAxisXBounds() {
		return new Rectangle(
				leftMargin,
				getHeight() - bottomMargin,
				getWidth() - (leftMargin + rightMargin), 
				axisXHeight);
	}
	
	public Rectangle getAxisYBounds() {
		return new Rectangle(
				getWidth() - rightMargin,
        		topMargin,
        		axisYWidth, 
        		getHeight() - (topMargin + bottomMargin));
	}

	public Range getRangeY() {
		return rangeY;
	}

	public void setRangeY(Range rangeY) {
		this.rangeY = rangeY;
	}
	
	public double getZoomYFactor() {
		return zoomYFactor;
	}

	public double getAxisXScale() {
		return chartPanel.getAxisXScale();
	}

	public double getAxisYScale() {
		if (autoScale && rangeY != null) {
			axisYScale = getDimension().height / rangeY.getLength();
		}
		return axisYScale;
	}
	
	public int getAxisXVisibleBars() {
		return Double.valueOf(getDimension().width / getAxisXScale()).intValue() + 1;
	}
	
	public double[] getVisibleReal(double[] in) {
    	int visiblePixels = getAxisXVisibleBars();
    	if (in.length <= visiblePixels || visiblePixels <= 0) {
    		return in;
    	} else {
    		double out[] = new double[visiblePixels];
    		System.arraycopy(in, in.length - visiblePixels, out, 0, visiblePixels);
    		return out;
    	}
	}
	
	public void setMargin(int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
		this.leftMargin = leftMargin;
		this.topMargin = topMargin;
		this.rightMargin = rightMargin;
		this.bottomMargin = bottomMargin;
	}
	
    public Point getCrossPoint() {
		return crossPoint;
	}

	public void setCrossPoint(Point crossPoint) {
		this.crossPoint = crossPoint;
	}

	public Marker getCrossXMarker() {
		return crossXMarker;
	}

	public void setCrossXMarker(Marker crossXMarker) {
		this.crossXMarker = crossXMarker;
	}

	public Marker getCrossYMarker() {
		return crossYMarker;
	}

	public void setCrossYMarker(Marker crossYMarker) {
		this.crossYMarker = crossYMarker;
	}

	public Marker getRateMarker() {
		return rateMarker;
	}

	public void setRateMarker(Marker rateMarker) {
		this.rateMarker = rateMarker;
	}
	
	public String getAxisYValFormat() {
		return axisYValFormat;
	}
	
	public void setAxisYValFormat(String format) {
		axisYValFormat = format;
	}
	
	public void moveCrossYMark() {
		if (rangeY == null) {
			crossYMarker.setVal(null);
		} else {
			Rectangle bounds = getBounds();
			if (crossPoint.getY() > bounds.getY() && crossPoint.getY() < bounds.getY() + bounds.getHeight()) {
				crossYMarker.setPosX(getWidth() - rightMargin + axisYWidth);
				crossYMarker.setPosY(crossPoint.y);
				crossYMarker.setVal(axisY2val(crossPoint.y));
			} else {
				crossYMarker.setVal(null);
			}
		}
	}
	
	public void moveRateMark() {
		Double val = (Double)rateMarker.getVal();
		if (val == null || rangeY == null) {
			return;
		}		
		rateMarker.setPosX(getWidth() - rightMargin + axisYWidth);
		rateMarker.setPosY(val2AxisY(val));		
	}
	
	public int val2AxisY(double val) {
		return Double.valueOf(
				(rangeY.getUpperBound() - val) * 
				(getHeight() - (topMargin + bottomMargin)) / 
				rangeY.getLength() +
				topMargin).intValue();
	}
	
	public double axisY2val(int axisY) {
		return rangeY.getUpperBound() -
				(axisY - topMargin) *
				rangeY.getLength() /
				(getHeight() - (topMargin + bottomMargin));
	}

	public int axisX2val(int axisX) {
		return Double.valueOf((axisX - leftMargin) / getAxisXScale()).intValue();
	}
	    
	@Override
    public void paint(Graphics g) {
		super.paint(g);
		
		Graphics2D g2 = (Graphics2D)g;
//		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		drawGrid(g2);
		drawAxisX(g2);
		drawAxisY(g2);
		
		moveRateMark();
		rateMarker.draw(g2);
		moveCrossXMark();
		crossXMarker.draw(g2);
		moveCrossYMark();
		crossYMarker.draw(g2);
		
		drawChart(g2);
		drawCross(g2);
	}
	
	protected void drawCross(Graphics2D g2) {
		Rectangle bounds = getBounds();
//		if (!bounds.contains(crossPoint)) {
//			return;
//		}
		
		g2.setPaint(crossColor);
		g2.setStroke(new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0, new float[]{3, 4}, 0));

		// horizontal line 
		if (crossPoint.getY() > bounds.getY() && crossPoint.getY() < bounds.getY() + bounds.getHeight()) {
			g2.draw(new Line2D.Double(
    			leftMargin, crossPoint.getY(), getWidth() - rightMargin, crossPoint.getY()));
		}
		// vertical line
		if (crossPoint.getX() > bounds.getX() && crossPoint.getX() < bounds.getX() + bounds.getWidth()) {
			g2.draw(new Line2D.Double(
    			crossPoint.getX(), topMargin, crossPoint.getX(), getHeight() - bottomMargin));
		}
	}
	
	public void mouseWheelMoved(MouseWheelEvent e) {
//		int notches = e.getWheelRotation();
//		if (notches < 0) {
//			//Mouse wheel moved UP 			
//		} else {
//			//Mouse wheel moved DOWN 
//		}
		if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
			//WHEEL_UNIT_SCROLL
			autoScale = false;
			chartPanel.zoom(Math.signum(e.getUnitsToScroll()) * chartPanel.getZoomXFactor());			
			axisYScale += (Math.signum(e.getUnitsToScroll()) * getZoomYFactor()); 
			chartPanel.refresh();
		} else {
			//WHEEL_BLOCK_SCROLL
		}
	}
	
	public void mousePressed(MouseEvent e) {
		if(!insideLabel(e.getPoint())) {
			mousePressedPos = e.getPoint();		
			if (getBounds().contains(e.getPoint())) {
				mousePressedArea = CHART_AREA_KBN;
				setCursor(dragHandCursor);
			} else if (getAxisXBounds().contains(e.getPoint())) {
				mousePressedArea = AXIS_X_AREA_KBN;
			} else if (getAxisYBounds().contains(e.getPoint())) {
				mousePressedArea = AXIS_Y_AREA_KBN;
			} else {
				mousePressedArea = -1;
			}
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		setCursor(Cursor.getDefaultCursor());
		mousePressedPos = null;
		mousePressedArea = -1;
	}
	
	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
		if(javax.swing.SwingUtilities.isRightMouseButton(e)){
			if (!insideLabel(e.getPoint())) {
			}
		} else if(javax.swing.SwingUtilities.isMiddleMouseButton(e)){
		} else if(javax.swing.SwingUtilities.isLeftMouseButton(e)){
			if (insideLabel(e.getPoint())) {
				chartPanel.getChartPopupMenu().setClickPosition(e.getPoint());
				chartPanel.getChartPopupMenu().show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}
	
	public void mouseMoved(MouseEvent e) {
		if (insideLabel(e.getPoint())) {
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else if (getAxisXBounds().contains(e.getPoint())) {
			setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
		} else if (getAxisYBounds().contains(e.getPoint())) {
			setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
		} else {
			setCursor(Cursor.getDefaultCursor());
		}
		crossPoint = e.getPoint();
		chartPanel.moveCrossY(crossPoint);
		chartPanel.repaint();
		//Move the mouse cloth on other Chart Panels
		if (chartPanel.getMainFrame().getBenchPanel().isChartSplitMode()) {
			chartPanel.getMainFrame().getBenchPanel().moveChartsCrossY(chartPanel, crossPoint);
		}
	}
	
	public void mouseDragged(MouseEvent e) {
		if (mousePressedArea == -1 || mousePressedPos == null) {
			return;
		}
		double shiftX = (mousePressedPos.x - e.getPoint().x) / getAxisXScale();
		double shiftY = (mousePressedPos.y - e.getPoint().y) / axisYScale;
		if (mousePressedArea == CHART_AREA_KBN) {
			if (rangeY != null) {
				autoScale = false;
				crossPoint = e.getPoint();
				chartPanel.moveCrossY(crossPoint);
				rangeY = Range.shift(rangeY, shiftY * -1, true);		
				chartPanel.shift(Utils.round(shiftX));
			}
		} else if (mousePressedArea == AXIS_X_AREA_KBN) {
			chartPanel.zoom(Math.signum(shiftX) * chartPanel.getZoomXFactor());
		} else if (mousePressedArea == AXIS_Y_AREA_KBN) {
			autoScale = false;
			axisYScale += (Math.signum(shiftY) * getZoomYFactor()); 
		}
		mousePressedPos = e.getPoint();	
		chartPanel.refresh();
	}
	
	public abstract void initialize();
	public abstract void moveCrossXMark();
	public abstract boolean insideLabel(Point clickPos);
	public abstract void update(); 
	protected abstract void drawGrid(Graphics2D g2);
	protected abstract void drawAxisX(Graphics2D g2);
	protected abstract void drawAxisY(Graphics2D g2);
	protected abstract void drawChart(Graphics2D g2);
}
