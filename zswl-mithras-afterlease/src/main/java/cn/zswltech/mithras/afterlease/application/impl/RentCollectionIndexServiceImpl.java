package cn.zswltech.mithras.afterlease.application.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.afterlease.RentCollectionListREQ;
import cn.zswltech.mithras.dto.afterlease.RentCollectionListRSP;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.afterlease.enums.*;
import cn.zswltech.mithras.customer.mobile.enums.AppCalendarEnum;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.afterlease.mapper.RentCollectionIndexMapper;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractLeasePriceMapper;
import cn.zswltech.mithras.afterlease.dto.RentCollectionIndexListDTO;
import cn.zswltech.mithras.afterlease.dto.RentCollectionIndexListParam;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.foundation.port.ClientNameResolver;
import cn.zswltech.mithras.foundation.port.CurrentUserDataScopeResolver;
import cn.zswltech.mithras.foundation.port.DeptNameResolver;
import cn.zswltech.mithras.foundation.port.UserNameResolver;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 租金催收首页
 *
 * @author wangchuanhao
 * @date 2022/11/17 3:35 PM
 */
@Service
public class RentCollectionIndexServiceImpl {

    @Resource
    private RentCollectionIndexMapper rentCollectionIndexMapper;
    @Resource
    private CurrentUserDataScopeResolver currentUserDataScopeResolver;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private ClientNameResolver clientNameResolver;
    @Resource
    private UserNameResolver userNameResolver;
    @Resource
    private DeptNameResolver deptNameResolver;
    @Resource
    private ContractLeasePriceMapper leasePriceMapper;

    public PageR<RentCollectionListRSP> indexList(RentCollectionListREQ req) {
        RentCollectionIndexListParam query = new RentCollectionIndexListParam();
        BeanUtil.copyProperties(req, query, new CopyOptions().ignoreError());
        // 合同状态转化
        query.setContractStatus(RentCollectionIndexContractStatus.convert(query.getContractStatus()));
        // 拼装过滤参数
        RentCollectionIndexFilterConditionType filterConditionType = RentCollectionIndexFilterConditionType.of(req.getFilterConditionType());
        switch (filterConditionType) {
            case HIDE_FINISH:
                query.setFilterFinishFlag(true);
                break;
            case NOT_NOTICE_YET:
                query.setNotNoticeYetFlag(true);
                break;
            case OVERDUE:
                query.setOverdueFlag(true);
                break;
            case ALL:
            default:
                break;
        }

        // 可看数据权限
        List<Long> canViewDeptIds = currentUserDataScopeResolver.canViewDeptIds();
        boolean isBizUser = null != canViewDeptIds;
        if (isBizUser && canViewDeptIds.isEmpty()) {
            //防止sql in报错
            canViewDeptIds.add(Long.MIN_VALUE);
        }
        query.setIsBizUser(isBizUser);
        query.setDeptIdList(canViewDeptIds);
        query.setCurrentUserId(AccountUtil.getLoginInfo().getId());
        Page<RentCollectionIndexListDTO> dtoPage = rentCollectionIndexMapper.indexList(new Page<>(req.getPage(), req.getPageSize()), query);
        if (dtoPage.getRecords().isEmpty()) {
            return PageR.of(new ArrayList<>(), dtoPage.getTotal(),
                    dtoPage.getPages(),
                    dtoPage.getCurrent(),
                    dtoPage.getSize());
        }
        List<RentCollectionListRSP> rspList = getCommonList(dtoPage);
        return PageR.of(rspList, dtoPage.getTotal(),
                dtoPage.getPages(),
                dtoPage.getCurrent(),
                dtoPage.getSize());
    }


