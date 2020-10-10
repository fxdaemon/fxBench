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

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import org.fxbench.chart.ta.Indicator;
import org.fxbench.ui.panel.ChartPanel;
import org.fxbench.util.SerialVersion;
import org.fxbench.util.properties.PropertySheet;

/**
 *
 * @author Administrator
 */
public class IndicatorPane extends Canvas
{
    private static final long serialVersionUID = SerialVersion.APPVERSION;
    public final static int MARGIN_LEFT = 0;
    public final static int MARGIN_TOP = 2;
    public final static int MARGIN_RIGHT = 55;
    public final static int MARGIN_BOTTOM = 2;
    public final static int AXIS_X_HEIGHT = 0;
    public final static int AXIS_Y_WIDTH = 5;
    
    private Indicator indicator;
    
    public IndicatorPane(ChartPanel panel, PropertySheet propertySheet) {
        super(panel);
        this.indicator = Indicator.valueOf(propertySheet);
        this.indicator.setPane(this);
//        rangeY = this.indicator.getRangeY();
        
        leftMargin = MARGIN_LEFT;
    	topMargin = MARGIN_TOP;
    	rightMargin = MARGIN_RIGHT;
    	bottomMargin = MARGIN_BOTTOM;
    	axisXHeight = AXIS_X_HEIGHT;
    	axisYWidth = AXIS_Y_WIDTH;
    	setBackground(chartPanel.getBackground());
    	
        initialize();
    }

	public void initialize() {
		autoScale = true;
		mousePressedPos = null;
		mousePressedArea = -1;
		
		crossXMarker.setVal(null);
		crossYMarker.setVal(null);
    	crossYMarker.setFormat(this.indicator.getValueFormat());
    	zoomYFactor = this.indicator.getZoomFactor();
    	rateMarker.setVal(null);
    }
    
    public int getSplitDividerLocation() {
    	return indicator.getPropertySheet().getIntVal("divider_location");
    }
    
    public void saveSplitDiveiderLocation(Object val) {
    	indicator.getPropertySheet().setProperty("divider_location", val);
    }
    
    public Point getIndicatorLabelOrginPos() {
    	return new Point(MARGIN_LEFT + 10, MARGIN_TOP + 5);
    }
	 
	public Indicator getIndicator() {
		return indicator;
	}

	public void setIndicator(Indicator indicator) {
		this.indicator = indicator;
	}
	
	@Override
	public boolean insideLabel(Point clickPos) {
		return indicator.getLabelBounds().contains(clickPos);
	}
	
	
	
	@Override
    public void moveCrossXMark() {
	}

	@Override
	public void update() {
		indicator.update();
		if (autoScale) {
			rangeY = indicator.getRangeY();
		}
	}

	@Override
    protected void drawGrid(Graphics2D g2) {
		g2.setPaint(BOUNDS_COLOR);
		Rectangle bounds = getBounds();
		bounds.setSize(bounds.width + axisYWidth, bounds.height + axisXHeight);
    	g2.draw(bounds);
    }
	
    @Override
    protected void drawAxisX(Graphics2D g2) {
    	
    }
    
    @Override
	protected void drawAxisY(Graphics2D g2) {
		
	}
	
    @Override
	protected void drawChart(Graphics2D g2) {
    	if (!indicator.isCalculated()) {
    		indicator.calculate();
    	}
    	indicator.draw(g2);
	}
}