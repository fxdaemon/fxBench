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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.fxbench.BenchApp;
import org.fxbench.entity.TPosition;
import org.fxbench.ui.auxi.UIManager;

/**
 *
 * @author viorel.gheba
 */
public class GPosition
{
	private OverlayPane overlayPane;
	private List<Position> positions;
	
	public GPosition(OverlayPane overlayPane) {
		this.overlayPane = overlayPane;
		positions = new ArrayList<Position>();
	}
	
	public void update() {
		positions.clear();
		Dataset dataset = overlayPane.getPriceBar().getDataset();
		if (dataset != null && dataset.getSize() > 0) {
			Date fromDate = dataset.getDateAt(0);
			Date toDate = dataset.getDateAt(dataset.getSize() - 1);
			List<TPosition> openPositions = 
				BenchApp.getInst().getTradeDesk().getOpenPositions().getVisiblePositions(fromDate, toDate);
			for (TPosition position : openPositions) {
				if (position.getSymbol().equals(overlayPane.getChartPanel().getSymbol())) {
					positions.add(new Position(position));
				}
			}
			List<TPosition> closedPositions = 
				BenchApp.getInst().getTradeDesk().getClosedPositions().getVisiblePositions(fromDate, toDate);
			for (TPosition position : closedPositions) {
				if (position.getSymbol().equals(overlayPane.getChartPanel().getSymbol())) {
					positions.add(new Position(position));
				}
			}
		}
	}
	
	public void draw(Graphics2D g2) {
		Rectangle2D.Double[] barDrawRegions = overlayPane.getPriceBar().getBarDrawRegions();
    	Dataset dataset = overlayPane.getPriceBar().getDataset();
    	if (barDrawRegions != null && dataset != null && barDrawRegions.length == dataset.getSize()) {
    		long intervalTime = overlayPane.getChartPanel().getInterval().getMilliSecond();
	    	for (Position position : positions) {
	    		long openTime = position.getPosition().getOpenTime().getTime();
	    		long closedTime = position.getPosition().getCloseTime() == null ?
	    				0 : position.getPosition().getCloseTime().getTime();
	    		for (int i = 0; i < dataset.getSize(); i++) {
	    			long datasetTime = dataset.getTimeAt(i);
	    			if (openTime >= datasetTime && openTime < datasetTime + intervalTime) {
	    				position.moveTo(Position.OPEN_POSITION, barDrawRegions[i]);
	    				position.draw(g2);
	    				if (position.getArea().contains(overlayPane.getCrossPoint())) {
	    					position.drawInfoBox(g2);
	    				}
	    				if (closedTime == 0) {
	    					break;
	    				}
	    			}
	    			if (closedTime >= datasetTime && closedTime < datasetTime + intervalTime) {
	    				position.moveTo(Position.CLOSED_POSITION, barDrawRegions[i]);
	    				position.draw(g2);
	    				position.drawText(g2);
	    				break;
	    			}
	    		}
	    	}
    	}
	}
	
	public class Position
	{
		public final static int OPEN_POSITION = 1;
		public final static int CLOSED_POSITION = 2;
		private final static int TRIANGLE_WIDTH = 16;
		private final static int TRIANGLE_HEIGHT = 12;
		private final static int LEAVE_FROM_BAR = 5;
		private final static String PL_TEXT_FORMAT = "#.#;-#.#";
		
	    private TPosition position;
	    private Area area;
	    private Color plusColor;
	    private Color minusColor;
	    private Font plFont;
	    private int ud;	//up or down
	
	    public Position(TPosition position) {
	        this.position = position;
	        this.plusColor = new Color(0, 255, 0);
	    	this.minusColor = new Color(255, 0, 0);
	    	this.plFont = new Font("Dialog", 1, 10);
	    }
	    
	    public void setColor(Color plusColor, Color minusColor) {
	    	this.plusColor = plusColor;
	    	this.minusColor = minusColor;
	    }
	    
	    public void moveTo(int stage, Rectangle2D.Double barRegion) {
	    	if (position.isBuy()) {
	    		if (stage == OPEN_POSITION) {
	    			moveToUp(barRegion.x + barRegion.width / 2, barRegion.y + barRegion.height + LEAVE_FROM_BAR);
	    		} else if (stage == CLOSED_POSITION) {
	    			moveToDown(barRegion.x + barRegion.width / 2, barRegion.y - LEAVE_FROM_BAR);
	    		}
	    	} else {
	    		if (stage == OPEN_POSITION) {
	    			moveToDown(barRegion.x + barRegion.width / 2, barRegion.y - LEAVE_FROM_BAR);
	    		} else if (stage == CLOSED_POSITION) {
	    			moveToUp(barRegion.x + barRegion.width / 2, barRegion.y + barRegion.height + LEAVE_FROM_BAR);
	    		}
	    	}
	    }
	    
	    private void moveToUp(double x, double y) {
	    	GeneralPath triangle = new GeneralPath();
	        triangle.moveTo(x, y);
	        triangle.lineTo(x + TRIANGLE_WIDTH / 2, y + TRIANGLE_HEIGHT);
	        triangle.lineTo(x - TRIANGLE_WIDTH / 2, y + TRIANGLE_HEIGHT);
	        triangle.lineTo(x, y);
	        triangle.closePath();
	        area = new Area(triangle);
	        area.add(new Area(new Rectangle2D.Double(
	        		x - TRIANGLE_WIDTH * 0.3,
	        		y + TRIANGLE_HEIGHT,
	        		TRIANGLE_WIDTH * 0.6,
	        		TRIANGLE_HEIGHT * 0.6)));
	        ud = 1;
	    }
	    
