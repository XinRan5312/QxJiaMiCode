package com.xinran.testjiami.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by qixinh on 16/9/21.
 */
public class QSharedPreferences implements IStorage{
    private SharedPreferences mSharedPreferences;
    private String prefix;
    private final String dekey="?wrenglory!";
    private QSharedPreferences(Context context, String name, String prefix) {
        mSharedPreferences = context.getSharedPreferences(name, Activity.MODE_PRIVATE);
        this.prefix = prefix;
    }

    public static IStorage newInstance(Context context,String name,String prefix) {
        return new QSharedPreferences(context,name,prefix);
    }

    /**
     * int到byte[]
     * @param i
     * @return
     */
    public static byte[] i2b(int i) {
        byte[] result = new byte[4];
        //由高位到低位
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }

    private static byte[] l2b(long value) {
        byte[] result = new byte[8];
        //由高位到低位
        result[0] = (byte)((value >> 56) & 0xFF);
        result[1] = (byte)((value >> 48) & 0xFF);
        result[2] = (byte)((value >> 40) & 0xFF);
        result[3] = (byte)((value >> 32) & 0xFF);
        result[4] = (byte)((value >> 24) & 0xFF);
        result[5] = (byte)((value >> 16) & 0xFF);
        result[6] = (byte)((value >> 8) & 0xFF);
        result[7] = (byte)(value & 0xFF);
        return result;
    }

