package cn.zswltech.mithras.report.handler.impl.newimpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.builder.EqualsBuilder;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.report.config.ReportConstants;
import cn.zswltech.mithras.report.enums.biz.AccountBizTypeEnum;
import cn.zswltech.mithras.report.enums.biz.DataTypeEnum;
import cn.zswltech.mithras.report.enums.biz.RepayCalcTypeEnum;
import cn.zswltech.mithras.report.enums.common.ApprovalStatus;
import cn.zswltech.mithras.report.enums.common.ReportModuleEnum;
import cn.zswltech.mithras.report.enums.common.ReportState;
import cn.zswltech.mithras.report.handler.CrAbstractHandler;
import cn.zswltech.mithras.report.handler.CrFacade;
import cn.zswltech.mithras.report.mapper.AccountReportRecordMapper;
import cn.zswltech.mithras.report.mapper.base.model.CrBaseModel;
import cn.zswltech.mithras.report.mapper.draft.CrAccountDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.CrRepayPlanDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.model.CrAccountDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrRepayPlanDraft;
import cn.zswltech.mithras.report.mapper.formal.model.CrAccount;
import cn.zswltech.mithras.report.mapper.model.AccountReportRecord;
import cn.zswltech.mithras.report.service.CommonInfoService;
import cn.zswltech.mithras.report.service.draft.CrAccountDraftService;
import cn.zswltech.mithras.report.service.draft.CrRepayPlanDraftService;
import cn.zswltech.mithras.report.service.formal.CrAccountService;
import cn.zswltech.mithras.report.util.ReportBizUtil;
import cn.zswltech.mithras.report.util.ReportCompareUtil;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.payment.domain.enums.WriteOffStatus;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.mapper.collection.CollectionBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractBaseInfoLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractFactoringPriceLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractLeasePriceLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractTenantryLibMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractFactoringPriceLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractLeasePriceLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantryLib;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentActualDetailMapper;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED;

/**
 * 征信报送-账户表
 * 理论上应该第一个执行 别的模块是否报送 还要看该模块是否报送
 *
 * @author wangchuanhao
 * @date 2022/10/8 4:24 PM
 */
@Slf4j
@Component
public class CrAccountNewHandler extends CrAbstractHandler<CrAccountDraft, CrAccount> {

    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private ContractBaseInfoLibMapper contractBaseInfoLibMapper;
    @Resource
    private ContractLeasePriceLibMapper contractLeasePriceLibMapper;
    @Resource
    private ContractFactoringPriceLibMapper contractFactoringPriceLibMapper;
    @Resource
    private CrAccountDraftService crAccountDraftService;
    @Resource
    private PaymentActualDetailMapper paymentActualDetailMapper;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private AccountReportRecordMapper accountReportRecordMapper;
    @Resource
    private CrAccountDraftMapper crAccountDraftMapper;
    @Resource
    private CrRepayPlanDraftService repayPlanDraftService;
    @Resource
    private CrRepayPlanDraftMapper repayPlanDraftMapper;
    @Resource
    private CrAccountNewHandler accountNewHandler;
    @Resource
    private CommonInfoService commonInfoService;

    @Override
    public ReportModuleEnum reportModule() {
        return ReportModuleEnum.ACCOUNT_NEW;
    }

