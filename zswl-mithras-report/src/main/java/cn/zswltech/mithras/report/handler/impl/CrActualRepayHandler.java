package cn.zswltech.mithras.report.handler.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.report.enums.common.ApprovalStatus;
import cn.zswltech.mithras.report.enums.common.ReportModuleEnum;
import cn.zswltech.mithras.report.enums.common.ReportState;
import cn.zswltech.mithras.report.handler.CrAbstractHandler;
import cn.zswltech.mithras.report.handler.ReportDataRepository;
import cn.zswltech.mithras.report.mapper.draft.CrRepayPlanDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.model.CrActualRepayDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrRepayPlanDraft;
import cn.zswltech.mithras.report.mapper.formal.model.CrActualRepay;
import cn.zswltech.mithras.report.util.ReportBizUtil;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.collection.mapper.CollectionRecordInfoMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractBaseInfoLibMapper;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.mapper.model.CollectionRecordInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.contract.core.application.ContractBaseInfoService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.hutool.core.util.NumberUtil.add;
import static cn.hutool.core.util.ObjectUtil.equal;

/**
 * 征信报送-实际还款表
 * 在还款计划之后执行
 * 在账户表之后执行
 *
 * @author wangchuanhao
 * @date 2022/10/8 5:40 PM
 */
@Component
@Slf4j
@Order(100)
public class CrActualRepayHandler extends CrAbstractHandler<CrActualRepayDraft, CrActualRepay> {

    @Resource
    private CollectionRecordInfoMapper collectionRecordInfoMapper;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private CrRepayPlanDraftMapper crRepayPlanDraftMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractBaseInfoLibMapper contractBaseInfoLibMapper;

    @Override
    public ReportModuleEnum reportModule() {
        return ReportModuleEnum.ACTUAL_REPAY;
    }


    private void cancelWriteOffProcess(List<CollectionRecordInfo> recordList) {
        //过滤出反核销的记录
        List<CollectionRecordInfo> cancelList = recordList.stream()
                .filter(e -> equal(1, e.getCancelWriteOffFlag())).collect(Collectors.toList());
        //映射出所有的对应的发核销记录id
        Map<Long, Long> map = cancelList.stream().collect(Collectors.toMap(CollectionRecordInfo::getId, CollectionRecordInfo::getCancelWriteOffId));
        Set<String> needRemoveRecordIdSet = new HashSet<>();
        map.forEach((k, v) -> {
            Long cancelWriteOffId = map.get(k);
            //如果对应的反核销记录也在本次记录中，则不用处理。否则需要去删除对应的记录
            if (!map.containsKey(cancelWriteOffId)) {
                needRemoveRecordIdSet.add(cancelWriteOffId.toString());
            }
        });

        if (!needRemoveRecordIdSet.isEmpty()) {
            draftMapper.delete(Wrappers.<CrActualRepayDraft>lambdaQuery()
                    .in(CrActualRepayDraft::getBusinessKey, needRemoveRecordIdSet));
        }
    }

