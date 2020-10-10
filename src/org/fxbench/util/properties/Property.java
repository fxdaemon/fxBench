package org.fxbench.util.properties;

import java.awt.Color;
import java.awt.Font;
import java.util.Date;

import org.fxbench.util.properties.type.AbstractPropertyType;

public class Property implements Cloneable
{
	private String id;	//reserve
	private String name;
	private String label;
	private AbstractPropertyType type;
	private Object value;
	private boolean visible;
	private boolean changed;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public AbstractPropertyType getType() {
		return type;
	}
	public void setType(AbstractPropertyType type) {
		this.type = type;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public void setData(Object[] data) {
		this.type.setDataList(data);
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}	
	public boolean isChanged() {
		return changed;
	}
	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	
	public String strValue() {
		return (String)value;
	}
	public int intValue() {
		return (Integer)value;
	}
	public double dblValue() {
		return (Double)value;
	}
	public float floatValue() {
		return (Float)value;
	}
	public boolean boolValue() {
		return (Boolean)value;
	}
	public Date dateValue() {
		return (Date)value;
	}
	public Color colorValue() {
		return (Color)value;
	}
	public Font fontValue() {
		return (Font)value;
	}
	
	@Override
	public String toString() {
		return type.toString(value);
	}
	
	@Override
	public boolean equals(Object obj) {
		Property prop = (Property)obj;
		if (type.equals(prop.getType())) {
			return type.equals(value, prop.getValue());
		} else {
			return false;
		}
	}
	
	public Property clone() {
		try {  
			Property prop = (Property)super.clone();
			prop.type = type.clone();
			prop.value = type.cloneValue(value);
			return prop;
		} catch (CloneNotSupportedException e) {  
			return null;  
		}  
	}
	
	public static Property valueOf(String name, String label, String type, String value) {
		if (name == null || name.length() == 0) {
			return null;
		}
		
		AbstractPropertyType propertyType = AbstractPropertyType.valueOf(type);
		if (propertyType == null) {
			return null;
		} else {
			Property property = new Property();
			property.id = name;
			property.name = name;
			property.label = label;
			property.type = propertyType;
			property.value = propertyType.toValue(value);
			property.visible = false;
			property.changed = false;
			return property;
		}
	}
	
//	public enum ProtetyType {
//		STRING {
//			@Override
//			public String type() {
//				return "string";
//			}
//		},
//		INT{
//			@Override
//			public String type() {
//				return "int";
//			}
//		},
//		DOUBLE{
//			@Override
//			public String type() {
//				return "double";
//			}
//		},
//		FLOAT {
//			@Override
//			public String type() {
//				return "float";
//			}
//		},
//		BOOLEAN {
//			@Override
//			public String type() {
//				return "boolean";
//			}
//		},
//		DATE {
//			@Override
//			public String type() {
//				return "date";
//			}
//		},
//		COLOR {
//			@Override
//			public String type() {
//				return "color";
//			}
//		},
//		FONT {
//			@Override
//			public String type() {
//				return "font";
//			}
//		};
//		
//		public String type() {
//			return name();
//		}
//		
//		public static ProtetyType getType(String type) {
//			if (type.equals(STRING.type())) {
//				return STRING;
//			} else if (type.equals(INT.type())) {
//				return INT;
//			} else if (type.equals(DOUBLE.type())) {
//				return DOUBLE;
//			} else if (type.equals(DOUBLE.type())) {
//				return FLOAT;
//			} else if (type.equals(BOOLEAN.type())) {
//				return BOOLEAN;
//			} else if (type.equals(DATE.type())) {
//				return DATE;
//			} else if (type.equals(COLOR.type())) {
//				return COLOR;
//			} else if (type.equals(FONT.type())) {
//				return FONT;
//			} else {
//				return STRING;
//			}
//		}
//
//		public static ProtetyType valueOf(Object val) {
//			if (val instanceof String) {
//				return STRING;
//			} else if (val instanceof Integer) {
//				return INT;
//			} else if (val instanceof Double) {
//				return DOUBLE;
//			} else if (val instanceof Float) {
//				return FLOAT;
//			} else if (val instanceof Boolean) {
//				return BOOLEAN;
//			} else if (val instanceof Date) {
//				return DATE;
//			} else if (val instanceof Color) {
//				return COLOR;
//			} else if (val instanceof Font) {
//				return FONT;
//			} else {
//				return STRING;
//			}
//		}
//	}
}
