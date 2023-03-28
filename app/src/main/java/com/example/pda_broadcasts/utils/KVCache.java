package com.example.pda_broadcasts.utils;

import android.content.Context;

import com.tencent.mmkv.MMKV;

import java.util.List;

/**
 * MMKV 作为类nosql型的存储方案，在存储数据时，通过追加数据实现最快的一次写入，
 * 这样虽然会提高写入速度，但是不可避免的造成了空间的浪费，如果存储数据量较大时，
 * 可以考虑通过在闲时使trim进行空间整理。同时，MMKV也有自己的空间整理触发机制。
 * 所以整体数据量不大的情况下，可以忽略trim的存在。
 */
public class KVCache {
    private static KVCache mCache;
    private Context context;
    private MMKV mKV;

    private KVCache(Context context) {
        this.context = context;
        MMKV.initialize(context);
        mKV = MMKV.defaultMMKV();
    }

    public static KVCache getInstance(Context context) {
        if (null == mCache) {
            mCache = new KVCache(context);
        }
        return mCache;
    }

    public static KVCache getInstance() {
        if (null == mCache)
            throw new RuntimeException("please init KVCache in your application");
        return mCache;
    }

    public void put(String key, Object value) {
        //清除缓存
        if (value instanceof Integer) {
            mKV.encode(key, (int) value);
        } else if (value instanceof Short) {
            mKV.encode(key, (short) value);
        } else if (value instanceof Byte) {
            mKV.encode(key, (byte) value);
        } else if (value instanceof Long) {
            mKV.encode(key, (long) value);
        } else if (value instanceof String) {
            mKV.encode(key, (String) value);
        } else if (value instanceof Boolean) {
            mKV.encode(key, (boolean) value);
        } else if (value != null) {
            throw new IllegalArgumentException();
        }
    }

    public String getString(String key) {
        return mKV.decodeString(key, "");
    }

    public String getString(String key, String def) {
        return mKV.decodeString(key, def);
    }

    public int getInt(String key) {
        return mKV.decodeInt(key, -1);
    }

    public int getInt(String key, int def) {
        return mKV.decodeInt(key, def);
    }

    public long getLong(String key) {
        return mKV.decodeLong(key, -1);
    }

    public long getLong(String key, long def) {
        return mKV.decodeLong(key, def);
    }

    public boolean getBoolean(String key) {
        return mKV.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean def) {
        return mKV.getBoolean(key, def);
    }

    public byte getByte(String key) {
        return (byte) mKV.getInt(key, 0);
    }

    public byte getByte(String key, byte def) {
        return (byte) mKV.getInt(key, def);
    }

    public short getShort(String key) {
        return (short) mKV.getInt(key, 0);
    }

    public short getShort(String key, short def) {
        return (short) mKV.getInt(key, def);
    }

    /**
     * 触发控件disk整理功能
     */
    public void tirm() {
        mKV.trim();
    }

    /**
     * 清楚所有数据
     */
    public void clearAll() {
        mKV.clearAll();
    }

    /**
     * 批量清楚
     *
     * @param keys
     */
    public void clearItems(List<String> keys) {
        for (String key : keys) {
            mKV.remove(key);
        }
    }

    public boolean contains(String key) {
        return mKV.contains(key);
    }
}
