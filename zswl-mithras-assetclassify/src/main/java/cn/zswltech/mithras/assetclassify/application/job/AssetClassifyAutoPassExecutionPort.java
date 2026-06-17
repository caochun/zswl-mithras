package cn.zswltech.mithras.assetclassify.application.job;

public interface AssetClassifyAutoPassExecutionPort {

    void passAllIfRunning(String processInstanceId, String message);

    void commitCurrentNodeIfRunning(String processInstanceId, String activityId, String handlerId, String message);
}
