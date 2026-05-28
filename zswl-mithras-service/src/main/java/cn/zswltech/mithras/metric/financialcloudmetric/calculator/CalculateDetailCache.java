package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/8/21 14:46
 */
public class CalculateDetailCache {

    private static final ConcurrentHashMap<String, String> CACHE = new ConcurrentHashMap<>(128);

    public static void put(String key, String value) {
        CACHE.put(key, value);
    }

    public static String get(String key) {
        return CACHE.remove(key);
    }
}
