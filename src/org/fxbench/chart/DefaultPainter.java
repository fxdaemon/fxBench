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
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author viorel.gheba
 */
public final class DefaultPainter {
	
    private DefaultPainter() {
    }

//    public static Graphics2D prepareGraphics(Graphics g) {
//		Graphics2D g2 = (Graphics2D) g.create();
//		g2.setRenderingHint(
//			RenderingHints.KEY_ANTIALIASING,
//			RenderingHints.VALUE_ANTIALIAS_ON);
//        g2.setRenderingHint(
//			RenderingHints.KEY_TEXT_ANTIALIASING,
//			RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//        /*g2.setRenderingHint(
//			RenderingHints.KEY_ALPHA_INTERPOLATION,
//			RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//		g2.setPaintMode();*/
//		return g2;
//	}
    
//    public static double getUnitX(Rectangle bounds, int length) {
//    	if (length == 0) {
//    		return 1.0D;
//    	} else {
//    		return bounds.getWidth() / length;
//    	}
//    }
//    
//    public static double getUnitY(Rectangle bounds, Range range) {
//    	if (range == null) {
//    		return 1.0D;
//    	} else {
//    		return bounds.getHeight() / range.getLength();
//    	}
//    }
    
//    public static Point2D.Double getTextMetric(Graphics2D g2, String text) {
//    	FontRenderContext frc = g2.getFontRenderContext();
//    	Font font = g2.getFont();
//    	LineMetrics lm = font.getLineMetrics(text, frc);
//    	
//    	Point2D.Double size = new Point2D.Double();
//    	size.setLocation(font.getStringBounds(text, frc).getWidth(),
//    				lm.getAscent() + lm.getDescent());
//    	return size;
//    }
    
    public static double getX(Point originPoint, double val, double scaleX) {
    	return originPoint.x + val * scaleX;
    }
    
    public static double getY(Point originPoint, double val, double scaleY, Range rangeY) {
    	return originPoint.y + (rangeY.getUpperBound() - val) * scaleY;
    }
    
    
    ///////////////////////////////////////////
    //// line
    public static void line(Graphics2D g, double x1, double v1, double x2, double v2, Point originPoint, Range rangeY, double scaleY, Color color) {      	
    	g.setPaint(color);
		g.draw(new Line2D.Double(
			x1, getY(originPoint, v1, scaleY, rangeY),
			x2, getY(originPoint, v2, scaleY, rangeY)));
    }
    public static void line(Graphics2D g, double[] data, Point originPoint, Range rangeY, double scaleX, double scaleY, Color color) {      	
    	g.setPaint(color);
    	for (int i = 0; i < data.length - 1; i++) {
    		g.draw(new Line2D.Double(
    			getX(originPoint, i, scaleX), getY(originPoint, data[i], scaleY, rangeY),
    			getX(originPoint, i + 1, scaleX), getY(originPoint, data[i+1], scaleY, rangeY)));
    	}
    }

    
    ///////////////////////////////////////////
    //// bar
    public static double bar(Graphics2D g, double[] data, Point originPoint, Range rangeY, double scaleX, double scaleY, Color upColor, Color downColor) {
    	double zeroY = getY(originPoint, 0D, scaleY, rangeY);
    	double barWidth = scaleX * 0.618;
    	for (int i = 0; i < data.length; i++) {
    		double x = getX(originPoint, i, scaleX);
    		double y = getY(originPoint, data[i], scaleY, rangeY);
    		double height = Math.abs(y - zeroY);
    		if (data[i] > 0) {
    			g.setPaint(upColor);
    			g.fill(new Rectangle2D.Double(x, y, barWidth, height));
    		} else {
    			g.setPaint(downColor);
    			g.fill(new Rectangle2D.Double(x, zeroY, barWidth, height));
    		}
    	}
    	return zeroY;
    }

    
    ///////////////////////////////////////////
    //// histogram
    public static void histogram(Graphics2D g, double[] data, Point originPoint, Range rangeY, double scaleX, double scaleY, Color upColor, Color downColor) {
    	double zeroY = bar(g, data, originPoint, rangeY, scaleX, scaleY, upColor, downColor);
    	g.draw(new Line2D.Double(
    			originPoint.getX(), zeroY, data.length * scaleX, zeroY));
    }


    ///////////////////////////////////////////
    //// band
    public static void band(Graphics2D g, double[] upData, double[] lowData, Point originPoint, Range rangeY, double scaleX, double scaleY, Color color) {
        g.setPaint(color);
        for (int i = 0; i < upData.length - 1; i++) {
        	GeneralPath gp = new GeneralPath();
        	gp.moveTo(getX(originPoint, i, scaleX), getY(originPoint, upData[i], scaleY, rangeY));
        	gp.lineTo(getX(originPoint, i + 1, scaleX), getY(originPoint, upData[i+1], scaleY, rangeY));
        	gp.lineTo(getX(originPoint, i + 1, scaleX), getY(originPoint, lowData[i+1], scaleY, rangeY));
        	gp.lineTo(getX(originPoint, i, scaleX), getY(originPoint, lowData[i], scaleY, rangeY));
        	gp.closePath();
            g.fill(gp);
    	}
    }


    ///////////////////////////////////////////
    //// dot
    public static void dot(Graphics2D g, double[] data, Point originPoint, Range rangeY, double scaleX, double scaleY, Color color) {
    	g.setPaint(color);
    	for (int i = 0; i < data.length; i++) {
    		Ellipse2D.Double circle =  new Ellipse2D.Double(
    			getX(originPoint, i, scaleX), getY(originPoint, data[i], scaleY, rangeY), 5, 5);
            g.fill(circle);
    	}
    }

    
    ///////////////////////////////////////////
    //// candlestick
    public static Rectangle2D.Double[] candlestick(Graphics2D g, Dataset dataset, Point originPoint, Range rangeY, double scaleX, double scaleY, Color upColor, Color downColor) {
    	double barWidth = scaleX * 0.618;
    	double[] openVals = dataset.getOpenValues();
    	double[] closeVals = dataset.getCloseValues();
    	double[] highVals = dataset.getHighValues();
    	double[] lowVals = dataset.getLowValues();
    	
    	Rectangle2D.Double[] drawRectangles = new Rectangle2D.Double[openVals.length];
    	for (int i = 0; i < openVals.length; i++) {
    		double barX = getX(originPoint, i, scaleX);
    		double barY = 0;
    		double barHeight = (closeVals[i] - openVals[i]) * scaleY;
    		if (barHeight >= 0) {
    			g.setPaint(upColor);
    			barY = getY(originPoint, closeVals[i], scaleY, rangeY); 
    		} else {
    			g.setPaint(downColor);
    			barY = getY(originPoint, openVals[i], scaleY, rangeY);
    			barHeight = Math.abs(barHeight);
    		}
    		double lineX = barX + barWidth / 2;
    		
    		g.draw(new Line2D.Double(
					lineX, getY(originPoint, highVals[i], scaleY, rangeY), lineX, barY));
			g.fill(new Rectangle2D.Double(barX, barY, barWidth, barHeight));
			g.draw(new Line2D.Double(
					lineX, barY + barHeight, lineX, getY(originPoint, lowVals[i], scaleY, rangeY)));
			
			drawRectangles[i] = new Rectangle2D.Double(
					barX,
					getY(originPoint, highVals[i], scaleY, rangeY),
					scaleX,
					(highVals[i] - lowVals[i]) * scaleY);
    	}
    	return drawRectangles;
    }
}
