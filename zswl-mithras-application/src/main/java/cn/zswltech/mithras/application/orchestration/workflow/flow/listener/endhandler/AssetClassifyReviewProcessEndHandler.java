package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler;

import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;

import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.VersionTypeEnum;
import cn.zswltech.mithras.assetclassify.application.AssetClassifyNodeRecordService;
import cn.zswltech.mithras.assetclassify.enums.AssetClassifyBizNodeEnum;
import cn.zswltech.mithras.assetclassify.enums.AssetClassifyStatusEnum;
import cn.zswltech.mithras.application.orchestration.assetclassify.AssetClassifyService;
import cn.zswltech.mithras.application.orchestration.assetclassify.AssetClassifyVersionService;
import cn.zswltech.mithras.assetclassify.application.lib.AssetClassifyLibVersionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;

/**
 * 项目评审流程结束
 *
 * @author wangchuanhao
 * @date 2022/11/9 2:34 PM
 */
@Slf4j
@Component
public class AssetClassifyReviewProcessEndHandler extends AbstractProcessEndHandler {

    @Resource
    private AssetClassifyVersionService assetClassifyVersionService;
    @Resource
    private AssetClassifyNodeRecordService assetClassifyNodeRecordService;
    @Resource
    private AssetClassifyLibVersionService assetClassifyLibVersionService;
    @Resource
    private AssetClassifyService assetClassifyService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), ProcessModelTypeEnum.AssetClassifyReviewFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        //审批完毕，更新节点状态
        Long startUserId = Long.valueOf(endContext.getStartUserId());
        Long assetClassifyId = Long.valueOf(endContext.getBusinessKey());
        String processInstanceId = endContext.getProcessInstanceId();
        boolean processPass = ProcessBusinessStatusEnum.success(endContext.getEndType());
        if (processPass) {
            assetClassifyNodeRecordService.updateNodeStatue(assetClassifyId, AssetClassifyBizNodeEnum.REVIEW.name(), AssetClassifyStatusEnum.FINISH.name());
            assetClassifyLibVersionService.recordVersion(assetClassifyId, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, VersionTypeConstants.NORMAL);
            // 异步生成评审会的汇总审批表
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    new Thread(() -> {
                        try {
                            assetClassifyService.generateSummaryFile(assetClassifyId, AssetClassifyBizNodeEnum.REVIEW_MEETING);
                        } catch (Exception e) {
                            log.error("资产五级分类<复核>流程结束，生成<认定汇总审批表_评审会>发生异常", e);
                        }
                    }).start();
                }
            });
        } else {
            assetClassifyNodeRecordService.updateNodeStatue(assetClassifyId, AssetClassifyBizNodeEnum.REVIEW.name(), AssetClassifyStatusEnum.WAIT.name());
            assetClassifyLibVersionService.recordVersion(assetClassifyId, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, VersionTypeConstants.INVALID);
        }
    }
}
