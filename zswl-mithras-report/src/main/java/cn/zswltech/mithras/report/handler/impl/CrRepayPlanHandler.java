package cn.zswltech.mithras.report.handler.impl;

import cn.hutool.core.builder.EqualsBuilder;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.mithras.report.enums.biz.DataTypeEnum;
import cn.zswltech.mithras.report.enums.common.ApprovalStatus;
import cn.zswltech.mithras.report.enums.common.ReportModuleEnum;
import cn.zswltech.mithras.report.enums.common.ReportState;
import cn.zswltech.mithras.report.handler.CrAbstractHandler;
import cn.zswltech.mithras.report.mapper.base.model.CrRepayPlanBase;
import cn.zswltech.mithras.report.mapper.draft.CrActualRepayDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.model.CrAccountDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrActualRepayDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrRepayPlanDraft;
import cn.zswltech.mithras.report.mapper.formal.model.CrRepayPlan;
import cn.zswltech.mithras.report.service.draft.CrAccountDraftService;
import cn.zswltech.mithras.report.service.draft.CrActualRepayDraftService;
import cn.zswltech.mithras.report.service.draft.CrRepayPlanDraftService;
import cn.zswltech.mithras.report.util.ReportBizUtil;
import cn.zswltech.mithras.report.util.ReportCompareUtil;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.payment.domain.enums.PaymentWriteOffStatus;
import cn.zswltech.mithras.service.mapper.collection.CollectionBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractBaseInfoLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractReceiptLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractRentActualLibMapper;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceiptLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActual;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActualLib;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.util.StreamUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;

/**
 * 征信报送-还款计划表
 *
 * @author wangchuanhao
 * @date 2022/10/8 6:42 PM
 */
@Component
@Slf4j
@Order(-1)
public class CrRepayPlanHandler extends CrAbstractHandler<CrRepayPlanDraft, CrRepayPlan> {

    @Value("${report.overdue.days}")
    private Integer overdueDays;

    @Resource
    private ContractBaseInfoLibMapper contractBaseInfoLibMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private ContractRentActualLibMapper contractRentActualLibMapper;
    @Resource
    private ContractReceiptLibMapper contractReceiptLibMapper;
    @Resource
    private CrActualRepayDraftMapper crActualRepayDraftMapper;
    @Resource
    private CrAccountDraftService crAccountDraftService;
    @Resource
    private CrRepayPlanDraftService crRepayPlanDraftService;

    @Override
    public ReportModuleEnum reportModule() {
        return ReportModuleEnum.REPAY_PLAN;
    }