	    private void moveToDown(double x, double y) {
			GeneralPath triangle = new GeneralPath();
	        triangle.moveTo(x, y);
	        triangle.lineTo(x - TRIANGLE_WIDTH / 2, y - TRIANGLE_HEIGHT);
	        triangle.lineTo(x + TRIANGLE_WIDTH / 2, y - TRIANGLE_HEIGHT);
	        triangle.lineTo(x, y);
	        triangle.closePath();
	        area = new Area(triangle);
	        area.add(new Area(new Rectangle2D.Double(
	        		x - TRIANGLE_WIDTH * 0.3,
	        		y - TRIANGLE_HEIGHT - TRIANGLE_HEIGHT * 0.6,
	        		TRIANGLE_WIDTH * 0.6,
	        		TRIANGLE_HEIGHT * 0.6)));
	        ud = -1;
	    }
	    
	    public TPosition getPosition() {
	    	return position;
	    }
	    
	    public Area getArea() {
	    	return area;
	    }
	    
	    public boolean insideArea(Point point) {
	    	return area == null ? false : area.contains(point);
	    }
	    
	    public String getText() {
	    	return position.toString();
	    }
	    
		public void draw(Graphics2D g2) {
			if (area == null) {
				return;
			}
	//		g.setPaint(new Color(255, 255, 255));
	//		g.draw(area);
			if (position.getPl() >= 0) {
				g2.setPaint(plusColor);
			} else {
				g2.setPaint(minusColor);
			}
			g2.fill(area);
	    }
		
		public void drawText(Graphics2D g2) {
			DecimalFormat decimalFormat = new DecimalFormat(PL_TEXT_FORMAT);
	    	int textHeight = UIManager.getInst().createLabel().getFontMetrics(plFont).getHeight();
	    	Rectangle bounds = area.getBounds();
	    	int y = bounds.y;
	    	if (ud == 1) {
	    		y += (bounds.height + textHeight);
	    	} else if (ud == -1) {
	    		y -= 2;
	    	}
	    	
	    	g2.setFont(plFont);
	    	if (position.getPl() >= 0) {
	    		g2.setColor(plusColor);
	    	} else {
	    		g2.setColor(minusColor);
	    	}
	    	
	    	double maxPl = position.getMaxPl();
	    	if (maxPl != 0) {
		    	g2.drawString(decimalFormat.format(maxPl), bounds.x, y);
		    	if (ud == 1) {
		    		y += textHeight;
		    	} else if (ud == -1) {
		    		y -= textHeight;
		    	}
	    	}
	    	
	    	g2.drawString(decimalFormat.format(position.getPl()), bounds.x, y);
	    	if (ud == 1) {
	    		y += textHeight;
	    	} else if (ud == -1) {
	    		y -= textHeight;
	    	}
	    	
	    	double minPl = position.getMinPl();
	    	if (minPl != 0) {
		    	g2.drawString(decimalFormat.format(minPl), bounds.x, y);
	    	}
		}
		
		public void drawInfoBox(Graphics2D g2) {
			DecimalFormat decimalFormat = new DecimalFormat(PL_TEXT_FORMAT);
			FontMetrics fm = UIManager.getInst().createLabel().getFontMetrics(plFont); 
	        int textHeight = fm.getHeight(); 
	        int textWidth = fm.stringWidth("OpenTime: " + position.getOpenTimeText());
	        int textWidth2 = fm.stringWidth("CloseTime: " + position.getCloseTimeText());
	        int textWidth3 = fm.stringWidth("TrailMoveTsName: " + position.getTrailMoveTsName());
	        if (textWidth < textWidth2) {
	        	textWidth = textWidth2;
	        }
	        if (textWidth < textWidth3) {
	        	textWidth = textWidth3;
	        }
	        Rectangle bounds = area.getBounds();
	        
	        int posX = bounds.x;
	        int posY = bounds.y + bounds.height + textHeight;
	        
	        g2.setColor(new Color(255, 255, 100)); 
	        g2.fillRect(posX, posY, textWidth, textHeight * 16);
	        
	        g2.setFont(plFont);
			g2.setPaint(new Color(0, 0, 0));
			g2.drawString("TradeID: " + position.getTradeID(), posX, posY + textHeight * 1);
			g2.drawString("Open: " + position.getOpen(), posX, posY + textHeight * 2);
			g2.drawString("Close: " + position.getClose(), posX, posY + textHeight * 3);
			g2.drawString("Stop: " + position.getStop(), posX, posY + textHeight * 4);
			g2.drawString("Limit: " + position.getLimit(), posX, posY + textHeight * 5);
			g2.drawString("Pl :" + decimalFormat.format(position.getPl()), posX, posY + textHeight * 6);
			g2.drawString("MaxPl: " + decimalFormat.format(position.getMaxPl()), posX, posY + textHeight * 7);
			g2.drawString("MinPl: " + decimalFormat.format(position.getMinPl()), posX, posY + textHeight * 8);
			g2.drawString("OpenTime: " + position.getOpenTimeText(), posX, posY + textHeight * 9);
			g2.drawString("CloseTime: " + position.getCloseTimeText(), posX, posY + textHeight * 10);
			g2.drawString("TradeMethodPeriod: " + position.getTradeMethodPeriod(), posX, posY + textHeight * 11);
			g2.drawString("TradeMethodName: " + position.getTradeMethodName(), posX, posY + textHeight * 12);
			g2.drawString("TrailStopPeriod: " + position.getTrailStopPeriod(), posX, posY + textHeight * 13);
			g2.drawString("TrailStopName: " + position.getTrailStopName(), posX, posY + textHeight * 14);			
			g2.drawString("TrailMoveTsName: " + position.getTrailMoveTsName(), posX, posY + textHeight * 15);
		}
	}
}