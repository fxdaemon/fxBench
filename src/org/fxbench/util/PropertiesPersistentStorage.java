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
package org.fxbench.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import org.fxbench.BenchApp;
import org.fxbench.util.properties.PropertyManager;

/**
 * PropertiesPersistentStorage class
 * This is simple PersistentStorage implementation
 * that stores all values in the properties file.
 */
class PropertiesPersistentStorage extends PersistentStorage {
    /**
     * Properties storage directory
     */
    private static final String PROPERTIES_STORAGE_DIR = "fxbench.properties_storage_dir";
    /**
     * Properties storage name
     */
    private static final String PROPERTIES_STORAGE_NAME = "fxbench.properties_storage_name";
    /**
     * Default properties storage directory
     */
    private static final String DEFAULT_PROPERTIES_STORAGE_DIR = "user.dir";
    private static final String DEFAULT_PROPERTIES_USER_HOME = "user.home";
    /**
     * Default properties storage name
     */
//    private static final String DEFAULT_PROPERTIES_STORAGE_NAME = "fxbench.properties";
    /**
     * Properties file header
     */
    private static final String PROPERTIES_FILE_HEADER = "This is the properties file for persistent storage";
    /**
     * Error string in case of null function argument
     */
    private static final String NULL_ARG_ERROR = "Null argument: ";
    /**
     * Properties that are used to store values.
     * All values are stored as strings.
     * Dates are stored as timestamps.
     * This object is initialized (loaded) during create of the storage
     * and stored during flush.
     */
//    private Properties mProperties;
    /**
     * Path to the properties file.
     */
    private String msPropertiesFilename;