    public List<RentCollectionListRSP> getRentCollectionList(RentCollectionListREQ req, String type) {
        RentCollectionIndexListParam query = new RentCollectionIndexListParam();
        BeanUtil.copyProperties(req, query, new CopyOptions().ignoreError());
        // 合同状态转化
        query.setContractStatus(RentCollectionIndexContractStatus.convert(query.getContractStatus()));
        // 拼装过滤参数
        RentCollectionIndexFilterConditionType filterConditionType = RentCollectionIndexFilterConditionType.of(req.getFilterConditionType());
        switch (filterConditionType) {
            case HIDE_FINISH:
                query.setFilterFinishFlag(true);
                break;
            case NOT_NOTICE_YET:
                query.setNotNoticeYetFlag(true);
                break;
            case OVERDUE:
                query.setOverdueFlag(true);
                break;
            case ALL:
            default:
                break;
        }
        // 可看数据权限
        List<Long> canViewDeptIds = currentUserDataScopeResolver.canViewDeptIds();
        boolean isBizUser = null != canViewDeptIds;
        if (isBizUser && canViewDeptIds.isEmpty()) {
            //防止sql in报错
            canViewDeptIds.add(Long.MIN_VALUE);
        } else if (isBizUser && !canViewDeptIds.isEmpty() && AppCalendarEnum.MYSELF.name().equalsIgnoreCase(type)) {
            //部门领导-个人
            canViewDeptIds = new ArrayList<>();
            //防止sql in报错
            canViewDeptIds.add(Long.MIN_VALUE);
        }
        query.setIsBizUser(isBizUser);
        query.setDeptIdList(canViewDeptIds);
        query.setCurrentUserId(AccountUtil.getLoginInfo().getId());
        Page<RentCollectionIndexListDTO> dtoPage = rentCollectionIndexMapper.indexList(new Page<>(req.getPage(), req.getPageSize()), query);
        if (dtoPage.getRecords().isEmpty()) {
            return new ArrayList<>();
        }
        List<RentCollectionListRSP> rspList = getCommonList(dtoPage);
        return rspList;
    }