    @Override
    protected void moduleHandle(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        Map<String, List<ContractBaseInfoLib>> stringListMap = reportDataRepository.listChangeContractBaseInfo(dealTime, lastDealTime);
        List<ContractBaseInfoLib> contractBaseInfoLibList = stringListMap.get(DataTypeEnum.ZHI_ZU.name());
        if (CollUtil.isEmpty(contractBaseInfoLibList)) {
            contractBaseInfoLibList = Collections.emptyList();
        }
        List<CrRepayPlanDraft> reportDataList = new ArrayList<>();
        reportDataList.addAll(collectContractChangeData(contractBaseInfoLibList, dealTime, lastDealTime));
        reportDataList.addAll(collectPaymentChangeData(dealTime, lastDealTime));
        reportDataList = reportDataList.stream().filter(StreamUtil.distinctByKey(CrRepayPlanDraft::getBusinessKey)).collect(Collectors.toList());
        Set<String> businessKeySet = reportDataList.stream().map(CrRepayPlanDraft::getBusinessKey).collect(Collectors.toSet());

        // 该次需上报的businessKey对应的列表 用于判断新增还是编辑
        List<CrRepayPlanDraft> existDataList = reportDataList.isEmpty() ? new ArrayList<>() : draftMapper.selectList(Wrappers.<CrRepayPlanDraft>lambdaQuery()
                .in(CrRepayPlanDraft::getBusinessKey, reportDataList.stream().map(CrRepayPlanDraft::getBusinessKey).collect(Collectors.toList()))
        );
        Map<String, CrRepayPlanDraft> existReportDataMap = existDataList.stream().collect(Collectors.toMap(CrRepayPlanDraft::getBusinessKey, Function.identity(), (k1, k2) -> k1));

        // 合同已存在的数据 用于判断发生变动需要删除的数据
        Map<Long, Map<Long, Set<String>>> pbkMap = new HashMap<>();
        for (CrRepayPlanDraft crRepayPlanDraft : reportDataList) {
            Map<Long, Set<String>> paymentMap = pbkMap.computeIfAbsent(crRepayPlanDraft.getContractId(), k -> new HashMap<>());
            Set<String> bkSet = paymentMap.computeIfAbsent(crRepayPlanDraft.getPaymentId(), k -> new HashSet<>());
            bkSet.add(crRepayPlanDraft.getBusinessKey());
        }
        List<CrRepayPlanDraft> existContractDataList = contractBaseInfoLibList.isEmpty() ? new ArrayList<>() : draftMapper.selectList(Wrappers.<CrRepayPlanDraft>lambdaQuery()
                .in(CrRepayPlanDraft::getContractId, contractBaseInfoLibList.stream().map(ContractBaseInfoLib::getOriginId).collect(Collectors.toList()))
        );
        Set<String> needDeleteKeyList = existContractDataList.stream().filter(k -> {
            if (!pbkMap.containsKey(k.getContractId()) || !pbkMap.get(k.getContractId()).containsKey(k.getPaymentId())) {
                // 如果此次收集的数据 不含变动合同的id或不含付款id则不处理
                return false;
            }
            // 该合同下该付款曾经有数据 但此次报送没有 需删除
            return !pbkMap.get(k.getContractId()).get(k.getPaymentId()).contains(k.getBusinessKey());
        }).map(CrRepayPlanDraft::getBusinessKey).collect(Collectors.toSet());

        log.info("直租征信报送-还款计划表处理，此次处理数量:{}, 处理businessKey列表:{}, 删除businessKey列表:{}", reportDataList.size(), JSON.toJSONString((businessKeySet)), JSON.toJSONString(needDeleteKeyList));
        if (CollectionUtils.isNotEmpty(needDeleteKeyList)) {
            draftMapper.delete(Wrappers.<CrRepayPlanDraft>lambdaQuery().in(CrRepayPlanDraft::getBusinessKey, needDeleteKeyList));
            formalMapper.delete(Wrappers.<CrRepayPlan>lambdaQuery().in(CrRepayPlan::getBusinessKey, needDeleteKeyList));
        }
        List<CrRepayPlanDraft> needInsertList = new ArrayList<>();
        for (CrRepayPlanDraft reportData : reportDataList) {
            if (!existReportDataMap.containsKey(reportData.getBusinessKey())) {
                reportData.setIsShow(YesOrNoNumberEnum.YES.getCode());
                needInsertList.add(reportData);
            } else {
                CrRepayPlanDraft existData = existReportDataMap.get(reportData.getBusinessKey());
                if (ReportCompareUtil.checkChange(reportData, existData, existData.ignoreCompareFieldNames())) {
                    reportData.setId(existData.getId());
                    draftMapper.updateById(reportData);
                    // 更新还款计划表时 需要把实际还款表待报送的数据一起从审批流里剔除掉
                    SpringContextHolder.getBean(CrActualRepayDraftService.class).lambdaUpdate()
                            .eq(CrActualRepayDraft::getPaymentId, reportData.getPaymentId())
                            .eq(CrActualRepayDraft::getPhase, reportData.getPhase())
                            .eq(CrActualRepayDraft::getReportState, ReportState.TO_BE_REPORT.name())
                            .set(CrActualRepayDraft::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name())
                            .set(CrActualRepayDraft::getProcBusinessKey, null)
                            .update();
                }
            }
        }
        draftService.saveBatch(needInsertList);
    }

