package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler;

import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;

import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionBaseInfoREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionBaseInfoRSP;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.VersionTypeEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.enums.common.ProcessStatus;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistribution;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.kpi.KpiProjectDistributionBaseInfoService;
import cn.zswltech.mithras.application.orchestration.kpi.KpiProjectDistributionService;
import cn.zswltech.mithras.kpi.application.distribution.lib.KpiProjectDistributionLibVersionService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;

/**
 * @author dingqi
 * @date 2023/6/16
 * @description
 */
@Component
public class KpiProjectDistributionProcessEndHandler extends AbstractProcessEndHandler {
    @Resource
    private KpiProjectDistributionService kpiProjectDistributionService;
    @Resource
    private KpiProjectDistributionBaseInfoService kpiProjectDistributionBaseInfoService;
    @Resource
    private KpiProjectDistributionLibVersionService kpiProjectDistributionLibVersionService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), ProcessModelTypeEnum.KpiProjectDistributionCreateFlow.name(), ProcessModelTypeEnum.KpiProjectDistributionModifyFlow.name(), ProcessModelTypeEnum.KpiProjectDistributionTransferFlow.name());
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void handle(ProcessEndContext endContext) {
        boolean isCreate = Objects.equals(endContext.getModelKey(), ProcessModelTypeEnum.KpiProjectDistributionCreateFlow.name());
        boolean isTransfer = Objects.equals(endContext.getModelKey(), ProcessModelTypeEnum.KpiProjectDistributionTransferFlow.name());
        Long projectDistributionId = Long.valueOf(endContext.getBusinessKey());
        KpiProjectDistribution kpiProjectDistribution = kpiProjectDistributionService.getById(projectDistributionId);
        if (Objects.isNull(kpiProjectDistribution)) {
            throw new MithrasException("项目分配信息不存在");
        }
        KpiProjectDistributionBaseInfoREQ req = new KpiProjectDistributionBaseInfoREQ();
        req.setProjectDistributionId(projectDistributionId);
        KpiProjectDistributionBaseInfoRSP rsp = kpiProjectDistributionBaseInfoService.detail(req);
        // 固化动态信息
        KpiProjectDistributionBaseInfo toUpdate = new KpiProjectDistributionBaseInfo();
        toUpdate.setId(rsp.getId());
        toUpdate.setProjClassify(rsp.getProjClassify());
        toUpdate.setProjSource(rsp.getProjSource());
        if (isCreate) {
            toUpdate.setChangeReason("创建");
        } else if (isTransfer) {
            toUpdate.setChangeReason("客户移交");
        } else {
            toUpdate.setChangeReason("手工调整");
        }
        kpiProjectDistributionBaseInfoService.updateById(toUpdate);
        // 流程相关处理
        ProcessBusinessStatusEnum item = ProcessBusinessStatusEnum.getByType(endContext.getEndType());
        if (Objects.isNull(item)) {
            throw new MithrasException("未知的审批流状态类型");
        }
        switch (item) {
            case PASS:
            case PASS_ALL: {
                if (isCreate) {
                    kpiProjectDistribution.setDistributionStatus(YesOrNoNumberEnum.YES.getCode());
                }
                kpiProjectDistribution.setApprovalStatus(ProcessStatus.APPROVAL_PASS.name());
                kpiProjectDistributionService.updateById(kpiProjectDistribution);
                kpiProjectDistributionLibVersionService.recordVersion(kpiProjectDistribution.getId(), VersionTypeEnum.APPROVAL, Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId(), VersionTypeConstants.NORMAL);
                break;
            }
            case CANCEL:
            case REJECT:
            case REJECT_ALL: {
                kpiProjectDistribution.setApprovalStatus(ProcessStatus.UN_SUBMIT.name());
                kpiProjectDistributionService.updateById(kpiProjectDistribution);
                kpiProjectDistributionLibVersionService.recordVersion(kpiProjectDistribution.getId(), VersionTypeEnum.APPROVAL, Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId(), VersionTypeConstants.INVALID);
                kpiProjectDistributionLibVersionService.reset(kpiProjectDistribution.getId());
                break;
            }
            /*case CANCEL: {
                kpiProjectDistribution.setApprovalStatus(ProcessStatus.UN_SUBMIT.name());
                kpiProjectDistributionService.updateById(kpiProjectDistribution);
                kpiProjectDistributionLibVersionService.recordVersion(kpiProjectDistribution.getId(), VersionTypeEnum.APPROVAL, Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId(), VersionTypeConstants.INVALID);
                kpiProjectDistributionLibVersionService.reset(kpiProjectDistribution.getId());
                break;
            }*/
            default: {
                throw new MithrasException("无法处理的流程结束类型");
            }
        }
    }
}
