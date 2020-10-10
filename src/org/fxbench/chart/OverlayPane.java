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
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.fxbench.BenchApp;
import org.fxbench.chart.ta.Indicator;
import org.fxbench.desk.TradeDesk;
import org.fxbench.ui.panel.ChartPanel;
import org.fxbench.util.SerialVersion;
import org.fxbench.util.Utils;
import org.fxbench.util.properties.PropertySheet;


/**
 *
 * @author viorel.gheba
 */
public class OverlayPane extends Canvas
{
    private static final long serialVersionUID = SerialVersion.APPVERSION;
    public final static int MARGIN_LEFT = 0;
    public final static int MARGIN_TOP = 2;
    public final static int MARGIN_RIGHT = 55;
    public final static int MARGIN_BOTTOM = 25;
    public final static int AXIS_X_HEIGHT = 5;
    public final static int AXIS_Y_WIDTH = 5;
	    
    private GPriceBar priceBar;
    private GPosition position;
    private List<Indicator> indicators;

    public OverlayPane(ChartPanel panel) {
    	super(panel);
    	position = new GPosition(this);
    	indicators = new ArrayList<Indicator>();
    	
    	leftMargin = MARGIN_LEFT;
    	topMargin = MARGIN_TOP;
    	rightMargin = MARGIN_RIGHT;
    	bottomMargin = MARGIN_BOTTOM;
    	axisXHeight = AXIS_X_HEIGHT;
    	axisYWidth = AXIS_Y_WIDTH;
    	setBackground(chartPanel.getBackground());
    	
    	initialize();
    }
    
    public OverlayPane(ChartPanel panel, PropertySheet priceBarPropSheet) {
    	this(panel);    	
    	priceBar = new GPriceBar(this, priceBarPropSheet);
//    	if (overLayPropSheets != null) {    		
//    		for (PropertySheet porpSheet : overLayPropSheets) {
//    			Indicator indicator = Indicator.valueOf(porpSheet);
//    	        indicator.setPane(this);
//    	        indicators.add(indicator);
//    		}
//    	}        
    }

    public void initialize() {
    	autoScale = true;
    	mousePressedPos = null;
		mousePressedArea = -1;
		
		TradeDesk tradeDesk = BenchApp.getInst().getTradeDesk();
		int symbolPrecision = tradeDesk.getTradingServerSession().getSymbolPrecision(chartPanel.getSymbol());
    	String precisionStr = Utils.getFormatStr(symbolPrecision, '.', '#');
    	axisYValFormat = "#" + precisionStr + "#";
    	zoomYFactor = Math.pow(10, symbolPrecision - 1);
    	
    	crossXMarker.setVal(null);
    	crossYMarker.setFormat(axisYValFormat);
    	crossYMarker.setVal(null);
		rateMarker.setVal(null);
		rateMarker.setFormat(axisYValFormat);
    }
    
    public GPriceBar getPriceBar() {
    	return priceBar;
    }
    
    public int getIndicatorIndex(Indicator indicator) {
    	return indicators.indexOf(indicator);
    }
    
    public Point getIndicatorLabelOrginPos() {
    	return new Point(MARGIN_LEFT + 10, MARGIN_TOP + 5);
    }
    
    public Indicator getIndicator(Point clickPos) {
    	for (Indicator indicator : indicators) {
    		if (indicator.getLabelBounds().contains(clickPos)) {
    			return indicator;
    		}
    	}
    	return null;
    }
    
    public void setPriceBar(PropertySheet porpSheet) {
    	priceBar = new GPriceBar(this, porpSheet);
    }
    
    public void addIndicator(PropertySheet porpSheet) {
    	Indicator indicator = Indicator.valueOf(porpSheet);
    	if (indicator != null) {
    		indicators.add(indicator);
    		indicator.setPane(this);
    	}
    }
    
    public void removeIndicator(Indicator indicator) {
    	indicators.remove(indicator);
    }
    
    public void rePosIndicatorLabel() {
    	if (indicators.size() == 0) {
    		return;
    	}
    	indicators.get(0).getLabelBounds().setLocation(getIndicatorLabelOrginPos());
    	for(int i = 1; i < indicators.size(); i++) {
    		Rectangle prevBounds = indicators.get(i-1).getLabelBounds();
    		Rectangle curPounds = indicators.get(i).getLabelBounds();
    		curPounds.y = prevBounds.y + prevBounds.height;
    	}
    }
    
    @Override
	public boolean insideLabel(Point clickPos) {
    	boolean contain = false;
    	for (Indicator indicator : indicators) {
    		if (indicator.getLabelBounds().contains(clickPos)) {
    			contain = true;
    			break;
    		}
    	}
    	return contain;
	}
    
    @Override
	public void update() {
		priceBar.shift();
		position.update();
		for (Indicator indicator : indicators) {
			indicator.update();
		}
		if (autoScale) {
			rangeY = priceBar.getRangeY();
			for (Indicator indicator : indicators) {
				rangeY = Range.combine(rangeY, indicator.getRangeY());
			}
		}
	}
    
    @Override
    public void moveCrossXMark() {
    	Dataset dataset = priceBar.getDataset(); 
    	if (dataset == null || dataset.getSize() == 0) {
    		return;
    	}
    	Rectangle bounds = getBounds();    	
    	if (crossPoint.x >= bounds.x && crossPoint.x <= bounds.x + bounds.width) {
    		crossXMarker.setPosX(crossPoint.x);
			crossXMarker.setPosY(getHeight() - bottomMargin + axisXHeight);
			
//    		int movePixels = axisX2val(crossPoint.x);
    		int movePixels = priceBar.getBarIndex(crossPoint.getX());
			long time = dataset.getTimeAt(movePixels);
			if (time == 0) {
				crossXMarker.setVal(null);
//				Date makerDate = new Date();
//				makerDate.setTime(
//					chartPanel.getPanelBeginDate().getTime() + chartPanel.getInterval().getMilliSecond() * movePixels);
//				crossXMarker.setVal(makerDate);
			} else {
				crossXMarker.setVal(new Date(time));
			}
    	} else {
    		crossXMarker.setVal(null);
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
    	Shape oldShape = g2.getClip();
    	g2.setClip(getBounds());
    	priceBar.draw(g2);
		for (Indicator indicator : indicators) {
			if (!indicator.isCalculated()) {
	    		indicator.calculate();
	    	}
			indicator.draw(g2);
		}
		position.draw(g2);
		g2.setClip(oldShape);
	}
}