    public List<CrRepayPlanDraft> collectContractChangeData(List<ContractBaseInfoLib> contractBaseInfoLibList, LocalDateTime dealTime, LocalDateTime lastDealTime) {
        List<CrRepayPlanDraft> resultList = new ArrayList<>();
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
                    .in(PaymentBaseInfo::getWriteOffStatus, Arrays.asList(PaymentWriteOffStatus.WRITTEN_OFF.name(), PaymentWriteOffStatus.PART_WRITTEN_OFF.name()))
                    .eq(PaymentBaseInfo::getPaymentStatus, RecordStatus.TAKE_EFFECT.name())
            );
            paymentBaseInfoList = reportDataRepository.filterNeedReportPaymentListSubTable(paymentBaseInfoList);
            if (CollectionUtils.isEmpty(paymentBaseInfoList)) {
                continue;
            }
            List<ContractReceiptLib> contractReceiptLibList = contractReceiptLibMapper.selectList(Wrappers.<ContractReceiptLib>lambdaQuery()
                    .eq(ContractReceiptLib::getContractId, contractBaseInfoLib.getOriginId())
                    .eq(ContractReceiptLib::getVersionType, VersionTypeConstants.NORMAL)
                    .eq(ContractReceiptLib::getVersion, contractBaseInfoLib.getVersion())
            );
            Map<Long, ContractReceiptLib> contractReceiptLibMap = contractReceiptLibList.stream().collect(Collectors.toMap(ContractReceiptLib::getOriginId, Function.identity(), (k1, k2) -> k1));
            if (CollectionUtils.isEmpty(contractReceiptLibList)) {
                continue;
            }
            Map<Long, List<ContractRentActualLib>> contractRentActualLibMap = contractRentActualLibMapper.selectList(Wrappers.<ContractRentActualLib>lambdaQuery()
                    .eq(ContractRentActualLib::getContractId, contractBaseInfoLib.getOriginId())
                    .eq(ContractRentActualLib::getVersionType, VersionTypeConstants.NORMAL)
                    .eq(ContractRentActualLib::getVersion, contractBaseInfoLib.getVersion())
                    .ne(ContractRentActualLib::getCashFlowCode, "")
                    .isNotNull(ContractRentActualLib::getCashFlowCode)
                    .in(ContractRentActualLib::getReceiptId, contractReceiptLibList.stream().map(ContractReceiptLib::getOriginId).collect(Collectors.toList()))
            ).stream().collect(Collectors.groupingBy(ContractRentActualLib::getReceiptId));
            for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfoList) {
                ContractReceiptLib contractReceiptLib = contractReceiptLibMap.get(paymentBaseInfo.getReceiptIdFinal());
                if (Objects.isNull(contractReceiptLib)) {
                    // 兼容错误数据 实际不会出现此情况
                    continue;
                }
                List<ContractRentActualLib> contractRentActualLibList = contractRentActualLibMap.get(contractReceiptLib.getOriginId());
                if (CollUtil.isEmpty(contractRentActualLibList)) {
                    continue;
                }
                // 因为这里只处理直租合同，而直租合同只有一个借据，所以这里这么写是没有问题的
                Map<Integer, CrRepayPlanDraft> planDraftMap = SpringContextHolder.getBean(CrRepayPlanDraftService.class).list(Wrappers.<CrRepayPlanDraft>lambdaQuery()
                                .in(CrRepayPlanBase::getContractId, contractRentActualLibList.stream().map(ContractRentActual::getContractId).collect(Collectors.toList())))
                        .stream().collect(Collectors.toMap(CrRepayPlanBase::getPhase, Function.identity(), (a, b) -> a));
                contractRentActualLibList.forEach(c -> {
                    CrRepayPlanDraft draft = buildCrRepayPlan(paymentBaseInfo, c, contractBaseInfoLib);
                    CrRepayPlanDraft existPlan = planDraftMap.get(c.getCashFlowPhase());
                    if (Objects.isNull(existPlan)) {
                        resultList.add(draft);
                        return;
                    }
                    //核销完毕状态（需在待报送还款表内不存在）才更新
//                    if(paymentBaseInfo.getWriteOffStatus().equals(PaymentWriteOffStatus.WRITTEN_OFF.name())){
//                        return;
//                    }
                    boolean isTrue = new EqualsBuilder()
                            .append(draft.getPaymentApplyCode(), existPlan.getPaymentApplyCode())
                            .append(draft.getRent(), existPlan.getRent())
                            .append(draft.getPrincipal(), existPlan.getPrincipal())
                            .append(draft.getCashFlowDate(), existPlan.getCashFlowDate())
                            .append(draft.getPhase(), existPlan.getPhase())
                            .build().booleanValue();
                    if (!isTrue) {
                        // 存在修改
                        existPlan.setIsShow(YesOrNoNumberEnum.YES.getCode());
                        existPlan.setReportState(ReportState.TO_BE_REPORT.name());
                        existPlan.setRent(draft.getRent());
                        existPlan.setPrincipal(draft.getPrincipal());
                        existPlan.setCashFlowDate(draft.getCashFlowDate());
                        resultList.add(existPlan);
                    } else {
                        draft.setIsShow(YesOrNoNumberEnum.NO.getCode());
                        draft.setReportState(ReportState.TO_BE_REPORT.name());
                        resultList.add(draft);
                    }
                });

            }
        }
        return resultList;
    }

    public List<CrRepayPlanDraft> collectPaymentChangeData(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        List<CrRepayPlanDraft> resultList = new ArrayList<>();
        // 查找付款模块有变动的数据
        Map<String, List<PaymentBaseInfo>> stringListMap = reportDataRepository.listNeedReportChangePaymentBaseInfoSubTable(dealTime, lastDealTime);
        List<PaymentBaseInfo> paymentBaseInfoList = stringListMap.get(DataTypeEnum.ZHI_ZU.name());
        if (CollUtil.isEmpty(paymentBaseInfoList)) {
            return Collections.emptyList();
        }

        //提前查询数据库，提升效率
        Set<Long> paymentIds = paymentBaseInfoList.stream().map(PaymentBaseInfo::getId).collect(Collectors.toSet());
        Set<Long> receiptIds = paymentBaseInfoList.stream().map(PaymentBaseInfo::getReceiptIdFinal).collect(Collectors.toSet());
        Map<Long, List<CrAccountDraft>> paymentIdAccountListMap = crAccountDraftService.list(Wrappers.<CrAccountDraft>lambdaQuery()
                .in(CrAccountDraft::getPaymentId, paymentIds)
        ).stream().collect(Collectors.groupingBy(CrAccountDraft::getPaymentId));

        Map<Long, List<ContractReceiptLib>> paymentIdReceiptListMap = contractReceiptLibMapper.selectList(Wrappers.<ContractReceiptLib>lambdaQuery()
                .in(ContractReceiptLib::getOriginId, receiptIds)
        ).stream().collect(Collectors.groupingBy(ContractReceiptLib::getOriginId));

        List<PaymentBaseInfo> paymentBaseInfoTempList = new ArrayList<>();
        // 过滤掉没有实际租金表变更的数据
        paymentBaseInfoList.forEach(paymentBaseInfo -> {
            //拿到当前付款申请的所有账户
            List<CrAccountDraft> accountDrafts = paymentIdAccountListMap.get(paymentBaseInfo.getId());
            if (CollUtil.isEmpty(accountDrafts) || Objects.isNull(paymentBaseInfo.getReceiptIdFinal())) {
                //还没有账户
                return;
            }
            List<ContractReceiptLib> contractReceiptLibs = paymentIdReceiptListMap.get(paymentBaseInfo.getReceiptIdFinal());
            contractReceiptLibs.sort(Comparator.comparing(ContractReceiptLib::getVersion).reversed());
            ContractReceiptLib contractReceiptLib = contractReceiptLibs.get(0);
            //查询实际租金表并根据期项映射
            List<ContractRentActualLib> contractRentActualLibs = contractRentActualLibMapper.selectList(Wrappers.<ContractRentActualLib>lambdaQuery()
                    .eq(ContractRentActualLib::getContractId, paymentBaseInfo.getContractId())
                    .eq(ContractRentActualLib::getReceiptId, contractReceiptLib.getOriginId())
                    .eq(ContractRentActualLib::getVersionType, VersionTypeConstants.NORMAL)
                    .eq(ContractRentActualLib::getVersion, contractReceiptLib.getVersion())
                    .gt(ContractRentActualLib::getCreateTime, lastDealTime)
                    .le(ContractRentActualLib::getCreateTime, dealTime)
                    .ne(ContractRentActualLib::getCashFlowCode, "")
                    .isNotNull(ContractRentActualLib::getCashFlowCode)
            );
            if (CollUtil.isNotEmpty(contractRentActualLibs)) {
                //当前时间段没有新的租金表生成
                paymentBaseInfoTempList.add(paymentBaseInfo);
            }
        });

        if (CollUtil.isEmpty(paymentBaseInfoTempList)) {
            return Collections.emptyList();
        }
        // 版本号倒序 根据主表id去重
        for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfoTempList) {
            ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibMapper.selectOne(Wrappers.<ContractBaseInfoLib>lambdaQuery()
                    .eq(ContractBaseInfoLib::getOriginId, paymentBaseInfo.getContractId())
                    .eq(ContractBaseInfoLib::getVersionType, VersionTypeConstants.NORMAL)
                    .orderByDesc(ContractBaseInfoLib::getVersion)
                    .last(StringUtil.mysqlLimitOne())
            );
            if (Objects.isNull(contractBaseInfoLib)) {
                // 兼容历史错误数据 实际上有付款申请，就会有合同版本
                continue;
            }

            ContractReceiptLib contractReceiptLib = contractReceiptLibMapper.selectOne(Wrappers.<ContractReceiptLib>lambdaQuery()
                    .eq(ContractReceiptLib::getContractId, contractBaseInfoLib.getOriginId())
                    .eq(ContractReceiptLib::getVersion, contractBaseInfoLib.getVersion())
                    .eq(ContractReceiptLib::getOriginId, paymentBaseInfo.getReceiptIdFinal())
                    .last(StringUtil.mysqlLimitOne())
            );
            if (Objects.isNull(contractReceiptLib)) {
                continue;
            }
            List<ContractRentActualLib> contractRentActualLibList = contractRentActualLibMapper.selectList(Wrappers.<ContractRentActualLib>lambdaQuery()
                    .eq(ContractRentActualLib::getContractId, contractBaseInfoLib.getOriginId())
                    .eq(ContractRentActualLib::getVersionType, VersionTypeConstants.NORMAL)
                    .eq(ContractRentActualLib::getVersion, contractBaseInfoLib.getVersion())
                    .ne(ContractRentActualLib::getCashFlowCode, "")
                    .isNotNull(ContractRentActualLib::getCashFlowCode)
                    .eq(ContractRentActualLib::getReceiptId, contractReceiptLib.getOriginId())
            );

            //需要将首期利息加到租金表的第0期，这个是征信特有的功能，先查询是否有首期利息
            CollectionBaseInfo collectionBaseInfo = getBean(CollectionBaseInfoMapper.class).selectOne(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                    .eq(CollectionBaseInfo::getPhase, 0)
                    .eq(CollectionBaseInfo::getContractId, contractBaseInfoLib.getOriginId()));
            //如果存在就加入
            if (Objects.nonNull(collectionBaseInfo)) {
                ContractRentActualLib firstInterest = new ContractRentActualLib();
                firstInterest.setContractId(collectionBaseInfo.getContractId());
                firstInterest.setPrincipal(0L);
                firstInterest.setRent(collectionBaseInfo.getPlanCollectionAmount());
                firstInterest.setInterest(collectionBaseInfo.getInterest());
                firstInterest.setCashFlowPhase(collectionBaseInfo.getPhase());
                firstInterest.setCashFlowDate(collectionBaseInfo.getPlanCollectionDate());
                contractRentActualLibList.add(firstInterest);
            }

            Map<Integer, CrRepayPlanDraft> planDraftMap = SpringContextHolder.getBean(CrRepayPlanDraftService.class).list(Wrappers.<CrRepayPlanDraft>lambdaQuery()
                            .in(CrRepayPlanBase::getContractId, contractRentActualLibList.stream().map(ContractRentActual::getContractId).collect(Collectors.toList())))
                    .stream().collect(Collectors.toMap(CrRepayPlanBase::getPhase, Function.identity(), (a, b) -> a));
            contractRentActualLibList.forEach(c -> {
                CrRepayPlanDraft draft = buildCrRepayPlan(paymentBaseInfo, c, contractBaseInfoLib);
                CrRepayPlanDraft existPlan = planDraftMap.get(c.getCashFlowPhase());
                if (Objects.isNull(existPlan)) {
                    resultList.add(draft);
                    return;
                }
                //核销完毕状态（需在待报送还款表内不存在）才更新
//                if(paymentBaseInfo.getWriteOffStatus().equals(PaymentWriteOffStatus.WRITTEN_OFF.name())){
//                    return;
//                }
                boolean isTrue = new EqualsBuilder()
                        .append(draft.getPaymentApplyCode(), existPlan.getPaymentApplyCode())
                        .append(draft.getRent(), existPlan.getRent())
                        .append(draft.getPrincipal(), existPlan.getPrincipal())
                        .append(draft.getCashFlowDate(), existPlan.getCashFlowDate())
                        .append(draft.getPhase(), existPlan.getPhase())
                        .build().booleanValue();
                if (!isTrue) {
                    draft.setIsShow(YesOrNoNumberEnum.YES.getCode());
                    draft.setBusinessKey(existPlan.genBusinessKey());
                    resultList.add(draft);
                } else {
                    draft.setReportState(ReportState.TO_BE_REPORT.name());
                    draft.setBusinessKey(existPlan.genBusinessKey());
                    draft.setIsShow(YesOrNoNumberEnum.NO.getCode());
                    resultList.add(draft);
                }
            });
        }
        return resultList;
    }

    private CrRepayPlanDraft buildCrRepayPlan(PaymentBaseInfo paymentBaseInfo, ContractRentActualLib contractRentActualLib, ContractBaseInfoLib contractBaseInfoLib) {
        CrRepayPlanDraft crRepayPlan = CrRepayPlanDraft.builder().build();
        crRepayPlan.setReportState(ReportState.TO_BE_REPORT.name())
                .setApprovalStatus(ApprovalStatus.UN_SUBMIT.name())
                .setProcBusinessKey(null)
                .setPaymentApplyCode(ReportBizUtil.bizCalPaymentCode(paymentBaseInfo.getPaymentCode(), contractBaseInfoLib.getLeaseType()))
                .setPaymentId(paymentBaseInfo.getId())
                .setPhase(contractRentActualLib.getCashFlowPhase())
                .setCashFlowDate(contractRentActualLib.getCashFlowDate())
                .setGracePeriod(overdueDays.toString())
                .setRent(contractRentActualLib.getRent())
                .setPrincipal(contractRentActualLib.getPrincipal());
        crRepayPlan.setBusinessKey(crRepayPlan.genBusinessKey());
        crRepayPlan.setContractId(paymentBaseInfo.getContractId());
        crRepayPlan.setIsShow(YesOrNoNumberEnum.YES.getCode());
        return crRepayPlan;
    }

    @Override
    public Integer sort() {
        return 12;
    }
}
