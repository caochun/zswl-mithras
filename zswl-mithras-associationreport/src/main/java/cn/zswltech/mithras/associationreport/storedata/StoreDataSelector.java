package cn.zswltech.mithras.associationreport.storedata;

import cn.zswltech.mithras.associationreport.storedata.DataStore;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dingqi
 * @date 2025/4/18
 * @description
 */
public class StoreDataSelector {
    private static final Map<String, DataStore> map = new HashMap<>();

    public static void register(String category, DataStore dataStore) {
        map.put(category, dataStore);
    }

    public static DataStore getInstance(String category) {
        return map.get(category);
    }
}
