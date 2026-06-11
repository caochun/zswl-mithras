package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler;

import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.flow.core.util.ApplicationContextUtil;
import cn.zswltech.gruul.common.util.UUIDUtil;
import cn.zswltech.mithras.dto.third.financial.ThirdCollectionRecordREQ;
import cn.zswltech.mithras.dto.third.financial.ThirdPaymentDetailREQ;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.filingmaterials.enums.FilingMaterialsFilingTypeEnum;
import cn.zswltech.mithras.filingmaterials.enums.FilingMaterialsInitiationMethodEnum;
import cn.zswltech.mithras.payment.enums.PaymentWriteOffStatus;
import cn.zswltech.mithras.payment.enums.WriteOffStatus;
import cn.zswltech.mithras.payment.enums.WriteOffTypeEnum;
import cn.zswltech.mithras.third.financialshare.enums.FinancialPaymentCodeENUM;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.model.PaymentActualDetailUnconfirmed;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.model.PaymentCollectionInfo;
import cn.zswltech.mithras.payment.mapper.PaymentCollectionInfoMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.collection.event.CollectionAddEvent;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.application.orchestration.filingmaterials.FilingMaterialsService;
import cn.zswltech.mithras.application.orchestration.payment.FtpAssessmentInfoService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentActualDetailService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentActualDetailUnconfirmedService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.third.financialshare.application.FinancialExtraService;
import cn.zswltech.mithras.third.financialshare.application.FinancialService;
import cn.zswltech.mithras.application.orchestration.third.financial.impl.FinancialManagerServiceImpl2;
import cn.zswltech.mithras.third.financialshare.application.dto.FinancialPaymentVO;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.foundation.async.ThreadPoolUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;

/**
 * @author dingqi
 * @date 2023/12/12
 * @description  付款实际核销确认 流程结束业务处理类
 */
