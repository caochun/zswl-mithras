package cn.zswltech.mithras.application.orchestration.monthly;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.dto.interestpay.InterestPayListREQ;
import cn.zswltech.mithras.dto.interestpay.InterestPayRSP;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.fund.enums.OrganizationType;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingTimeLimitTypeEnum;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.fund.directfinancing.model.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.fund.directfinancing.model.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingPledgeInfoService;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.fund.model.FundOrganization;
import cn.zswltech.mithras.fund.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.model.financing.FundFinancingCreditRef;
import cn.zswltech.mithras.fund.model.financing.FundFinancingPlan;
import cn.zswltech.mithras.fund.model.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.monthly.mapper.model.FundsDailyCostMain;
import cn.zswltech.mithras.monthly.mapper.FundsDailyCostMainMapper;
import cn.zswltech.mithras.monthly.mapper.FundsDailyCostMapper;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.fund.application.financing.FundFinancingCreditRefService;
import cn.zswltech.mithras.fund.application.organization.FundOrganizationService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingPlanService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingPledgeInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2025/11/4
 * @description
 */
@Slf4j
@Service
public class FundsDailyCostMainService extends ServiceImpl<FundsDailyCostMainMapper, FundsDailyCostMain> {
    private static final String BIZ_TYPE_MAP_KEY = "bizType";
    private static final String LEASE_TYPE_MAP_KEY = "leaseType";

