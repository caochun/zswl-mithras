package cn.zswltech.mithras.report.handler.impl.newimpl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.report.enums.common.ReportModuleEnum;
import cn.zswltech.mithras.report.handler.CrAbstractHandler;
import cn.zswltech.mithras.report.mapper.draft.CrRepayPlanDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.model.CrActualRepayDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrRepayPlanDraft;
import cn.zswltech.mithras.report.mapper.formal.model.CrActualRepay;
import cn.zswltech.mithras.report.service.draft.CrActualRepayDraftService;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.collection.mapper.CollectionRecordInfoMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractBaseInfoLibMapper;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.mapper.model.CollectionRecordInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import com.alibaba.fastjson.JSON;
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
public class CrActualRepayNewHandler extends CrAbstractHandler<CrActualRepayDraft, CrActualRepay> {

    @Resource
    private CollectionRecordInfoMapper collectionRecordInfoMapper;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private CrRepayPlanDraftMapper crRepayPlanDraftMapper;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private ContractBaseInfoLibMapper contractBaseInfoLibMapper;
    @Resource
    private CrActualRepayDraftService crActualRepayDraftService;

    @Override
    public ReportModuleEnum reportModule() {
        return ReportModuleEnum.ACTUAL_REPAY_NEW;
    }


    private void cancelWriteOffProcess(List<CollectionRecordInfo> recordList) {
        //过滤出反核销的记录
        List<CollectionRecordInfo> cancelList =
                recordList.stream().filter(e -> equal(1, e.getCancelWriteOffFlag())).collect(Collectors.toList());
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

        log.info("非直租征信报送-实际还款表，此次处理数量:{}, 处理businessKey列表:{}", operateList.size(), JSON.toJSONString((operateList.stream().map(CollectionRecordInfo::getId).collect(Collectors.toList()))));
        if (CollectionUtils.isNotEmpty(operateList)) {
            List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .in(CollectionBaseInfo::getId, operateList.stream().map(CollectionRecordInfo::getCollectionId).collect(Collectors.toSet()))
                    .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
            );
            if (CollUtil.isEmpty(collectionBaseInfos)) {
                return;
            }
            //过滤掉直租的数据
            List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.listByIds(collectionBaseInfos.stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toSet()));
            Set<Long> contractIds = contractBaseInfos.stream()
                    .filter(a -> !LeaseType.zhi_zu.name().equals(a.getLeaseType()))
                    .map(ContractBaseInfo::getId).collect(Collectors.toSet());

            if (CollUtil.isEmpty(contractIds)) {
                return;
            }

            Set<Long> longSet = contractBaseInfoLibMapper.selectList(Wrappers.<ContractBaseInfoLib>lambdaQuery()
                            .eq(ContractBaseInfoLib::getVersionType, VersionTypeConstants.NORMAL)
                            .in(ContractBaseInfoLib::getOriginId, contractIds)).stream()
                    .filter(a -> Objects.equals(Boolean.TRUE, reportDataRepository.contractReport(a)))
                    .map(ContractBaseInfoLib::getOriginId).collect(Collectors.toSet());

            Map<Long, CollectionBaseInfo> collectionBaseInfoMap = collectionBaseInfos.stream()
                    .filter(a -> longSet.contains(a.getContractId()))
                    .collect(Collectors.toMap(CollectionBaseInfo::getId, Function.identity()));

            // 过滤掉收款明细列表中 收款主表信息为空的（非租金或错误数据）
            operateList = operateList.stream().filter(o -> Objects.nonNull(collectionBaseInfoMap.get(o.getCollectionId()))).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(operateList)) {
                // 找到借据关联的付款
                List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery().in(PaymentBaseInfo::getReceiptIdFinal, collectionBaseInfoMap.values().stream().map(CollectionBaseInfo::getReceiptId).collect(Collectors.toList())));
                paymentBaseInfoList = reportDataRepository.filterNeedReportPaymentListSubTableNew(paymentBaseInfoList);
                Map<Long, PaymentBaseInfo> receiptPaymentMap = paymentBaseInfoList.stream().collect(Collectors.toMap(PaymentBaseInfo::getReceiptIdFinal, p -> p));

                // 合同主承租人map
                Map<Long, Boolean> contractReportMap = reportDataRepository.contractReportMap(collectionBaseInfoMap.values().stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toSet()));
                //同一天的收款记录进合并成一条
                operateList = new ArrayList<>(mergePhaseAmount(operateList));
                for (CollectionRecordInfo operateRecord : operateList) {
                    CollectionBaseInfo baseInfo = collectionBaseInfoMap.get(operateRecord.getCollectionId());
                    if (Objects.isNull(baseInfo)) {
                        continue;
                    }
                    PaymentBaseInfo paymentBaseInfo = receiptPaymentMap.get(baseInfo.getReceiptId());
                    if (Objects.isNull(paymentBaseInfo) || !Boolean.TRUE.equals(contractReportMap.get(baseInfo.getContractId()))) {
                        // 付款核销完毕、合同主承租人上报逻辑过滤、债权转让不报
                        continue;
                    }

                    //查询当前付款申请、当前期项下的所有还款计划
                    List<CrRepayPlanDraft> crRepayPlanDrafts = crRepayPlanDraftMapper.selectList(Wrappers.<CrRepayPlanDraft>lambdaQuery()
                            .eq(CrRepayPlanDraft::getPaymentId, paymentBaseInfo.getId())
                            .eq(CrRepayPlanDraft::getPhase, baseInfo.getPhase()));
                    //排序，还款金额少的需要先还
                    crRepayPlanDrafts.sort(Comparator.comparing(CrRepayPlanDraft::getRent));
                    crActualRepayDraftService.handlerRepay(operateRecord, baseInfo, paymentBaseInfo, crRepayPlanDrafts,dealTime);
                    crActualRepayDraftService.handlerInterestRepay(operateRecord, crRepayPlanDrafts);
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
            log.info("非直租: merged collection record. {}条->{}条", operateList.size(), map.values().size());
        }
        return map.values();
    }

    @Override
    public Integer sort() {
        return 20;
    }
}
