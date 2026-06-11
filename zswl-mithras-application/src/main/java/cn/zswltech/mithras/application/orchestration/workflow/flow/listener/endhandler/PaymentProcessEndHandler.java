package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler;

import cn.zswltech.mithras.projectprocess.flow.listener.endhandler.ILifecycleProcessor;

import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.projectprocess.projlifecycle.enums.ProcessEventDescEnum;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.projectprocess.projlifecycle.model.ProjLifecycleEvent;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.application.orchestration.payment.PaymentService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum.PaymentCreateFlow;

/**
 * 付款模块流程结束
 *
 * @author wangchuanhao
 * @date 2022/11/9 2:34 PM
 */
@Component
public class PaymentProcessEndHandler extends AbstractProcessEndHandler implements ILifecycleProcessor {

    @Resource
    private PaymentService paymentService;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), PaymentCreateFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        paymentService.processEnd(Long.valueOf(endContext.getBusinessKey()), endContext.getEndType(), Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId(), endContext.getModelKey());
        processLifecycle(endContext);
    }

    @Override
    public void customfillLifcycleEvent(ProjLifecycleEvent endEvent, ProcessEndContext endContext) {
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(Long.valueOf(endContext.getBusinessKey()));
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(paymentBaseInfo.getContractId());
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(contractBaseInfo.getProjReviewId());
        getProjIdAndProjType(endEvent, projReviewBaseInfo);
        endEvent.setEvent(Optional.ofNullable(ProcessEventDescEnum.getByName(endContext.getModelKey())).map(ProcessEventDescEnum::getEvent).orElse("付款审批"));
    }
}