    @Override
    public void moduleHandle(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        List<CrAccountDraft> resultList = new ArrayList<>();
        //查询前一天的付款数据
        List<PaymentActualDetail> paymentActualDetails = paymentActualDetailMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                .ge(PaymentActualDetail::getOperationDate, lastDealTime.toLocalDate())
                .lt(PaymentActualDetail::getOperationDate, dealTime.toLocalDate())
                .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name()));

        if (CollUtil.isEmpty(paymentActualDetails)) {
            return;
        }
        Map<Long, List<PaymentActualDetail>> paymentIdDetailMap = paymentActualDetails.stream().collect(Collectors.groupingBy(PaymentActualDetail::getPaymentId));
        //再将实际付款核销根据操作日期分组
        Map<Long, Map<LocalDate, List<PaymentActualDetail>>> paymentDetailMap = new LinkedHashMap<>();
        paymentIdDetailMap.forEach((k, v) -> paymentDetailMap.put(k, v.stream().collect(Collectors.groupingBy(PaymentActualDetail::getOperationDate))));
        //查询 paymentBaseInfo
        Map<Long, PaymentBaseInfo> baseInfoMap = paymentBaseInfoMapper.selectBatchIds(paymentIdDetailMap.keySet())
                .stream().collect(Collectors.toMap(PaymentBaseInfo::getId, Function.identity(), (k1, k2) -> k1));

        //查询合同信息
        List<ContractBaseInfoLib> contractBaseInfoLibList = contractBaseInfoLibMapper.selectList(Wrappers.<ContractBaseInfoLib>lambdaQuery()
                .eq(ContractBaseInfoLib::getVersionType, VersionTypeConstants.NORMAL)
                .in(ContractBaseInfoLib::getOriginId, baseInfoMap.values().stream().map(PaymentBaseInfo::getContractId).collect(Collectors.toSet()))
                .in(ContractBaseInfoLib::getContractStatus, Arrays.asList(ContractStatus.TAKE_EFFECT.name(), ContractStatus.START_RENT.name()))
        );
        //主承租人报送过滤
        contractBaseInfoLibList = contractBaseInfoLibList.stream()
                .filter(a -> Objects.equals(Boolean.TRUE, reportDataRepository.contractReport(a)))
                .filter(a -> !LeaseType.zhi_zu.name().equals(a.getLeaseType()))
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(contractBaseInfoLibList)) {
            return;
        }
        Map<Long, List<ContractBaseInfoLib>> contractLibs = contractBaseInfoLibList.stream().collect(Collectors.groupingBy(ContractBaseInfoLib::getOriginId));
        //拿到最新版本待用
        Map<Long, ContractBaseInfoLib> contractBaseInfoLibMap = new HashMap<>(8);
        contractLibs.forEach((k, v) -> {
            if (CollUtil.isNotEmpty(v)) {
                v.sort(Comparator.comparing(ContractBaseInfoLib::getVersion).reversed());
                contractBaseInfoLibMap.put(k, v.get(0));
            }
        });

        //查询已经有的账户
        Map<Long, List<CrAccountDraft>> paymentAccountMap = new HashMap<>(8);
        List<CrAccountDraft> crAccountDrafts = crAccountDraftMapper.selectList(Wrappers.<CrAccountDraft>lambdaQuery().in(CrAccountDraft::getPaymentId, paymentIdDetailMap.keySet()));
        if (CollUtil.isNotEmpty(crAccountDrafts)) {
            Map<Long, List<CrAccountDraft>> listMap = crAccountDrafts.stream().collect(Collectors.groupingBy(CrAccountDraft::getPaymentId));
            paymentAccountMap.putAll(listMap);
        }

        // 找到合同的承租人
        List<ContractTenantryLib> contractTenantryLibs = SpringUtil.getBean(ContractTenantryLibMapper.class).newestList();
        Map<Long, ContractTenantryLib> tenantryLibMap = contractTenantryLibs.stream().collect(Collectors.toMap(ContractTenantryLib::getContractId, Function.identity(), (m1, m2) -> m1));
        paymentDetailMap.forEach((paymentId, map) ->
                map.forEach((k, v) -> {
                    //合并当天的所有金额
                    if (CollUtil.isEmpty(v)) {
                        return;
                    }
                    //如果已经有还款表了，认为是数据订正，不再生成账户
                    List<CrRepayPlanDraft> list = repayPlanDraftService.list(Wrappers.<CrRepayPlanDraft>lambdaQuery().eq(CrRepayPlanDraft::getPaymentId, paymentId));
                    if (CollUtil.isNotEmpty(list)) {
                        return;
                    }
                    PaymentBaseInfo paymentBaseInfo = Optional.ofNullable(baseInfoMap.get(paymentId)).orElse(new PaymentBaseInfo());
                    ContractBaseInfoLib contractBaseInfoLib = Optional.ofNullable(contractBaseInfoLibMap.get(paymentBaseInfo.getContractId())).orElse(new ContractBaseInfoLib());
                    //这里需要处理下是不是第一次报送
                    v.sort(Comparator.comparing(PaymentActualDetail::getPaidInDate).reversed());
                    LocalDate paidInDate = v.get(0).getPaidInDate();
                    LocalDate earlyPaidInDate = v.get(v.size() - 1).getPaidInDate();
                    //查询是否有收款信息
                    if (Objects.nonNull(contractBaseInfoLib.getOriginId()) && Objects.nonNull(paymentBaseInfo.getId()) && Objects.nonNull(paymentBaseInfo.getReceiptIdFinal())) {
                        CollectionBaseInfo collectionBaseInfo = collectionBaseInfoMapper.selectOne(Wrappers.<CollectionBaseInfo>lambdaQuery()
                                .eq(CollectionBaseInfo::getContractId, contractBaseInfoLib.getOriginId())
                                .eq(CollectionBaseInfo::getReceiptId, paymentBaseInfo.getReceiptIdFinal())
                                .ne(CollectionBaseInfo::getPhase, 0)
                                .in(CollectionBaseInfo::getWriteOffStatus, Arrays.asList(WRITE_OFF_COMPLETED.name(), CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF.name()))
                                .orderByAsc(CollectionBaseInfo::getCollectionDate)
                                .last(StringUtil.mysqlLimitOne()));
                        if (Objects.nonNull(collectionBaseInfo) && Objects.nonNull(collectionBaseInfo.getCollectionDate()) && paidInDate.isAfter(collectionBaseInfo.getCollectionDate())) {
                            paidInDate = collectionBaseInfo.getCollectionDate();
                        }
                    }
                    if (ProjectBizType.ZL.name().equals(contractBaseInfoLib.getBizType()) || ProjectBizType.ZZ.name().equals(contractBaseInfoLib.getBizType())) {
                        ContractLeasePriceLib contractLeasePriceLib = contractLeasePriceLibMapper.selectOne(Wrappers.<ContractLeasePriceLib>lambdaQuery()
                                .eq(ContractLeasePriceLib::getContractId, paymentBaseInfo.getContractId())
                                .eq(ContractLeasePriceLib::getVersionType, VersionTypeConstants.NORMAL)
                                .eq(ContractLeasePriceLib::getVersion, contractBaseInfoLib.getVersion())
                                .orderByDesc(ContractLeasePriceLib::getVersion)
                                .last(StringUtil.mysqlLimitOne()));
                        Long total = mergeAmount(v, paymentAccountMap, paymentBaseInfo, contractLeasePriceLib, contractBaseInfoLib);
                        CrAccountDraft accountDraft = buildCrAccount(tenantryLibMap, paymentBaseInfo, contractLeasePriceLib, total, k);
                        BigDecimal currentAmount = BigDecimal.valueOf(accountDraft.getPaymentAmount());
                        BigDecimal allAmount = BigDecimal.valueOf(contractBaseInfoLib.getApplyCreditAmount() - contractLeasePriceLib.getDownPayment());
                        BigDecimal originalEarnestMoney = BigDecimal.valueOf(contractLeasePriceLib.getEarnestMoney());
                        long earnestMoney = Util.mithrasLongDecimalTwo(currentAmount.divide(allAmount, 15, RoundingMode.HALF_UP).multiply(originalEarnestMoney).longValue());
                        //冯莎张赟说这个地方取同一天的付款核销的实付日期最大的日期，需要进行排序
                        accountDraft.setLendingDate(paidInDate);
                        //到期日的计算方式：到期日期=资金确认最早付款日期+租赁期限-1天
                        LocalDate expirationDate = earlyPaidInDate.plusMonths(contractLeasePriceLib.getLeaseMonthCount()).plusDays(-1);
                        accountDraft.setExpirationDate(expirationDate);
                        //保证金金额从合同的报价方案中获取,冯莎说的！
                        accountDraft.setEarnestMoney(earnestMoney);
                        resultList.add(accountDraft);
                    } else if (ProjectBizType.BL.name().equals(contractBaseInfoLib.getBizType())) {
                        ContractFactoringPriceLib contractFactoringPriceLib = contractFactoringPriceLibMapper.selectOne(Wrappers.<ContractFactoringPriceLib>lambdaQuery()
                                .eq(ContractFactoringPriceLib::getContractId, paymentBaseInfo.getContractId())
                                .eq(ContractFactoringPriceLib::getVersionType, VersionTypeConstants.NORMAL)
                                .eq(ContractFactoringPriceLib::getVersion, contractBaseInfoLib.getVersion())
                                .orderByDesc(ContractFactoringPriceLib::getVersion)
                                .last(StringUtil.mysqlLimitOne()));

                        Long total = mergeAmount(v, paymentAccountMap, paymentBaseInfo, contractFactoringPriceLib, contractBaseInfoLib);
                        BigDecimal currentAmount = BigDecimal.valueOf(v.stream().mapToLong(PaymentActualDetail::getPaidInAmount).sum());
                        BigDecimal allAmount = BigDecimal.valueOf(contractBaseInfoLib.getApplyCreditAmount());
                        BigDecimal originalEarnestMoney = BigDecimal.valueOf(contractFactoringPriceLib.getEarnestMoney());
                        long earnestMoney = Util.mithrasLongDecimalTwo(currentAmount.divide(allAmount, 15, RoundingMode.HALF_UP).multiply(originalEarnestMoney).longValue());
                        CrAccountDraft crAccountDraft = buildCrAccount(tenantryLibMap, paymentBaseInfo, contractBaseInfoLib, contractFactoringPriceLib, total, k);
                        //到期日的计算方式：到期日期=资金确认最早付款日期+租赁期限-1天
                        LocalDate expirationDate = earlyPaidInDate.plusMonths(contractFactoringPriceLib.getFactoringCreditTerm()).plusDays(-1);
                        crAccountDraft.setExpirationDate(expirationDate);
                        //冯莎张赟说这个地方取同一天的付款核销的实付日期最大的日期，需要进行排序
                        crAccountDraft.setLendingDate(paidInDate);
                        //保证金金额从合同的报价方案中获取,冯莎说的！
                        crAccountDraft.setEarnestMoney(earnestMoney);
                        resultList.add(crAccountDraft);
                    }
                }));

        if (CollUtil.isEmpty(resultList)) {
            return;
        }
        List<CrAccountDraft> list = new ArrayList<>(resultList);
        List<CrAccountDraft> existAccount = crAccountDraftMapper.selectList(Wrappers.<CrAccountDraft>lambdaQuery()
                .in(CrAccountDraft::getPaymentApplyCode, resultList.stream().map(CrAccountDraft::getPaymentApplyCode).collect(Collectors.toSet())));
        if (CollUtil.isNotEmpty(existAccount)) {
            Map<String, CrAccountDraft> crAccountDraftMap = existAccount.stream().collect(Collectors.toMap(CrAccountDraft::getPaymentApplyCode, Function.identity(), (k1, k2) -> k1));
            for (CrAccountDraft accountDraft : resultList) {
                CrAccountDraft draft = crAccountDraftMap.get(accountDraft.getPaymentApplyCode());
                if (Objects.isNull(draft)) {
                    continue;
                }
                //合并当天的数据，只产生一个账户
                CrAccountDraft newAccount = BeanUtil.copyProperties(accountDraft, CrAccountDraft.class);
                newAccount.setBusinessKey(draft.getBusinessKey());
                newAccount.setId(draft.getId());
                newAccount.setReportState(ReportState.TO_BE_REPORT.name());
                list.removeIf(a -> a.getPaymentApplyCode().equals(draft.getPaymentApplyCode()));
                // FIXME 骏化项目直接略过，有实际变动的话，直接数据订正
                if (!newAccount.getContractId().equals(1013L) && !newAccount.getContractId().equals(1017L)) {
                    list.add(newAccount);
                }
            }
        }
        //插入用户信息
        fillClientInfo(list);
        //填充结清日期
        commonInfoService.fillClosedDate(list);

        List<String> businessKeyList = list.stream().map(CrAccountDraft::getBusinessKey).collect(Collectors.toList());
        log.info("非直租征信报送-非直租账户表处理，此次处理数量:{}, 处理businessKey列表:{}", list.size(), JSON.toJSONString((businessKeyList)));
        if (CollectionUtils.isNotEmpty(businessKeyList)) {
            Map<String, CrAccountDraft> existReportDataMap = draftMapper.selectList(Wrappers.<CrAccountDraft>lambdaQuery().in(CrAccountDraft::getBusinessKey, businessKeyList))
                    .stream().collect(Collectors.toMap(CrAccountDraft::getBusinessKey, Function.identity(), (k1, k2) -> k1));
            List<CrAccountDraft> needInsertList = new ArrayList<>();
            List<PaymentBaseInfo> paymentBaseInfos = paymentBaseInfoMapper.selectBatchIds(list.stream().map(CrAccountDraft::getPaymentId).collect(Collectors.toList()));
            // 过滤一下没有实际租金表的数据
            paymentBaseInfos = paymentBaseInfos.stream().filter(p -> reportDataRepository.receiptPayment(p)).collect(Collectors.toList());
            Map<Long, Long> receiptIdMap = CollUtil.isEmpty(paymentBaseInfos) ? new HashMap<>() :
                    paymentBaseInfos.stream().collect(Collectors.toMap(PaymentBaseInfo::getId, PaymentBaseInfo::getReceiptIdFinal));

            for (CrAccountDraft reportData : list) {
                if (!existReportDataMap.containsKey(reportData.getBusinessKey())) {
                    boolean receiptNotReportFlag = accountReportRecordMapper.selectCount(Wrappers.<AccountReportRecord>lambdaQuery()
                            .eq(AccountReportRecord::getReceiptId, receiptIdMap.get(reportData.getPaymentId()))) == 0;
                    if (receiptNotReportFlag) {
                        needInsertList.add(reportData);
                        AccountReportRecord accountReportRecord = AccountReportRecord.builder()
                                .contractId(reportData.getContractId())
                                .receiptId(receiptIdMap.get(reportData.getPaymentId()))
                                .paymentId(reportData.getPaymentId())
                                .build();
                        accountReportRecordMapper.insert(accountReportRecord);
                    }
                } else {
                    CrAccountDraft existData = existReportDataMap.get(reportData.getBusinessKey());
                    if (ReportCompareUtil.checkChange(reportData, existData, existData.ignoreCompareFieldNames())) {
                        reportData.setId(existData.getId());
                        needInsertList.add(reportData);
                    }
                }
            }
            if (CollUtil.isNotEmpty(needInsertList)) {
                draftService.saveOrUpdateBatch(needInsertList);
            }
        }
        if (CollUtil.isNotEmpty(list)) {
            //处理当天更新，重复数据不再入库
            crAccountDraftService.saveOrUpdateBatch(list);
        }
    }

    private CrAccountDraft buildCrAccount(Map<Long, ContractTenantryLib> tenantryLibMap, PaymentBaseInfo paymentBaseInfo, ContractLeasePriceLib contractLeasePriceLib, Long amount, LocalDate k) {
        CrAccountDraft crAccountDraft = CrAccountDraft.builder().reportFlag(YesOrNoNumberEnum.YES.getCode()).build();
        RepayCalcTypeEnum calcTypeEnum = RepayCalcTypeEnum.find(contractLeasePriceLib.getRentalCalcType());
        ContractTenantryLib tenantryLib = tenantryLibMap.get(paymentBaseInfo.getContractId());
        if (Objects.isNull(tenantryLib)) {
            log.error("主承租人怎么可能是空的呢！！！");
            throw new MithrasException("主承租人怎么可能是空的呢！！！");
        }
        crAccountDraft.setReportState(ReportState.TO_BE_REPORT.name())
                .setApprovalStatus(ApprovalStatus.UN_SUBMIT.name())
                .setReportFlagInit(YesOrNoNumberEnum.YES.getCode())
                .setReportFlag(YesOrNoNumberEnum.YES.getCode())
                .setProcBusinessKey(null)
                .setPaymentApplyCode(String.format("%s-%s", paymentBaseInfo.getPaymentCode(), LocalDateTimeUtil.format(k, "yyyyMMdd")))
                // 租赁只有一种类型 设置成租赁的类型
                .setBizType(AccountBizTypeEnum.RZZL.getValue())
                .setRentalCalcType(Optional.ofNullable(calcTypeEnum).map(RepayCalcTypeEnum::getValue).orElse(null))
                .setRepayRate(ReportBizUtil.getRepayRate(calcTypeEnum))
                //保证金金额从付款申请获取，不从合同的报价方案中获取
                .setEarnestMoney(paymentBaseInfo.getEarnestMoney())
                .setProjLeaseMonthCount(contractLeasePriceLib.getLeaseMonthCount())
                // 直租业务付款金额 = 合同金额
                .setPaymentAmount(amount)
                .setPaymentId(paymentBaseInfo.getId())
                .setClientId(tenantryLib.getLesseeId());
        crAccountDraft.setBusinessKey(crAccountDraft.genBusinessKey(k));
        crAccountDraft.setContractId(paymentBaseInfo.getContractId());
        return crAccountDraft;
    }

    private CrAccountDraft buildCrAccount(Map<Long, ContractTenantryLib> tenantryLibMap, PaymentBaseInfo paymentBaseInfo, ContractBaseInfoLib contractBaseInfoLib,
                                          ContractFactoringPriceLib contractFactoringPriceLib, Long amount, LocalDate k) {
        CrAccountDraft crAccountDraft = CrAccountDraft.builder().build();
        RepayCalcTypeEnum calcTypeEnum = RepayCalcTypeEnum.find(contractFactoringPriceLib.getRepayCalcType());
        ContractTenantryLib tenantryLib = tenantryLibMap.get(paymentBaseInfo.getContractId());
        if (Objects.isNull(tenantryLib)) {
            log.error("主承租人怎么可能是空的呢！！！");
            throw new MithrasException("主承租人怎么可能是空的呢！！！");
        }

        crAccountDraft.setReportFlag(YesOrNoNumberEnum.YES.getCode())
                .setReportState(ReportState.TO_BE_REPORT.name())
                .setReportFlagInit(YesOrNoNumberEnum.YES.getCode())
                .setApprovalStatus(ApprovalStatus.UN_SUBMIT.name())
                .setPaymentApplyCode(String.format("%s-%s", paymentBaseInfo.getPaymentCode(), LocalDateTimeUtil.format(k, "yyyyMMdd")))
                .setBizType(AccountBizTypeEnum.convertBL(contractBaseInfoLib.getFactoringType()))
                .setRentalCalcType(Optional.ofNullable(calcTypeEnum).map(RepayCalcTypeEnum::getValue).orElse(null))
                .setRepayRate(ReportBizUtil.getRepayRate(calcTypeEnum))
                .setProjLeaseMonthCount(contractFactoringPriceLib.getFactoringCreditTerm())
                .setPaymentAmount(amount)
                .setPaymentId(paymentBaseInfo.getId())
                .setClientId(tenantryLib.getLesseeId());
        crAccountDraft.setBusinessKey(crAccountDraft.genBusinessKey(k));
        crAccountDraft.setContractId(paymentBaseInfo.getContractId());
        CrFacade.AMOUNT_MAP.put(crAccountDraft.getPaymentApplyCode() + crAccountDraft.getPaymentId(), crAccountDraft.getPaymentAmount());
        return crAccountDraft;
    }

    private void fillClientInfo(List<CrAccountDraft> reportDataList) {
        if (CollUtil.isEmpty(reportDataList)) {
            return;
        }
        Map<Long, Client> clientMap = reportDataRepository.clientMap(reportDataList.stream().map(CrAccountDraft::getClientId).collect(Collectors.toSet()));
        reportDataList.forEach(c -> {
            c.setClientCode(Optional.ofNullable(clientMap.get(c.getClientId())).map(Client::getClientCode).orElse(null));
            c.setClientName(Optional.ofNullable(clientMap.get(c.getClientId())).map(Client::getClientName).orElse(null));
        });
    }

    /**
     * 合并金额有首期租金需要减掉首期租金
     *
     * @param paymentActualDetails 投放详情
     * @param paymentAccountMap    标识该付款申请已经存在数据，不需要再减去首期租金
     * @param paymentBaseInfo      付款申请基本信息
     * @param price
     * @param contractBaseInfoLib  合同基本信息
     * @return Long 付款金额
     */
    private Long mergeAmount(List<PaymentActualDetail> paymentActualDetails, Map<Long, List<CrAccountDraft>> paymentAccountMap,
                             PaymentBaseInfo paymentBaseInfo, Object price, ContractBaseInfoLib contractBaseInfoLib) {
        Long amount = 0L;
        for (PaymentActualDetail actualDetail : paymentActualDetails) {
            amount += actualDetail.getPaidInAmount();
        }

        //保理取合同金额
        if (ProjectBizType.BL.name().equals(contractBaseInfoLib.getBizType())) {
            return Math.max(0, amount);
        }
        //回租金额=合同金额-付款里面的首期租金
        if (ProjectBizType.ZL.name().equals(contractBaseInfoLib.getBizType()) && !LeaseType.zhi_zu.name().equals(contractBaseInfoLib.getLeaseType()) &&
                (CollUtil.isEmpty(paymentAccountMap.get(paymentBaseInfo.getId())))) {
            ContractLeasePriceLib priceLib = (ContractLeasePriceLib) price;
            amount -= priceLib.getDownPayment();
        }
        return Math.max(0L, amount);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, transactionManager = ReportConstants.TRANSACTION_MANAGER)
    public void afterModuleHandle(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        //处理合同报价方案变化，导致信息需要更新的处理
        accountNewHandler.contractChangeHandler(dealTime, lastDealTime);
        //填充到期日期，查找租金表，没有租金表的话，需要使用报价方案计算
        //找到这段时间的还款计划，如果最大期项发生变化则对到期日期进行改变，但是每次同步还款计划都会发生变化，所以直接每次找出最大的期项的应还日期
        List<CrRepayPlanDraft> maxPhaseList = repayPlanDraftMapper.selectAccountMaxPhaseList();
        if (CollUtil.isEmpty(maxPhaseList)) {
            return;
        }
        //将所有的未结清账户按照 paymentApplyCode 做映射
        Map<String, CrAccountDraft> accountDraftMap = crAccountDraftService.list(Wrappers.<CrAccountDraft>lambdaQuery()
                        .isNull(CrAccountDraft::getClosedDate))
                .stream().collect(Collectors.toMap(CrAccountDraft::getPaymentApplyCode, Function.identity()));

        List<CrAccountDraft> accountDrafts = new LinkedList<>();
        maxPhaseList.forEach(repayPlanDraft -> {
            CrAccountDraft accountDraft = accountDraftMap.get(repayPlanDraft.getPaymentApplyCode());
            if (Objects.isNull(accountDraft)) {
                return;
            }
            LocalDate expirationDate = accountDraft.getExpirationDate();
            if (Objects.isNull(expirationDate)) {
                return;
            }
            //移除有还款计划的账户
            accountDraftMap.remove(repayPlanDraft.getPaymentApplyCode());

            //到期日期不一样更新，同时账户需要再次展示在待报送
            LocalDate cashFlowDate = repayPlanDraft.getCashFlowDate();
            if (expirationDate.isEqual(cashFlowDate)) {
                return;
            }

            CrAccountDraft draft = BeanUtil.copyProperties(accountDraft, CrAccountDraft.class);
            draft.setExpirationDate(repayPlanDraft.getCashFlowDate());
            draft.setReportState(ReportState.TO_BE_REPORT.name());
            draft.setApprovalStatus(ApprovalStatus.UN_SUBMIT.name());
            accountDrafts.add(draft);
        });

        //没有还款计划的需要单独处理
        if (CollUtil.isNotEmpty(accountDraftMap)) {
            List<String> paymentApplyCodeList = accountDraftMap.values().stream().map(CrAccountDraft::getPaymentApplyCode).collect(Collectors.toList());
            log.info("没有还款计划的账户：【{}】", paymentApplyCodeList);
            Map<String, CrAccount> codeAccountMap = SpringContextHolder.getBean(CrAccountService.class).list(Wrappers.<CrAccount>lambdaQuery()
                            .in(CrAccount::getPaymentApplyCode, paymentApplyCodeList))
                    .stream().collect(Collectors.toMap(CrAccount::getPaymentApplyCode, Function.identity()));
            accountDraftMap.values().forEach(account -> {
                //找到付款核销记录最早的日期
                PaymentActualDetail paymentActualDetail = paymentActualDetailMapper.selectOne(Wrappers.<PaymentActualDetail>lambdaQuery()
                        .eq(PaymentActualDetail::getPaymentId, account.getPaymentId())
                        .orderByAsc(PaymentActualDetail::getPaidInDate)
                        .last(StringUtil.mysqlLimitOne()));
                CrAccountDraft accountDraft = BeanUtil.copyProperties(account, CrAccountDraft.class);
                //到期日的计算方式：到期日期=资金确认最早付款日期+租赁期限-1天
                LocalDate expirationDate = paymentActualDetail.getPaidInDate().plusMonths(account.getProjLeaseMonthCount()).minusDays(1);
                accountDraft.setExpirationDate(expirationDate);
                accountDraft.setReportState(ReportState.TO_BE_REPORT.name());
                accountDraft.setApprovalStatus(ApprovalStatus.UN_SUBMIT.name());
                //如果数据已经报送过且没有变化，不需要再次展示
                CrAccount crAccount = codeAccountMap.get(accountDraft.getPaymentApplyCode());
                if (Objects.nonNull(crAccount) && (crAccount.getExpirationDate().isEqual(accountDraft.getExpirationDate()))) {
                    return;
                }
                accountDrafts.add(accountDraft);
            });
        }

        if (CollUtil.isNotEmpty(accountDrafts)) {
            SpringContextHolder.getBean(CrAccountDraftService.class).updateBatchById(accountDrafts);
        }
    }

    public void contractChangeHandler(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        Map<String, List<ContractBaseInfoLib>> stringListMap = reportDataRepository.listChangeContractBaseInfo(dealTime, lastDealTime);
        if (CollUtil.isEmpty(stringListMap)) {
            return;
        }
        List<ContractBaseInfoLib> contractBaseInfoLibs = stringListMap.get(DataTypeEnum.NOT_ZHI_ZU.name());
        if (CollUtil.isEmpty(contractBaseInfoLibs)) {
            return;
        }

        //处理数据更新
        Map<Long, List<CrAccountDraft>> existAccountMap = crAccountDraftService.list(Wrappers.<CrAccountDraft>lambdaQuery()
                        .in(CrBaseModel::getContractId, contractBaseInfoLibs.stream().map(ContractBaseInfoLib::getOriginId).collect(Collectors.toList())))
                .stream().collect(Collectors.groupingBy(CrBaseModel::getContractId));

        contractBaseInfoLibs.forEach(contractBaseInfoLib -> {
            List<CrAccountDraft> crAccountDrafts = existAccountMap.get(contractBaseInfoLib.getOriginId());
            if (CollUtil.isEmpty(crAccountDrafts)) {
                return;
            }

            //非直租租赁和转租赁
            if (CharSequenceUtil.equalsAny(contractBaseInfoLib.getBizType(), ProjectBizType.ZL.name(), ProjectBizType.ZZ.name())) {
                List<CrAccountDraft> list = new LinkedList<>();
                ContractLeasePriceLib contractLeasePriceLib = contractLeasePriceLibMapper.selectOne(Wrappers.<ContractLeasePriceLib>lambdaQuery()
                        .eq(ContractLeasePriceLib::getContractId, contractBaseInfoLib.getOriginId())
                        .eq(ContractLeasePriceLib::getVersionType, VersionTypeConstants.NORMAL)
                        .eq(ContractLeasePriceLib::getVersion, contractBaseInfoLib.getVersion())
                        .orderByDesc(ContractLeasePriceLib::getVersion)
                        .last(StringUtil.mysqlLimitOne()));

                BigDecimal allAmount = BigDecimal.valueOf(contractBaseInfoLib.getApplyCreditAmount() - contractLeasePriceLib.getDownPayment());
                RepayCalcTypeEnum calcTypeEnum = Optional.ofNullable(RepayCalcTypeEnum.find(contractLeasePriceLib.getRentalCalcType())).orElse(null);

                crAccountDrafts.forEach(draft -> {
                    CrAccountDraft accountDraft = BeanUtil.copyProperties(draft, CrAccountDraft.class);
                    accountDraft.setReportFlag(YesOrNoNumberEnum.YES.getCode());
                    accountDraft.setReportState(ReportState.TO_BE_REPORT.name());
                    accountDraft.setApprovalStatus(ApprovalStatus.UN_SUBMIT.name());
                    accountDraft.setBizType(AccountBizTypeEnum.RZZL.getValue());
                    accountDraft.setRentalCalcType(Optional.ofNullable(calcTypeEnum).map(RepayCalcTypeEnum::getValue).orElse(null));
                    accountDraft.setRepayRate(ReportBizUtil.getRepayRate(calcTypeEnum));
                    accountDraft.setProjLeaseMonthCount(contractLeasePriceLib.getLeaseMonthCount());
                    accountDraft.setEarnestMoney(BigDecimal.valueOf(draft.getPaymentAmount())
                            .multiply(BigDecimal.valueOf(contractLeasePriceLib.getEarnestMoney()))
                            .divide(allAmount, 20, RoundingMode.HALF_UP).longValue());

                    //填充客户信息
                    this.fillClientInfo(Collections.singletonList(accountDraft));
                    commonInfoService.fillClosedDate(Collections.singletonList(accountDraft));

                    //比较两个账户是否发生变化
                    Boolean isEqual = new EqualsBuilder().append(draft.getBizType(), accountDraft.getBizType())
                            .append(draft.getClientCode(), accountDraft.getClientCode())
                            .append(draft.getLendingDate(), accountDraft.getLendingDate())
                            .append(draft.getRepayRate(), accountDraft.getRepayRate())
                            .append(draft.getRentalCalcType(), accountDraft.getRentalCalcType())
                            .append(draft.getProjLeaseMonthCount(), accountDraft.getProjLeaseMonthCount())
                            .append(draft.getEarnestMoney(), accountDraft.getEarnestMoney())
                            .append(draft.getClientId(), accountDraft.getClientId())
                            .build();

                    if (Boolean.FALSE.equals(isEqual)) {
                        //不相等，则需要进行数据的更新
                        list.add(accountDraft);
                    }
                });

                if (CollUtil.isNotEmpty(list)) {
                    crAccountDraftService.updateBatchById(list);
                }
            }

            //保理
            if (CharSequenceUtil.equals(contractBaseInfoLib.getBizType(), ProjectBizType.BL.name())) {
                List<CrAccountDraft> list = new LinkedList<>();
                ContractFactoringPriceLib contractFactoringPriceLib = contractFactoringPriceLibMapper.selectOne(Wrappers.<ContractFactoringPriceLib>lambdaQuery()
                        .eq(ContractFactoringPriceLib::getContractId, contractBaseInfoLib.getOriginId())
                        .eq(ContractFactoringPriceLib::getVersionType, VersionTypeConstants.NORMAL)
                        .eq(ContractFactoringPriceLib::getVersion, contractBaseInfoLib.getVersion())
                        .orderByDesc(ContractFactoringPriceLib::getVersion)
                        .last(StringUtil.mysqlLimitOne()));
                BigDecimal allAmount = BigDecimal.valueOf(contractBaseInfoLib.getApplyCreditAmount());
                RepayCalcTypeEnum calcTypeEnum = Optional.ofNullable(RepayCalcTypeEnum.find(contractFactoringPriceLib.getRepayCalcType())).orElse(null);

                crAccountDrafts.forEach(draft -> {
                    CrAccountDraft accountDraft = BeanUtil.copyProperties(draft, CrAccountDraft.class);
                    accountDraft.setReportFlag(YesOrNoNumberEnum.YES.getCode());
                    accountDraft.setReportState(ReportState.TO_BE_REPORT.name());
                    accountDraft.setApprovalStatus(ApprovalStatus.UN_SUBMIT.name());
                    accountDraft.setBizType(AccountBizTypeEnum.convertBL(contractBaseInfoLib.getFactoringType()));
                    accountDraft.setRentalCalcType(Optional.ofNullable(calcTypeEnum).map(RepayCalcTypeEnum::getValue).orElse(null));
                    accountDraft.setRepayRate(ReportBizUtil.getRepayRate(calcTypeEnum));
                    accountDraft.setProjLeaseMonthCount(contractFactoringPriceLib.getFactoringCreditTerm());
                    accountDraft.setEarnestMoney(BigDecimal.valueOf(draft.getPaymentAmount())
                            .multiply(BigDecimal.valueOf(contractFactoringPriceLib.getEarnestMoney()))
                            .divide(allAmount, 20, RoundingMode.HALF_UP).longValue());

                    //填充客户信息
                    this.fillClientInfo(Collections.singletonList(accountDraft));

                    //比较两个账户是否发生变化
                    Boolean isEqual = new EqualsBuilder().append(draft.getBizType(), accountDraft.getBizType())
                            .append(draft.getClientCode(), accountDraft.getClientCode())
                            .append(draft.getLendingDate(), accountDraft.getLendingDate())
                            .append(draft.getRepayRate(), accountDraft.getRepayRate())
                            .append(draft.getRentalCalcType(), accountDraft.getRentalCalcType())
                            .append(draft.getProjLeaseMonthCount(), accountDraft.getProjLeaseMonthCount())
                            .append(draft.getEarnestMoney(), accountDraft.getEarnestMoney())
                            .append(draft.getClientId(), accountDraft.getClientId())
                            .build();

                    if (Boolean.FALSE.equals(isEqual)) {
                        //不相等，则需要进行数据的更新
                        list.add(accountDraft);
                    }
                });

                if (CollUtil.isNotEmpty(list)) {
                    crAccountDraftService.updateBatchById(list);
                }
            }
        });
    }

    @Override
    public Integer sort() {
        return 2;
    }
}