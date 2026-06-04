package cn.zswltech.mithras.associationreport.storedata;

import java.io.InputStream;

/**
 * @author dingqi
 * @date 2025/4/18
 * @description
 */
public interface DataStore {
    void storeFromExcel(String reportInstanceId, InputStream inputStream);

    void storeFromSystemJob(String reportInstanceId);

    default boolean storeFromSystemJobCheck(int year, int period) {
        return false;
    }
}
