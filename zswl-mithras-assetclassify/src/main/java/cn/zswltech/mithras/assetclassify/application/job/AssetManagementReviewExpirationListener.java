package cn.zswltech.mithras.assetclassify.application.job;

import cn.zswltech.mithras.assetclassify.application.port.AssetClassifyAutoPassExecutionPort;
import cn.zswltech.mithras.foundation.cache.RedisDelayedQueueListener;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @description: 超时消息监听
 * @author: luyujie
 * @date: 2026/1/6 10:44
 */
@Component
@Slf4j
public class AssetManagementReviewExpirationListener implements RedisDelayedQueueListener<String> {

    private static final String ASSET_MANAGEMENT_REVIEW_ACTIVITY_ID = "assetManagementReview";

    @Resource
    private AssetClassifyAutoPassExecutionPort autoPassExecutionPort;

    @Override
    public void invoke(String processInstanceId) {
        log.info("===" + processInstanceId + "=== ");
        autoPassExecutionPort.commitCurrentNodeIfRunning(processInstanceId, ASSET_MANAGEMENT_REVIEW_ACTIVITY_ID,
                String.valueOf(GlobalConstants.READONLY_ID), "超过24小时自动通过");
    }
}