    @Override
    protected void moduleHandle(LocalDateTime dealTime, LocalDateTime lastDealTime) {
        List<CollectionRecordInfo> operateList = collectionRecordInfoMapper.selectList(Wrappers.<CollectionRecordInfo>lambdaQuery()
                .gt(CollectionRecordInfo::getCreateTime, lastDealTime)
                .le(CollectionRecordInfo::getCreateTime, dealTime)
        );
        //处理反核销的记录（之前的正常记录被反核销掉了，需要删除）
        cancelWriteOffProcess(operateList);
        //只处理非反核销的记录
        operateList.removeIf(e -> equal(1, e.getCancelWriteOffFlag()));

        log.info("征信报送-实际还款表，此次处理数量:{}, 处理businessKey列表:{}", operateList.size(), JSON.toJSONString((operateList.stream().map(CollectionRecordInfo::getId).collect(Collectors.toList()))));
        if (CollectionUtils.isNotEmpty(operateList)) {
            List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .in(CollectionBaseInfo::getId, operateList.stream().map(CollectionRecordInfo::getCollectionId).collect(Collectors.toSet()))
                    .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
            );
            if(CollUtil.isEmpty(collectionBaseInfos)){
                return;
            }
            //过滤掉非直租的数据
            List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.listByIds(collectionBaseInfos.stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toSet()));
            Set<Long> contractIds = contractBaseInfos.stream().filter(a -> LeaseType.zhi_zu.name().equals(a.getLeaseType())).map(ContractBaseInfo::getId).collect(Collectors.toSet());
            Map<Long, CollectionBaseInfo> collectionBaseInfoMap = collectionBaseInfos.stream().filter(a -> contractIds.contains(a.getContractId())).collect(Collectors.toMap(CollectionBaseInfo::getId, Function.identity(), (k1, k2)->k1));
            // 过滤掉收款明细列表中 收款主表信息为空的（非租金或错误数据）
            operateList = operateList.stream().filter(o -> Objects.nonNull(collectionBaseInfoMap.get(o.getCollectionId()))).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(operateList)) {
                // 找到借据关联的付款
                List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery().in(PaymentBaseInfo::getReceiptIdFinal, collectionBaseInfoMap.values().stream().map(CollectionBaseInfo::getReceiptId).collect(Collectors.toList())));
                paymentBaseInfoList = reportDataRepository.filterNeedReportPaymentListSubTable(paymentBaseInfoList);
                Map<Long, PaymentBaseInfo> receiptPaymentMap = paymentBaseInfoList.stream().collect(Collectors.toMap(PaymentBaseInfo::getReceiptIdFinal, Function.identity(), (k1,k2)->k1));

                // 合同主承租人map
                Map<Long, Boolean> contractReportMap = reportDataRepository.contractReportMap(collectionBaseInfoMap.values().stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toSet()));
                List<CrActualRepayDraft> crActualRepayDraftList = new ArrayList<>();
                //同一天的收款记录进合并成一条
                operateList = new ArrayList<>(mergePhaseAmount(operateList));
                for (CollectionRecordInfo operateRecord : operateList) {
                    CollectionBaseInfo baseInfo = collectionBaseInfoMap.get(operateRecord.getCollectionId());
                    if (Objects.isNull(baseInfo)) {
                        continue;
                    }
                    //调整还款表内数据推送逻辑，对于已到还款日但是未进行还款的客户，每日推送该期次应收数据至待报送-还款表内，
                    //当该期租金客户已还款且已完成核销，或该期租金已在逾期表内存在，则停止更新
                    //获取是否在逾期表中存在，是否逾期字段写入列表
//                    boolean isOverdue = reportDataRepository.getPlanIsOverdue(baseInfo.getContractId(),baseInfo.getReceiptCode(),baseInfo.getPhase());
//                    if(baseInfo.getWriteOffStatus().equals(CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name())||isOverdue){
//                        return;
//                    }
//                    //对其项下各期次的计划收款日期<=当前日期&核销状态=未核销/部分核销/核销完毕（且在待报送还款表内不存在）&该期次数据在逾期表内不存在的数据更新至待报送-还款表内
//                    if(dealTime.isBefore(baseInfo.getPlanCollectionDate().plusDays(1).atStartOfDay())){
//                        return;
//                    }
                    PaymentBaseInfo paymentBaseInfo = receiptPaymentMap.get(baseInfo.getReceiptId());
                    if (Objects.isNull(paymentBaseInfo)
                            || !Boolean.TRUE.equals(contractReportMap.get(baseInfo.getContractId()))
                    ) {
                        // 付款核销完毕、合同主承租人上报逻辑过滤、债权转让不报
                        continue;
                    }
                    ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibMapper.selectOne(Wrappers.<ContractBaseInfoLib>lambdaQuery()
                            .eq(ContractBaseInfoLib::getOriginId, paymentBaseInfo.getContractId())
                            .eq(ContractBaseInfoLib::getVersionType, VersionTypeConstants.NORMAL)
                            .orderByDesc(ContractBaseInfoLib::getVersion)
                            .last(StringUtil.mysqlLimitOne())
                    );
                    CrActualRepayDraft crActualRepayDraft = CrActualRepayDraft.builder().build();
                    crActualRepayDraft.setReportState(ReportState.TO_BE_REPORT.name())
                            .setApprovalStatus(ApprovalStatus.UN_SUBMIT.name())
                            .setProcBusinessKey(null)
                            .setPaymentApplyCode(ReportBizUtil.bizCalPaymentCode(paymentBaseInfo.getPaymentCode(), contractBaseInfoLib.getLeaseType()))
                            .setPaymentId(paymentBaseInfo.getId())
                            .setPhase(baseInfo.getPhase())
                            .setPayDate(operateRecord.getCollectionDate())
                            .setCollectionAmount(operateRecord.getCollectionAmount())
                            .setCollectionPrincipal(operateRecord.getPrincipal());
                    crActualRepayDraft.setBusinessKey(crActualRepayDraft.genBusinessKey(operateRecord.getId(), operateRecord.getCollectionDate()));
                    crActualRepayDraft.setContractId(baseInfo.getContractId());
                    crActualRepayDraftList.add(crActualRepayDraft);
                }
                draftService.saveBatch(crActualRepayDraftList);
                for (CrActualRepayDraft draft : crActualRepayDraftList) {
                    // 更新还款计划表时 需要把实际还款表待报送的数据、该实际还款表对应的实际付款表 一起从审批流里剔除掉
                    // 还款计划表
                    LambdaUpdateWrapper<CrRepayPlanDraft> repayPlanUpdateWrapper = new LambdaUpdateWrapper<>();
                    repayPlanUpdateWrapper.eq(CrRepayPlanDraft::getPaymentId, draft.getPaymentId());
                    repayPlanUpdateWrapper.eq(CrRepayPlanDraft::getPhase, draft.getPhase());
                    repayPlanUpdateWrapper.eq(CrRepayPlanDraft::getReportState, ReportState.TO_BE_REPORT.name());
                    repayPlanUpdateWrapper.set(CrRepayPlanDraft::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name());
                    repayPlanUpdateWrapper.set(CrRepayPlanDraft::getProcBusinessKey, null);
                    repayPlanUpdateWrapper.set(CrRepayPlanDraft::getIsShow, YesOrNoNumberEnum.YES.getCode());
                    crRepayPlanDraftMapper.update(null, repayPlanUpdateWrapper);
                    // 实际还款表
                    LambdaUpdateWrapper<CrActualRepayDraft> actualRepayUpdateWrapper = new LambdaUpdateWrapper<>();
                    actualRepayUpdateWrapper.eq(CrActualRepayDraft::getPaymentId, draft.getPaymentId());
                    actualRepayUpdateWrapper.eq(CrActualRepayDraft::getPhase, draft.getPhase());
                    actualRepayUpdateWrapper.eq(CrActualRepayDraft::getReportState, ReportState.TO_BE_REPORT.name());
                    actualRepayUpdateWrapper.set(CrActualRepayDraft::getApprovalStatus, ApprovalStatus.UN_SUBMIT.name());
                    actualRepayUpdateWrapper.set(CrActualRepayDraft::getProcBusinessKey, null);
                    draftMapper.update(null, actualRepayUpdateWrapper);
                }
            }
        }
    }

    /**
     * 合并同一笔收款同一期项的收款
     */
    protected Collection<CollectionRecordInfo> mergePhaseAmount(Collection<CollectionRecordInfo> operateList) {
        Map<String, CollectionRecordInfo> map = new HashMap<>(operateList.size());
        for (CollectionRecordInfo record : operateList) {
            String key = StrUtil.join(",", record.getCollectionId(), record.getCollectionDate());
            if (map.containsKey(key)) {
                CollectionRecordInfo collectionRecordInfo = map.get(key);
                collectionRecordInfo.setCollectionAmount(add(collectionRecordInfo.getCollectionAmount(), record.getCollectionAmount()).longValue());
                collectionRecordInfo.setPrincipal(add(collectionRecordInfo.getPrincipal(), record.getPrincipal()).longValue());
                collectionRecordInfo.setInterest(add(collectionRecordInfo.getInterest(), record.getInterest()).longValue());
                map.put(key, collectionRecordInfo);
            } else {
                map.put(key, record);
            }
        }
        if (operateList.size() != map.values().size()) {
            log.info("merged collection record. {}条->{}条", operateList.size(), map.values().size());
        }
        return map.values();
    }

    @Override
    public Integer sort() {
        return 15;
    }
}
