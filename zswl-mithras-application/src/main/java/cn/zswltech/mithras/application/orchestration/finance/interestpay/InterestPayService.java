package cn.zswltech.mithras.application.orchestration.finance.interestpay;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.dto.interestpay.*;
import cn.zswltech.mithras.dto.monthly.MonthlyCostInfo;
import cn.zswltech.mithras.dto.monthly.MonthlyCostREQ;
import cn.zswltech.mithras.dto.monthly.MonthlyCostRSP;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.fund.enums.DirectFinancingType;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingTimeLimitTypeEnum;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.fund.directfinancing.model.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.fund.directfinancing.model.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingPledgeInfoService;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.fund.model.FundOrganization;
import cn.zswltech.mithras.fund.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.model.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.monthly.mapper.model.FundsDailyCost;
import cn.zswltech.mithras.monthly.mapper.model.FundsDailyCostMain;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.fund.application.organization.FundOrganizationService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingPledgeInfoService;
import cn.zswltech.mithras.application.orchestration.monthly.FundsDailyCostMainService;
import cn.zswltech.mithras.application.orchestration.monthly.FundsDailyCostService;
import cn.zswltech.mithras.application.orchestration.monthly.MonthlyManageService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.hutool.core.util.ObjectUtil.isNotNull;

@Service
@Slf4j
public class InterestPayService {

    @Resource
    private FundFinancingBaseInfoService financingBaseInfoService;
    @Resource
    private FundDirectFinancingBaseInfoService fundDirectFinancingBaseInfoService;
    @Resource
    private FundFinancingPledgeInfoService fundFinancingPledgeInfoService;
    @Resource
    private FundDirectFinancingPledgeInfoService fundDirectFinancingPledgeInfoService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private FundsDailyCostService fundsDailyCostService;
    @Resource
    private FundOrganizationService organizationService;

    @Resource
    private MonthlyManageService monthlyManageService;

    private static final String BEGINNING_ITEM_TEXT = "期初余额";

    private static final String DIFF_ITEM_TEXT = "差额";

    private final static String HZ = "汇总";


    // 废弃，使用cn.zswltech.mithras.application.orchestration.monthly.FundsDailyCostMainService.listInterestPayRSP替代
    @Deprecated
    public List<InterestPayRSP> listPage(InterestPayListREQ req) {
        StopWatch st = new StopWatch("应付利息列表");
        st.start("查询数据");
        List<InterestPayRSP> interestPayRSPS = getAll();
        st.stop();
        st.start("封装数据");
        if (interestPayRSPS.isEmpty()) {
            return new ArrayList<InterestPayRSP>();
        }
        List<InterestPayRSP> res = new ArrayList<>();
        for (InterestPayRSP interestPayRSP : interestPayRSPS) {
            if (StringUtils.isNotBlank(req.getFinancingCode())
                    && !interestPayRSP.getFinancingCode().equalsIgnoreCase(req.getFinancingCode())) {
                continue;
            }
            if (StringUtils.isNotBlank(req.getOrganizationName())
                    && !interestPayRSP.getOrganizationName().equalsIgnoreCase(req.getOrganizationName())) {
                continue;
            }
            if (StringUtils.isNotBlank(req.getLoanProperty())
                    && !interestPayRSP.getLoanProperty().equalsIgnoreCase(req.getLoanProperty())) {
                continue;
            }
            res.add(interestPayRSP);
        }
        st.stop();
        log.info(st.prettyPrint(TimeUnit.SECONDS));
        return res;
    }

    public List<InterestPayRSP> calculate(InterestPayREQ req) {
        MonthlyCostREQ monthlyCostREQ = new MonthlyCostREQ();
        monthlyCostREQ.setInterestPay(true);
        monthlyCostREQ.setPage(1);
        monthlyCostREQ.setPageSize(Integer.MAX_VALUE);
        monthlyCostREQ.setYearAndMonth(req.getStartYearAndMonth());
        // 计算应付利息
        List<MonthlyCostRSP> calculatedRSPS = monthlyManageService.calculateDailyInterest2(monthlyCostREQ);
        if (calculatedRSPS.isEmpty()) {
            return new ArrayList<InterestPayRSP>();
        }
//        List<InterestPayRSP> interestPayRSPS = getAll();
        List<InterestPayRSP> interestPayRSPS = SpringUtil.getBean(FundsDailyCostMainService.class).listInterestPayRSP(new InterestPayListREQ());
        if (interestPayRSPS.isEmpty()) {
            return new ArrayList<InterestPayRSP>();
        }
        return interestPayRSPS;
    }

