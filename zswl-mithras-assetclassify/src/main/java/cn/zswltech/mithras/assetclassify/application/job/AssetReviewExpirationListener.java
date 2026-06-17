package cn.zswltech.mithras.assetclassify.application.job;

import cn.zswltech.mithras.foundation.cache.RedisDelayedQueueListener;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @description: 超时消息监听
 * @author: zhaozhengkang
 * @date: 2023/8/23 10:54
 */
@Component
@Slf4j
public class AssetReviewExpirationListener implements RedisDelayedQueueListener<String>, AssetClassifyReviewAutoPassJobService {

    @Resource
    private AssetClassifyAutoPassExecutionPort autoPassExecutionPort;

    @Override
    public void invoke(String processInstanceId) {
        log.info("===" + processInstanceId + "=== ");
        // 是running状态，掉接口自动结束
        // 由于后续只有一个客户主办节点，因此采用一键通过的形式比较简单
        autoPassExecutionPort.passAllIfRunning(processInstanceId, "超过24小时自动通过");
    }

    @Override
    public void reviewAutoPass(String processInstanceIds) {
        if (StrUtil.isBlank(processInstanceIds)) {
            return;
        }
        String[] ids = processInstanceIds.split(",");
        for (String processInstanceId : ids) {
            try {
                this.invoke(processInstanceId);
            } catch (Exception e) {
                log.error("资产五级分类-自动通过复核流程任务发生异常[{}]", processInstanceId, e);
            }
        }
    }
}