    /**
     * Constructor.
     */
    PropertiesPersistentStorage() {
        String sPropertiesStorageDir;
        String sPropertiesStorageName;
        String sFilesep;
        FileInputStream in = null;
//        mProperties = new Properties();
        // getting properties file name
        sPropertiesStorageDir = System.getProperty(PROPERTIES_STORAGE_DIR);
        if (sPropertiesStorageDir == null) {
            sPropertiesStorageDir = System.getProperty(DEFAULT_PROPERTIES_USER_HOME);
        }
        sPropertiesStorageName = System.getProperty(PROPERTIES_STORAGE_NAME);
        if (sPropertiesStorageName == null) {
//            sPropertiesStorageName = DEFAULT_PROPERTIES_STORAGE_NAME;
        	sPropertiesStorageName = BenchApp.APP_NAME + "." + BenchApp.getHost() + ".properties";
        }
        sFilesep = System.getProperty("file.separator");
        if (!sPropertiesStorageDir.endsWith(sFilesep)) {
            sPropertiesStorageDir += sFilesep;
        }
        msPropertiesFilename = sPropertiesStorageDir + sPropertiesStorageName;
        // opening properties file
        try {
            in = new FileInputStream(msPropertiesFilename);//throws FileNotFoundException
//            mProperties.load(in);
            PropertyManager.getInstance().loadFromXml(in);
        } catch (Exception ex) {
        	PropertyManager.getInstance().loadFromXml(null);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    /**
     * This method flushes all changes to the persistent location.
     * Should be called before application exit to ensure
     * that in the next session the same values will be retrieved.
     */
    public void flush() throws Exception {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(msPropertiesFilename);
//            mProperties.store(out, PROPERTIES_FILE_HEADER);
            PropertyManager.getInstance().saveToXml(
            		out, BenchApp.getInst().getTradeDesk().getLoginUserName());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception ex) {
                }
            }
        }
    }

    /**
     * Gets named value of the specific type from the storage.
     *
     * @param asKey     key for the value
     * @param abDefault default value if corresponding one was not found
     *
     * @return value corresponding to the specific type from the storage.
     */
    public boolean getBoolean(String asKey, boolean abDefault)
            throws NullPointerException {
//        String sValue;
        if (asKey == null) {
            throw new NullPointerException(NULL_ARG_ERROR + "org.fxbench.util.PropertiesPersistentStorage.getBoolean");
        }
//        sValue = mProperties.getProperty(asKey);
//        if (sValue == null) {
//            return abDefault;
//        }
//        try {
//            return Boolean.valueOf(sValue).booleanValue();
//        } catch (NumberFormatException ex) {
//            return abDefault;
//        }
        Object val = PropertyManager.getInstance().getVal(asKey);
        return val == null ? abDefault : (Boolean)val;
    }

    /**
     * Gets named value of the specific type from the storage.
     *
     * @param asKey      key for the value
     * @param adtDefault default value if corresponding one was not found
     *
     * @return value corresponding to the specific type from the storage.
     */
    public Date getDate(String asKey, Date adtDefault)
            throws NullPointerException {
//        String sValue;
        if (asKey == null) {
            throw new NullPointerException(NULL_ARG_ERROR + "org.fxbench.util.PropertiesPersistentStorage.getDate");
        }
//        sValue = mProperties.getProperty(asKey);
//        try {
//            return new Date(Long.parseLong(sValue));
//        } catch (NumberFormatException ex) {
//            return adtDefault;
//        }
        Object val = PropertyManager.getInstance().getVal(asKey);
        return val == null ? adtDefault : (Date)val;
    }

    /**
     * Gets named value of the specific type from the storage.
     *
     * @param asKey       key for the value
     * @param adblDefault default value if corresponding one was not found
     *
     * @return value corresponding to the specific type from the storage.
     */
    public double getDouble(String asKey, double adblDefault)
            throws NullPointerException {
//        String sValue;
        if (asKey == null) {
            throw new NullPointerException(NULL_ARG_ERROR + "org.fxbench.util.PropertiesPersistentStorage.getDouble");
        }
//        sValue = mProperties.getProperty(asKey);
//        if (sValue == null) {
//            return adblDefault;
//        }
//        try {
//            return Double.valueOf(sValue);
//        } catch (NumberFormatException ex) {
//            return adblDefault;
//        }
        Object val = PropertyManager.getInstance().getVal(asKey);
        return val == null ? adblDefault : (Double)val;
    }

    /**
     * Gets named value of the specific type from the storage.
     *
     * @param asKey     key for the value
     * @param aiDefault default value if corresponding one was not found
     *
     * @return value corresponding to the specific type from the storage.
     */
    public int getInt(String asKey, int aiDefault)
            throws NullPointerException {
        String sValue;
        if (asKey == null) {
            throw new NullPointerException(NULL_ARG_ERROR + "org.fxbench.util.PropertiesPersistentStorage.getInt");
        }
//        sValue = mProperties.getProperty(asKey);
//        if (sValue == null) {
//            return aiDefault;
//        }
//        try {
//            return Integer.parseInt(sValue);
//        } catch (NumberFormatException ex) {
//            return aiDefault;
//        }
        Object val = PropertyManager.getInstance().getVal(asKey);
        return val == null ? aiDefault : (Integer)val;
    }

    /**
     * Gets named value of the specific type from the storage.
     *
     * @param asKey     key for the value
     * @param asDefault default value if corresponding one was not found
     *
     * @return value corresponding to the specific type from the storage.
     */
    public String getString(String asKey, String asDefault)
            throws NullPointerException {
        String sRet;
        if (asKey == null) {
            throw new NullPointerException(NULL_ARG_ERROR + "org.fxbench.util.PropertiesPersistentStorage.getString");
        }
//        sRet = mProperties.getProperty(asKey);
//        return sRet != null ? sRet : asDefault;
        Object val = PropertyManager.getInstance().getVal(asKey);
        return val == null ? asDefault : (String)val;
    }

    /**
     * Removes the specified entry.
     */
    public void remove(String asKey) {
//        System.out.println("PropertiesPersistentStorage remove asKey = " + asKey);
//        mProperties.remove(asKey);
    }

    /**
     * Sets the value with specified name.
     *
     * @param asKey   key for the value
     * @param asValue value to set
     */
    public void set(String asKey, String asValue) {
        if (asKey != null && asValue != null) {
//            mProperties.setProperty(asKey, asValue);
            PropertyManager.getInstance().setProperty(asKey, asValue);
        }
    }

    /**
     * Sets the value with specified name.
     *
     * @param asKey   key for the value
     * @param aiValue value to set
     */
    public void set(String asKey, int aiValue) {
        if (asKey != null) {
//            mProperties.setProperty(asKey, String.valueOf(aiValue));
        	PropertyManager.getInstance().setProperty(asKey, aiValue);
        }
    }

    /**
     * Sets the value with specified name.
     *
     * @param asKey     key for the value
     * @param adblValue value to set
     */
    public void set(String asKey, double adblValue) {
        if (asKey != null) {
//            mProperties.setProperty(asKey, String.valueOf(adblValue));
        	PropertyManager.getInstance().setProperty(asKey, adblValue);
        }
    }

    /**
     * Sets the value with specified name.
     *
     * @param asKey    key for the value
     * @param adtValue value to set
     */
    public void set(String asKey, Date adtValue) {
        if (asKey != null) {
//            mProperties.setProperty(asKey, String.valueOf(adtValue.getTime()));
        	PropertyManager.getInstance().setProperty(asKey, adtValue);
        }
    }

    /**
     * Sets the value with specified name.
     *
     * @param asKey   key for the value
     * @param abValue value to set
     */
    public void set(String asKey, boolean abValue) {
        if (asKey != null) {
//            mProperties.setProperty(asKey, String.valueOf(abValue));
        	PropertyManager.getInstance().setProperty(asKey, abValue);
        }
    }
}