@Slf4j
@Component
public class PaymentActualDetailEndHandler extends AbstractProcessEndHandler {
    @Resource
    private PaymentActualDetailUnconfirmedService paymentActualDetailUnconfirmedService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private FinancialExtraService financialExtraService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentActualDetailEndHandler endHandler;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private FinancialManagerServiceImpl2 financialManagerServiceImpl2;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private PaymentCollectionInfoMapper paymentCollectionInfoMapper;
    @Resource
    private FinancialService financialService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private FilingMaterialsService filingMaterialsService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), ProcessModelTypeEnum.PaymentActualDetailFlow.name());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handle(ProcessEndContext endContext) {
        Long paymentId = Long.parseLong(endContext.getBusinessKey());
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(paymentId);
        if (Objects.isNull(paymentBaseInfo)) {
            throw new MithrasException("付款申请不存在");
        }
        List<PaymentActualDetailUnconfirmed> paymentActualDetailUnconfirmedList = paymentActualDetailUnconfirmedService.listByPaymentId(paymentId);
        paymentActualDetailUnconfirmedList.removeIf(e -> !Objects.equals(e.getWriteOffStatus(), WriteOffStatus.COMMIT.name()));
        if (CollectionUtil.isEmpty(paymentActualDetailUnconfirmedList)) {
            return;
        }
        ProcessBusinessStatusEnum processBusinessStatusEnum = ProcessBusinessStatusEnum.getByType(endContext.getEndType());
        if (Objects.isNull(processBusinessStatusEnum)) {
            throw new MithrasException("未定义的审批状态");
        }
        switch (processBusinessStatusEnum) {
            case PASS:
            case PASS_ALL: {
//                List<Long> toRemoveIds = new LinkedList<>();
//                for (PaymentActualDetailUnconfirmed paymentActualDetailUnconfirmed : paymentActualDetailUnconfirmedList) {
//                    toRemoveIds.add(paymentActualDetailUnconfirmed.getId());
//                    // 复用苍穹处理逻辑
//                    ThirdPaymentDetailREQ req = this.build(paymentActualDetailUnconfirmed);
//                    req.setCollectionCode(paymentBaseInfo.getPaymentCode());
//                    R<String> result = financialExtraService.paymentRecodeExtra(req);
//                    if (!result.isSuccess()) {
//                        throw new MithrasException(result.getMsg());
//                    }
//                }
//                if (CollectionUtil.isNotEmpty(toRemoveIds)) {
//                    paymentActualDetailUnconfirmedService.removeByIds(toRemoveIds);
//                }
//                endHandler.canWeStartRentalProcessHandler(paymentId);
//                endHandler.canWeStartRentalProcessHandler(paymentId);
                /*是否合同下首笔付款核销流程*/
                List<PaymentActualDetailUnconfirmed> paymentActualDetailUnconfirmedByContractIdList = paymentActualDetailUnconfirmedService.listByContractId(paymentBaseInfo.getContractId());

                    // 变更付款实际核销记录状态
                paymentActualDetailUnconfirmedList.forEach(e -> e.setWriteOffStatus(WriteOffStatus.CONFIRM.name()));
                paymentActualDetailUnconfirmedService.updateBatchById(paymentActualDetailUnconfirmedList);
                if(!paymentBaseInfo.getIsFinishPut()){
                    // 用户选择不结束投放，不参与核销与起租
                    updatePutStatus(paymentBaseInfo, false);
                    log.info("是否首笔付款核销审批:" + paymentActualDetailUnconfirmedByContractIdList.size());
                    if (CollUtil.isEmpty(paymentActualDetailUnconfirmedByContractIdList)) {
                        log.info("开始进入项目资料归档待办生成逻辑" );
                        doProcessPrepare(paymentBaseInfo.getContractId());
                    }
                    return;
                }
                updatePutStatus(paymentBaseInfo, true);

                PaymentCollectionInfo paymentCollectionInfo = paymentCollectionInfoMapper.selectOne(Wrappers.<PaymentCollectionInfo>lambdaQuery()
                        .eq(PaymentCollectionInfo::getPaymentId, paymentBaseInfo.getId()));
                if (paymentCollectionInfo == null) {
                    if (ProcessBusinessStatusEnum.PASS.equals(processBusinessStatusEnum)) {
                        throw new MithrasException("请维护收款确认信息");
                    } else {
                        paymentCollectionInfo = new PaymentCollectionInfo();
                    }
                }
                // 生成应收款
                notifyAdd(paymentBaseInfo, paymentCollectionInfo);
                // 生效FTP考核价格信息
                SpringUtil.getBean(FtpAssessmentInfoService.class).effectByPaymentReceiptId(paymentId, paymentBaseInfo.getReceiptId());

                log.info("是否首笔付款核销审批:" + paymentActualDetailUnconfirmedByContractIdList.size());
                if (CollUtil.isEmpty(paymentActualDetailUnconfirmedByContractIdList)) {
                    log.info("开始进入项目资料归档待办生成逻辑" );
                    doProcessPrepare(paymentBaseInfo.getContractId());
                }

//                // 异步尝试自动核销
//                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
//                    @Override
//                    public void afterCommit() {
//                        ThreadPoolUtil.getCommonPool().execute(() -> {
//                            // 尝试自动核销
//                            try {
//                                getBean(FinanceFlowAutoWriteOffService.class).autoWriteOffSomeAmount(paymentBaseInfo);
//                            } catch (Exception e) {
//                                log.error("付款实际核销流程结束-异步尝试自动核销发生异常[{}]", JSONUtil.toJsonStr(endContext), e);
//                            }
//                            //推付款单
//                            if (!cq2PaymentVOS.isEmpty()) {
//                                financialManagerServiceImpl2.cq2PaymentExec(SpringContextHolder.getBean(CollectionAddEventListener.class).getBizInfo(paymentBaseInfo.getContractId()), cq2PaymentVOS);
//                            }
//                        });
//                    }
//                });
//                //通知苍穹创建付款申请
//                financialManagerServiceImpl2.paymentExec(getBean(CollectionAddEventListener.class).getBizInfo(paymentBaseInfo.getContractId()), Collections.singletonList(changeFinancialPaymentVO(paymentBaseInfo)));
//                //核销流水
//                List<CollectionBaseInfo> list = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
//                        .eq(CollectionBaseInfo::getPaymentId, paymentBaseInfo.getId())
//                        .in(CollectionBaseInfo::getCashFlowItem, Stream.of(CashFlowItemEnum.FIRST_RENT.name(), CashFlowItemEnum.RETENTION_MONEY.name(), CashFlowItemEnum.OTHERAMOUNT.name(),
//                                CashFlowItemEnum.EARNEST_MONEY.name(), CashFlowItemEnum.FIRST_INSTALLMENT_INTEREST.name(), CashFlowItemEnum.COMMISSION.name()).collect(Collectors.toList()))
//                        .ne(CollectionBaseInfo::getWriteOffStatus, MarginWriteOffStatusEnum.WRITE_OFF_COMPLETED));
//
//                List<PaymentActualDetail> actualDetails = paymentActualDetailService.list(Wrappers.<PaymentActualDetail>lambdaQuery()
//                        .eq(PaymentActualDetail::getPaymentId, paymentBaseInfo.getId()).in(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name()).orderByDesc(PaymentActualDetail::getCreateTime));
//                PaymentActualDetail paymentActualDetail = CollectionUtil.isNotEmpty(actualDetails) ? actualDetails.get(0) : new PaymentActualDetail();
//
//                PaymentCollectionInfo finalPaymentCollectionInfo = paymentCollectionInfo;
//                list.forEach(collectionBaseInfo ->{
//                    ThirdCollectionRecordREQ req = buildReq(collectionBaseInfo, finalPaymentCollectionInfo);
//                    req.setCollectionDate(paymentActualDetail.getPaidInDate());
//                    financialService.collectionRecode(req);
//                });

//                // 走结束投放逻辑
//                try {
//                    ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(paymentBaseInfo.getContractId());
//                    if (!Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
//                        paymentBaseInfoService.finish(paymentId);
//                    }
//                }catch (MithrasException e){
//                    throw new MithrasException("付款核销完毕,自动起租失败:"+e.getMessage());
//                }catch (Exception e){
//                    throw new MithrasException("付款核销完毕，自动起租发生未知异常");
//                }
                break;
            }
            default: {
                // 关闭前需要做校验 规则：如果实际核销的金额总和不等于非当前流程的已确认金额，则不允许关闭
                List<PaymentActualDetailUnconfirmed> list = paymentActualDetailUnconfirmedService.list(Wrappers.<PaymentActualDetailUnconfirmed>lambdaQuery()
                        .eq(PaymentActualDetailUnconfirmed::getPaymentId, paymentId)
                        .in(PaymentActualDetailUnconfirmed::getWriteOffStatus, WriteOffStatus.CONFIRM.name(), WriteOffStatus.TO_BE_WRITE_OFF.name(), WriteOffStatus.COMMIT.name()));
                if (CollUtil.isNotEmpty(list)) {
                    // 找到所有的实际核销记录
                    List<PaymentActualDetail> actualDetails = paymentActualDetailService.list(Wrappers.<PaymentActualDetail>lambdaQuery()
                            .in(PaymentActualDetail::getUnConfirmedId, list.stream().map(PaymentActualDetailUnconfirmed::getId).collect(Collectors.toList())));
                    if (CollUtil.isNotEmpty(actualDetails)) {
                        throw new MithrasException("当前流程存在已经核销的数据，请先反核销或者联系管理员！");
                    }
                }

                // 更新状态
                for (PaymentActualDetailUnconfirmed detail : paymentActualDetailUnconfirmedList) {
                    detail.setWriteOffStatus(WriteOffStatus.CLOSED.name());
                }
                paymentActualDetailUnconfirmedService.updateBatchById(paymentActualDetailUnconfirmedList);
            }
        }
    }


    private void updatePutStatus(PaymentBaseInfo paymentBaseInfo, boolean b) {
        paymentBaseInfoService.update(Wrappers.<PaymentBaseInfo>lambdaUpdate()
                .eq(PaymentBaseInfo::getId, paymentBaseInfo.getId())
                .set(PaymentBaseInfo::getIsFinishPutFinal, b));
    }

    private ThirdCollectionRecordREQ buildReq(CollectionBaseInfo baseInfo, PaymentCollectionInfo paymentCollectionInfo) {
        ThirdCollectionRecordREQ thirdCollectionRecordREQ = new ThirdCollectionRecordREQ();
        thirdCollectionRecordREQ.setPknumber(getPkNumber());
        thirdCollectionRecordREQ.setDataSource(WriteOffTypeEnum.AUTO_RECORD.display());
        thirdCollectionRecordREQ.setCollectionCode(baseInfo.getCode());
        thirdCollectionRecordREQ.setCollectionType("");
        if (CashFlowItemEnum.FIRST_INSTALLMENT_INTEREST.name().equals(baseInfo.getCashFlowItem())) {
            thirdCollectionRecordREQ.setCollectionAmount(BigDecimal.ZERO);
        } else {
            thirdCollectionRecordREQ.setCollectionAmount(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(getCashFlowItemValue(baseInfo.getCashFlowItem(), paymentCollectionInfo)))));
        }
        thirdCollectionRecordREQ.setCashFlowItem(baseInfo.getCashFlowItem());
        thirdCollectionRecordREQ.setInvoiceFlag(YesOrNoNumberEnum.YES.getCode());
        thirdCollectionRecordREQ.setPrincipal(BigDecimal.ZERO);
        if (CashFlowItemEnum.FIRST_INSTALLMENT_INTEREST.name().equals(baseInfo.getCashFlowItem())) {
            thirdCollectionRecordREQ.setInterest(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(getCashFlowItemValue(baseInfo.getCashFlowItem(), paymentCollectionInfo)))));
        } else {
            thirdCollectionRecordREQ.setInterest(BigDecimal.ZERO);
        }
        thirdCollectionRecordREQ.setPenaltyInterest(BigDecimal.ZERO);
        return thirdCollectionRecordREQ;
    }

    private String getPkNumber() {
        return String.join("-", "ZSZL", UUIDUtil.genUuid());
    }

    private Long getCashFlowItemValue(String cashFlowItem, PaymentCollectionInfo paymentCollectionInfo) {
        CashFlowItemEnum cashFlowItemEnum = CashFlowItemEnum.of(cashFlowItem);
        switch (cashFlowItemEnum) {
            case COMMISSION:
                return paymentCollectionInfo.getCommission();
            case EARNEST_MONEY:
                return paymentCollectionInfo.getEarnestMoney();
            case FIRST_RENT:
                return paymentCollectionInfo.getDownPayment();
            case OTHERAMOUNT:
                return paymentCollectionInfo.getConsultingFee();
            case FIRST_INSTALLMENT_INTEREST:
                return paymentCollectionInfo.getFirstInstallmentInterest();
            case RETENTION_MONEY:
                return paymentCollectionInfo.getRetentionMoney();
            default:
                return 0L;
        }
    }


    /**
     * 根据PaymentCollection表中取应收
     *
     * @param baseInfo
     */
    private void notifyAdd(PaymentBaseInfo baseInfo, PaymentCollectionInfo paymentCollectionInfo) {
        // 发送事件
        if (Objects.nonNull(baseInfo.getEarnestMoney())) {
            CollectionAddEvent collectionAddEvent = new CollectionAddEvent(baseInfo.getPaymentCode(), baseInfo.getContractId(), CashFlowItemEnum.EARNEST_MONEY, paymentCollectionInfo.getEarnestMoney(), baseInfo.getApplyPaymentDate().toLocalDate());
            ApplicationContextUtil.getApplicationContext().publishEvent(collectionAddEvent);
        }
        if (Objects.nonNull(baseInfo.getConsultingFee()) && baseInfo.getConsultingFee() > 0) {
            CollectionAddEvent collectionAddEvent = new CollectionAddEvent(baseInfo.getPaymentCode(), baseInfo.getContractId(), CashFlowItemEnum.OTHERAMOUNT, paymentCollectionInfo.getConsultingFee(), baseInfo.getApplyPaymentDate().toLocalDate());
            ApplicationContextUtil.getApplicationContext().publishEvent(collectionAddEvent);
        }
        if (Objects.nonNull(baseInfo.getDownPayment()) && baseInfo.getDownPayment() > 0) {
            CollectionAddEvent collectionAddEvent = new CollectionAddEvent(baseInfo.getPaymentCode(), baseInfo.getContractId(), CashFlowItemEnum.FIRST_RENT, paymentCollectionInfo.getDownPayment(), baseInfo.getApplyPaymentDate().toLocalDate());
            ApplicationContextUtil.getApplicationContext().publishEvent(collectionAddEvent);
        }
        if (Objects.nonNull(baseInfo.getRetentionMoney()) && baseInfo.getRetentionMoney() > 0) {
            CollectionAddEvent collectionAddEvent = new CollectionAddEvent(baseInfo.getPaymentCode(), baseInfo.getContractId(), CashFlowItemEnum.RETENTION_MONEY,
                    paymentCollectionInfo.getRetentionMoney(), paymentCollectionInfo.getWarrantyReturnDate());
            ApplicationContextUtil.getApplicationContext().publishEvent(collectionAddEvent);
        }
        if (Objects.nonNull(baseInfo.getCommission()) && baseInfo.getCommission() > 0) {
            CollectionAddEvent collectionAddEvent = new CollectionAddEvent(baseInfo.getPaymentCode(), baseInfo.getContractId(), CashFlowItemEnum.COMMISSION,
                    paymentCollectionInfo.getCommission(), baseInfo.getApplyPaymentDate().toLocalDate());
            ApplicationContextUtil.getApplicationContext().publishEvent(collectionAddEvent);
        }
        if (Objects.nonNull(baseInfo.getFirstInstallmentInterest()) && baseInfo.getFirstInstallmentInterest() > 0) {
            CollectionAddEvent collectionAddEvent = new CollectionAddEvent(baseInfo.getPaymentCode(), baseInfo.getContractId(), CashFlowItemEnum.FIRST_INSTALLMENT_INTEREST,
                    paymentCollectionInfo.getFirstInstallmentInterest(), baseInfo.getApplyPaymentDate().toLocalDate());
            ApplicationContextUtil.getApplicationContext().publishEvent(collectionAddEvent);
        }
//        if (Objects.nonNull(baseInfo.getNominalPrice()) && baseInfo.getNominalPrice() > 0) {
//            CollectionAddEvent collectionAddEvent = new CollectionAddEvent(baseInfo.getPaymentCode(), baseInfo.getContractId(), CashFlowItemEnum.NOMINAL_PRICE, baseInfo.getNominalPrice(),baseInfo.getApplyPaymentDate().toLocalDate());
//            ApplicationContextUtil.getApplicationContext().publishEvent(collectionAddEvent);
//        }
    }

    private FinancialPaymentVO changeFinancialPaymentVO(PaymentBaseInfo paymentBaseInfo) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(paymentBaseInfo.getContractId());
        FinancialPaymentVO financialPayment = new FinancialPaymentVO();
        financialPayment.setPaymentCode(paymentBaseInfo.getPaymentCode());
        financialPayment.setContractCode(paymentBaseInfo.getContractCode());
        financialPayment.setPaymentType(FinancialPaymentCodeENUM.changePaymentCode(contractBaseInfo.getBizType()).getDisplay());
        financialPayment.setApplyPaymentAmount(LongUtil.null2zero(paymentBaseInfo.getApplyPaymentAmount()));
        financialPayment.setApplyPaymentDate(paymentBaseInfo.getApplyPaymentDate());
        financialPayment.setIsInitialRent(paymentBaseInfo.getDownPaymentType());
        financialPayment.setInitialRentAmount(paymentBaseInfo.getDownPayment());
        if (YesOrNoNumberEnum.NO.getCode().equals(paymentBaseInfo.getDownPaymentType())) {
            //不含首期租金，需要减去首期租金
            financialPayment.setApplyPaymentAmount(financialPayment.getApplyPaymentAmount() - LongUtil.null2zero(paymentBaseInfo.getDownPayment()));
        }
        if (YesOrNoNumberEnum.NO.getCode().equals(paymentBaseInfo.getRetentionMoneyType())) {
            //有内扣质保金
            financialPayment.setRetentionMoney(paymentBaseInfo.getRetentionMoney());
            financialPayment.setApplyPaymentAmount(financialPayment.getApplyPaymentAmount() - LongUtil.null2zero(paymentBaseInfo.getRetentionMoney()));
        }
        return financialPayment;
    }


    /**
     * 付款核销金额到达付款申请金额自动发起起租流程
     *
     * @param paymentId 付款申请ID
     */
    public void canWeStartRentalProcessHandler(Long paymentId) {
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(paymentId);
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(paymentBaseInfo.getContractId());
        if (Objects.nonNull(paymentBaseInfo) && PaymentWriteOffStatus.WRITTEN_OFF.name().equals(paymentBaseInfo.getWriteOffStatus())) {
            if (Objects.nonNull(contractBaseInfo)) {
                if (!ContractStatus.SETTLE.name().equals(contractBaseInfo.getContractStatus())) {
                    //核销完毕，需要发起起租流程
                    try {
                        paymentBaseInfoService.finish(paymentId);
                    } catch (Exception e) {
                        log.error("付款申请ID：{}, 付款核销完毕，自动起租失败", paymentId, e);
                    }
                }
            }
        }
    }

    private ThirdPaymentDetailREQ build(PaymentActualDetailUnconfirmed paymentActualDetailUnconfirmed) {
        ThirdPaymentDetailREQ req = new ThirdPaymentDetailREQ();
        req.setReqFromCq(false);
        req.setReqNeedHandle(true);
        req.setInfoSource(paymentActualDetailUnconfirmed.getInfoSource());
        req.setPknumber(LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_MS_PATTERN));
        req.setPaymentMethod(paymentActualDetailUnconfirmed.getPaymentMethod());
        req.setOppositeAccountName(paymentActualDetailUnconfirmed.getOppositeAccountName());
        req.setOppositeAccountNumber(paymentActualDetailUnconfirmed.getOppositeAccountNumber());
        req.setOppositeAccountBank(paymentActualDetailUnconfirmed.getOppositeAccountBank());
        req.setOurAccountId(paymentActualDetailUnconfirmed.getOurAccountId());
        req.setOurAccountName(paymentActualDetailUnconfirmed.getOurAccountName());
        req.setOurAccountNumber(paymentActualDetailUnconfirmed.getOurAccountNumber());
        req.setOurAccountBank(paymentActualDetailUnconfirmed.getOurAccountBank());
        req.setPaidInDate(paymentActualDetailUnconfirmed.getPaidInDate());
        req.setPaidInAmount(BigDecimal.valueOf(paymentActualDetailUnconfirmed.getPaidInAmount()).divide(new BigDecimal(GlobalConstants.MONEY_MULTIPLE), 2, RoundingMode.HALF_UP));
        return req;
    }

    private void doProcessPrepare(Long contractId) {
        // 异步生成项目资料归档待办
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                ThreadPoolUtil.getCommonPool().execute(() -> {
                    try {
                        filingMaterialsService.initCommonProcessPrepare(contractId, FilingMaterialsFilingTypeEnum.BUSINESS_MATERIALS.name(),
                                FilingMaterialsInitiationMethodEnum.SYSTEM.name());
                    } catch (Exception e) {
                        log.error("异步生成项目资料归档待办发生异常[contractId:{}]", contractId, e);
                    }
                });
            }
        });

    }
}
