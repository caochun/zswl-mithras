package cn.zswltech.mithras.assetclassify.application.job;

import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.mithras.foundation.cache.RedisDelayedQueueListener;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

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
    @Resource
    private FlowTaskApiService flowTaskApiService;

    @Override
    public void invoke(String processInstanceId) {
        log.info("===" + processInstanceId + "=== ");
        // 查询流程状态
        ProcessResp processResp = flowTaskApiService.queryProcessById(processInstanceId);
        // 不是running状态，直接返回
        if (!Objects.equals(processResp.getProcessStatus(), ProcessBusinessStatusEnum.RUNNING.getType())) {
            return;
        }
        // 是running状态，掉接口自动结束
        // 由于后续只有一个客户主办节点，因此采用一键通过的形式比较简单
        autoPassExecutionPort.passAll(processInstanceId, "超过24小时自动通过");
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