    public List<MonthlyCostRSP> getMonthlyCostRSP(LocalDate interestDate) {
        List<MonthlyCostRSP> rspList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<Long, List<MonthlyCostInfo>> financingIdNameMap = new HashMap<>();
//        LambdaQueryWrapper<FundsDailyCost> query = Wrappers.lambdaQuery();
//        query.eq(FundsDailyCost::getItemText, BEGINNING_ITEM_TEXT);
//        List<FundsDailyCost> todoList = fundsDailyCostService.list(query);
//        if (CollectionUtil.isEmpty(todoList)) {
//            log.error("执行每日成本计息为空");
//            return Collections.emptyList();
//        }
//        //返回增量期初+存量期初
//        List<FundsDailyCost> beginRes = monthlyManageService.getIncrementFunds(todoList);
        List<FundsDailyCostMain> todoList = SpringUtil.getBean(FundsDailyCostMainService.class).listNotFinish();
        //获取间融的融资信息
//        List<Long> financingIds = beginRes.stream().filter(f -> "DK".equals(f.getType())).map(FundsDailyCost::getFinancingId).collect(Collectors.toList());
        List<Long> financingIds = todoList.stream().filter(f -> "DK".equals(f.getFinancingType())).map(FundsDailyCostMain::getFinancingId).collect(Collectors.toList());
        final List<FundFinancingBaseInfo> fundFinancingBaseInfos = financingBaseInfoService.listByIds(financingIds);
        Map<Long, Integer> map = fundFinancingBaseInfos.stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, FundFinancingBaseInfo::getInitialInterestReceivedOnce, (a, b) -> a));
//        Map<Long, FundFinancingBaseInfo> fundFinancingId2Bean = financingBaseInfoService.listByIds(beginRes.stream().filter(e -> "DK".equalsIgnoreCase(e.getType())).map(FundsDailyCost::getFinancingId).collect(Collectors.toList())).stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, e -> e, (a, b) -> a));
        Map<Long, FundFinancingBaseInfo> fundFinancingId2Bean = financingBaseInfoService.listByIds(todoList.stream().filter(e -> "DK".equalsIgnoreCase(e.getFinancingType())).map(FundsDailyCostMain::getFinancingId).collect(Collectors.toList())).stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, e -> e, (a, b) -> a));
//        Map<Long, FundDirectFinancingBaseInfo> FundDirectFinancingId2Bean = fundDirectFinancingBaseInfoService.listByIds(beginRes.stream().filter(e -> "ZR".equalsIgnoreCase(e.getType())).map(FundsDailyCost::getFinancingId).collect(Collectors.toList())).stream().collect(Collectors.toMap(FundDirectFinancingBaseInfo::getId, e -> e, (a, b) -> a));
        Map<Long, FundDirectFinancingBaseInfo> FundDirectFinancingId2Bean = fundDirectFinancingBaseInfoService.listByIds(todoList.stream().filter(e -> "ZR".equalsIgnoreCase(e.getFinancingType())).map(FundsDailyCostMain::getFinancingId).collect(Collectors.toList())).stream().collect(Collectors.toMap(FundDirectFinancingBaseInfo::getId, e -> e, (a, b) -> a));
        Set<Long> financingIdList = new HashSet<>();
        Map<Long, List<FundOrganization>> orgMap = organizationService.getBatchByFinancingId(financingIds);
