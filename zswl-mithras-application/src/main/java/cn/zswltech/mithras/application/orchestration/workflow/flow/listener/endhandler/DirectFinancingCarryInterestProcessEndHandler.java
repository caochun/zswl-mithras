package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler;

import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.filingmaterials.enums.FilingMaterialsFilingTypeEnum;
import cn.zswltech.mithras.filingmaterials.enums.FilingMaterialsInitiationMethodEnum;
import cn.zswltech.mithras.fund.enums.financing.FinancingTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.fund.directfinancing.model.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.fund.directfinancing.mapper.FundDirectFinancingBaseInfoMapper;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingPledgeInfoService;
import cn.zswltech.mithras.fund.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
//import cn.zswltech.mithras.application.orchestration.filingmaterials.FundFilingMaterialsService;
import cn.zswltech.mithras.application.orchestration.filingmaterials.FundFilingMaterialsService;
import cn.zswltech.mithras.application.orchestration.ftp.FtpIncomeBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.application.orchestration.monthly.FundsDailyCostMainService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;

import java.util.List;
import java.util.Objects;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum.DirectFinancingCarryInterestFlow;
import static com.baomidou.mybatisplus.core.toolkit.Wrappers.update;

@Component
@Slf4j
public class DirectFinancingCarryInterestProcessEndHandler extends AbstractProcessEndHandler {

    @Resource
    private FundDirectFinancingBaseInfoMapper directFinancingBaseInfoMapper;
    @Resource
    private FundReceiptRepayBaseInfoService fundReceiptRepayBaseInfoService;
    @Resource
    private FundDirectFinancingPledgeInfoService directFinancingPledgeInfoService;
    @Resource
    private FundDirectFinancingBaseInfoService fundDirectFinancingBaseInfoService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), DirectFinancingCarryInterestFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        //获取流程状态
        ProcessBusinessStatusEnum pbs = ProcessBusinessStatusEnum.getByType(endContext.getEndType());
        //    运行中 RUNNING
        //    审批通过 结束 PASS
        //    审批拒绝 结束 REJECT
        //    取消 结束 CANCEL
        //    流程挂起 SUSPEND
        //    审批 一键通过 结束 PASS_ALL
        //    审批 一键拒绝 结束 REJECT_ALL
        switch (pbs) {
            case PASS:
            case PASS_ALL: {
                this.doPass(endContext);
                break;
            }
            case REJECT:
            case REJECT_ALL: {
                break;
            }
            case CANCEL: {
                break;
            }
            default: {
                throw new MithrasException("非法的流程状态");
            }
        }
    }
    //流程结束后起息
    private void doPass(ProcessEndContext endContext) {
        Long id = Long.parseLong(endContext.getBusinessKey());
        FundDirectFinancingBaseInfo baseInfo = directFinancingBaseInfoMapper.selectById(id);
        String financingStatus = baseInfo.getFinancingStatus();
        // 更新状态
        baseInfo.setFinancingStatus(FundFinancingStatusEnum.CARRY_INTEREST.name());
        directFinancingBaseInfoMapper.updateById(baseInfo);
//        update(Wrappers.<FundDirectFinancingBaseInfo>lambdaUpdate().eq(FundDirectFinancingBaseInfo::getId, id)
//                .set(FundDirectFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name()));
        List<FundReceiptRepayBaseInfo> receipt = fundReceiptRepayBaseInfoService.list(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                .eq(FundReceiptRepayBaseInfo::getFinancingId, id)
                .eq(FundReceiptRepayBaseInfo::getFinancingType, "DIRECT"));
        Long receiptRepayId;
        if (ObjectUtil.isEmpty(receipt)) {
            receiptRepayId = fundReceiptRepayBaseInfoService.addDirectFinancingReceipt(id);
        } else {
            receiptRepayId = fundReceiptRepayBaseInfoService.upgradeDirectFinancingReceipt(id);
        }
        // 发送弹窗消息给质押合同主办
        directFinancingPledgeInfoService.sendPopUpMsg(id);
        // 同步数据至新的统一现金流表，老表保留继续主要用于审批流历史快照及不改动老的一些业务逻辑
        if (Objects.nonNull(receiptRepayId)) {
            fundDirectFinancingBaseInfoService.syncToFlowPlan(receiptRepayId);
        }
        // 向对应的资金经理推送归档的待办 停用2026-02-06版本
//        fundDirectFinancingBaseInfoService.startFileProcessDirectFinancing(baseInfo);
        // 更新综合融资成本
        fundDirectFinancingBaseInfoService.updateFinancingCost(id);
        //仅第一次起息时时向对应的资金经理推送档案归档待办
        if (CharSequenceUtil.equals(financingStatus, FundFinancingStatusEnum.NEW.name())) {
            SpringContextHolder.getBean(FundFilingMaterialsService.class)
                    .initFundCommonProcessPrepare(baseInfo.getId(), baseInfo.getFundManagerId(), baseInfo.getFinancingCode(),
                            FilingMaterialsFilingTypeEnum.FUND_DIRECT_FINANCING.name(), FilingMaterialsInitiationMethodEnum.SYSTEM.name());
        }
        //计算ftp
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                // 创建应付利息数据
                try {
                    SpringUtil.getBean(FundsDailyCostMainService.class).createByDirect(baseInfo);
                } catch (Exception e) {
                    log.error("融资{}已起息，但是创建应付利息数据发生异常", baseInfo.getFinancingCode(), e);
                }
            }
        });
//        FundDirectFinancingBaseInfo baseInfo = directFinancingBaseInfoMapper.selectById(id);
//        // 更新状态
//        update(Wrappers.<FundDirectFinancingBaseInfo>lambdaUpdate().eq(FundDirectFinancingBaseInfo::getId, id)
//                .set(FundDirectFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name()));
//
//        List<FundReceiptRepayBaseInfo> receipt = fundReceiptRepayBaseInfoService.list(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
//                .eq(FundReceiptRepayBaseInfo::getFinancingId, id)
//                .eq(FundReceiptRepayBaseInfo::getFinancingType, "DIRECT"));
//        Long receiptRepayId;
//        if (ObjectUtil.isEmpty(receipt)) {
//            receiptRepayId = fundReceiptRepayBaseInfoService.addDirectFinancingReceipt(id);
//        } else {
//            receiptRepayId = fundReceiptRepayBaseInfoService.upgradeDirectFinancingReceipt(id);
//        }
//        // 发送弹窗消息给质押合同主办
//        directFinancingPledgeInfoService.sendPopUpMsg(id);
//        // 同步数据至新的统一现金流表，老表保留继续主要用于审批流历史快照及不改动老的一些业务逻辑
//        if (Objects.nonNull(receiptRepayId)) {
//            fundDirectFinancingBaseInfoService.syncToFlowPlan(receiptRepayId);
//        }
//        // 向对应的资金经理推送归档的待办
//        fundDirectFinancingBaseInfoService.startFileProcessDirectFinancing(baseInfo);
//        // 更新综合融资成本
//        fundDirectFinancingBaseInfoService.updateFinancingCost(id);
//        //计算ftp
//        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
//            @Override
//            public void afterCommit() {
//                try {
//                    SpringContextHolder.getBean(FtpIncomeBaseInfoService.class).createOrUpdate(baseInfo.getId(), FinancingTypeEnum.DIRECT.name(), null);
//                } catch (Exception e) {
//                    log.error("计算直融ftp收益失败 financingCode = {}, financingId = {}", baseInfo.getFinancingCode(), baseInfo.getId(), e);
//                }
//            }
//        });
    }
}
