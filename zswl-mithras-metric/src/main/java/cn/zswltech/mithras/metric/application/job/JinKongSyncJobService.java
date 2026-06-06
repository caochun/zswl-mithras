package cn.zswltech.mithras.metric.application.job;

public interface JinKongSyncJobService {

    void syncGZKB(String param);

    void syncAccountBalance(String param);

    void jinKongSyncAssetJob(String param);

    void jinKongSyncProfitJob(String param);

    void jinKongSyncCashflowJob(String param);
}