//        for (FundsDailyCost base : beginRes) {
        for (FundsDailyCostMain base : todoList) {
//            //空的起息日判断
//            if (base.getInterestDate() == null) {
//                continue;
//            }
//            //是否期初一次性收息=是 的融资，不参与计息计算。
//            if (Objects.nonNull(map.get(base.getFinancingId())) && map.get(base.getFinancingId()) == 1) {
//                continue;
//            }
//            if (base.getFinancingAmount() <= 0) {
//                continue;
//            }
//            String type = base.getType();
            String type = base.getFinancingType();
            Long financingId = base.getFinancingId();
            if ("DK".equalsIgnoreCase(type)) {
                FundFinancingBaseInfo fundFinancingBaseInfo = fundFinancingId2Bean.get(base.getFinancingId());
                if (Objects.isNull(fundFinancingBaseInfo)) {
                    continue;
                }
                if (!financingIdNameMap.containsKey(financingId)) {
                    financingIdNameMap.putIfAbsent(financingId, new ArrayList<MonthlyCostInfo>());
                }
                List<FundOrganization> organizationList = orgMap.getOrDefault(financingId, Collections.emptyList());
                financingIdNameMap.get(financingId).add(new MonthlyCostInfo(Optional.ofNullable(organizationList.get(0)).map(FundOrganization::getOrganizationName).orElse(null), "DK"));
            } else if ("ZR".equalsIgnoreCase(type)) {
                FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo = FundDirectFinancingId2Bean.get(base.getFinancingId());
                if (Objects.isNull(fundDirectFinancingBaseInfo)) {
                    continue;
                }
                if (!financingIdNameMap.containsKey(financingId)) {
                    financingIdNameMap.putIfAbsent(financingId, new ArrayList<MonthlyCostInfo>());
                }
                financingIdNameMap.get(financingId).add(new MonthlyCostInfo(fundDirectFinancingBaseInfo.getProductName(), "ZR"));
            }
            financingIdList.add(financingId);
        }

        LambdaQueryWrapper<FundsDailyCost> wrapper = Wrappers.<FundsDailyCost>lambdaQuery()
                .in(FundsDailyCost::getFinancingId, financingIdList)
                .ne(FundsDailyCost::getItemText, BEGINNING_ITEM_TEXT)
                .eq(FundsDailyCost::getDeleted,false)
                .eq(isNotNull(interestDate), FundsDailyCost::getInterestDate, interestDate)
                .gt(FundsDailyCost::getFinancingAmount, 0)
                .orderByDesc(FundsDailyCost::getInterestDate);
        List<FundsDailyCost> fundsDailyCostList = fundsDailyCostService.list(wrapper);
        if (fundsDailyCostList.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Long, List<FundsDailyCost>> fundMap = new HashMap<>();
        Map<Long, List<FundsDailyCost>> fundDirectMap = new HashMap<>();
        for (FundsDailyCost fundsDailyCost : fundsDailyCostList) {
            if (fundsDailyCost.getType().equalsIgnoreCase("DK")) {
                fundMap.putIfAbsent(fundsDailyCost.getFinancingId(), new ArrayList<>());
                fundMap.get(fundsDailyCost.getFinancingId()).add(fundsDailyCost);
            } else if (fundsDailyCost.getType().equalsIgnoreCase( "ZR")) {
                fundDirectMap.putIfAbsent(fundsDailyCost.getFinancingId(), new ArrayList<>());
                fundDirectMap.get(fundsDailyCost.getFinancingId()).add(fundsDailyCost);
            }
        }
        List<FundsDailyCost> res = new ArrayList<>();
        for (Map.Entry<Long, List<FundsDailyCost>> entry : fundMap.entrySet()) {
            List<FundsDailyCost> fundsDailyCosts = entry.getValue();
            if (!fundsDailyCosts.isEmpty()) {
                res.add(fundsDailyCosts.get(0));
            }
        }
        for (Map.Entry<Long, List<FundsDailyCost>> entry : fundDirectMap.entrySet()) {
            List<FundsDailyCost> fundsDailyCosts = entry.getValue();
            if (!fundsDailyCosts.isEmpty()) {
                res.add(fundsDailyCosts.get(0));
            }
        }

        for (FundsDailyCost base : res) {
            MonthlyCostRSP monthlyCostRSP = new MonthlyCostRSP();
            monthlyCostRSP.setId(base.getId());
            monthlyCostRSP.setYearAndMonth(formatter.format(base.getInterestDate()));
            monthlyCostRSP.setBusinessType(base.getType());
            monthlyCostRSP.setPropertyType(base.getPropertyType());
            monthlyCostRSP.setFinancingId(base.getFinancingId());
            List<MonthlyCostInfo> costInfoList = financingIdNameMap.get(base.getFinancingId());
            if (costInfoList != null && !costInfoList.isEmpty()) {
                for (MonthlyCostInfo monthlyCostInfo : costInfoList) {
                    if (monthlyCostInfo.getType().equalsIgnoreCase(base.getType())) {
                        monthlyCostRSP.setOrganizationName(monthlyCostInfo.getOrganizationName());
                        break;
                    }
                }
            }
            if (StringUtils.isNotBlank(base.getPropertyType())) {
                LeaseType leaseType = LeaseType.of(base.getPropertyType());
                if (leaseType != null) {
                    monthlyCostRSP.setPropertyTypeDisplay(ProjectBizType.ZL.display());
                } else {
                    ProjectBizType projectBizType = ProjectBizType.of(base.getPropertyType());
                    if (projectBizType != null) {
                        monthlyCostRSP.setPropertyTypeDisplay(projectBizType.display());
                    }
                }
            }
            if ("DK".equalsIgnoreCase(base.getType())) {
                FundFinancingBaseInfo fundFinancingBaseInfo = fundFinancingId2Bean.get(base.getFinancingId());
                monthlyCostRSP.setFinancingCode(fundFinancingBaseInfo.getFinancingCode());
                monthlyCostRSP.setFinancingAmount(fundFinancingBaseInfo.getFinancingAmount());
                monthlyCostRSP.setValueDate(fundFinancingBaseInfo.getActualLoanDate());
                monthlyCostRSP.setLoanProperty(fundFinancingBaseInfo.getTimeLimitType());
            } else if ("ZR".equalsIgnoreCase(base.getType())) {
                FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo = FundDirectFinancingId2Bean.get(base.getFinancingId());
                monthlyCostRSP.setFinancingCode(fundDirectFinancingBaseInfo.getFinancingCode());
                monthlyCostRSP.setFinancingAmount(fundDirectFinancingBaseInfo.getFinancingAmount()*10000);
                monthlyCostRSP.setValueDate(fundDirectFinancingBaseInfo.getDurationFrom());
                monthlyCostRSP.setLoanProperty(FundFinancingTimeLimitTypeEnum.BOND_PAYABLE.name());
            }
            monthlyCostRSP.setFinancingCost(base.getFinancingCost());
            monthlyCostRSP.setTotalCapitalCost(base.getTotalCapitalCost());
            monthlyCostRSP.setRemainingAmount(base.getFinancingAmount());
            monthlyCostRSP.setFinancingRate(base.getFinancingRate());
            monthlyCostRSP.setDailyRate(base.getDailyRate());
            monthlyCostRSP.setIsConfirmed(base.getIsConfirmed());
            monthlyCostRSP.setTotalCapitalCostAfterTax(base.getTotalCapitalCostAfterTax());
            rspList.add(monthlyCostRSP);
        }
        return rspList;
    }

    private List<InterestPayRSP> getAll() {
        List<MonthlyCostRSP> rspList = getMonthlyCostRSP(null);
        List<InterestPayRSP> interestPayRSPList = new ArrayList<InterestPayRSP>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        for (MonthlyCostRSP monthlyCostRSP : rspList) {
            /*FundsDailyCost lastDayThisMonthCost = monthlyManageService.
                    getLastDayFundsDailyCost(monthlyCostRSP.getFinancingId(), monthlyCostRSP.getBusinessType());
            if (lastDayThisMonthCost == null) continue;*/
            String yearAndMonth = monthlyCostRSP.getYearAndMonth();
            LocalDate localDate = LocalDateTimeUtil.parseDate(yearAndMonth, DatePattern.NORM_MONTH_PATTERN);
            LocalDate lastDayOfThisMonth = LocalDate.of(localDate.getYear(), localDate.getMonthValue(), localDate.lengthOfMonth());
            InterestPayRSP interestPayRSP = new InterestPayRSP();
            interestPayRSP.setId(monthlyCostRSP.getId());
            interestPayRSP.setFinancingCode(monthlyCostRSP.getFinancingCode());
            interestPayRSP.setFinancingId(monthlyCostRSP.getFinancingId());
            interestPayRSP.setOrganizationName(monthlyCostRSP.getOrganizationName());
            interestPayRSP.setBizType(monthlyCostRSP.getPropertyType());
            interestPayRSP.setBizTypeDisplay(monthlyCostRSP.getPropertyTypeDisplay());
            interestPayRSP.setValueDate(monthlyCostRSP.getValueDate());
            interestPayRSP.setLoanProperty(monthlyCostRSP.getLoanProperty());
            interestPayRSP.setFinancingRate(monthlyCostRSP.getFinancingRate());
            interestPayRSP.setDailyRate(monthlyCostRSP.getDailyRate());
            interestPayRSP.setType(monthlyCostRSP.getBusinessType());
            interestPayRSP.setYearCapitalCost(monthlyCostRSP.getTotalCapitalCost());
            interestPayRSP.setYearCapitalCostAfterTax(monthlyCostRSP.getTotalCapitalCostAfterTax());
            LocalDate lastDayOfLastMonth = lastDayOfThisMonth.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
            FundsDailyCost fundsDailyCost = getDailyCost(monthlyCostRSP.getFinancingId(), monthlyCostRSP.getBusinessType(), lastDayOfLastMonth);
            if (fundsDailyCost == null) {
                fundsDailyCost = getConfirmedDailyCost(monthlyCostRSP.getFinancingId(), monthlyCostRSP.getBusinessType(), lastDayOfLastMonth);
            }
            Long lastTotalCapitalcosts = fundsDailyCost == null ? 0 : fundsDailyCost.getTotalCapitalCost();
            Long lastTotalCapitalcostsAfterTax = fundsDailyCost == null ? 0 : fundsDailyCost.getTotalCapitalCostAfterTax();
            interestPayRSP.setTermCapitalCost(monthlyCostRSP.getTotalCapitalCost() - lastTotalCapitalcosts);
            interestPayRSP.setTermCapitalCostAfterTax(monthlyCostRSP.getTotalCapitalCostAfterTax() - lastTotalCapitalcostsAfterTax);
            interestPayRSP.setCalculateTime(formatter.format(lastDayOfThisMonth));
            interestPayRSP.setUpdateTime(lastDayOfThisMonth);
            interestPayRSPList.add(interestPayRSP);
        }
        return interestPayRSPList;
    }

    public FundsDailyCost getDailyCost(Long financingId, String type, LocalDate interestDate) {
        LambdaQueryWrapper<FundsDailyCost> query = Wrappers.lambdaQuery();
        query.eq(FundsDailyCost::getFinancingId, financingId);
        query.eq(FundsDailyCost::getType, type);
        query.eq(FundsDailyCost::getInterestDate, interestDate);
        query.ne(FundsDailyCost::getItemText, BEGINNING_ITEM_TEXT);
        query.ne(FundsDailyCost::getItemText, DIFF_ITEM_TEXT);
        query.last(StringUtil.mysqlLimitOne());
        return fundsDailyCostService.getOne(query);
    }

    public FundsDailyCost getConfirmedDailyCost(Long financingId, String type, LocalDate interestDate) {
        LambdaQueryWrapper<FundsDailyCost> query = Wrappers.lambdaQuery();
        query.eq(FundsDailyCost::getFinancingId, financingId);
        query.eq(FundsDailyCost::getType, type);
        query.eq(FundsDailyCost::getInterestDate, interestDate);
        query.eq(FundsDailyCost::getIsConfirmed, true);
        query.ne(FundsDailyCost::getItemText, BEGINNING_ITEM_TEXT);
        query.ne(FundsDailyCost::getItemText, DIFF_ITEM_TEXT);
        query.last(StringUtil.mysqlLimitOne());
        return fundsDailyCostService.getOne(query);
    }

    public InterestPayBasicDetailRSP basicDetail(InterestPayBasicDetailREQ req) {
        InterestPayBasicDetailRSP res = new InterestPayBasicDetailRSP();
        // 找主表
        FundsDailyCostMain fundsDailyCostMain = SpringUtil.getBean(FundsDailyCostMainService.class).getByFinancingIdAndType(req.getFinancingId(), req.getType());
        if (Objects.isNull(fundsDailyCostMain)) {
            throw new MithrasException("主表数据不存在");
        }
        if (StrUtil.isNotBlank(fundsDailyCostMain.getLeaseType())) {
            LeaseType leaseType = LeaseType.of(fundsDailyCostMain.getLeaseType());
            if (Objects.nonNull(leaseType)) {
                res.setBizType(leaseType.name());
                res.setBizTypeDisplay(leaseType.display());
            }
        } else if (StrUtil.isNotBlank(fundsDailyCostMain.getBizType())) {
            ProjectBizType projectBizType = ProjectBizType.of(fundsDailyCostMain.getBizType());
            if (Objects.nonNull(projectBizType)) {
                res.setBizType(projectBizType.name());
                res.setBizTypeDisplay(projectBizType.display());
            }
        }
        if ("DK".equalsIgnoreCase(req.getType())) {
            LambdaQueryWrapper<FundFinancingBaseInfo> directQuery = Wrappers.lambdaQuery();
            directQuery.eq(FundFinancingBaseInfo::getId, req.getFinancingId());
            List<FundFinancingBaseInfo> fundFinancingBaseInfos = financingBaseInfoService.list(directQuery);
            if (!fundFinancingBaseInfos.isEmpty()) {
                FundFinancingBaseInfo fundFinancingBaseInfo = fundFinancingBaseInfos.get(0);
//                LambdaQueryWrapper<FundFinancingPledgeInfo> query = Wrappers.lambdaQuery();
//                query.eq(FundFinancingPledgeInfo::getFinancingId, fundFinancingBaseInfo.getId())
//                        .eq(FundFinancingPledgeInfo::getIsPledge, 1);
//                List<FundFinancingPledgeInfo> fundFinancingPledgeInfoList = fundFinancingPledgeInfoService.list(query);
//                fundFinancingPledgeInfoList = fundFinancingPledgeInfoList.stream()
//                        .sorted(Comparator.comparing(FundFinancingPledgeInfo::getContractAmount).reversed()).collect(Collectors.toList());
//                if (!fundFinancingPledgeInfoList.isEmpty()) {
//                    FundFinancingPledgeInfo fundFinancingPledgeInfo = fundFinancingPledgeInfoList.get(0);
//                    ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(fundFinancingPledgeInfo.getContractId());
//                    res.setBizType(getPropertyType(contractBaseInfo));
//                    if (StringUtils.isNotBlank(res.getBizType())) {
//                        LeaseType leaseType = LeaseType.of(res.getBizType());
//                        if (leaseType != null) {
//                            res.setBizTypeDisplay(ProjectBizType.ZL.display());
//                        } else {
//                            ProjectBizType projectBizType = ProjectBizType.of(res.getBizType());
//                            if (projectBizType != null) {
//                                res.setBizTypeDisplay(projectBizType.display());
//                            }
//                        }
//                    }
//                }
                res.setFinancingAmount(fundFinancingBaseInfo.getFinancingAmount());
                res.setFinancingCode(fundFinancingBaseInfo.getFinancingCode());
                res.setValueDate(fundFinancingBaseInfo.getActualLoanDate());
                res.setLoanProperty(fundFinancingBaseInfo.getTimeLimitType());
                List<FundOrganization> organizationList = organizationService.getByFinancingId(fundFinancingBaseInfo.getId());
                res.setOrganizationName(Optional.ofNullable(organizationList.get(0)).map(FundOrganization::getOrganizationName).orElse(null));
            }
        } else if ("ZR".equalsIgnoreCase(req.getType())) {
            LambdaQueryWrapper<FundDirectFinancingBaseInfo> directQuery = Wrappers.lambdaQuery();
            directQuery.eq(FundDirectFinancingBaseInfo::getId, req.getFinancingId());
            List<FundDirectFinancingBaseInfo> fundDirectFinancingBaseInfos = fundDirectFinancingBaseInfoService.list(directQuery);
            if (!fundDirectFinancingBaseInfos.isEmpty()) {
                FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo = fundDirectFinancingBaseInfos.get(0);
//                LambdaQueryWrapper<FundDirectFinancingPledgeInfo> query = Wrappers.lambdaQuery();
//                query.eq(FundDirectFinancingPledgeInfo::getFinancingId, fundDirectFinancingBaseInfo.getId())
//                        .eq(FundDirectFinancingPledgeInfo::getIsPledge, 1);
//                List<FundDirectFinancingPledgeInfo> fundDirectFinancingPledgeInfoList = fundDirectFinancingPledgeInfoService.list(query);
//                if (fundDirectFinancingPledgeInfoList != null && !fundDirectFinancingPledgeInfoList.isEmpty()) {
//                    fundDirectFinancingPledgeInfoList = fundDirectFinancingPledgeInfoList.stream()
//                            .sorted(Comparator.comparing(FundDirectFinancingPledgeInfo::getContractAmount).reversed()).collect(Collectors.toList());
//                    if (!fundDirectFinancingPledgeInfoList.isEmpty()) {
//                        FundDirectFinancingPledgeInfo fundDirectFinancingPledgeInfo = fundDirectFinancingPledgeInfoList.get(0);
//                        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(fundDirectFinancingPledgeInfo.getContractId());
//                        res.setBizType(getPropertyType(contractBaseInfo));
//                        if (StringUtils.isNotBlank(res.getBizType())) {
//                            LeaseType leaseType = LeaseType.of(res.getBizType());
//                            if (leaseType != null) {
//                                res.setBizTypeDisplay(ProjectBizType.ZL.display());
//                            } else {
//                                ProjectBizType projectBizType = ProjectBizType.of(res.getBizType());
//                                if (projectBizType != null) {
//                                    res.setBizTypeDisplay(projectBizType.display());
//                                }
//                            }
//                        }
//                    }
//                }
                res.setFinancingAmount(fundDirectFinancingBaseInfo.getFinancingAmount() * 10000);
                res.setFinancingCode(fundDirectFinancingBaseInfo.getFinancingCode());
                res.setValueDate(fundDirectFinancingBaseInfo.getDurationFrom());
                res.setLoanProperty(FundFinancingTimeLimitTypeEnum.BOND_PAYABLE.name());
                res.setOrganizationName(fundDirectFinancingBaseInfo.getProductName());
            }
        }
        return res;
    }


    public List<InterestPayCalDetailMultiRSP> calDetail2(InterestPayCalDetailREQ req) {
        List<InterestPayCalDetailRSP> interestPayCalDetailRSPS = calDetail(req);
        if (ObjectUtil.isEmpty(interestPayCalDetailRSPS)) {
            return null;
        }
        List<InterestPayCalDetailMultiRSP> rsps = new ArrayList<>();
        Map<Long, List<InterestPayCalDetailRSP>> productId2CalDetailMap = interestPayCalDetailRSPS.stream().collect(Collectors.groupingBy(e -> LongUtil.null2zero(e.getFinancingProductId())));
        productId2CalDetailMap.forEach((productId, calDetails) -> {
            if (CollectionUtil.isEmpty(calDetails)) {
                return;
            }
            InterestPayCalDetailMultiRSP rsp = new InterestPayCalDetailMultiRSP();
            List<String> collect = calDetails.stream().map(InterestPayCalDetailRSP::getAbbreviation).filter(ObjectUtil::isNotEmpty).collect(Collectors.toList());
            if (ObjectUtil.isNotEmpty(collect)) {
                rsp.setAbbreviation(collect.get(0));
            }
            rsp.setDates(calDetails);
            rsps.add(rsp);
        });
        if (MonthlyManageService.ZR.equals(req.getType())) {
            FundDirectFinancingBaseInfo directFinancingBaseInfo = fundDirectFinancingBaseInfoService.getById(req.getFinancingId());
            if (ObjectUtil.isNotEmpty(directFinancingBaseInfo) && StrUtil.equalsAny(directFinancingBaseInfo.getDirectFinancingType(), DirectFinancingType.ABN.name(), DirectFinancingType.ABS.name())) {
                //直融要求合并
                Map<LocalDate, InterestPayCalDetailRSP> date2BodyMap = new HashMap<>();
                interestPayCalDetailRSPS.forEach(e -> {
                    InterestPayCalDetailRSP orDefault = date2BodyMap.getOrDefault(e.getInterestDate(), new InterestPayCalDetailRSP());
                    orDefault.setDailyRate(e.getDailyRate());
                    orDefault.setInterestDate(e.getInterestDate());
                    orDefault.setRemainingAmount(Optional.ofNullable(orDefault.getRemainingAmount()).orElse(0L) + Optional.ofNullable(e.getRemainingAmount()).orElse(0L));
                    orDefault.setDailyAmount(Optional.ofNullable(orDefault.getDailyAmount()).orElse(0L) + Optional.ofNullable(e.getDailyAmount()).orElse(0L));
                    orDefault.setYearCapitalCost(Optional.ofNullable(orDefault.getYearCapitalCost()).orElse(0L) + Optional.ofNullable(e.getYearCapitalCost()).orElse(0L));
                    orDefault.setFinancingCostDiff(Optional.ofNullable(orDefault.getFinancingCostDiff()).orElse(0L) + Optional.ofNullable(e.getFinancingCostDiff()).orElse(0L));
                    orDefault.setBeginOfPeriodInterestBalance(Optional.ofNullable(orDefault.getBeginOfPeriodInterestBalance()).orElse(0L) + Optional.ofNullable(e.getBeginOfPeriodInterestBalance()).orElse(0L));
                    orDefault.setEndOfPeriodInterestBalance(Optional.ofNullable(orDefault.getEndOfPeriodInterestBalance()).orElse(0L) + Optional.ofNullable(e.getEndOfPeriodInterestBalance()).orElse(0L));
                    orDefault.setIsConfirmed(e.getIsConfirmed());
                    date2BodyMap.put(e.getInterestDate(), orDefault);
                });

                InterestPayCalDetailMultiRSP rsp = new InterestPayCalDetailMultiRSP();
                rsp.setAbbreviation(HZ);
                rsp.setDates(date2BodyMap.values().stream().sorted(Comparator.comparing(InterestPayCalDetailRSP::getInterestDate)).collect(Collectors.toList()));
                rsps.add(0, rsp);
            }

        }
        return rsps;
    }
    public List<InterestPayCalDetailRSP> calDetail(InterestPayCalDetailREQ req) {
        Page<FundsDailyCost> interestPayCalDetailPage = new Page<>();
        interestPayCalDetailPage.setCurrent(req.getPage());
        interestPayCalDetailPage.setSize(req.getPageSize());
        LambdaQueryWrapper<FundsDailyCost> query = Wrappers.lambdaQuery();
        query.eq(FundsDailyCost::getFinancingId, req.getFinancingId())
                .eq(FundsDailyCost::getType, req.getType())
                .orderByAsc(FundsDailyCost::getInterestDate);
        query.ne(FundsDailyCost::getItemText, BEGINNING_ITEM_TEXT);
        query.orderByDesc(FundsDailyCost::getInterestDate);
        query.orderByDesc(FundsDailyCost::getId);
        List<FundsDailyCost> fundsDailyCosts = fundsDailyCostService.list(query);
        if (CollUtil.isEmpty(fundsDailyCosts)) {
            return new ArrayList<>();
        }
        List<InterestPayCalDetailRSP> res = new ArrayList<>();
        fundsDailyCosts.forEach(one -> {
            InterestPayCalDetailRSP interestPayCalRSP = InterestPayCalDetailRSP.builder()
                    .id(one.getId())
                    .abbreviation(one.getAbbreviation())
                    .financingProductId(one.getFinancingProductId())
                    .remainingAmount(one.getFinancingAmount())
                    .interestDate(one.getInterestDate())
                    .principleAmount(one.getPrincipleAmount())
                    .interestAmount(one.getInterestAmount())
                    .financingId(one.getFinancingId())
                    .financingRate(one.getFinancingRate())
                    .dailyRate(one.getDailyRate())
                    .dailyAmount(one.getFinancingCost())
                    .yearCapitalCost(one.getTotalCapitalCost())
                    .yearCapitalCostAfterTax(one.getTotalCapitalCostAfterTax())
                    .isConfirmed(one.getIsConfirmed())
                    .financingCostDiff(one.getFinancingCostDiff())
                    .beginOfPeriodInterestBalance(one.getBeginOfPeriodInterestBalance())
                    .endOfPeriodInterestBalance(one.getEndOfPeriodInterestBalance())
                    .build();
            res.add(interestPayCalRSP);
        });
        return res;

//        List<InterestPayCalDetailRSP> rsp = new ArrayList<>();
//        Map<LocalDate, List<InterestPayCalDetailRSP>> map = new TreeMap<>();
//        for (InterestPayCalDetailRSP interestPayCalDetailRSP : res) {
//            map.putIfAbsent(interestPayCalDetailRSP.getInterestDate(), new ArrayList<>());
//            map.get(interestPayCalDetailRSP.getInterestDate()).add(interestPayCalDetailRSP);
//        }
//        for (Map.Entry<LocalDate, List<InterestPayCalDetailRSP>> entry : map.entrySet()) {
//            List<InterestPayCalDetailRSP> value = entry.getValue();
//            InterestPayCalDetailRSP temp = null;
//            InterestPayCalDetailRSP diffTemp = null;
//            if (!value.isEmpty()) {
//                value.sort(new Comparator<InterestPayCalDetailRSP>() {
//                    @Override
//                    public int compare(InterestPayCalDetailRSP o1, InterestPayCalDetailRSP o2) {
//                        return (int) (o2.getId() - o1.getId());
//                    }
//                });
//                for (InterestPayCalDetailRSP detailRSP : value) {
//                    if (detailRSP.getFinancingRate() == null && detailRSP.getDailyRate() == null) {
//                        diffTemp = detailRSP;
//                    } else if (temp == null) {
//                        temp = detailRSP;
//                    } else {
//                        temp = detailRSP;
//                    }
//                    /*else if (temp.getIsConfirmed() == 0 && detailRSP.getIsConfirmed() == 1) {
//                        temp = detailRSP;
//                    } else if (temp.getIsConfirmed() == 1 && detailRSP.getIsConfirmed() == 1 && detailRSP.getId() > temp.getId()) {
//                        temp = detailRSP;
//                    }*/
//                }
//                if (diffTemp != null) {
//                    rsp.add(diffTemp);
//                }
//                if (temp != null) {
//                    rsp.add(temp);
//                }
//            }
//        }
//
//        return rsp;
    }


    private String getPropertyType(ContractBaseInfo contractBaseInfo) {
        String propertyType = null;
        if (contractBaseInfo != null) {
            if (ProjectBizType.BL.name().equalsIgnoreCase(contractBaseInfo.getBizType())) {
                propertyType = ProjectBizType.BL.name();
            } else if (ProjectBizType.ZL.name().equalsIgnoreCase(contractBaseInfo.getBizType())
                    && LeaseType.zhi_zu.name().equalsIgnoreCase(contractBaseInfo.getLeaseType())) {
                propertyType = LeaseType.zhi_zu.name();
            } else if (ProjectBizType.ZL.name().equalsIgnoreCase(contractBaseInfo.getBizType())
                    && !LeaseType.zhi_zu.name().equalsIgnoreCase(contractBaseInfo.getLeaseType())){
                propertyType= contractBaseInfo.getLeaseType();
            } else {
                propertyType = contractBaseInfo.getBizType();
            }
        }
        return propertyType;
    }
}
