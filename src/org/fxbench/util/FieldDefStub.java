package org.fxbench.util;

import java.util.EnumMap;
import java.util.Map;

public class FieldDefStub<E extends Enum<E>> {
	private E[] fieldDefArray;
	private Map<E, Integer> fieldDefMap;

	public FieldDefStub(E[] fieldDefArray, Class<E> cls) {
		this.fieldDefArray = fieldDefArray;
		this.fieldDefMap = new EnumMap<E, Integer>(cls);
		for (int i = 0; i < fieldDefArray.length; i++) {
			this.fieldDefMap.put(fieldDefArray[i], i);
		}
	}
	
	public E[] getFieldDefArray() {
		return fieldDefArray;
	}

	public Map<E, Integer> getFieldDefMap() {
		return fieldDefMap;
	}
	
	public String[] getFiledNameArray() {
		String[] fieldNameArray = new String[fieldDefArray.length];
		for (int i = 0; i < fieldDefArray.length; i++) {
			fieldNameArray[i] = fieldDefArray[i].name();
		}
		return fieldNameArray;
	}

	public int getFieldNo(E field) {
		Integer fieldNo = fieldDefMap.get(field);
		if (fieldNo == null) {
			return -1;
		} else {
			return fieldNo.intValue();
		}
	}
}
