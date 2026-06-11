package cn.zswltech.mithras.application.orchestration.collection;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.collection.mapper.dto.CollectionContractSettleDTO;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractReceipt;
import cn.zswltech.mithras.payment.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractReceiptService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentActualDetailService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName CollectionService
 * @Description 收付款对外提供api
 * @Author jackerhe
 * @Date 2022/12/20 10:36 上午
 * @Version 1.0
 **/
@Slf4j
@Service
public class CollectionService {

    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private ContractReceiptService contractReceiptService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;

    /**
     * 根据项目查询剩余本金
     *
     * @param projIds 项目评审ID
     *                返回<项目id,剩余本金>
     * @author: jackerhe
     * @date: 2022/12/20 10:40 上午
     **/
    public Map<Long, Long> listProRemainPrinci(List<Long> projIds) {
        return calculation(projIds, Boolean.FALSE);
    }

    /**
     * 计算项目逾期未还金额（本金+利息）
     *
     * @author: jackerhe
     * @date: 2022/12/20 11:56 上午
     **/
    public Map<Long, Long> listProOverdueAmount(List<Long> projReviewIds) {
        return calculation(projReviewIds, Boolean.TRUE);
    }

    private Map<Long, Long> calculation(List<Long> projReviewIds, Boolean isExpired) {
        if (ObjectUtil.isEmpty(projReviewIds)) {
            return MapUtil.empty();
        }
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getProjReviewId, projReviewIds));
        if (ObjectUtil.isEmpty(contractBaseInfos)) {
            return MapUtil.empty();
        }
        List<Long> contractIds = contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .in(CollectionBaseInfo::getContractId, contractIds));
        if (ObjectUtil.isEmpty(collectionBaseInfos)) {
            return MapUtil.empty();
        }
        Map<Long, Long> rsp = new HashMap<>(projReviewIds.size());
        //合同id转项目ID
        Map<Long, Long> contractId2projId = contractBaseInfos.stream().collect(Collectors.toMap(ContractBaseInfo::getId, ContractBaseInfo::getProjReviewId));
        for (CollectionBaseInfo baseInfo : collectionBaseInfos) {
            if (isExpired && LocalDate.now().isBefore(baseInfo.getPlanCollectionDate())) {
                //未到计划收款日期
                continue;
            }
            if (ObjectUtil.isNull(rsp.get(contractId2projId.get(baseInfo.getContractId())))) {
                //新增项目
                rsp.put(contractId2projId.get(baseInfo.getContractId()), LongUtil.null2zero(baseInfo.getPrincipal()) - LongUtil.null2zero(baseInfo.getCollectionPrincipal()));
            } else {
                //更新项目金额
                rsp.put(contractId2projId.get(baseInfo.getContractId()),
                        rsp.get(contractId2projId.get(baseInfo.getContractId())) +
                                LongUtil.null2zero(baseInfo.getPrincipal()) - LongUtil.null2zero(baseInfo.getCollectionPrincipal()));
            }
            if (isExpired) {
                //逾期的加上逾期利息
                rsp.put(contractId2projId.get(baseInfo.getContractId()),
                        rsp.get(contractId2projId.get(baseInfo.getContractId())) +
                                LongUtil.null2zero(baseInfo.getInterest()) - LongUtil.null2zero(baseInfo.getCollectionInterest()));
            }
        }
        return rsp;
    }

    /**
     * 根据合同查询剩余本金，逾期租金(租金表维度)
     *
     * @param contractIds  合同ID
     * @param isExpired    是否逾期
     *                     false  返回<合同id,剩余金额>
     *                     true   返回<合同id,逾期金额>
     * @param needInterest 是否需要加上利息
     *                     false 返回值 金额 =剩余本金/逾期本金
     *                     true 返回值 金额 =（剩余本金+利息）/（逾期本金+逾期利息）
     * @author: jackerhe
     * @date: 2022/1/05 14:40 下午
     **/
    public Map<Long, Long> listContractAmountByContractIds(List<Long> contractIds, Boolean isExpired, Boolean needInterest) {
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .in(CollectionBaseInfo::getContractId, contractIds));
        if (ObjectUtil.isEmpty(collectionBaseInfos)) {
            return MapUtil.empty();
        }
        Map<Long, Long> rsp = new HashMap<>(contractIds.size());
        for (CollectionBaseInfo baseInfo : collectionBaseInfos) {
            if (isExpired && LocalDate.now().isBefore(baseInfo.getPlanCollectionDate())) {
                //未到计划收款日期
                continue;
            }
            if (ObjectUtil.isNull(rsp.get(baseInfo.getContractId()))) {
                //新增项目
                rsp.put(baseInfo.getContractId(), LongUtil.null2zero(baseInfo.getPrincipal()) - LongUtil.null2zero(baseInfo.getCollectionPrincipal()));
            } else {
                //更新项目金额
                rsp.put(baseInfo.getContractId(),
                        rsp.get(baseInfo.getContractId())+
                                LongUtil.null2zero(baseInfo.getPrincipal()) - LongUtil.null2zero(baseInfo.getCollectionPrincipal()));
            }
            if (needInterest) {
                //逾期的加上逾期利息
                rsp.put(baseInfo.getContractId(),
                        rsp.get(baseInfo.getContractId()) +
                                LongUtil.null2zero(baseInfo.getInterest()) - LongUtil.null2zero(baseInfo.getCollectionInterest()));
            }
        }
        return rsp;
    }
    /**
     * 计算实际剩余本金 = 付款金额 - 已经核销本金
     * @return Map<Long, Long> map<contractId, 剩余本金>
     **/
    public Map<Long, Long> listContractAmountByContractIds(List<Long> contractIds){
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .in(CollectionUtil.isNotEmpty(contractIds), CollectionBaseInfo::getContractId, contractIds));
        List<PaymentActualDetail> paymentBaseInfos = paymentActualDetailService.list(Wrappers.<PaymentActualDetail>lambdaQuery()
                .in(CollectionUtil.isNotEmpty(contractIds), PaymentActualDetail::getContractId, contractIds));
        List<CollectionBaseInfo> fistRentBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.FIRST_RENT.name())
                .in(CollectionUtil.isNotEmpty(contractIds), CollectionBaseInfo::getContractId, contractIds));
        Map<Long, Long> contractFistRentMap = new HashMap<>();
        if(CollectionUtil.isNotEmpty(fistRentBaseInfos)){
            contractFistRentMap = fistRentBaseInfos.stream().filter(base -> LongUtil.null2zero(base.getCollectionAmount()) > 0).collect(Collectors.toMap(CollectionBaseInfo::getContractId, CollectionBaseInfo::getCollectionAmount, Long::sum));
        }
        Map<Long, Long> payMap = new HashMap<>();
        for(PaymentActualDetail detail : paymentBaseInfos){
            payMap.put(detail.getContractId(), LongUtil.null2zero(payMap.get(detail.getContractId())) + LongUtil.null2zero(detail.getPaidInAmount()));
        }
        for(CollectionBaseInfo baseInfo : collectionBaseInfos){
            payMap.put(baseInfo.getContractId(), LongUtil.null2zero(payMap.get(baseInfo.getContractId())) - LongUtil.null2zero(baseInfo.getCollectionPrincipal()));
        }
        //减去首期租金
        for (Map.Entry<Long, Long> entry : contractFistRentMap.entrySet()) {
            Long key = entry.getKey();
            Long value = entry.getValue();
            payMap.put(key, LongUtil.null2zero(payMap.get(key)) - LongUtil.null2zero(value));
        }
        return payMap;
    }

    /**
     * 返回每个合同下最后一期租金计划收取时间
     * @author: jackerhe
     * @date: 2023/2/25 11:21 上午
     **/
    public Map<Long, LocalDateTime> getLastRentDateByContractIds(List<Long> contractIds){
        if(ObjectUtil.isEmpty(contractIds)){
            return new HashMap<>();
        }
        List<CollectionContractSettleDTO> lastRentDateByContractIds = collectionBaseInfoMapper.getLastRentDateByContractIds(contractIds);
        if(ObjectUtil.isEmpty(lastRentDateByContractIds)){
            return new HashMap<>();
        }
        return lastRentDateByContractIds.stream().collect(Collectors.toMap(CollectionContractSettleDTO::getContractId, CollectionContractSettleDTO::getSettleDate));
    }

    /**
     * 根据付款（后续需修改为借据）查询剩余本金
     *
     * @param rentActualIds 实际租金表IDID
     * @param isExpired    是否逾期
     *                     false  返回<借据code,剩余金额>
     *                     true   返回<借据code,逾期金额>
     * @param needInterest 是否需要加上利息
     *                     false 返回值 金额 =剩余本金/逾期本金
     *                     true 返回值 金额 =（剩余本金+利息）/（逾期本金+逾期利息）
     * @author: jackerhe
     * @date: 2022/1/05 14:40 下午
     **/
    public Map<String, Long> listContractAmountByRentActualIds(List<Long> rentActualIds, Boolean isExpired, Boolean needInterest) {
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .in(CollectionBaseInfo::getRentActualId, rentActualIds));
        if (ObjectUtil.isEmpty(collectionBaseInfos)) {
            return MapUtil.empty();
        }
        Map<String, Long> rsp = new HashMap<>();
        for (CollectionBaseInfo baseInfo : collectionBaseInfos) {
            if (isExpired && LocalDate.now().isBefore(baseInfo.getPlanCollectionDate())) {
                //未到计划收款日期
                continue;
            }
            if (ObjectUtil.isNull(rsp.get(baseInfo.getReceiptCode()))) {
                //新增项目
                rsp.put(baseInfo.getReceiptCode(), LongUtil.null2zero(baseInfo.getPrincipal()) - LongUtil.null2zero(baseInfo.getCollectionPrincipal()));
            } else {
                //更新项目金额
                rsp.put(baseInfo.getReceiptCode(),
                        rsp.get(baseInfo.getReceiptCode())+
                                LongUtil.null2zero(baseInfo.getPrincipal()) - LongUtil.null2zero(baseInfo.getCollectionPrincipal()));
            }
            if (needInterest) {
                //逾期的加上逾期利息
                rsp.put(baseInfo.getReceiptCode(),
                        rsp.get(baseInfo.getReceiptCode()) +
                                LongUtil.null2zero(baseInfo.getInterest()) - LongUtil.null2zero(baseInfo.getCollectionInterest()));
            }
        }
        return rsp;
    }


    /**
     * 获取最近一期的待收款
     *
     * @param contractId
     * @return
     */
    public CollectionBaseInfo getCurrentIssue(Long contractId) {
        return collectionBaseInfoMapper.selectOne(
                Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                        .eq(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.UNCOLLECTION.name())
                        .eq(CollectionBaseInfo::getContractId, contractId)
                        .orderByAsc(CollectionBaseInfo::getPlanCollectionDate)
                        .last("limit 1"));
    }

    /**
     * 计算借据维度的剩余本金
     * @param receiptId 借据维度
     * @return 剩余本金
     */
    public long calcRemainingPrincipalByReceiptId(Long receiptId, LocalDate targetDate) {
        // 找借据
        ContractReceipt contractReceipt = contractReceiptService.getById(receiptId);
        if (Objects.isNull(contractReceipt)) {
            throw new MithrasException("借据不存在[" + receiptId + "]");
        }
        // 找付款
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoService.listByReceiptId(receiptId);
        // 找到对应应收记录
        LambdaQueryWrapper<CollectionBaseInfo> collectionBaseInfoQuery = Wrappers.lambdaQuery();
        collectionBaseInfoQuery.and(innerQuery -> {
            innerQuery.eq(CollectionBaseInfo::getReceiptId, receiptId);
            if (CollectionUtil.isNotEmpty(paymentBaseInfoList)) {
                innerQuery.or();
                innerQuery.in(CollectionBaseInfo::getPaymentId, paymentBaseInfoList.stream().map(PaymentBaseInfo::getId).collect(Collectors.toList()));
            }
        });
        collectionBaseInfoQuery.in(CollectionBaseInfo::getCashFlowItem, Arrays.asList(CashFlowItemEnum.FIRST_RENT.name(), CashFlowItemEnum.RENT.name()));
        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoMapper.selectList(collectionBaseInfoQuery);
        if (CollectionUtil.isEmpty(collectionBaseInfoList)) {
            return 0L;
        }
        // 过滤出首期租金和租金
        List<CollectionBaseInfo> firstRentCollectionList = collectionBaseInfoList.stream().filter(e -> Objects.equals(e.getCashFlowItem(), CashFlowItemEnum.FIRST_RENT.name())).collect(Collectors.toList());
        List<CollectionBaseInfo> rentCollectionList = collectionBaseInfoList.stream().filter(e -> Objects.equals(e.getCashFlowItem(), CashFlowItemEnum.RENT.name())).collect(Collectors.toList());
        // 应收金额
        long principalPlanAmount = 0L;
        // 实收金额
        long principalActualAmount = 0L;
        if (CollectionUtil.isNotEmpty(firstRentCollectionList)) {
            principalPlanAmount = principalPlanAmount + firstRentCollectionList.stream().filter(e -> Objects.nonNull(e.getPlanCollectionAmount())).mapToLong(CollectionBaseInfo::getPlanCollectionAmount).sum();
            principalActualAmount = principalActualAmount + firstRentCollectionList.stream().filter(e -> {
                if (Objects.isNull(targetDate) || Objects.isNull(e.getCollectionDate())) {
                    return true;
                }
                return targetDate.isAfter(e.getCollectionDate());
            }).filter(e -> Objects.nonNull(e.getCollectionAmount())).mapToLong(CollectionBaseInfo::getCollectionAmount).sum();
        }
        if (CollectionUtil.isNotEmpty(rentCollectionList)) {
            principalPlanAmount = principalPlanAmount + rentCollectionList.stream().filter(e -> Objects.nonNull(e.getPrincipal())).mapToLong(CollectionBaseInfo::getPrincipal).sum();
            principalActualAmount = principalActualAmount + rentCollectionList.stream().filter(e -> {
                if (Objects.isNull(targetDate) || Objects.isNull(e.getCollectionDate())) {
                    return true;
                }
                return targetDate.isAfter(e.getCollectionDate());
            }).filter(e -> Objects.nonNull(e.getCollectionPrincipal())).mapToLong(CollectionBaseInfo::getCollectionPrincipal).sum();
        }
        long remainingPrincipal =  principalPlanAmount - principalActualAmount;
        log.info("借据{}的剩余本金为{}", contractReceipt.getReceiptCode(), remainingPrincipal);
        return remainingPrincipal;
    }

    /**
     * 获取合同下某种收款的剩余未还金额
     * @param contractId 合同id
     * @param cashFlowItemEnum 收款类型
     **/
    public Long getRemainingAmountByContract(Long contractId, String cashFlowItemEnum){
        long sum = 0L;
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCashFlowItem, cashFlowItemEnum)
                .eq(CollectionBaseInfo::getContractId, contractId));
        if(CollectionUtil.isNotEmpty(collectionBaseInfos)){
            sum = collectionBaseInfos.stream().mapToLong(base -> Math.max(0L, LongUtil.null2zero(base.getPlanCollectionAmount()) - LongUtil.null2zero(base.getCollectionAmount()))).sum();
        }
        return sum;
    }
}

