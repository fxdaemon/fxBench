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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Marker
{
	public static final int ALIGN_H_STANDARD = 0;
	public static final int ALIGN_H_CENTER = 1;
	public static final int ALIGN_H_RIGHT = 2;
	public static final int ALIGN_V_STANDARD = 4;
	public static final int ALIGN_V_CENTER = 6;
	public static final int ALIGN_V_TOP = 8;
	
	private Object val;
	private String format;
	private Point position;
	private int hAlign;
	private int vAlign;
	private Font font;
	private Color foreground;
	private Color background;
	
	public Marker(Font font, Color foreground, Color background) {
		this.font = font;
		this.foreground = foreground;
		this.background = background;
		this.position = new Point(-100, -100);
		this.hAlign = ALIGN_H_STANDARD;
		this.vAlign = ALIGN_V_STANDARD;
	}
	
	public Object getVal() {
		return val;
	}

	public void setVal(Object val) {
		this.val = val;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}
	
	public int getHAlign() {
		return hAlign;
	}

	public void setHAlign(int align) {
		this.hAlign = align;
	}
	
	public int getVAlign() {
		return vAlign;
	}

	public void setVAlign(int align) {
		this.vAlign = align;
	}
	
	public void setAlign(int hAlign, int vAlign) {
		this.hAlign = hAlign;
		this.vAlign = vAlign;
	}

	public void setPosX(int x) {
		this.position.x = x;
	}
	
	public void setPosY(int y) {
		this.position.y = y;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public Color getForeground() {
		return foreground;
	}

	public void setForeground(Color foreground) {
		this.foreground = foreground;
	}

	public Color getBackground() {
		return background;
	}

	public void setBackground(Color background) {
		this.background = background;
	}
	
	public int getTextHeight(Graphics2D g2) {
		g2.setFont(font);
		return g2.getFontMetrics().getHeight();
	}
	
	public void draw(Graphics2D g2) {
		String text = "";
		if (val instanceof Date) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
			text = simpleDateFormat.format(val);
		} else if (val instanceof Double) {
			DecimalFormat decimalFormat = new DecimalFormat(format);
			text = decimalFormat.format(val);
		}
		
		if (text.length() > 0) {
			g2.setFont(font);
			
			FontMetrics fm = g2.getFontMetrics(); 
	        int height = fm.getHeight(); 
	        int width = fm.stringWidth(text);
	        int left = position.x;
	        if (hAlign == ALIGN_H_CENTER) {
	        	left -= width / 2;
	        } else if (hAlign == ALIGN_H_RIGHT) {
	        	left -= width;
	        }
	        int top = position.y;
	        if (vAlign == ALIGN_V_CENTER) {
	        	top += height / 2;
	        } else if (vAlign == ALIGN_V_TOP) {
	        	top += height;
	        }
	        
	        g2.setColor(background); 
	        g2.fillRect(left, top - height + 2, width, height); 
			g2.setPaint(foreground);
			g2.drawString(text, left, top);
		}
	}
}
