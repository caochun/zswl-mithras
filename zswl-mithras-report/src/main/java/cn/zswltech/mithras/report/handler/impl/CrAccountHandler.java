package cn.zswltech.mithras.report.handler.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.report.config.ReportConstants;
import cn.zswltech.mithras.report.enums.biz.AccountBizTypeEnum;
import cn.zswltech.mithras.report.enums.biz.DataTypeEnum;
import cn.zswltech.mithras.report.enums.biz.RepayCalcTypeEnum;
import cn.zswltech.mithras.report.enums.common.ApprovalStatus;
import cn.zswltech.mithras.report.enums.common.ReportModuleEnum;
import cn.zswltech.mithras.report.enums.common.ReportState;
import cn.zswltech.mithras.report.handler.CrAbstractHandler;
import cn.zswltech.mithras.report.mapper.AccountReportRecordMapper;
import cn.zswltech.mithras.report.mapper.base.model.CrAccountBase;
import cn.zswltech.mithras.report.mapper.draft.CrAccountDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.CrActualRepayDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.CrRepayPlanDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.model.CrAccountDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrActualRepayDraft;
import cn.zswltech.mithras.report.mapper.formal.model.CrAccount;
import cn.zswltech.mithras.report.mapper.model.AccountReportRecord;
import cn.zswltech.mithras.report.service.CommonInfoService;
import cn.zswltech.mithras.report.service.draft.CrAccountDraftService;
import cn.zswltech.mithras.report.util.ReportBizUtil;
import cn.zswltech.mithras.report.util.ReportCompareUtil;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.enums.contract.ContractStatus;
import cn.zswltech.mithras.service.enums.payment.PaymentStatusEnum;
import cn.zswltech.mithras.service.enums.payment.PaymentWriteOffStatus;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.mapper.collection.CollectionBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.lib.contract.*;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.*;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentActualDetail;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.payment.PaymentActualDetailMapper;
import cn.zswltech.mithras.service.mapper.payment.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StreamUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED;

/**
 * 征信报送-账户表
 * 理论上应该第一个执行 别的模块是否报送 还要看该模块是否报送
 *
 * @author wangchuanhao
 * @date 2022/10/8 4:24 PM
 */
@Component
@Slf4j
@Order(-2)
public class CrAccountHandler extends CrAbstractHandler<CrAccountDraft, CrAccount> {

    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private ContractBaseInfoLibMapper contractBaseInfoLibMapper;
    @Resource
    private ContractLeasePriceLibMapper contractLeasePriceLibMapper;
    @Resource
    private PaymentActualDetailMapper paymentActualDetailMapper;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private AccountReportRecordMapper accountReportRecordMapper;
    @Resource
    private CrActualRepayDraftMapper crActualRepayDraftMapper;
    @Resource
    private CrAccountDraftMapper crAccountDraftMapper;
    @Resource
    private CrAccountDraftService accountDraftService;
    @Resource
    private ContractReceiptLibMapper contractReceiptLibMapper;
    @Resource
    private CommonInfoService commonInfoService;

    @Override
    public ReportModuleEnum reportModule() {
        return ReportModuleEnum.ACCOUNT;
    }

    @Override
    public void moduleHandle(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        List<CrAccountDraft> reportDataList = new ArrayList<>();
        reportDataList.addAll(collectPaymentChangeData(dealTime, lastDealTime));
        reportDataList.addAll(collectContractChangeData(dealTime, lastDealTime));
        reportDataList.addAll(collectRentCollectionData(dealTime, lastDealTime));
        reportDataList = reportDataList.stream().filter(StreamUtil.distinctByKey(CrAccountDraft::getBusinessKey)).collect(Collectors.toList());
        // 组装
        fillClientInfo(reportDataList);
        fillLendingDate(reportDataList);
        commonInfoService.fillClosedDate(reportDataList);

        List<String> businessKeyList = reportDataList.stream().map(CrAccountDraft::getBusinessKey).collect(Collectors.toList());
        log.info("征信报送-直租账户表处理，此次处理数量:{}, 处理businessKey列表:{}", reportDataList.size(), JSON.toJSONString((businessKeyList)));
        if (CollectionUtils.isNotEmpty(businessKeyList)) {
            Map<String, CrAccountDraft> existReportDataMap = draftMapper.selectList(Wrappers.<CrAccountDraft>lambdaQuery().in(CrAccountDraft::getBusinessKey, businessKeyList))
                    .stream().collect(Collectors.toMap(CrAccountDraft::getBusinessKey, Function.identity(), (k1, k2) -> k1));
            List<CrAccountDraft> needInsertList = new ArrayList<>();
            Map<Long, Long> receiptIdMap = paymentBaseInfoMapper.selectBatchIds(reportDataList.stream().map(CrAccountDraft::getPaymentId).collect(Collectors.toList()))
                    .stream().collect(Collectors.toMap(PaymentBaseInfo::getId, PaymentBaseInfo::getReceiptIdFinal, (k1, k2) -> k1));

            for (CrAccountDraft reportData : reportDataList) {
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
                        draftMapper.updateById(reportData);
                    }
                }
            }
            draftService.saveBatch(needInsertList);
        }

        // 叶芳说该笔付款申请，对应的租金最后一期核销完毕的时候，修改账户表的结清日期为租金最后一期的实际收款明细的最新的实收时间
        List<CollectionBaseInfo> changeCollectionList = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
