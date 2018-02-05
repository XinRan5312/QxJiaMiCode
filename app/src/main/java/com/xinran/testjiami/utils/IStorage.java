package com.xinran.testjiami.utils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by qixinh on 16/9/21.
 */
public interface IStorage {

    /**
     * 根据key值存储value
     *
     * @param key
     * @param value
     * @return
     */
    public boolean putSerializable(String key, Serializable value);

    public boolean putBytes(String key, byte[] value);

    public boolean putInt(String key, int value);

    public boolean putShort(String key, short value);

    public boolean putLong(String key, long value);

    public boolean putFloat(String key, float value);

    public boolean putDouble(String key, double value);

    public boolean putString(String key, String value);

    public boolean putBoolean(String key, boolean value);

    /**
     * 根据Key值获取value
     *
     * @param key
     * @param <T>
     * @return
     */
    public <T extends Serializable> T getSerializable(String key, Class<T> clazz, T defValue);

    public int getInt(String key, int defaultValue);

    public double getDouble(String key, double defaultValue);

    public float getFloat(String key, float defaultValue);

    public short getShort(String key, short defaultValue);

    public long getLong(String key, long defaultValue);

    public String getString(String key, String defaultValue);

    public boolean getBoolean(String key, boolean defaultValue);

    public byte[] getBytes(String key, byte[] defaultValue);

    /**
     * 删除对应的key
     *
     * @param key
     * @return
     */
    public boolean remove(String key);

    public boolean contains(String key);

    public Map<String, Object> getAll();

    public List<String> getKeys();

    public boolean cleanAllStorage();

}
