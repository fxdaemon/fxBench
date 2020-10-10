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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.fxbench.entity.TPriceBar.FieldDef;
import org.fxbench.util.FieldDefStub;
import org.fxbench.util.properties.PropertySheet;

/**
 *
 * @author viorel.gheba
 */
public class GPriceBar
{
    private static final FieldDef[] FIELDS_DEF = {
		FieldDef.PRICEBAR_DATE, FieldDef.PRICEBAR_ASK_OPEN, FieldDef.PRICEBAR_ASK_HIGH,
		FieldDef.PRICEBAR_ASK_LOW, FieldDef.PRICEBAR_ASK_CLOSE, FieldDef.PRICEBAR_BID_OPEN, 
		FieldDef.PRICEBAR_BID_HIGH, FieldDef.PRICEBAR_BID_LOW, FieldDef.PRICEBAR_BID_CLOSE};
	private static final FieldDefStub<FieldDef> fieldDefStub = new FieldDefStub<FieldDef>(FIELDS_DEF, FieldDef.class);
    
    private OverlayPane overlayPane;
    private PropertySheet propSheet;
    private Dataset dataset;
    private Rectangle2D.Double[] barDrawRegions;

    public GPriceBar(OverlayPane overlayPane, PropertySheet propertySheet) {
        this.overlayPane = overlayPane;
        this.propSheet = propertySheet;
    }

    /**
	 * @return the fielddefstub
	 */
	public static FieldDefStub<FieldDef> getFielddefStub() {
		return fieldDefStub;
	}

	public void draw(Graphics2D g) {
		if (dataset == null || dataset.getSize() == 0 || overlayPane.getRangeY() == null) {
			return;
		}
		barDrawRegions = DefaultPainter.candlestick(
    			g, dataset, overlayPane.getOriginPoint(), overlayPane.getRangeY(),
    			overlayPane.getAxisXScale(), overlayPane.getAxisYScale(), getUpColor(), getDownColor());
    }
    
	public int getCount() {
		if (dataset == null) {
			return 0;
		} else {
			return dataset.getSize();
		}
	}
	
	public int getBarIndex(double x) {
		int index = -1;
		if (barDrawRegions != null) {
			for (int i = 0; i < barDrawRegions.length; i++) {
				if (x >= barDrawRegions[i].getX() &&
					x <= barDrawRegions[i].getX() + barDrawRegions[i].getWidth()) {
					index = i;
					break;
				}
			}
		}
		return index;
	}
	
	public Dataset getDataset() {
		return dataset;
	}
	
	public Rectangle2D.Double[] getBarDrawRegions() {
		return barDrawRegions;
	}
	
	public Range getRangeY() {
		return dataset == null ? null : Range.valueOf(dataset);
	}
	
    public void shift() {
    	dataset = overlayPane.getChartPanel().getVisibleDataset();
    }
    
    private Color getUpColor() {
    	return propSheet.getColorVal("color_raised");
    }
    private Color getDownColor() {
    	return propSheet.getColorVal("color_down");
    }    
}