    /**
     * byte[]转int
     * @param bytes
     * @return
     */
    public static int b2i(byte[] bytes) {
        int value = 0;
        //由高位到低位
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (bytes[i] & 0x000000FF) << shift;//往高位游
        }
        return value;
    }

    private static long b2l(byte[] b) {
        long result = 0;
        //由高位到低位
        for (int i = 0; i < 8; i++) {
            int shift= (8 - 1 - i) * 8;
            result += (b[i] & (long)0xFF) << shift;//往高位游
        }
        return result;
    }

    /**
     * byte 类型位 :
     *              0:byte[]
     *              1:short
     *              2:int
     *              3:long
     *              4:float
     *              5:double
     *              6:boolean
     *              7:String
     *              8:Serializable
     * @param type
     * @param key
     * @param value
     * @return
     */
    private boolean putBytes(int type,String key,byte[] value) {
        if(value == null || value.length == 0) {
            final SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(getPrefix() + key,"");
            if (Build.VERSION.SDK_INT < 9) {
                return editor.commit();
            } else {
                new Object(){
                    void apply() {
                        editor.apply();
                    }
                }.apply();
                return  true;
            }
        }
        try {
            byte[] data = new byte[value.length + 1];
            data[0] = (byte) type;
            System.arraycopy(value, 0, data, 1, value.length);
            value = QxDESJieMi.desEncode(data, dekey);

            final SharedPreferences.Editor editor = mSharedPreferences.edit();
            String v = Base64.encodeToString(value, Base64.NO_WRAP);
            editor.putString(getPrefix() + key,v);
            if (Build.VERSION.SDK_INT < 9) {
                return editor.commit();
            } else {
                new Object(){
                    void apply() {
                        editor.apply();
                    }
                }.apply();
                return  true;
            }
        } catch (Throwable e) {
        }
        return false;
    }

    private byte[] de(String value) {
        if(!TextUtils.isEmpty(value)) {
            byte[] data = Base64.decode(value, Base64.NO_WRAP);
            data = QxDESJieMi.desDecode(data, dekey);
            return data;
        }
        return null;
    }

    private byte[] getBytes(String key) {
        String value = mSharedPreferences.getString(getPrefix() + key, null);
        return de(value);
    }

    private byte[] getBytesAndCheck(int type, String key) {
        byte[] data = getBytes(key);
        if(data != null && data.length > 0) {
            //check type
            if(data[0] != (byte)type) {
                throw new RuntimeException("类型不匹配");
            }
            byte[] result = new byte[data.length - 1];
            System.arraycopy(data, 1, result, 0, result.length);
            data = result;
        }

        return data;
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public boolean putBytes(String key, byte[] value) {
        return putBytes(0,key,value);
    }

    public byte[] getBytes(String key, byte[] defaultValue) {
        try {
            byte[] data = getBytesAndCheck(0, key);
            if (data == null || data.length == 0) {
                data = defaultValue;
            }
            return data;
        } catch (Throwable e) {
        }
        return defaultValue;
    }



    @Override
    public boolean putInt(String key, int value) {
        byte[] result = i2b(value);
        return putBytes(2,key, result);
    }

    @Override
    public boolean putShort(String key, short value) {
        byte[] result = new byte[2];
        //由高位到低位
        result[0] = (byte)((value >> 8) & 0xFF);
        result[1] = (byte)(value & 0xFF);
        return putBytes(1,key, result);
    }

    @Override
    public boolean putLong(String key, long value) {
        byte[] result = l2b(value);
        return putBytes(3,key, result);
    }

    @Override
    public boolean putFloat(String key, float value) {
        byte[] result = i2b(Float.floatToIntBits(value));
        return putBytes(4, key, result);
    }

    @Override
    public boolean putDouble(String key, double value) {
        byte[] result = l2b(Double.doubleToLongBits(value));
        return putBytes(5, key, result);
    }

    @Override
    public boolean putString(String key, String value) {
        try {
            byte[] data = value == null ? null : value.getBytes("UTF-8");
            return putBytes(7,key, data);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean putBoolean(String key, boolean value) {
        byte b = (byte) (value ? 1 : 0);
        byte[] data = new byte[]{b};
        return putBytes(6,key, data);
    }

    public boolean putSerializable(String key, Serializable value) {
        byte[] data = null;
        if(value != null) {
            ByteArrayOutputStream bytestream = null;
            ObjectOutputStream objectstream = null;
            try {
                bytestream = new ByteArrayOutputStream();
                objectstream = new ObjectOutputStream(bytestream);
                objectstream.writeObject(value);
                objectstream.flush();
                data = bytestream.toByteArray();
            } catch (IOException e) {
                return false;
            } finally {
                if (objectstream != null) {
                    try {
                        objectstream.close();
                    } catch (IOException e) {
                    }
                }
                if (bytestream != null) {
                    try {
                        bytestream.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        return putBytes(8,key,data);
    }

    @Override
    public short getShort(String key, short defaultValue) {
        short result = defaultValue;
        try {
            byte[] data = getBytesAndCheck(1, key);
            if (data != null) {
                result = 0;
                //由高位到低位
                for (int i = 0; i < 2; i++) {
                    int shift= (2 - 1 - i) * 8;
                    result += (data[i] & 0xFF) << shift;//往高位游
                }
            }
        } catch (Throwable e) {
        }
        return result;
    }

    @Override
    public int getInt(String key, int defaultValue) {
        int result = defaultValue;
        try {
            byte[] data = getBytesAndCheck(2, key);
            if (data != null) {
                result = b2i(data);
            }
        } catch (Throwable e) {
        }
        return result;
    }

    @Override
    public long getLong(String key, long defaultValue) {
        long result = defaultValue;
        try {
            byte[] data = getBytesAndCheck(3, key);
            if (data != null) {
                result = b2l(data);
            }
        } catch (Throwable e) {
        }
        return result;
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        float result = defaultValue;
        try {
            byte[] data = getBytesAndCheck(4, key);
            if (data != null) {
                result = Float.intBitsToFloat(b2i(data));
            }
        } catch (Throwable e) {
        }
        return result;
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        double result = defaultValue;
        try {
            byte[] data = getBytesAndCheck(5, key);//8 bit
            if (data != null) {
                result = Double.longBitsToDouble(b2l(data));
            }
        } catch (Throwable e) {
        }
        return result;
    }

    @Override
    public String getString(String key, String defaultValue) {
        String result = defaultValue;
        try {
            byte[] data = getBytesAndCheck(7, key);//8 bit
            if (data != null) {
                result = new String(data,"UTF-8");
            }
        } catch (Throwable e) {
        }
        return result;
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        boolean result = defaultValue;
        try {
            byte[] data = getBytesAndCheck(6, key);//1 bit
            if (data != null) {
                result = data[0] == 1;
            }
        } catch (Throwable e) {
        }
        return result;
    }

    @Override
    public <T extends Serializable> T getSerializable(String key, Class<T> clazz, T defaultValue) {
        ByteArrayInputStream bytestream = null;
        ObjectInputStream objectstream = null;

        T result = defaultValue;
        try {
            byte[] data = getBytesAndCheck(8, key);
            if (data != null) {
                objectstream = new ObjectInputStream(new ByteArrayInputStream(data));
                result = (T) objectstream.readObject();
            }
        } catch (Throwable e) {
        } finally {
            if (objectstream != null) {
                try {
                    objectstream.close();
                } catch (IOException e) {
                }
            }
            if (bytestream != null) {
                try {
                    bytestream.close();
                } catch (IOException e) {
                }
            }
        }
        return result;
    }

    @Override
    public boolean remove(String key) {
        try {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.remove(getPrefix() + key);
            return editor.commit();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean contains(String key) {
        try {
            return mSharedPreferences.contains(getPrefix() + key);
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public List<String> getKeys() {
        ArrayList<String> result = new ArrayList<String>();
        Map<String, ?> all = mSharedPreferences.getAll();
        if (all != null && !all.isEmpty()) {
            String p = getPrefix();
            for (String key : all.keySet()) {
                if(key.startsWith(p)) {
                    String newKey = key.substring(p.length());
                    result.add(newKey);
                }
            }
        }
        return result;
    }

    @Override
    public Map<String, Object> getAll() {
        Map<String, Object> map = new HashMap<String, Object>();

        Map<String, ?> all = mSharedPreferences.getAll();
        if(all != null && !all.isEmpty()) {
            String p = getPrefix();
            for (String key : all.keySet()) {
                if(key.startsWith(p)) {
                    String newKey = key.substring(p.length());
                    map.put(newKey,all.get(key));
                }
            }
        }

        for (String key : map.keySet()) {
            String strValue = String.valueOf(map.get(key));
            byte[] data = de(strValue);
            Object value = null;
            if(data != null && data.length > 0) {
                int type = data[0];
                byte[] rbytes = new byte[data.length - 1];
                System.arraycopy(data, 1, rbytes, 0, rbytes.length);
                data = rbytes;

                switch (type) {
                    case 0:
                        value = data;
                        break;
                    case 1:
                        short result = 0;
                        //由高位到低位
                        for (int i = 0; i < 2; i++) {
                            int shift= (2 - 1 - i) * 8;
                            result += (data[i] & 0xFF) << shift;//往高位游
                        }
                        value = result;
                        break;
                    case 2:
                        value = b2i(data);
                        break;
                    case 3:
                        value = b2l(data);
                        break;
                    case 4:
                        value = Float.intBitsToFloat(b2i(data));
                        break;
                    case 5:
                        value = Double.longBitsToDouble(b2l(data));
                        break;
                    case 6:
                        value = Boolean.valueOf(data[0] == 1);
                        break;
                    case 7:
                        try {
                            value = new String(data,"UTF-8");
                        } catch (UnsupportedEncodingException e) {
                        }
                        break;
                    case 8:
                        ByteArrayInputStream bytestream = null;
                        ObjectInputStream objectstream = null;

                        try {
                            objectstream = new ObjectInputStream(new ByteArrayInputStream(data));
                            value = objectstream.readObject();
                        } catch (Throwable e) {
                        } finally {
                            if (objectstream != null) {
                                try {
                                    objectstream.close();
                                } catch (IOException e) {
                                }
                            }
                            if (bytestream != null) {
                                try {
                                    bytestream.close();
                                } catch (IOException e) {
                                }
                            }
                        }
                        break;
                }
            }

            map.put(key,value);
        }
        return map;
    }

    @Override
    public boolean cleanAllStorage() {
        Map<String, ?> all = mSharedPreferences.getAll();
        if(all != null && !all.isEmpty()) {
            List<String> delList = new ArrayList<String>();
            String p = getPrefix();
            for (String key : all.keySet()) {
                if(key.startsWith(p)) {
                    delList.add(key);
                }
            }
            if(!delList.isEmpty()) {
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                for (String key : delList) {
                    editor.remove(key);
                }
                return editor.commit();
            }
        }
        return false;
    }

    private String getPrefix() {
        return hashKeyForDisk(prefix) + "_";
    }

    /**
     * A hashing method that changes a string (like a URL) into a hash suitable for using as a disk filename.
     */
    public static String hashKeyForDisk(String key) {
        if (key == null) {
            return null;
        }
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