    public List<InterestPayRSP> listInterestPayRSP(InterestPayListREQ req) {
        // 查询条件
        LambdaQueryWrapper<FundsDailyCostMain> query = Wrappers.lambdaQuery();
        if (StrUtil.isNotBlank(req.getFinancingCode())) {
            query.like(FundsDailyCostMain::getFinancingCode, req.getFinancingCode());
        }
        if (StrUtil.isNotBlank(req.getOrganizationName())) {
            query.like(FundsDailyCostMain::getFinancingChannel, req.getOrganizationName());
        }
        if (StrUtil.isNotBlank(req.getLoanProperty())) {
            query.eq(FundsDailyCostMain::getLoanProperty, req.getLoanProperty());
        }
        query.orderByDesc(FundsDailyCostMain::getCarryInterestDate);
        List<FundsDailyCostMain> dbList = this.list(query);
        return dbList.stream().map(e -> {
            InterestPayRSP rsp = new InterestPayRSP();
            rsp.setId(e.getId());
            rsp.setType(e.getFinancingType());
            rsp.setFinancingCode(e.getFinancingCode());
            rsp.setFinancingId(e.getFinancingId());
            rsp.setOrganizationName(e.getFinancingChannel());
            rsp.setValueDate(e.getCarryInterestDate());
            rsp.setBizType(e.getBizType());
            if (StrUtil.isBlank(e.getLeaseType())) {
                ProjectBizType pbt = ProjectBizType.of(e.getBizType());
                rsp.setBizTypeDisplay(Optional.ofNullable(pbt).map(ProjectBizType::display).orElse(""));
            } else {
                LeaseType lt = LeaseType.of(e.getLeaseType());
                rsp.setBizTypeDisplay(Optional.ofNullable(lt).map(LeaseType::display).orElse(""));
            }
            rsp.setLoanProperty(e.getLoanProperty());
            rsp.setFinancingRate(e.getFinancingRate());
            rsp.setYearCapitalCost(e.getTotalCapitalCostThisYear());
            rsp.setTermCapitalCost(e.getTotalCapitalCostThisMonth());
            rsp.setUpdateTime(e.getLastUpdateDate());
            if (Objects.nonNull(e.getLastUpdateDate())) {
                rsp.setCalculateTime(LocalDateTimeUtil.format(e.getLastUpdateDate(), DatePattern.NORM_MONTH_PATTERN));
            }
            return rsp;
        }).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void createByIndirect(FundFinancingBaseInfo financingBaseInfo) {
        String financingType = "DK";
        // 融资方案
        FundFinancingPlan fundFinancingPlan = SpringUtil.getBean(FundFinancingPlanService.class).getOneByFinancingId(financingBaseInfo.getId());
        // 授信机构
        String orgName = null;
        YesOrNoNumberEnum isSameBiz = YesOrNoNumberEnum.NO;
        List<FundFinancingCreditRef> refList = SpringUtil.getBean(FundFinancingCreditRefService.class).queryByFinancingId(financingBaseInfo.getId());
        if (CollectionUtil.isNotEmpty(refList)) {
            List<FundOrganization> fundOrgList = SpringUtil.getBean(FundOrganizationService.class).listByIds(refList.stream().map(FundFinancingCreditRef::getOrganizationId).collect(Collectors.toSet()));
            if (CollectionUtil.isNotEmpty(fundOrgList)) {
                orgName = fundOrgList.get(0).getOrganizationName();
                for (FundOrganization fundOrganization : fundOrgList) {
                    if (StrUtil.equals(fundOrganization.getOrganizationType(), OrganizationType.ZL.name())) {
                        isSameBiz = YesOrNoNumberEnum.YES;
                        break;
                    }
                }
            }
        }
        // 关联合同
//        ContractBaseInfo relatedContract = null;
        String bizType = null;
        String leaseType = null;
        List<FundFinancingPledgeInfo> pledgeInfoList = SpringUtil.getBean(FundFinancingPledgeInfoService.class).list(financingBaseInfo.getId());
        if (CollectionUtil.isNotEmpty(pledgeInfoList)) {
            List<ContractBaseInfo> contractBaseInfoList = SpringUtil.getBean(ContractBaseInfoService.class).listByIds(pledgeInfoList.stream().map(FundFinancingPledgeInfo::getContractId).collect(Collectors.toList()));
            if (CollectionUtil.isNotEmpty(contractBaseInfoList)) {
//                // 按照合同金额倒排
//                contractBaseInfoList.sort(Comparator.comparing(ContractBaseInfo::getApplyCreditAmount).reversed());
//                relatedContract = contractBaseInfoList.get(0);
                Map<String, String> map = this.ensureBiz(contractBaseInfoList);
                bizType = map.get(BIZ_TYPE_MAP_KEY);
                leaseType = map.get(LEASE_TYPE_MAP_KEY);
            }
        }
        // 应付利息主表
        FundsDailyCostMain fundsDailyCostMain = new FundsDailyCostMain();
        fundsDailyCostMain.setFinancingType(financingType);
        fundsDailyCostMain.setFinancingId(financingBaseInfo.getId());
        fundsDailyCostMain.setFinancingCode(financingBaseInfo.getFinancingCode());
        fundsDailyCostMain.setFinancingChannel(orgName);
        fundsDailyCostMain.setCarryInterestDate(financingBaseInfo.getActualLoanDate());
//        if (Objects.nonNull(relatedContract)) {
//            fundsDailyCostMain.setBizType(relatedContract.getBizType());
//            fundsDailyCostMain.setLeaseType(relatedContract.getLeaseType());
//        }
        fundsDailyCostMain.setBizType(bizType);
        fundsDailyCostMain.setLeaseType(leaseType);
        fundsDailyCostMain.setLoanProperty(financingBaseInfo.getTimeLimitType());
        if (Objects.nonNull(fundFinancingPlan)) {
            int financingRate = Optional.ofNullable(fundFinancingPlan.getLprRatePercent()).orElse(0) + Optional.ofNullable(fundFinancingPlan.getLprAddPercent()).orElse(0);
            fundsDailyCostMain.setFinancingRate(financingRate);
        }
        fundsDailyCostMain.setIsSameBiz(isSameBiz.getCode());
        fundsDailyCostMain.generateQueryKey();
        SpringUtil.getBean(FundsDailyCostMainService.class).save(fundsDailyCostMain);
    }

    public void createByDirect(FundDirectFinancingBaseInfo directFinancingBaseInfo) {
        int count = this.count(Wrappers.<FundsDailyCostMain>lambdaQuery()
                .eq(FundsDailyCostMain::getFinancingId, directFinancingBaseInfo.getId())
                .eq(FundsDailyCostMain::getFinancingType, "ZR")
        );
        if (count > 0) {
            return;
        }
        String financingType = "ZR";
        // 关联合同
//        ContractBaseInfo relatedContract = null;
        String bizType = null;
        String leaseType = null;
        List<FundDirectFinancingPledgeInfo> pledgeInfoList = SpringUtil.getBean(FundDirectFinancingPledgeInfoService.class).listByFinancingId(directFinancingBaseInfo.getId());
        if (CollectionUtil.isNotEmpty(pledgeInfoList)) {
            List<ContractBaseInfo> contractBaseInfoList = SpringUtil.getBean(ContractBaseInfoService.class).listByIds(pledgeInfoList.stream().map(FundDirectFinancingPledgeInfo::getContractId).collect(Collectors.toList()));
            if (CollectionUtil.isNotEmpty(contractBaseInfoList)) {
//                // 按照合同金额倒排
//                contractBaseInfoList.sort(Comparator.comparing(ContractBaseInfo::getApplyCreditAmount).reversed());
//                relatedContract = contractBaseInfoList.get(0);
                Map<String, String> map = this.ensureBiz(contractBaseInfoList);
                bizType = map.get(BIZ_TYPE_MAP_KEY);
                leaseType = map.get(LEASE_TYPE_MAP_KEY);
            }
        }
        // 应付利息主表
        FundsDailyCostMain fundsDailyCostMain = new FundsDailyCostMain();
        fundsDailyCostMain.setFinancingType(financingType);
        fundsDailyCostMain.setFinancingId(directFinancingBaseInfo.getId());
        fundsDailyCostMain.setFinancingCode(directFinancingBaseInfo.getFinancingCode());
        fundsDailyCostMain.setFinancingChannel(directFinancingBaseInfo.getProductName());
        fundsDailyCostMain.setCarryInterestDate(directFinancingBaseInfo.getCarryInterestTime());
//        if (Objects.nonNull(relatedContract)) {
//            fundsDailyCostMain.setBizType(relatedContract.getBizType());
//            fundsDailyCostMain.setLeaseType(relatedContract.getLeaseType());
//        }
        fundsDailyCostMain.setBizType(bizType);
        fundsDailyCostMain.setLeaseType(leaseType);
        fundsDailyCostMain.setLoanProperty(FundFinancingTimeLimitTypeEnum.BOND_PAYABLE.name());
        fundsDailyCostMain.setFinancingRate(Integer.valueOf(directFinancingBaseInfo.getAverageCouponRate().toString()));
        fundsDailyCostMain.setIsSameBiz(YesOrNoNumberEnum.NO.getCode());
        fundsDailyCostMain.generateQueryKey();
        SpringUtil.getBean(FundsDailyCostMainService.class).save(fundsDailyCostMain);
    }

    private Map<String, String> ensureBiz(List<ContractBaseInfo> contractBaseInfoList) {
        // 只过滤租赁类型
        contractBaseInfoList.removeIf(e -> !StrUtil.equals(e.getBizType(), ProjectBizType.ZL.name()));
        if (CollectionUtil.isEmpty(contractBaseInfoList)) {
            return Collections.emptyMap();
        }
        Map<String, String> result = new HashMap<>();
        result.put(BIZ_TYPE_MAP_KEY, ProjectBizType.ZL.name());
        int huiZuCount = 0;
        int zhiZuCount = 0;
        for (ContractBaseInfo contractBaseInfo : contractBaseInfoList) {
            if (StrUtil.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
                zhiZuCount++;
            }
            if (StrUtil.equals(contractBaseInfo.getLeaseType(), LeaseType.hui_zu.name())) {
                huiZuCount++;
            }
        }
        if (huiZuCount > 0) {
            // 只要有回租就用回租
            result.put(LEASE_TYPE_MAP_KEY, LeaseType.hui_zu.name());
        } else if (zhiZuCount > 0) {
            // 没回租但是有直租就用直租
            result.put(LEASE_TYPE_MAP_KEY, LeaseType.zhi_zu.name());
        }
        return result;
    }

    public FundsDailyCostMain getByFinancingIdAndType(Long financingId, String financingType) {
        LambdaQueryWrapper<FundsDailyCostMain> query = Wrappers.lambdaQuery();
        query.eq(FundsDailyCostMain::getFinancingId, financingId);
        query.eq(FundsDailyCostMain::getFinancingType, financingType);
        return this.getOne(query);
    }

    public List<FundsDailyCostMain> listNotFinish() {
        LambdaQueryWrapper<FundsDailyCostMain> query = Wrappers.lambdaQuery();
        query.eq(FundsDailyCostMain::getFinish, YesOrNoNumberEnum.NO.getCode());
        return this.list(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void finish(Long financingId, boolean isDirect) {
        LambdaQueryWrapper<FundsDailyCostMain> query = Wrappers.lambdaQuery();
        query.eq(FundsDailyCostMain::getFinancingId, financingId);
        query.eq(FundsDailyCostMain::getFinish, YesOrNoNumberEnum.NO.getCode());
        if (isDirect) {
            query.eq(FundsDailyCostMain::getFinancingType, "ZR");
        } else {
            query.eq(FundsDailyCostMain::getFinancingType, "DK");
        }
        List<FundsDailyCostMain> todoList = this.list(query);
        if (CollectionUtil.isEmpty(todoList)) {
            return;
        }
        List<FundsDailyCostMain> updateList = todoList.stream().map(e -> {
            FundsDailyCostMain update = new FundsDailyCostMain();
            update.setId(e.getId());
            update.setFinish(YesOrNoNumberEnum.YES.getCode());
            return update;
        }).collect(Collectors.toList());
        this.updateBatchById(updateList);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void refreshTotalCapitalCost(LocalDate targetDate) {
        if (Objects.isNull(targetDate)) {
            return;
        }
        List<FundsDailyCostMain> todoList = this.list();
        if (CollectionUtil.isEmpty(todoList)) {
            return;
        }
        List<FundsDailyCostMain> updateList = new LinkedList<>();
        LocalDate startDateThisYear = LocalDate.of(targetDate.getYear(), 1, 1);
        LocalDate endDateThisYear = LocalDate.of(targetDate.getYear(), 12, 31);
        LocalDate startDateThisMonth = LocalDate.of(targetDate.getYear(), targetDate.getMonthValue(), 1);
        LocalDate endDateThisMonth = LocalDate.of(targetDate.getYear(), targetDate.getMonthValue(), targetDate.lengthOfMonth());
        // 分别统计本年和本月
        List<FundsDailyCostMapper.SumGroupMainBO> monthSumList = SpringUtil.getBean(FundsDailyCostMapper.class).selectSumGroupByMainId(startDateThisMonth, endDateThisMonth);
        Map<Long, Long> monthSumMap = monthSumList.stream().collect(Collectors.toMap(FundsDailyCostMapper.SumGroupMainBO::getMainId, FundsDailyCostMapper.SumGroupMainBO::getSumAmount));
        List<FundsDailyCostMapper.SumGroupMainBO> yearSumList = SpringUtil.getBean(FundsDailyCostMapper.class).selectSumGroupByMainId(startDateThisYear, endDateThisYear);
        Map<Long, Long> yearSumMap = yearSumList.stream().collect(Collectors.toMap(FundsDailyCostMapper.SumGroupMainBO::getMainId, FundsDailyCostMapper.SumGroupMainBO::getSumAmount));
        // 更新数据
        for (FundsDailyCostMain main : todoList) {
            FundsDailyCostMain update = new FundsDailyCostMain();
            update.setId(main.getId());
            update.setLastUpdateDate(endDateThisMonth);
            update.setTotalCapitalCostThisMonth(Optional.ofNullable(monthSumMap.get(main.getId())).orElse(0L));
            update.setTotalCapitalCostThisYear(Optional.ofNullable(yearSumMap.get(main.getId())).orElse(0L));
            updateList.add(update);
        }
        if (CollectionUtil.isNotEmpty(updateList)) {
            this.updateBatchById(updateList);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void refreshFinancingRate(Map<String, Integer> map) {
        if (CollectionUtil.isEmpty(map)) {
            return;
        }
        // 查询数据
        List<FundsDailyCostMain> list = this.list(Wrappers.<FundsDailyCostMain>lambdaQuery().in(FundsDailyCostMain::getFinancingCode, map.keySet()));
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        // 更新
        List<FundsDailyCostMain> updateList = list.stream().map(e -> {
            FundsDailyCostMain update = new FundsDailyCostMain();
            update.setId(e.getId());
            update.setFinancingRate(map.get(e.getFinancingCode()));
            return update;
        }).collect(Collectors.toList());
        this.updateBatchById(updateList);
    }
}