    private List<RentCollectionListRSP> getCommonList(Page<RentCollectionIndexListDTO> dtoPage) {
        // 填充名称
        Set<Long> clientIdSet = dtoPage.getRecords().stream().map(RentCollectionIndexListDTO::getClientId).collect(Collectors.toSet());
        Map<Long, String> clientNameMap = clientNameResolver.clientId2Name(clientIdSet);
        Set<Long> userIdSet = dtoPage.getRecords().stream().map(RentCollectionIndexListDTO::getProjSponsorUserId).collect(Collectors.toSet());
        Map<Long, String> userNameMap = userNameResolver.sysUserId2Name(userIdSet);
        Set<Long> deptIdSet = dtoPage.getRecords().stream().map(RentCollectionIndexListDTO::getBizDeptId).collect(Collectors.toSet());
        Map<Long, String> deptNameMap = deptNameResolver.deptId2Name(deptIdSet);

        // 填充借据状态 起租的生效的 如果有收款主表逾期 就 判作逾期标签
        Map<Long, List<CollectionBaseInfo>> overdueCollectionMap = new HashMap<>();
        Set<Long> startRentReceiptIdSet = dtoPage.getRecords().stream().filter(p -> CharSequenceUtil.equalsAny(p.getContractStatus(),
                ContractStatus.TAKE_EFFECT.name(), ContractStatus.START_RENT.name())).map(RentCollectionIndexListDTO::getReceiptId).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(startRentReceiptIdSet)) {
            overdueCollectionMap = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .in(CollectionBaseInfo::getReceiptId, startRentReceiptIdSet)
                    .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                    .ne(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name())
                    .lt(CollectionBaseInfo::getPlanCollectionDate, LocalDate.now())
            ).stream().collect(Collectors.groupingBy(CollectionBaseInfo::getReceiptId));
        }

        // 填充收款卡片数据
        Set<Long> collectionIdSet = dtoPage.getRecords().stream().map(RentCollectionIndexListDTO::getCollectionIds)
                .filter(StringUtils::isNotBlank)
                .map(s -> s.split(","))
                .flatMap(Arrays::stream)
                .map(Long::valueOf)
                .collect(Collectors.toSet());
        Map<Long, CollectionBaseInfo> collectionBaseInfoMap = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery().in(CollectionBaseInfo::getId, collectionIdSet)).stream().collect(Collectors.toMap(CollectionBaseInfo::getId, c -> c));
        List<RentCollectionListRSP> rspList = new ArrayList<>(dtoPage.getRecords().size());
        Set<Long> contractIdset = dtoPage.getRecords().stream().map(RentCollectionIndexListDTO::getContractId).collect(Collectors.toSet());
        List<ContractLeasePrice> contractLeasePriceList = leasePriceMapper.selectList(
                Wrappers.<ContractLeasePrice>lambdaQuery().in(ContractLeasePrice::getContractId, contractIdset));
        Map<Long, List<ContractLeasePrice>> contractLeasePriceMap = contractLeasePriceList.stream().collect(Collectors.groupingBy(ContractLeasePrice::getContractId));
        for (RentCollectionIndexListDTO dto : dtoPage.getRecords()) {
            RentCollectionListRSP rsp = new RentCollectionListRSP();
            BeanUtil.copyProperties(dto, rsp, new CopyOptions().ignoreError());
            List<Long> paymentCollectionIdList = Stream.of(dto.getCollectionIds().split(",")).map(Long::valueOf).collect(Collectors.toList());
            List<RentCollectionListRSP.CollectionCardData> cardDataList = paymentCollectionIdList.stream().map(colletionId -> {
                return buildCollectionCardData(collectionBaseInfoMap.get(colletionId));
            }).filter(Objects::nonNull).sorted(Comparator.comparing(RentCollectionListRSP.CollectionCardData::getPhase)).collect(Collectors.toList());
            int repayTimesTotal = 0;
            if (contractLeasePriceMap.containsKey(rsp.getContractId())
                && contractLeasePriceMap.get(rsp.getContractId()) != null
                && !contractLeasePriceMap.get(rsp.getContractId()).isEmpty()) {
                if (contractLeasePriceMap.get(rsp.getContractId()).get(0).getRepayTimesTotal() == null) {
                    continue;
                }
                repayTimesTotal = contractLeasePriceMap.get(rsp.getContractId()).get(0).getRepayTimesTotal();
            }
            for (RentCollectionListRSP.CollectionCardData collectionCardData : cardDataList) {
                collectionCardData.setContractId(rsp.getContractId());
                collectionCardData.setRepayTimesRate(new StringBuilder().append(collectionCardData.getPhase()).append("/").append(repayTimesTotal).toString());
            }
            rsp.setCollectionCardList(cardDataList);
            rsp.setClientName(clientNameMap.get(rsp.getClientId()));
            rsp.setProjSponsorUserName(userNameMap.get(rsp.getProjSponsorUserId()));
            rsp.setBizDeptName(deptNameMap.get(rsp.getBizDeptId()));
            // paymentState
            if (ContractStatus.TAKE_EFFECT.name().equals(rsp.getContractStatus())) {
                rsp.setPaymentState(CollectionUtils.isEmpty(overdueCollectionMap.get(rsp.getPaymentId())) ? RentCollectionIndexPaymentState.TAKE_EFFECT.name() : RentCollectionIndexPaymentState.OVERDUE.name());
            } else if (ContractStatus.SETTLE.name().equals(rsp.getContractStatus())) {
                rsp.setPaymentState(RentCollectionIndexPaymentState.SETTLE.name());
            } else if (ContractStatus.START_RENT.name().equals(rsp.getContractStatus())) {
                rsp.setPaymentState(CollectionUtils.isEmpty(overdueCollectionMap.get(rsp.getPaymentId())) ? RentCollectionIndexPaymentState.START_RENT.name() : RentCollectionIndexPaymentState.OVERDUE.name());
            }
            rspList.add(rsp);
        }
        return rspList;
    }

    private RentCollectionListRSP.CollectionCardData buildCollectionCardData(CollectionBaseInfo collectionBaseInfo) {
        if (Objects.isNull(collectionBaseInfo)) {
            return null;
        }
        RentCollectionListRSP.CollectionCardData cardData = RentCollectionListRSP.CollectionCardData.builder()
                .collectionId(collectionBaseInfo.getId())
                .planCollectionAmount(collectionBaseInfo.getPlanCollectionAmount())
                .collectionAmount(collectionBaseInfo.getCollectionAmount())
                .unCollectionAmount(LongUtil.null2zero(collectionBaseInfo.getPlanCollectionAmount()) - LongUtil.null2zero(collectionBaseInfo.getCollectionAmount()))
                .phase(collectionBaseInfo.getPhase())
                .planCollectionDate(collectionBaseInfo.getPlanCollectionDate())
                .collectionDate(collectionBaseInfo.getCollectionDate())
                .penaltyInterest(LongUtil.null2zero(collectionBaseInfo.getPenaltyInterest()))
                .penaltyInterestDeductionAmount(LongUtil.null2zero(collectionBaseInfo.getPenaltyInterestDeductionAmount()))
                .collectionPenaltyInterest(LongUtil.null2zero(collectionBaseInfo.getCollectionPenaltyInterest()))
                .unCollectionPenaltyInterest(LongUtil.null2zero(collectionBaseInfo.getPenaltyInterest()) - LongUtil.null2zero(collectionBaseInfo.getCollectionPenaltyInterest()))
                .noticeFinancialFlag(false)
                .build();
        // 计算卡片状态
        long planCollectionDiffDayCount = collectionBaseInfo.getPlanCollectionDate().toEpochDay() - LocalDate.now().toEpochDay();
        if (CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name().equals(collectionBaseInfo.getWriteOffStatus())) {
            // 已收款
            cardData.setState(RentCollectionIndexCardState.PAID.name());
        } else if (planCollectionDiffDayCount >= 0 && planCollectionDiffDayCount <= 7) {
            // 待收款 计划收款日期0-7天内
            cardData.setState(RentCollectionIndexCardState.PENDING.name());
        } else if (LocalDate.now().plusDays(-3).isAfter(collectionBaseInfo.getPlanCollectionDate()) && !CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name().equals(collectionBaseInfo.getWriteOffStatus())) {
            // 已逾期
            cardData.setState(RentCollectionIndexCardState.OVERDUE.name());
        } else {
            // 尚未到期
            cardData.setState(RentCollectionIndexCardState.NOT_YET_EXPIRED.name());
        }
        //是否逾期
        if (!CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name().equals(collectionBaseInfo.getWriteOffStatus())
                && Optional.ofNullable(collectionBaseInfo.getPlanCollectionDate()).orElse(LocalDate.MAX).isBefore(LocalDate.now())) {
            cardData.setIsOverDue(true);
        } else {
            cardData.setIsOverDue(false);
        }
        // 计算卡片标签
        List<String> tagList = new ArrayList<>();
        if (Objects.nonNull(collectionBaseInfo.getPenaltyInterestDeductionAmount())
                && collectionBaseInfo.getPenaltyInterestDeductionAmount() > 0) {
            // 罚息减免金额 > 0
            tagList.add(RentCollectionIndexCardTag.DEDUCTION.name());
        }
        if (CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name().equals(collectionBaseInfo.getWriteOffStatus())
            && Optional.ofNullable(collectionBaseInfo.getCollectionDate()).orElse(LocalDate.MAX).isAfter(collectionBaseInfo.getPlanCollectionDate())) {
            // 已收款 且 实收日期 > 计划收款日期
            tagList.add(RentCollectionIndexCardTag.OVERDUE.name());
        }
        if (RentCollectionIndexCardState.PENDING.name().equals(cardData.getState())
                && Objects.nonNull(collectionBaseInfo.getEmailNoticeCount())
                && collectionBaseInfo.getEmailNoticeCount() > 0) {
            // 待收款 且 通知过
            tagList.add(RentCollectionIndexCardTag.NOTIFIED.name());
        }
        if (RentCollectionIndexCardState.OVERDUE.name().equals(cardData.getState())
                && LocalDate.now().equals(collectionBaseInfo.getPlanPenaltyInterestDate())) {
            // 逾期 且 通知过苍穹
            cardData.setNoticeFinancialFlag(true);
        }
        cardData.setTagList(tagList);
        return cardData;
    }

}
