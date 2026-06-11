package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler;

import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.enums.VersionTypeEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.filingmaterials.enums.FilingMaterialsFilingTypeEnum;
import cn.zswltech.mithras.filingmaterials.enums.FilingMaterialsInitiationMethodEnum;
import cn.zswltech.mithras.fund.enums.financing.FinancingTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
//import cn.zswltech.mithras.application.orchestration.filingmaterials.FundFilingMaterialsService;
import cn.zswltech.mithras.application.orchestration.filingmaterials.FundFilingMaterialsService;
import cn.zswltech.mithras.application.orchestration.ftp.FtpIncomeBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.fund.application.lib.financing.FundFinancingLibVersionService;
import cn.zswltech.mithras.application.orchestration.monthly.FundsDailyCostMainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;

import java.util.Objects;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum.IndirectFinancingCarryInterestFlow;

@Component
@Slf4j
public class IndirectFinancingCarryInterestProcessEndHandler extends AbstractProcessEndHandler {

    @Resource
    private FundFinancingBaseInfoService financingBaseInfoService;
    @Resource
    private FundFinancingLibVersionService financingLibVersionService;
    @Resource
    private FundReceiptRepayBaseInfoService receiptRepayBaseInfoService;
    @Resource
    private FundFinancingService fundFinancingService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), IndirectFinancingCarryInterestFlow.name());
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
        FundFinancingBaseInfo existBaseInfo = financingBaseInfoService.getById(id);
        FundFinancingBaseInfo baseInfo = new FundFinancingBaseInfo();
        baseInfo.setId(id);
        // 修改状态
        baseInfo.setFinancingStatus(FundFinancingStatusEnum.CARRY_INTEREST.name());
        financingBaseInfoService.updateById(baseInfo);
        // 固化动态数据
//        financingBaseInfoService.fixDynamicData(existBaseInfo);
        // 生成数据版本
        financingLibVersionService.recordVersion(baseInfo.getMainId(), VersionTypeEnum.EFFECT, AccountUtil.getLoginInfo().getId(), null, VersionTypeConstants.NORMAL);
        // 通知收付款模块尝试更新还款表
        receiptRepayBaseInfoService.processFinancingEffectEnd(id);
        // 向对应的资金经理推送归档的待办 停用2026-02-06版本
//        fundFinancingService.startFileProcessFinancing(existBaseInfo);
        //状态为起息时向对应的资金经理推送档案归档待办
        SpringContextHolder.getBean(FundFilingMaterialsService.class)
                .initFundCommonProcessPrepare(existBaseInfo.getId(), existBaseInfo.getFundManagerId(), existBaseInfo.getFinancingCode(),
                        FilingMaterialsFilingTypeEnum.FUND_FINANCING.name(), FilingMaterialsInitiationMethodEnum.SYSTEM.name());
        //计算ftp
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                FundFinancingBaseInfo financingBaseInfo = financingBaseInfoService.getById(existBaseInfo.getId());
                // 非期初一次性收息
                boolean condition1 = Objects.equals(financingBaseInfo.getInitialInterestReceivedOnce(), YesOrNoNumberEnum.NO.getCode());
                // 非银票和商票
                boolean condition2 = !StrUtil.equalsAny(financingBaseInfo.getBusinessType(), FundFinancingBizTypeEnum.COMMERCE_ACCEPTANCE.name(), FundFinancingBizTypeEnum.BANK_ACCEPTANCE.name());
                if (condition1 && condition2) {
                    try {
                        SpringUtil.getBean(FundsDailyCostMainService.class).createByIndirect(financingBaseInfo);
                    } catch (Exception e) {
                        log.error("融资{}已起息，但是创建应付利息数据发生异常", financingBaseInfo.getFinancingCode(), e);
                    }
                }
            }
        });
//        FundFinancingBaseInfo existBaseInfo = financingBaseInfoService.getById(id);
//        FundFinancingBaseInfo baseInfo = new FundFinancingBaseInfo();
//        baseInfo.setId(id);
//        // 修改状态
//        baseInfo.setFinancingStatus(FundFinancingStatusEnum.CARRY_INTEREST.name());
//        financingBaseInfoService.updateById(baseInfo);
//        // 固化动态数据
////        financingBaseInfoService.fixDynamicData(existBaseInfo);
//        // 生成数据版本
//        financingLibVersionService.recordVersion(baseInfo.getMainId(), VersionTypeEnum.EFFECT, AccountUtil.getLoginInfo().getId(), null, VersionTypeConstants.NORMAL);
//        // 通知收付款模块尝试更新还款表
//        receiptRepayBaseInfoService.processFinancingEffectEnd(id);
//        // 向对应的资金经理推送归档的待办
//        fundFinancingService.startFileProcessFinancing(existBaseInfo);
//        //计算ftp
//        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
//            @Override
//            public void afterCommit() {
//                try {
//                    SpringContextHolder.getBean(FtpIncomeBaseInfoService.class).createOrUpdate(baseInfo.getId(), FinancingTypeEnum.INDIRECT.name(), null);
//                } catch (Exception e) {
//                    log.error("计算间融ftp收益失败 financingCode = {}, financingId = {}", baseInfo.getFinancingCode(), baseInfo.getId(), e);
//                }
//            }
//        });
    }
}