//                .eq(CollectionBaseInfo::getWriteOffStatus, WRITE_OFF_COMPLETED.name())
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .ne(CollectionBaseInfo::getPhase, 0)
                .gt(CollectionBaseInfo::getUpdateTime, lastDealTime)
                .le(CollectionBaseInfo::getUpdateTime, dealTime));
        for (CollectionBaseInfo changeCollection : changeCollectionList) {
            // 判断这期是不是最后一期
            CollectionBaseInfo finalCollection = collectionBaseInfoMapper.selectOne(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                    .ne(CollectionBaseInfo::getPhase, 0)
                    .eq(CollectionBaseInfo::getReceiptId, changeCollection.getReceiptId())
                    .orderByDesc(CollectionBaseInfo::getPhase)
                    .last(StringUtil.mysqlLimitOne())
            );
            if (!changeCollection.getId().equals(finalCollection.getId())) {
                // 不是最后一期
                continue;
            }
            // 最后一期判断是否核销完毕
            long planRent = Optional.ofNullable(finalCollection.getPlanCollectionAmount()).orElse(0L);
            long actualRent = Optional.ofNullable(finalCollection.getCollectionAmount()).orElse(0L);
            if (planRent > actualRent) {
                // 最后一期租金没有核销完
                continue;
            }
            // 是最后一期核销完毕了 找到receiptId 对应的 paymentId
            List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery().eq(PaymentBaseInfo::getReceiptIdFinal, changeCollection.getReceiptId()));
            if (CollectionUtils.isNotEmpty(paymentBaseInfoList)) {
                // 为什么会出现最后一期还款都核销完毕 付款还没核销完毕的数据？
                LambdaUpdateWrapper<CrAccountDraft> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.in(CrAccountDraft::getPaymentId, paymentBaseInfoList.stream().map(PaymentBaseInfo::getId).collect(Collectors.toList()));
                updateWrapper.set(CrAccountDraft::getClosedDate, changeCollection.getCollectionDate());
                updateWrapper.set(CrAccountDraft::getProcBusinessKey, null);
                updateWrapper.set(CrAccountDraft::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name());
                updateWrapper.set(CrAccountDraft::getReportState, ReportState.TO_BE_REPORT.name());
                updateWrapper.set(CrAccountDraft::getReportFlag, YesOrNoNumberEnum.YES.getCode());
                draftMapper.update(null, updateWrapper);
            }
        }

        // 已结清的账户表 直租业务 计算补全数据
        calZhizuClosedFixLastPhase();
    }


    /**
     * 收集由于付款数据变动 导致的需要报送
     *
     * @param dealTime
     * @param lastDealTime
     * @return
     */
    private List<CrAccountDraft> collectPaymentChangeData(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        List<CrAccountDraft> resultList = new ArrayList<>();
        Map<String, List<PaymentBaseInfo>> stringListMap = reportDataRepository.listNeedReportChangePaymentBaseInfo(dealTime, lastDealTime);
        List<PaymentBaseInfo> paymentBaseInfoList = stringListMap.get(DataTypeEnum.ZHI_ZU.name());
        if (CollUtil.isEmpty(paymentBaseInfoList)) {
            return resultList;
        }
        // 找到合同的承租人
        List<ContractTenantryLib> contractTenantryLibs = SpringUtil.getBean(ContractTenantryLibMapper.class).newestList();
        Map<Long, ContractTenantryLib> tenantryLibMap = contractTenantryLibs.stream().collect(Collectors.toMap(ContractTenantryLib::getContractId, Function.identity(), (m1, m2) -> m1));

        for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfoList) {
            ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibMapper.selectOne(Wrappers.<ContractBaseInfoLib>lambdaQuery()
                    .eq(ContractBaseInfoLib::getOriginId, paymentBaseInfo.getContractId())
                    .eq(ContractBaseInfoLib::getVersionType, VersionTypeConstants.NORMAL)
                    .orderByDesc(ContractBaseInfoLib::getVersion)
                    .last(StringUtil.mysqlLimitOne())
            );
            if (ProjectBizType.ZR.name().equals(contractBaseInfoLib.getBizType())) {
                // 债权转让不报
                continue;
            }

            if (ProjectBizType.ZL.name().equals(contractBaseInfoLib.getBizType()) || ProjectBizType.ZZ.name().equals(contractBaseInfoLib.getBizType())) {
                ContractLeasePriceLib contractLeasePriceLib = contractLeasePriceLibMapper.selectOne(Wrappers.<ContractLeasePriceLib>lambdaQuery()
                        .eq(ContractLeasePriceLib::getContractId, paymentBaseInfo.getContractId())
                        .eq(ContractLeasePriceLib::getVersionType, VersionTypeConstants.NORMAL)
                        .eq(ContractLeasePriceLib::getVersion, contractBaseInfoLib.getVersion())
                        .orderByDesc(ContractLeasePriceLib::getVersion)
                        .last(StringUtil.mysqlLimitOne()));

                List<PaymentActualDetail> details = paymentActualDetailMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                        .eq(PaymentActualDetail::getPaymentId, paymentBaseInfo.getId()));
                if (CollUtil.isEmpty(details)) {
                    continue;
                }
                details = details.stream().sorted(Comparator.comparing(PaymentActualDetail::getPaidInDate)).collect(Collectors.toList());
                LocalDate earlyPaidInDate = details.get(0).getPaidInDate();
                CrAccountDraft accountDraft = buildCrAccount(tenantryLibMap, paymentBaseInfo, contractLeasePriceLib, contractBaseInfoLib, resultList);
                //到期日的计算方式：到期日期=资金确认最早付款日期+租赁期限-1天
                LocalDate expirationDate = earlyPaidInDate.plusMonths(contractLeasePriceLib.getLeaseMonthCount()).plusDays(-1);
                accountDraft.setExpirationDate(expirationDate);
                resultList.add(accountDraft);
            }
        }
        return resultList;
    }


    /**
     * 如果是期间将合同租金核销完毕，那么征信认为是已结清，这时候需要同步给征信，让其产生结清日期
     */
    public List<CrAccountDraft> collectRentCollectionData(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        List<CrAccountDraft> resultList = new ArrayList<>();
        //期间发生了改变的租金核销完毕记录
        List<CollectionBaseInfo> changedRentList = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .gt(CollectionBaseInfo::getUpdateTime, lastDealTime)
                .le(CollectionBaseInfo::getUpdateTime, dealTime)
                .ne(CollectionBaseInfo::getPhase, 0)
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .eq(CollectionBaseInfo::getWriteOffStatus, WRITE_OFF_COMPLETED.name())
        );
        Set<Long> contractIdList = changedRentList.stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toSet());
        if (CollUtil.isEmpty(contractIdList)) {
            return resultList;
        }
        //继续判断是否所有租金都核销完毕
        List<CollectionBaseInfo> notAllWriteOffList = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .in(CollectionBaseInfo::getContractId, contractIdList)
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .ne(CollectionBaseInfo::getPhase, 0)
                .ne(CollectionBaseInfo::getWriteOffStatus, WRITE_OFF_COMPLETED.name())
        );
        Set<Long> notAllWriteOffContractIdList = notAllWriteOffList.stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toSet());
        contractIdList.removeAll(notAllWriteOffContractIdList);
        //
        Map<Long, ContractBaseInfoLib> contractLibMap = contractBaseInfoLibMapper.listNewestContractByContractIds(contractIdList)
                .stream().collect(Collectors.toMap(ContractBaseInfoLib::getOriginId, Function.identity(), (k1, k2) -> k2));

        // 找到合同的承租人
        List<ContractTenantryLib> contractTenantryLibs = SpringUtil.getBean(ContractTenantryLibMapper.class).newestList();
        Map<Long, ContractTenantryLib> tenantryLibMap = contractTenantryLibs.stream().collect(Collectors.toMap(ContractTenantryLib::getContractId, Function.identity(), (m1, m2) -> m1));
        for (Long contractId : contractIdList) {
            ContractBaseInfoLib contractBaseInfoLib = contractLibMap.get(contractId);
            if (null == contractBaseInfoLib) {
                log.error("直租: 未知的有效的合同版本,合同id:{}", contractId);
                continue;
            }
            List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                    .eq(PaymentBaseInfo::getContractId, contractId)
                    .eq(PaymentBaseInfo::getPaymentStatus, PaymentStatusEnum.TAKE_EFFECT.name())
                    .in(PaymentBaseInfo::getWriteOffStatus, Arrays.asList(PaymentWriteOffStatus.PART_WRITTEN_OFF.name(), PaymentWriteOffStatus.WRITTEN_OFF.name()))
            );
            // 过滤一下没有实际租金表的数据
            paymentBaseInfoList = paymentBaseInfoList.stream().filter(p -> reportDataRepository.receiptPayment(p)).collect(Collectors.toList());

            if (ProjectBizType.ZL.name().equals(contractBaseInfoLib.getBizType()) || ProjectBizType.ZZ.name().equals(contractBaseInfoLib.getBizType())) {
                ContractLeasePriceLib contractLeasePriceLib = contractLeasePriceLibMapper.selectOne(Wrappers.<ContractLeasePriceLib>lambdaQuery()
                        .eq(ContractLeasePriceLib::getContractId, contractBaseInfoLib.getOriginId())
                        .eq(ContractLeasePriceLib::getVersionType, VersionTypeConstants.NORMAL)
                        .eq(ContractLeasePriceLib::getVersion, contractBaseInfoLib.getVersion())
                        .orderByDesc(ContractLeasePriceLib::getVersion)
                        .last(StringUtil.mysqlLimitOne()));
                paymentBaseInfoList.forEach(c -> resultList.add(buildCrAccount(tenantryLibMap, c, contractLeasePriceLib, contractBaseInfoLib, resultList)));
            }
        }
        return resultList;
    }

    /**
     * 收集由于合同数据变动 导致的需要报送
     * 过滤掉业务类型为债权转让的数据
     *
     * @param dealTime
     * @param lastDealTime
     * @return
     */
    private List<CrAccountDraft> collectContractChangeData(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        List<CrAccountDraft> resultList = new ArrayList<>();
        Map<String, List<ContractBaseInfoLib>> stringListMap = reportDataRepository.listChangeContractBaseInfo(dealTime, lastDealTime);
        List<ContractBaseInfoLib> contractBaseInfoLibList = stringListMap.get(DataTypeEnum.ZHI_ZU.name());
        if (CollectionUtils.isEmpty(contractBaseInfoLibList)) {
            return resultList;
        }
        for (ContractBaseInfoLib contractBaseInfoLib : contractBaseInfoLibList) {
            Boolean contractReportFlag = reportDataRepository.contractReport(contractBaseInfoLib);
            if (!Boolean.TRUE.equals(contractReportFlag)) {
                // 主承租人报送过滤逻辑
                continue;
            }
            List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                    .eq(PaymentBaseInfo::getContractId, contractBaseInfoLib.getOriginId())
                    .eq(PaymentBaseInfo::getPaymentStatus, PaymentStatusEnum.TAKE_EFFECT)
                    .in(PaymentBaseInfo::getWriteOffStatus, Arrays.asList(PaymentWriteOffStatus.PART_WRITTEN_OFF.name(), PaymentWriteOffStatus.WRITTEN_OFF.name()))
            );
            // 过滤一下没有实际租金表的数据
            paymentBaseInfoList = paymentBaseInfoList.stream().filter(p -> reportDataRepository.receiptPayment(p)).collect(Collectors.toList());

            if (ProjectBizType.ZL.name().equals(contractBaseInfoLib.getBizType()) || ProjectBizType.ZZ.name().equals(contractBaseInfoLib.getBizType())) {
                ContractLeasePriceLib contractLeasePriceLib = contractLeasePriceLibMapper.selectOne(Wrappers.<ContractLeasePriceLib>lambdaQuery()
                        .eq(ContractLeasePriceLib::getContractId, contractBaseInfoLib.getOriginId())
                        .eq(ContractLeasePriceLib::getVersionType, VersionTypeConstants.NORMAL)
                        .eq(ContractLeasePriceLib::getVersion, contractBaseInfoLib.getVersion())
                        .orderByDesc(ContractLeasePriceLib::getVersion)
                        .last(StringUtil.mysqlLimitOne()));
                // 找到合同的承租人
                List<ContractTenantryLib> contractTenantryLibs = SpringUtil.getBean(ContractTenantryLibMapper.class).newestList();
                Map<Long, ContractTenantryLib> tenantryLibMap = contractTenantryLibs.stream().collect(Collectors.toMap(ContractTenantryLib::getContractId, Function.identity(), (m1, m2) -> m1));
                paymentBaseInfoList.forEach(c -> {
                    List<PaymentActualDetail> details = paymentActualDetailMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                            .eq(PaymentActualDetail::getPaymentId, c.getId()));
                    if (CollUtil.isEmpty(details)) {
                        return;
                    }
                    details.sort(Comparator.comparing(PaymentActualDetail::getPaidInDate));
                    LocalDate earlyPaidInDate = details.get(0).getPaidInDate();
                    CrAccountDraft accountDraft = buildCrAccount(tenantryLibMap, c, contractLeasePriceLib, contractBaseInfoLib, resultList);
                    //到期日的计算方式：到期日期=资金确认最早付款日期+租赁期限-1天
                    LocalDate expirationDate = earlyPaidInDate.plusMonths(contractLeasePriceLib.getLeaseMonthCount()).plusDays(-1);
                    accountDraft.setExpirationDate(expirationDate);
                    resultList.add(accountDraft);
                });
            }
        }
        return resultList;
    }

    private CrAccountDraft buildCrAccount(Map<Long, ContractTenantryLib> tenantryLibMap,
                                          PaymentBaseInfo paymentBaseInfo,
                                          ContractLeasePriceLib contractLeasePriceLib,
                                          ContractBaseInfoLib contractBaseInfoLib,
                                          List<CrAccountDraft> resultList) {
        CrAccountDraft crAccountDraft = CrAccountDraft.builder().build();
        RepayCalcTypeEnum calcTypeEnum = Optional.ofNullable(RepayCalcTypeEnum.find(contractLeasePriceLib.getRentalCalcType())).orElse(null);
        ContractTenantryLib tenantryLib = tenantryLibMap.get(paymentBaseInfo.getContractId());
        if (Objects.isNull(tenantryLib)) {
            log.error("主承租人怎么可能是空的呢！！！");
            throw new MithrasException("主承租人怎么可能是空的呢！！！");
        }
        crAccountDraft.setReportFlag(YesOrNoNumberEnum.YES.getCode())
                .setReportState(ReportState.TO_BE_REPORT.name())
                .setApprovalStatus(ApprovalStatus.UN_SUBMIT.name())
                .setProcBusinessKey(null)
                .setReportFlagInit(YesOrNoNumberEnum.YES.getCode())
                .setPaymentApplyCode(ReportBizUtil.bizCalPaymentCode(paymentBaseInfo.getPaymentCode(), contractBaseInfoLib.getLeaseType()))
                // 租赁只有一种类型 设置成租赁的类型
                .setBizType(AccountBizTypeEnum.RZZL.getValue())
                .setRentalCalcType(Optional.ofNullable(calcTypeEnum).map(RepayCalcTypeEnum::getValue).orElse(null))
                .setRepayRate(ReportBizUtil.getRepayRate(calcTypeEnum))
                //保证金金额从付款申请获取，不从合同的报价方案中获取
                .setEarnestMoney(contractLeasePriceLib.getEarnestMoney())
                .setProjLeaseMonthCount(contractLeasePriceLib.getLeaseMonthCount())
                // 直租业务付款金额 = 合同金额
                .setPaymentAmount(setLoanAmount(paymentBaseInfo, contractBaseInfoLib, resultList, contractLeasePriceLib))
                .setPaymentId(paymentBaseInfo.getId())
                .setClientId(tenantryLib.getLesseeId());
        crAccountDraft.setBusinessKey(crAccountDraft.genBusinessKey(contractBaseInfoLib.getActualLeaseDate()));
        crAccountDraft.setContractId(paymentBaseInfo.getContractId());
        return crAccountDraft;
    }

    /**
     * 统一处理借款金额
     */
    private Long setLoanAmount(PaymentBaseInfo paymentBaseInfo, ContractBaseInfoLib contractBaseInfoLib,
                               List<CrAccountDraft> resultList, ContractLeasePriceLib contractLeasePriceLib) {
        Long applyPaymentAmount = paymentBaseInfo.getApplyPaymentAmount();
        //查看是否是第一笔付款，不是第一笔付款直接返回，否则需要扣减首期租金
        List<CrAccountDraft> crAccountDrafts = crAccountDraftMapper.selectList(Wrappers.<CrAccountDraft>lambdaQuery()
                .eq(CrAccountDraft::getContractId, paymentBaseInfo.getContractId()));

        if (CollUtil.isNotEmpty(crAccountDrafts)) {
            Map<Long, CrAccountDraft> crAccountDraftMap = resultList.stream()
                    .collect(Collectors.toMap(CrAccountDraft::getContractId, Function.identity(), (k1, k2) -> k1));

            if (Objects.nonNull(crAccountDraftMap.get(contractBaseInfoLib.getOriginId())) && (LeaseType.zhi_zu.name().equals(contractBaseInfoLib.getLeaseType()))) {
                return contractBaseInfoLib.getApplyCreditAmount();
            }
        }
        if (ProjectBizType.ZL.name().equals(contractBaseInfoLib.getBizType())) {
            //直租
            Long downPayment = contractLeasePriceLib.getDownPayment();
            if (LeaseType.zhi_zu.name().equals(contractBaseInfoLib.getLeaseType())) {
                return contractBaseInfoLib.getApplyCreditAmount() - downPayment;
            }
        }
        return applyPaymentAmount;
    }

    private void fillClientInfo(List<CrAccountDraft> reportDataList) {
        if (CollectionUtils.isEmpty(reportDataList)) {
            return;
        }
        Map<Long, Client> clientMap = reportDataRepository.clientMap(reportDataList.stream().map(CrAccountDraft::getClientId).collect(Collectors.toSet()));
        reportDataList.forEach(c -> {
            c.setClientCode(Optional.ofNullable(clientMap.get(c.getClientId())).map(Client::getClientCode).orElse(null));
            c.setClientName(Optional.ofNullable(clientMap.get(c.getClientId())).map(Client::getClientName).orElse(null));
        });
    }

    /**
     * 组装lendingDate
     *
     * @param reportDataList
     */
    private void fillLendingDate(List<CrAccountDraft> reportDataList) {
        if (CollectionUtils.isEmpty(reportDataList)) {
            return;
        }
        reportDataList.forEach(c -> {
            // 找到日期最新的一条实际支付明细
            List<PaymentActualDetail> paymentActualDetailList = paymentActualDetailMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                    .eq(PaymentActualDetail::getPaymentId, c.getPaymentId())
                    .orderByDesc(PaymentActualDetail::getPaidInDate)
            );
            //冯莎张赟说这个地方取同一天的付款核销的实付日期
            if (CollUtil.isEmpty(paymentActualDetailList)) {
                return;
            }
            //查借据
            ContractReceiptLib contractReceiptLib = contractReceiptLibMapper.selectOne(Wrappers.<ContractReceiptLib>lambdaQuery()
                    .eq(ContractReceiptLib::getContractId, c.getContractId())
                    .eq(ContractReceiptLib::getVersionType, VersionTypeConstants.NORMAL)
                    .last(StringUtil.mysqlLimitOne()));
            paymentActualDetailList.sort(Comparator.comparing(PaymentActualDetail::getPaidInDate).reversed());
            LocalDate paidInDate = paymentActualDetailList.get(0).getPaidInDate();
            //查询是否有收款信息
            if (Objects.nonNull(contractReceiptLib)) {
                CollectionBaseInfo collectionBaseInfo = collectionBaseInfoMapper.selectOne(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .eq(CollectionBaseInfo::getContractId, c.getContractId())
                        .eq(CollectionBaseInfo::getReceiptId, contractReceiptLib.getOriginId())
                        .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                        .ne(CollectionBaseInfo::getPhase, 0)
                        .in(CollectionBaseInfo::getWriteOffStatus, Arrays.asList(WRITE_OFF_COMPLETED.name(), CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF.name()))
                        .orderByAsc(CollectionBaseInfo::getCollectionDate)
                        .last(StringUtil.mysqlLimitOne()));
                if (Objects.nonNull(collectionBaseInfo) && Objects.nonNull(paidInDate) && Objects.nonNull(collectionBaseInfo.getCollectionDate()) && paidInDate.isAfter(collectionBaseInfo.getCollectionDate())) {
                    paidInDate = collectionBaseInfo.getCollectionDate();
                }
            }
            c.setLendingDate(paidInDate);
        });
    }

    /**
     * 已结清的账户表 直租业务 计算补全数据
     * 当最后一期核销完毕时，通过 合同金额-累计已还本金-累计已收首期租金 计算得到本金余额，
     * 当本金余额大于0时，自动往实际还款表中插入一期现金流，日期取最后一期，实际归还本金取上述计算得到的本金余额，利息默认为0。
     */
    private void calZhizuClosedFixLastPhase() {
        List<CrAccountDraft> zhizuClosedAccountList = draftMapper.selectList(Wrappers.<CrAccountDraft>lambdaQuery()
                .likeLeft(CrAccountDraft::getPaymentApplyCode, "HZ")
                .isNotNull(CrAccountDraft::getClosedDate)
        );
        if (CollectionUtils.isEmpty(zhizuClosedAccountList)) {
            return;
        }
        // 过滤掉已经处理过的数据
        Map<Long, CrActualRepayDraft> hadFixedDataMap = crActualRepayDraftMapper.selectList(Wrappers.<CrActualRepayDraft>lambdaQuery()
                        .likeLeft(CrActualRepayDraft::getBusinessKey, ReportConstants.ZHI_ZU_LAST_PHASE_BK_SUFFIX)
                        .in(CrActualRepayDraft::getPaymentId, zhizuClosedAccountList.stream().map(CrAccountDraft::getPaymentId).collect(Collectors.toList())))
                .stream().collect(Collectors.toMap(CrActualRepayDraft::getPaymentId, Function.identity(), (k1, k2) -> k1));
        List<CrAccountDraft> needHandleZhizuClosedAccountList = zhizuClosedAccountList.stream().filter(a -> !hadFixedDataMap.containsKey(a.getPaymentId())).collect(Collectors.toList());

        for (CrAccountDraft crAccountDraft : needHandleZhizuClosedAccountList) {
            // 加强判断 如果 不是直租就直接返回
            ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibMapper.selectOne(Wrappers.<ContractBaseInfoLib>lambdaQuery()
                    .eq(ContractBaseInfoLib::getOriginId, crAccountDraft.getContractId())
                    .eq(ContractBaseInfoLib::getVersionType, VersionTypeConstants.NORMAL)
                    .orderByDesc(ContractBaseInfoLib::getVersion)
                    .last(StringUtil.mysqlLimitOne())
            );
            if (!LeaseType.zhi_zu.name().equals(contractBaseInfoLib.getLeaseType())) {
                continue;
            }
            // 找到 首期租金
            long firstRentCollectionAmount = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getContractId, crAccountDraft.getContractId())
                    .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.FIRST_RENT.name())
            ).stream().mapToLong(s -> LongUtil.null2zero(s.getCollectionAmount())).sum();

            // 找到租金 - 已收本金
            long rentCollectionPrincipal = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getContractId, crAccountDraft.getContractId())
                    .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                    .ne(CollectionBaseInfo::getPhase, 0)
            ).stream().mapToLong(s -> LongUtil.null2zero(s.getCollectionPrincipal())).sum();

            long notCollectionPrincipal = contractBaseInfoLib.getApplyCreditAmount() - firstRentCollectionAmount - rentCollectionPrincipal;
            if (notCollectionPrincipal > 0) {
                // 找到最后一期还款
                CollectionBaseInfo finalCollection = collectionBaseInfoMapper.selectOne(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                        .ne(CollectionBaseInfo::getPhase, 0)
                        .eq(CollectionBaseInfo::getContractId, crAccountDraft.getContractId())
                        .orderByDesc(CollectionBaseInfo::getPhase)
                        .last(StringUtil.mysqlLimitOne())
                );
                CrActualRepayDraft crActualRepayDraft = CrActualRepayDraft.builder().build();
                crActualRepayDraft.setReportState(ReportState.TO_BE_REPORT.name())
                        .setApprovalStatus(ApprovalStatus.UN_SUBMIT.name())
                        .setProcBusinessKey(null)
                        .setPaymentApplyCode(crAccountDraft.getPaymentApplyCode())
                        .setPaymentId(crAccountDraft.getPaymentId())
                        .setPhase(finalCollection.getPhase())
                        .setPayDate(finalCollection.getCollectionDate())
                        .setCollectionAmount(notCollectionPrincipal)
                        .setCollectionPrincipal(notCollectionPrincipal);
                crActualRepayDraft.setBusinessKey(IdUtil.getSnowflakeNextIdStr() + ReportConstants.ZHI_ZU_LAST_PHASE_BK_SUFFIX);
                crActualRepayDraft.setContractId(crAccountDraft.getContractId());
                crActualRepayDraftMapper.insert(crActualRepayDraft);
            }
        }
    }

    @Override
    public Integer sort() {
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, transactionManager = ReportConstants.TRANSACTION_MANAGER)
    public void afterModuleHandle(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        //查询这段时间内已经有结清日期的账户
        List<CrAccountDraft> closeAccount = accountDraftService.list(Wrappers.<CrAccountDraft>lambdaQuery()
                .eq(CrAccountDraft::getReportState, ReportState.TO_BE_REPORT.name())
                .eq(CrAccountDraft::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name())
                .ge(CrAccountDraft::getUpdateTime, lastDealTime)
                .lt(CrAccountDraft::getUpdateTime, dealTime)
                .isNotNull(CrAccountDraft::getClosedDate)
        );

        if (CollUtil.isEmpty(closeAccount)) {
            return;
        }
        log.info("可能不应该结清且已经结清的账户：{}", closeAccount.stream().map(CrAccountDraft::getId).collect(Collectors.toList()));

        Map<Long, List<CrAccountDraft>> listMap = closeAccount.stream().collect(Collectors.groupingBy(CrAccountDraft::getContractId));
        //将这些账户的实际收款信息集中
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .in(CollectionBaseInfo::getContractId, closeAccount.stream().map(CrAccountDraft::getContractId).collect(Collectors.toSet()))
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .ne(CollectionBaseInfo::getWriteOffStatus, WRITE_OFF_COMPLETED.name()));

        if (CollUtil.isEmpty(collectionBaseInfos)) {
            return;
        }

        Map<Long, List<CollectionBaseInfo>> contractIdDoMap = collectionBaseInfos.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getContractId));

        //先判断业务库的合同状态，如果是结清，再判断租金是否还完，否则直接返回
        List<CrAccountDraft> res = new ArrayList<>();
        contractIdDoMap.forEach((contractId, collectionBaseInfoList) -> {
            ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibMapper.selectOne(Wrappers.<ContractBaseInfoLib>lambdaQuery()
                    .ne(ContractBaseInfo::getContractProcessStatus, ContractStatus.SETTLE.name())
                    .eq(ContractBaseInfoLib::getOriginId, contractId)
                    .eq(ContractBaseInfoLib::getVersionType, VersionTypeConstants.NORMAL)
                    .orderByDesc(ContractBaseInfoLib::getVersion)
                    .last(StringUtil.mysqlLimitOne()));

            if (ObjectUtil.isNotNull(contractBaseInfoLib)) {
                res.addAll(Optional.ofNullable(listMap.get(contractId)).orElse(Collections.emptyList()));
                return;
            }

            long surplusRent = collectionBaseInfoList.stream().mapToLong(a ->
                    ifNull2Zero(a.getCollectionAmount()) - (ifNull2Zero(a.getPrincipal()) + ifNull2Zero(a.getInterest()))
            ).sum();

            //如果剩余租金小于0，则说明钱还没还完
            if (surplusRent < 0) {
                res.addAll(Optional.ofNullable(listMap.get(contractId)).orElse(Collections.emptyList()));
            }
        });

        if (CollUtil.isNotEmpty(res)) {
            accountDraftService.lambdaUpdate()
                    .in(CrAccountBase::getContractId, res.stream().map(CrAccountDraft::getContractId).collect(Collectors.toSet()))
                    .set(CrAccountBase::getClosedDate, null)
                    .update();
            log.info("合同未结清，借据还完，处理的账户表记录列表：{}", res.stream().map(CrAccountDraft::getContractId).collect(Collectors.toList()));
        }
    }

    private Long ifNull2Zero(Long number) {
        if (Objects.isNull(number)) {
            return 0L;
        }
        return number;
    }
}
