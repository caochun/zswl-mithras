package cn.zswltech.mithras.service.flow.listener.endhandler;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.projlifecycle.enums.ProcessEventDescEnum;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.AfterLeaseAdjustInfo;
import cn.zswltech.mithras.projlifecycle.mapper.model.ProjLifecycleEvent;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseAdjustInfoService;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

import java.util.Optional;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;

/**
 * 项目评审流程结束
 *
 * @author wangchuanhao
 * @date 2022/11/9 2:34 PM
 */
@Component
public class AfterLeaseAdjustProcessEndHandler extends AbstractProcessEndHandler implements ILifecycleProcessor{

    @Resource
    private AfterLeaseAdjustInfoService infoService;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), ProcessModelTypeEnum.AfterLeaseExtendFlow.name(), ProcessModelTypeEnum.AfterLeaseRepaymentFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        infoService.processEnd(Long.valueOf(endContext.getBusinessKey()), endContext.getEndType(), Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId());
        processLifecycle(endContext);
    }

    @Override
    public void customfillLifcycleEvent(ProjLifecycleEvent endEvent, ProcessEndContext endContext) {
        AfterLeaseAdjustInfo afterLeaseAdjustInfo = infoService.getById(Long.valueOf(endContext.getBusinessKey()));
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(afterLeaseAdjustInfo.getProjId());
        getProjIdAndProjType(endEvent,projReviewBaseInfo);
        endEvent.setEvent(Optional.ofNullable(ProcessEventDescEnum.getByName(endContext.getModelKey())).map(ProcessEventDescEnum::getEvent).orElse("租后调整审批"));
    }
}
