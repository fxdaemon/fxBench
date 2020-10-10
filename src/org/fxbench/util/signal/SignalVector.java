/*
 * Copyright 2006 FXCM LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fxbench.util.signal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fxbench.entity.BaseEntity;
import org.fxbench.entity.Field;

/**
 * Abstract class that is base for all tables
 * containing business data.
 */
public abstract class SignalVector extends Signaler {
	protected final List<BaseEntity> listEntity = new ArrayList<BaseEntity>();
    protected final Map<String, BaseEntity> mapEntity = new HashMap<String, BaseEntity>();
    private Comparator comparator = null;
    
    public boolean isEmpty() {
    	synchronized (listEntity) {
    		return listEntity.isEmpty();
    	}
    }
    
    public int size() {
    	synchronized (listEntity) {
    		return listEntity.size();
    	}
    }
 
    public void clear() {
    	synchronized (listEntity) {
    		listEntity.clear();
    		mapEntity.clear();
    	}
    }
    
    public void add(BaseEntity entity) {
    	int index = size();
    	if (comparator != null) {
            // Search for the object to determine its insertion point
    		synchronized (listEntity) {
    			index = Collections.binarySearch(listEntity, entity, comparator);
    		}
            if (index < 0) {
                index = -index - 1;
            }
        }
    	
    	synchronized (listEntity) {
	    	listEntity.add(index, entity);
	    	mapEntity.put(entity.getKey(), entity);
    	}
    	notify(Signal.newAddSignal(index, entity));
    }
    
    /**
     * Sends CHANGE signal. Should be called when some element was changed directly (not with set() method call).
     */
    public void elementChanged(int aIndex) {
        try {
            Object obj = get(aIndex);
            notify(Signal.newChangeSignal(aIndex, obj, null));
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
    }
    
    public void elementChanged(List<Integer> indexList) {
    	for (Integer integer : indexList) {
            elementChanged(integer);
        }
    }

    /**
     * Returns an enumeration of the components of this SignalVector.
     *
     * @see List#elements()
     */
//    public Enumeration elements() {
//        return Collections.enumeration(listEntity);
//    }
    
    public BaseEntity get(int index) {
    	BaseEntity baseEntity = null;
        try {
        	synchronized (listEntity) {
        		baseEntity = listEntity.get(index);
        	}
        } catch (Exception e) {
        }
        return baseEntity;
    }

    public BaseEntity get(String key) {
    	synchronized (listEntity) {
    		return mapEntity.get(key);
    	}
    }

    public BaseEntity set(int index, BaseEntity entity) {
    	BaseEntity oldEntity = null;
    	try {
    		synchronized (listEntity) {
    			oldEntity = listEntity.set(index, entity);
    			mapEntity.put(entity.getKey(), entity);
    		}
        	notify(Signal.newChangeSignal(index, entity, oldEntity));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return oldEntity;
    }

    public int indexOf(String key) {
    	int index = -1;
    	int i = 0;
    	synchronized (listEntity) {
	        for (BaseEntity e : listEntity) {
	        	if (e.getKey().equals(key)){
	        		index = i;
	        		break;
	        	}
	        	i++;
	        }
    	}
        return index;
    }
    
    public int indexOf(BaseEntity entity) {
    	return indexOf(entity.getKey());
    }
    
    public int indexOf(Object obj) {
    	return indexOf(((BaseEntity)obj).getKey());
    }
      
    public BaseEntity remove(int index) {
    	BaseEntity entity = null;
    	try {
    		synchronized (listEntity) {
		    	entity = listEntity.remove(index);
		    	if (entity instanceof BaseEntity) {
		    		mapEntity.remove(entity.getKey());
		    	}
    		}
	    	notify(Signal.newRemoveSignal(index, entity));
    	} catch (Exception ex) {
            ex.printStackTrace();
        }
    	return entity;
    }
    
    public BaseEntity remove(String key) {
    	int index = indexOf(key);
    	if (index == -1) {
    		return null;
    	} else {
    		return remove(index);
    	}
    }
    
    public BaseEntity remove(BaseEntity entity) {
    	return remove(entity.getKey());
    }
    
    public void setComparator(Comparator aComparator) {
    	comparator = aComparator;
        sort();
    }
    
    public void sort() {
        if (comparator != null) {
        	synchronized (listEntity) {
        		Collections.sort(listEntity, comparator);
        	}
        }
    }
    
    protected Field getFieldTotal(int fieldNo) {
    	try {
			double total = 0;
			synchronized (listEntity) {
				for (int i = 0; i < listEntity.size(); i++) {
					total += Double.valueOf(listEntity.get(i).getField(fieldNo).toString()).doubleValue();
				}
			}
			Field totalField = get(0).getField(fieldNo).clone();
			totalField.setFieldVal(total);
			return totalField;
        } catch (Exception e) {
        //    e.printStackTrace();
            return null;
        }
    }
    
    public abstract boolean isTotal();
    public abstract Field getTotal(int fieldNo);
}
