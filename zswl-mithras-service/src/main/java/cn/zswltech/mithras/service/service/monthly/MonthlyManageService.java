package cn.zswltech.mithras.service.service.monthly;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.monthly.MonthlyCostInfo;
import cn.zswltech.mithras.dto.monthly.MonthlyCostREQ;
import cn.zswltech.mithras.dto.monthly.MonthlyCostRSP;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.capital.domain.enums.FinanceCashFlowItemEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.enums.fund.DirectFinancingType;
import cn.zswltech.mithras.service.enums.fund.financing.FinancingTypeEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingTimeLimitTypeEnum;
import cn.zswltech.mithras.service.enums.fund.receiptrepay.CashFlowState;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.fund.direct.entity.*;
import cn.zswltech.mithras.service.fund.direct.service.*;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingPlan;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingRepayActual;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptFlowPlan;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.service.mapper.model.monthly.FundsDailyCost;
import cn.zswltech.mithras.service.mapper.model.monthly.FundsDailyCostMain;
import cn.zswltech.mithras.service.service.fund.FundOrganizationService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPlanService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPledgeInfoService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingRepayActualService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptFlowPlanService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.service.util.FinancialUtil;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MonthlyManageService {

    @Resource
    private FundFinancingBaseInfoService financingBaseInfoService;
    @Resource
    private FundDirectFinancingBaseInfoService fundDirectFinancingBaseInfoService;
    @Resource
    private FundsDailyCostService fundsDailyCostService;
    @Resource
    private FundOrganizationService organizationService;

    @Resource
    private FundFinancingPlanService fundFinancingPlanService;

    @Resource
    private FundDirectFinancingRepayActualSplitService fundDirectFinancingRepayActualSplitService;

    @Resource
    private FundDirectFinancingProductDetailService fundDirectFinancingProductDetailService;
    @Resource
    private FundDirectFinancingRepayActualSplitRecordService fundDirectFinancingRepayActualSplitRecordService;
    @Resource
    private FundDirectFinancingPledgeInfoService fundDirectFinancingPledgeInfoService;
    @Resource
    private FundFinancingPledgeInfoService fundFinancingPledgeInfoService;

    @Resource
    private FundReceiptRepayBaseInfoService fundReceiptRepayBaseInfoService;

    private static final String BEGINNING_ITEM_TEXT = "期初余额";

    private static final String DIFF_ITEM_TEXT = "差额";

    public static final String ZR = "ZR";

    public static final String DK = "DK";

    private final static String YEAR_DAY = "360";

    private final static String BJ = "BJ";

    private final static String LX = "LX";

    /**
     * 计算融资日利率
     * @param financingRate 年化利率
     * @return 日利率（无缩放，传入的参数如果是扩大倍数的则返回也是扩大倍数的）
     */
    public static BigDecimal calculateFinancingRateDaily(Integer financingRate) {
        if (Objects.isNull(financingRate)) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(financingRate).divide(new BigDecimal(MonthlyManageService.YEAR_DAY), 20, RoundingMode.HALF_UP);
    }

//
//    public PageR<MonthlyListRSP> listPage(MonthlyListREQ req) {
//        LocalDate month = LocalDateTimeUtil.parseDate("2024-05", "yyyy-MM");
//        LocalDate startMonth = month;
//        LocalDate closeMonth = startMonth.with(TemporalAdjusters.lastDayOfMonth());
//        List<MonthlyListRSP> result = new ArrayList<>();
//        while (true) {
//            Long rpCount = 0L;
//            Long rpCountExcludeTax = 0L;
//            Long airCount = 0L;
//            Long airCountExcludeTax = 0L;
//            Long stampDutyCount = 0L;
//            Long costCount = 0L;
//            Long costCountExcludeTax = 0L;
//            List<LocalDate> confirmDateList = new ArrayList<>();
//            MonthlyListRSP rsp = new MonthlyListRSP();
//            List<ContractIncomeSharing> contractIncomeSharingList = contractIncomeSharingService.list(Wrappers.<ContractIncomeSharing>lambdaQuery()
//                    .le(ContractIncomeSharing::getIncomeDate, closeMonth)
//                    .ge(ContractIncomeSharing::getIncomeDate, startMonth)
//                    .eq(ContractIncomeSharing::getDeleted, false)
//                    .eq(ContractIncomeSharing::getIsConfirmed, true)
//                    .orderByDesc(ContractIncomeSharing::getUpdateTime));
//            // air rp处理
//            if (CollectionUtils.isNotEmpty(contractIncomeSharingList)) {
//                if (contractIncomeSharingList.get(0).getConfirmTime() != null) {
//                    confirmDateList.add(LocalDate.from(contractIncomeSharingList.get(0).getConfirmTime()));
//                }
//                Set<Long> contractIdList = contractIncomeSharingList.stream().map(ContractIncomeSharing::getContractId).collect(Collectors.toSet());
//                List<ContractBaseInfo> contractBaseInfos = contractBaseInfoMapper.selectBatchIds(contractIdList);
//                Map<Long, String> contractId2Type = contractBaseInfos.stream().collect(Collectors.toMap(ContractBaseInfo::getId, ContractBaseInfo::getIncomeConfirmType, (m1, m2) -> m1));
//                for (ContractIncomeSharing contractIncomeSharing : contractIncomeSharingList) {
//                    IncomeConfirmTypeEnum incomeConfirmTypeEnum = IncomeConfirmTypeEnum.find(contractId2Type.get(contractIncomeSharing.getContractId()));
//                    if (incomeConfirmTypeEnum != null) {
//                        switch (incomeConfirmTypeEnum) {
//                            case RP:
//                                rpCount += contractIncomeSharing.getIncome();
//                                rpCountExcludeTax += contractIncomeSharing.getIncomeWithoutTax();
//                                break;
//                            case AIR:
//                                airCount += contractIncomeSharing.getIncome();
//                                airCountExcludeTax += contractIncomeSharing.getIncomeWithoutTax();
//                                break;
//                        }
//                    }
//                }
//            }
//            // 成本计提处理
//            List<FundsDailyCost> fundsDailyCostList = fundsDailyCostService.list(Wrappers.<FundsDailyCost>lambdaQuery()
//                    .le(FundsDailyCost::getInterestDate, closeMonth)
//                    .ge(FundsDailyCost::getInterestDate, startMonth)
//                    .orderByDesc(FundsDailyCost::getInterestDate)
//                    .eq(FundsDailyCost::getDeleted, false)
//                    .isNotNull(FundsDailyCost::getConfirmBatch)
//                    .eq(FundsDailyCost::getIsConfirmed, true)
//                    .ne(FundsDailyCost::getItemText, BEGINNING_ITEM_TEXT));
//            if (CollectionUtils.isNotEmpty(fundsDailyCostList)) {
//                for (FundsDailyCost fundsDailyCost : fundsDailyCostList) {
//                    costCount += fundsDailyCost.getTotalCapitalCost();
//                    costCountExcludeTax += fundsDailyCost.getTotalCapitalCostAfterTax();
//                }
//                if (fundsDailyCostList.get(0).getConfirmTime() != null) {
//                    confirmDateList.add(LocalDate.from(fundsDailyCostList.get(0).getConfirmTime()));
//                }
//            }
//            // 印花税处理
//            List<MonthlyStampDuty> list = monthlyStampDutyService.list(Wrappers.<MonthlyStampDuty>lambdaQuery()
//                    .eq(MonthlyStampDuty::getDeleted, false)
//                    .eq(MonthlyStampDuty::getIsConfirmed, true)
//                    .le(MonthlyStampDuty::getDate, closeMonth)
//                    .ge(MonthlyStampDuty::getDate, startMonth)
//                    .orderByDesc(MonthlyStampDuty::getUpdateTime));
//            if (CollectionUtils.isNotEmpty(list)) {
//                if (list.get(0).getConfirmTime() != null) {
//                    confirmDateList.add(LocalDate.from(list.get(0).getConfirmTime()));
//                }
//                stampDutyCount = list.stream().mapToLong(MonthlyStampDuty::getStampDuty).sum();
//            }
//            LocalDate confirmDate = null;
//            if (CollectionUtil.isNotEmpty(confirmDateList)) {
//                List<LocalDate> collect = confirmDateList.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
//                confirmDate = collect.get(0);
//            }
//            rsp.setAirCount(airCount)
//                    .setAirCountExcludeTax(airCountExcludeTax)
//                    .setRpCount(rpCount)
//                    .setRpCountExcludeTax(rpCountExcludeTax)
//                    .setStampDutyCount(stampDutyCount)
//                    .setCostCount(costCount)
//                    .setCostCountExcludeTax(costCountExcludeTax)
//                    .setConfirmDate(confirmDate)
//                    .setYearAndMonth(startMonth.getYear() + "-" + startMonth.getMonthValue());
//            result.add(rsp);
//            startMonth = startMonth.plusMonths(1);
//            closeMonth = startMonth.with(TemporalAdjusters.lastDayOfMonth());
//            if (!startMonth.isBefore(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()))) {
//                break;
//            }
//        }
//        return PageR.of(result, result.size(), req.getPage(), req.getPageSize());
//    }
//
//
//    public PageR<MonthlyAIRListRSP> airList(MonthlyAIRListREQ req) {
//        LocalDate startDate = LocalDateTimeUtil.parseDate(req.getYearAndMonth(), "yyyy-MM").with(TemporalAdjusters.firstDayOfMonth());
//        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
//        Map<Long, ContractBaseInfo> contract = getContract(IncomeConfirmTypeEnum.AIR, endDate, false);
//        MonthlyQuery monthlyQuery = new MonthlyQuery(endDate, startDate, null, contract.keySet(), req.getBatchNumber(), null);
//        com.baomidou.mybatisplus.extension.plugins.pagination.Page<MonthlyQueryResult> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, Integer.MAX_VALUE);
//        List<MonthlyQueryResult> list = contractIncomeSharingMapper.queryMonthlyData(page, monthlyQuery).getRecords();
//        List<MonthlyAIRListRSP> result = list.stream().map(collect -> {
//            MonthlyAIRListRSP rsp = new MonthlyAIRListRSP();
//            BeanUtils.copyProperties(collect, rsp);
//            BigDecimal bigDecimal = kpiParameterConfigService.ensureXMSRate(collect.getBizType(), collect.getLeaseType());
//            rsp.setTaxRate(Util.toMithrasUnit(bigDecimal.multiply(new BigDecimal(100))));
//            // 以将逾期的数据过滤
//            rsp.setOverdueType(OverdueTypeEnum.NOT_OVERDUE.name());
//            rsp.setYearAndMonth(req.getYearAndMonth());
//            return rsp;
//        }).collect(Collectors.toList());
//        return PageR.of(result, page.getTotal());
//    }
//
//
//    public PageR<MonthlyRPListRSP> rpList(MonthlyRPListREQ req) {
//        LocalDate startDate = LocalDateTimeUtil.parseDate(req.getYearAndMonth(), "yyyy-MM").withDayOfMonth(1);
//        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
//        Map<Long, ContractBaseInfo> contract = getContract(IncomeConfirmTypeEnum.RP, endDate, false);
//        MonthlyQuery monthlyQuery = new MonthlyQuery(endDate, startDate, null, contract.keySet(), req.getBatchNumber(), null);
//        com.baomidou.mybatisplus.extension.plugins.pagination.Page<MonthlyQueryResult> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, Integer.MAX_VALUE);
//        List<MonthlyQueryResult> list = contractIncomeSharingMapper.queryMonthlyData(page, monthlyQuery).getRecords();
//        List<Long> contractIdList = list.stream().map(MonthlyQueryResult::getContractId).collect(Collectors.toList());
//        Map<Long, ContractRentActual> rentActualMap = getRent(contractIdList);
//        Map<Long, Integer> contractLprMap = getContractLpr(contractIdList);
//        List<MonthlyRPListRSP> result = list.stream().map(collect -> {
//            Long contractId = collect.getContractId();
//            ContractRentActual contractRentActual = Optional.ofNullable(rentActualMap.get(contractId)).orElse(new ContractRentActual());
//            MonthlyRPListRSP rsp = new MonthlyRPListRSP();
//            BeanUtils.copyProperties(collect, rsp);
//            BigDecimal bigDecimal = kpiParameterConfigService.ensureXMSRate(collect.getBizType(), collect.getLeaseType());
//            rsp.setTaxRate(Util.toMithrasUnit(bigDecimal.multiply(new BigDecimal(100))));
//            // 以将逾期的数据过滤
//            rsp.setOverdueType(OverdueTypeEnum.NOT_OVERDUE.name());
//            rsp.setYearAndMonth(req.getYearAndMonth());
//            rsp.setContractNominalInterestRate(contractLprMap.get(contractId));
//            rsp.setTheLatestFullRefundRentPeriod(contractRentActual.getCashFlowPhase());
//            rsp.setTheLatestFullRefundRentDate(contractRentActual.getCashFlowDate());
//            rsp.setTheLatestFullRefundRentCapital(contractRentActual.getRemainingPrincipal());
//            return rsp;
//        }).collect(Collectors.toList());
//        return PageR.of(result, page.getTotal());
//    }
//
//    public PageR<MonthlyStampDutyProjRSP> projPage(MonthlyStampDutyProjREQ req) {
//
//        LocalDate startMonth = LocalDateTimeUtil.parseDate(req.getYearAndMonth(), "yyyy-MM");
//        LocalDate endMonth = startMonth.with(TemporalAdjusters.lastDayOfMonth());
//
//        Page<Object> page = PageHelper.startPage(req.getPage(), req.getPageSize());
//        LambdaQueryWrapper<MonthlyStampDuty> wrapper = Wrappers.<MonthlyStampDuty>lambdaQuery()
//                .eq(MonthlyStampDuty::getIsConfirmed, ObjectUtil.isNotEmpty(req.getIsConfirmed()) ? req.getIsConfirmed() : YesOrNoNumberEnum.NO.getCode())
//                .eq(MonthlyStampDuty::getDeleted, false)
//                .in(MonthlyStampDuty::getType, StampDutyTypeEnum.PROJ.name())
//                .ge(MonthlyStampDuty::getDate, startMonth)
//                .eq(req.getBatchNumber() != null, MonthlyStampDuty::getConfirmBatch, req.getBatchNumber())
//                .le(MonthlyStampDuty::getDate, endMonth);
//        List<MonthlyStampDuty> monthlyStampDutyList = monthlyStampDutyService.list(wrapper);
//
//        List<MonthlyStampDutyProjRSP> result = new ArrayList<>();
//        if (CollectionUtils.isNotEmpty(monthlyStampDutyList)) {
//            result = BeanUtil.copyToList(monthlyStampDutyList, MonthlyStampDutyProjRSP.class);
//        }
//        result.forEach(rsp -> {
//            rsp.setYearAndMonth(req.getYearAndMonth());
//        });
//        return PageR.of(result, page.getTotal());
//    }
//
//    public PageR<MonthlyStampDutyFinRSP> finPage(MonthlyStampDutyFinREQ req) {
//        // 融资：业务类型=项目贷款/流动资金贷款
//        LocalDate startMonth = LocalDateTimeUtil.parseDate(req.getYearAndMonth(), "yyyy-MM").with(TemporalAdjusters.firstDayOfMonth());
//        LocalDate endMonth = startMonth.with(TemporalAdjusters.lastDayOfMonth());
//        com.baomidou.mybatisplus.extension.plugins.pagination.Page<MonthlyStampDuty> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(req.getPage(), req.getPageSize());
//        LambdaQueryWrapper<MonthlyStampDuty> wrapper = Wrappers.<MonthlyStampDuty>lambdaQuery()
//                .eq(MonthlyStampDuty::getIsConfirmed, ObjectUtil.isNotEmpty(req.getIsConfirmed()) ? req.getIsConfirmed() : YesOrNoNumberEnum.NO.getCode())
//                .eq(MonthlyStampDuty::getDeleted, false)
//                .in(MonthlyStampDuty::getType, Arrays.asList(StampDutyTypeEnum.FIN_DIRECT_FIN.name(), StampDutyTypeEnum.FIN_FIN.name()))
//                .ge(MonthlyStampDuty::getDate, startMonth)
//                .le(MonthlyStampDuty::getDate, endMonth)
//                .eq(req.getBatchNumber() != null, MonthlyStampDuty::getConfirmBatch, req.getBatchNumber());
//        List<MonthlyStampDuty> monthlyStampDutyList = monthlyStampDutyService.list(wrapper);
//        List<MonthlyStampDutyFinRSP> result = new ArrayList<>();
//        if (CollectionUtils.isNotEmpty(monthlyStampDutyList)) {
//            result = BeanUtil.copyToList(monthlyStampDutyList, MonthlyStampDutyFinRSP.class);
//        }
//        result.forEach(rsp -> {
//            rsp.setYearAndMonth(req.getYearAndMonth());
//        });
//        return PageR.of(result, page.getTotal());
//    }
//
//
//    /**
//     * 获取下个月的第一天
//     *
//     * @param yearAndMonth yyyy-MM
//     * @return
//     */
//    private LocalDate handleDate(String yearAndMonth) {
//        String[] split = yearAndMonth.split("-");
//        try {
//            if (split.length != 0) {
//                int year = Integer.parseInt(split[0]);
//                int month = Integer.parseInt(split[1]);
//                return LocalDate.of(year, month, 1).plusMonths(1);
//            }
//        } catch (Exception e) {
//            throw new MithrasException("无法识别的时间格式");
//        }
//        throw new MithrasException("无法识别的时间格式");
//    }
//
//
//    /**
//     * 获取合同
//     *
//     * @param incomeConfirmTypeEnum
//     * @param month
//     * @param overdue               true:获取逾期的合同，false:获取不逾期的合同，null:我全都要
//     * @return
//     */
//    private Map<Long, ContractBaseInfo> getContract(IncomeConfirmTypeEnum incomeConfirmTypeEnum, LocalDate month, Boolean overdue) {
//        Map<Long, ContractBaseInfo> result = new HashMap<>();
//        List<ContractBaseInfo> list = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
//                .in(ContractBaseInfo::getContractStatus, Arrays.asList(ContractStatus.START_RENT.name(), ContractStatus.SETTLE.name()))
//                .eq(ContractBaseInfo::getIncomeConfirmType, incomeConfirmTypeEnum.name()));
//        if (CollectionUtils.isNotEmpty(list)) {
//            if (overdue != null) {
//                List<Long> contractIdList = list.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
//                List<Long> contractIdOverdue = contractIsOverdue(contractIdList, month);
//                list = list.stream().filter(contract -> overdue.equals(contractIdOverdue.contains(contract.getId()))).collect(Collectors.toList());
//            }
//            result = list.stream().collect(Collectors.toMap(ContractBaseInfo::getId, Function.identity()));
//        }
//        return result;
//    }
//
//
//    /**
//     * 判断合同是否逾期 逾期的判定规则：月结月份及之前应收的租金均已收到
//     *
//     * @return
//     */
//    private Boolean contractIsOverdue(Long contractId, LocalDate targetDate) {
//        LambdaQueryWrapper<CollectionBaseInfo> collectQuery = Wrappers.lambdaQuery();
//        collectQuery.eq(CollectionBaseInfo::getContractId, contractId);
//        collectQuery.eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name());
//        collectQuery.gt(CollectionBaseInfo::getPhase, 0);
//        collectQuery.lt(CollectionBaseInfo::getPlanCollectionDate, targetDate);
//        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.list(collectQuery);
//        if (CollectionUtil.isNotEmpty(collectionBaseInfoList)) {
//            // 逾期合同数量
//            long overdueCount = collectionBaseInfoList.stream().filter(e -> {
//                long planRent = Optional.ofNullable(e.getPlanCollectionAmount()).orElse(0L);
//                long actualRent = Optional.ofNullable(e.getCollectionAmount()).orElse(0L);
//                return planRent >= actualRent;
//            }).count();
//            return overdueCount == 0;
//        } else {
//            return null;
//        }
//    }
//
//    private List<Long> contractIsOverdue(List<Long> contractIdList, LocalDate targetDate) {
//        LambdaQueryWrapper<CollectionBaseInfo> collectQuery = Wrappers.lambdaQuery();
//        collectQuery.in(CollectionUtils.isNotEmpty(contractIdList), CollectionBaseInfo::getContractId, contractIdList);
//        collectQuery.eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name());
//        collectQuery.gt(CollectionBaseInfo::getPhase, 0);
//        collectQuery.le(CollectionBaseInfo::getPlanCollectionDate, targetDate);
//        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.list(collectQuery);
//        Map<Long, List<CollectionBaseInfo>> collectionMap = collectionBaseInfoList.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getContractId));
//        List<Long> contractIdResult = new ArrayList<>();
//        if (CollectionUtil.isNotEmpty(collectionBaseInfoList)) {
//            // 逾期合同数量
//            for (Long contractId : collectionMap.keySet()) {
//                List<CollectionBaseInfo> collectionBaseInfos = collectionMap.get(contractId);
//                long overdueCount = collectionBaseInfos.stream().filter(e -> {
//                    long planRent = Optional.ofNullable(e.getPlanCollectionAmount()).orElse(0L);
//                    long actualRent = Optional.ofNullable(e.getCollectionAmount()).orElse(0L);
//                    return planRent > actualRent;
//                }).count();
//                if (overdueCount > 0) {
//                    // 返回逾期的合同
//                    contractIdResult.add(contractId);
//                }
//            }
//        }
//        return contractIdResult;
//    }
//
//    public void download(ServletOutputStream outputStream, MonthlyExcelREQ req) {
//        MonthlyModuleTypeEnum monthlyModuleTypeEnum = MonthlyModuleTypeEnum.find(req.getModuleType());
//        if (monthlyModuleTypeEnum == null) {
//            return;
//        }
//        switch (monthlyModuleTypeEnum) {
//            case AIR:
//                MonthlyAIRListREQ monthlyAIRListREQ = new MonthlyAIRListREQ();
//                BeanUtils.copyProperties(req, monthlyAIRListREQ);
//                PageR<MonthlyAIRListRSP> monthlyAIRListRSPPageR = airList(monthlyAIRListREQ);
//                List<MonthlyAIRListRSP> monthlyAIRListRSPList = monthlyAIRListRSPPageR.getList();
//                if (CollectionUtils.isNotEmpty(monthlyAIRListRSPList)) {
//                    List<MonthlyAIRExcelModel> collect = monthlyAIRListRSPList.stream().map(air -> {
//                        MonthlyAIRExcelModel model = new MonthlyAIRExcelModel();
//                        BeanUtils.copyProperties(air, model);
//                        model.setOverdueType(Objects.requireNonNull(OverdueTypeEnum.find(model.getOverdueType())).display());
//                        model.setLeaseType(ensureType(air.getBizType(), air.getLeaseType()));
//                        model.setTaxRateExcel(Util.mithrasLong2BigDecimal(air.getTaxRate()));
//                        model.setIncomeSumExcel(Util.mithrasLong2BigDecimal(air.getIncomeSum()));
//                        model.setIncomeWithoutTaxSumExcel(Util.mithrasLong2BigDecimal(air.getIncomeWithoutTaxSum()));
//                        return model;
//                    }).collect(Collectors.toList());
//                    monthlyAIRExcelManagerExporter.exportExcel(collect, outputStream);
//                }
//                break;
//            case RP:
//                MonthlyRPListREQ monthlyRPListREQ = new MonthlyRPListREQ();
//                BeanUtils.copyProperties(req, monthlyRPListREQ);
//                PageR<MonthlyRPListRSP> monthlyRPListRSPPageR = rpList(monthlyRPListREQ);
//                List<MonthlyRPListRSP> monthlyRPListRSPList = monthlyRPListRSPPageR.getList();
//                if (CollectionUtils.isNotEmpty(monthlyRPListRSPList)) {
//                    List<MonthlyRPExcelModel> collect = monthlyRPListRSPList.stream().map(rp -> {
//                        MonthlyRPExcelModel model = new MonthlyRPExcelModel();
//                        BeanUtils.copyProperties(rp, model);
//                        model.setOverdueType(Objects.requireNonNull(OverdueTypeEnum.find(model.getOverdueType())).display());
//                        model.setLeaseType(ensureType(rp.getBizType(), rp.getLeaseType()));
//                        model.setTaxRateExcel(Util.mithrasLong2BigDecimal(rp.getTaxRate()));
//                        model.setContractNominalInterestRateExcel(Util.mithrasLong2BigDecimal(Long.valueOf(rp.getContractNominalInterestRate())));
//                        model.setIncomeSumExcel(Util.mithrasLong2BigDecimal(rp.getIncomeSum()));
//                        model.setIncomeWithoutTaxSumExcel(Util.mithrasLong2BigDecimal(rp.getIncomeWithoutTaxSum()));
//                        model.setTheLatestFullRefundRentCapitalExcel(Util.mithrasLong2BigDecimal(rp.getTheLatestFullRefundRentCapital()));
//                        return model;
//                    }).collect(Collectors.toList());
//                    monthlyRPExcelManagerExporter.exportExcel(collect, outputStream);
//                }
//                break;
//            case COST:
//                MonthlyCostREQ monthlyCostREQ = new MonthlyCostREQ();
//                BeanUtils.copyProperties(req, monthlyCostREQ);
//                monthlyCostREQ.setPageSize(10000);
//                List<MonthlyCostRSP> monthlyCostRSPList = costList(monthlyCostREQ);
//                //List<MonthlyCostRSP> monthlyCostRSPList = monthlyCostRSPPageR.getList();
//                if (CollectionUtils.isNotEmpty(monthlyCostRSPList)) {
//                    List<MonthlyCostExcelModel> collect = monthlyCostRSPList.stream().map(cost -> {
//                        MonthlyCostExcelModel model = new MonthlyCostExcelModel();
//                        BeanUtils.copyProperties(cost, model);
//                        model.setFinancingRateExcel(Util.mithrasLong2BigDecimal(Long.valueOf(cost.getFinancingRate())));
//                        model.setDailyRateExcel(Util.mithrasLong2BigDecimal(Long.valueOf(cost.getDailyRate())));
//                        model.setFinancingAmountExcel(Util.mithrasLong2BigDecimal(cost.getFinancingAmount()));
//                        model.setRemainingAmountExcel(Util.mithrasLong2BigDecimal(cost.getRemainingAmount()));
//                        model.setTotalCapitalCostExcel(Util.mithrasLong2BigDecimal(cost.getTotalCapitalCost()));
//                        model.setTotalCapitalCostAfterTaxExcel(Util.mithrasLong2BigDecimal(cost.getTotalCapitalCostAfterTax()));
//                        return model;
//                    }).collect(Collectors.toList());
//                    monthlyCostExcelManagerExporter.exportExcel(collect, outputStream);
//                }
//
//                break;
//            case STAMP_DUTY_PROJ:
//                MonthlyStampDutyProjREQ projREQ = new MonthlyStampDutyProjREQ();
//                BeanUtils.copyProperties(req, projREQ);
//                PageR<MonthlyStampDutyProjRSP> monthlyStampDutyProjRSPPageR = projPage(projREQ);
//                List<MonthlyStampDutyProjRSP> monthlyStampDutyProjRSPList = monthlyStampDutyProjRSPPageR.getList();
//                if (CollectionUtils.isNotEmpty(monthlyStampDutyProjRSPList)) {
//                    List<MonthlyDutyProjExcelModel> collect = monthlyStampDutyProjRSPList.stream().map(proj -> {
//                        MonthlyDutyProjExcelModel model = new MonthlyDutyProjExcelModel();
//                        BeanUtils.copyProperties(proj, model);
//                        model.setStampDutyExcel(Util.mithrasLong2BigDecimal(proj.getStampDuty()));
//                        return model;
//                    }).collect(Collectors.toList());
//                    monthlyDutyProjExcelManagerExporter.exportExcel(collect, outputStream);
//                }
//                break;
//            case STAMP_DUTY_FIN:
//                MonthlyStampDutyFinREQ finREQ = new MonthlyStampDutyFinREQ();
//                BeanUtils.copyProperties(req, finREQ);
//                PageR<MonthlyStampDutyFinRSP> monthlyStampDutyFinRSPPageR = finPage(finREQ);
//                List<MonthlyStampDutyFinRSP> monthlyStampDutyFinRSPList = monthlyStampDutyFinRSPPageR.getList();
//                if (CollectionUtils.isNotEmpty(monthlyStampDutyFinRSPList)) {
//                    List<MonthlyDutyFinExcelModel> collect = monthlyStampDutyFinRSPList.stream().map(fin -> {
//                        MonthlyDutyFinExcelModel model = new MonthlyDutyFinExcelModel();
//                        BeanUtils.copyProperties(fin, model);
//                        model.setStampDutyExcel(Util.mithrasLong2BigDecimal(fin.getStampDuty()));
//                        return model;
//                    }).collect(Collectors.toList());
//                    monthlyDutyFinExcelManagerExporter.exportExcel(collect, outputStream);
//                }
//                break;
//        }
//
//
//    }
//
//    @Transactional
//    public void submit(MonthlySubmitREQ req) {
//        List<MonthlyUserRecord> monthlyUserRecords = userRecordMapper.selectList(Wrappers.<MonthlyUserRecord>lambdaQuery()
//                .eq(MonthlyUserRecord::getUserId, AccountUtil.getLoginInfo().getId()));
//        if (CollectionUtils.isEmpty(monthlyUserRecords)) {
//            return;
//        }
//        List<Long> contractIncomeSharingList = new ArrayList<>();
//        List<Long> monthlyStampDutyList = new ArrayList<>();
//        List<Long> fundsDailyCostList = new ArrayList<>();
//        boolean flag = false;
//        for (MonthlyUserRecord userRecord : monthlyUserRecords) {
//            String type = userRecord.getType();
//            MonthlyModuleTypeEnum monthlyModuleTypeEnum = MonthlyModuleTypeEnum.find(type);
//            if (monthlyModuleTypeEnum == null) {
//                continue;
//            }
//            List<Long> recordValue = getRecordValue(userRecord.getValue());
//            // 页面中可能会存在未查看到资金端数据便提交的情况
//            if (CollectionUtils.isNotEmpty(recordValue)) {
//                switch (monthlyModuleTypeEnum) {
//                    case RP:
//                    case AIR:
//                        contractIncomeSharingList.addAll(recordValue);
//                        break;
//                    case STAMP_DUTY_PROJ:
//                        monthlyStampDutyList.addAll(recordValue);
//                        break;
//                    case STAMP_DUTY_FIN:
//                        flag = true;
//                        monthlyStampDutyList.addAll(recordValue);
//                        break;
//                    case COST:
//                        fundsDailyCostList.addAll(recordValue);
//                        break;
//                }
//            }
//        }
//        // 本次提交的批次号
//        String nowDate = LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_PATTERN);
//        LocalDate month = handleDate(req.getYearAndMonth());
//        if (CollectionUtils.isNotEmpty(contractIncomeSharingList)) {
//            contractIncomeSharingService.update(Wrappers.<ContractIncomeSharing>lambdaUpdate()
//                    .in(ContractIncomeSharing::getReceiptId, contractIncomeSharingList)
//                    .lt(ContractIncomeSharing::getIncomeDate, month)
//                    .isNull(ContractIncomeSharing::getConfirmBatch)
//                    .eq(ContractIncomeSharing::getIsConfirmed, false)
//                    .set(ContractIncomeSharing::getConfirmBatch, nowDate)
//                    .set(ContractIncomeSharing::getUpdateTime, LocalDateTime.now()));
//        }
//        if (CollectionUtils.isNotEmpty(monthlyStampDutyList)) {
//            if (!flag) {
//                List<MonthlyStampDutyFinRSP> finList = finPage(new MonthlyStampDutyFinREQ(req.getYearAndMonth(), true)).getList();
//                if (CollectionUtils.isNotEmpty(finList)) {
//                    List<Long> collect = finList.stream().map(MonthlyStampDutyFinRSP::getId).collect(Collectors.toList());
//                    monthlyStampDutyList.addAll(collect);
//                }
//            }
//            monthlyStampDutyService.update(Wrappers.<MonthlyStampDuty>lambdaUpdate()
//                    .in(MonthlyStampDuty::getId, monthlyStampDutyList)
//                    .isNull(MonthlyStampDuty::getConfirmBatch)
//                    .eq(MonthlyStampDuty::getIsConfirmed, false)
//                    .set(MonthlyStampDuty::getConfirmBatch, nowDate)
//                    .set(MonthlyStampDuty::getUpdateTime, LocalDateTime.now()));
//        }
//        if (CollectionUtils.isNotEmpty(fundsDailyCostList)) {
//            fundsDailyCostService.update(Wrappers.<FundsDailyCost>lambdaUpdate()
//                    .in(FundsDailyCost::getId, fundsDailyCostList)
//                    .isNull(FundsDailyCost::getConfirmBatch)
//                    .eq(FundsDailyCost::getIsConfirmed, false)
//                    .set(FundsDailyCost::getConfirmBatch, nowDate)
//                    .set(FundsDailyCost::getUpdateTime, LocalDateTime.now()));
//        }
//        userRecordMapper.deleteBatchIds(monthlyUserRecords.stream().map(MonthlyUserRecord::getId).collect(Collectors.toList()));
//
//        // 剩余本金法提交后需要将逾期合同的金额设置为0
//        Map<Long, ContractBaseInfo> contract = getContract(IncomeConfirmTypeEnum.RP, month, true);
//        if (CollectionUtils.isEmpty(contract)) {
//            return;
//        }
//        List<ContractIncomeSharing> incomeZeroList = contractIncomeSharingService.list(Wrappers.<ContractIncomeSharing>lambdaQuery()
//                .eq(ContractIncomeSharing::getIsConfirmed, false).eq(ContractIncomeSharing::getDeleted, false)
//                .in(CollectionUtils.isNotEmpty(contract.keySet()), ContractIncomeSharing::getContractId, contract.keySet()));
//        if (CollectionUtils.isNotEmpty(incomeZeroList)) {
//            incomeZeroList.forEach(income -> {
//                income.setIncome(0L);
//                income.setIncomeWithoutTax(0L);
//                income.setTax(0L);
//            });
//            contractIncomeSharingService.updateBatchById(incomeZeroList);
//        }
//
//        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
//            @Override
//            public void afterCommit() {
//                // 异步
//                new Thread(() -> {
//                    try {
//                        SpringContextHolder.getBean(MonthlyManageService.class).accountApplication(req.getYearAndMonth(), nowDate);
//                    } catch (Exception e) {
//                        log.error("月结管理通知苍穹错误", e);
//                    }
//                }).start();
//            }
//        });
//
//    }
//
//
//    //同步数据至财务系统
//    @Async
//    public void accountApplication(String yearAndMonth, String batchNumber) {
//        //实际利率法
//        this.sendMonthlyAIR(yearAndMonth, batchNumber);
//        //剩余本金法
//        this.sendMonthlyRP(yearAndMonth, batchNumber);
//        //成本计提
//        this.sendMonthlyCost(yearAndMonth, batchNumber);
//        //印花税 项目端
//        this.sendMonthlyStampDutyProj(yearAndMonth, batchNumber);
//        //印花税 资产端
//        this.sendMonthlyStampDutyFin(yearAndMonth, batchNumber);
//        //这里发送完成任务已经确认
//        SpringContextHolder.getBean(MonthlyManageService.class).callBackApplication(yearAndMonth, batchNumber);
//    }
//
//    /**
//     * 回调方法-修改状态
//     *
//     * @param batchNumber
//     */
//    @Transactional(rollbackFor = Throwable.class)
//    public void callBackApplication(String yearAndMonth, String batchNumber) {
//        contractIncomeSharingService.update(Wrappers.<ContractIncomeSharing>lambdaUpdate()
//                .eq(ContractIncomeSharing::getConfirmBatch, batchNumber)
//                .set(ContractIncomeSharing::getIsConfirmed, true)
//                .set(ContractIncomeSharing::getConfirmTime, LocalDateTime.now()));
//        monthlyStampDutyService.update(Wrappers.<MonthlyStampDuty>lambdaUpdate()
//                .eq(MonthlyStampDuty::getConfirmBatch, batchNumber)
//                .set(MonthlyStampDuty::getIsConfirmed, true)
//                .set(MonthlyStampDuty::getConfirmTime, LocalDateTime.now()));
//
//        List<FundsDailyCost> list = fundsDailyCostService.list(Wrappers.<FundsDailyCost>lambdaQuery().eq(FundsDailyCost::getConfirmBatch, batchNumber));
//        if (CollectionUtils.isNotEmpty(list)) {
//            LocalDate endDate = handleDate(yearAndMonth);
//            LocalDate startDate = endDate.plusMonths(-1);
//            Map<String, List<FundsDailyCost>> financingIdList = list.stream().collect(Collectors.groupingBy(FundsDailyCost::getType));
//            if (CollectionUtils.isNotEmpty(financingIdList)) {
//                List<FundsDailyCost> dk = financingIdList.get("DK");
//                if (CollectionUtils.isNotEmpty(dk)) {
//                    List<Long> dkFinancingIdList = dk.stream().map(FundsDailyCost::getFinancingId).collect(Collectors.toList());
//                    fundsDailyCostService.update(Wrappers.<FundsDailyCost>lambdaUpdate()
//                            .le(FundsDailyCost::getInterestDate, endDate)
//                            .gt(FundsDailyCost::getInterestDate, startDate)
//                            .eq(FundsDailyCost::getType, "DK")
//                            .in(FundsDailyCost::getFinancingId, dkFinancingIdList)
//                            .ne(FundsDailyCost::getItemText, BEGINNING_ITEM_TEXT)
//                            .set(FundsDailyCost::getIsConfirmed, true)
//                            .set(FundsDailyCost::getConfirmTime, LocalDateTime.now()));
//                }
//                List<FundsDailyCost> zr = financingIdList.get("ZR");
//                if (CollectionUtils.isNotEmpty(zr)) {
//                    List<Long> zrFinancingIdList = zr.stream().map(FundsDailyCost::getFinancingId).collect(Collectors.toList());
//                    fundsDailyCostService.update(Wrappers.<FundsDailyCost>lambdaUpdate()
//                            .le(FundsDailyCost::getInterestDate, endDate)
//                            .gt(FundsDailyCost::getInterestDate, startDate)
//                            .eq(FundsDailyCost::getType, "ZR")
//                            .in(FundsDailyCost::getFinancingId, zrFinancingIdList)
//                            .ne(FundsDailyCost::getItemText, BEGINNING_ITEM_TEXT)
//                            .set(FundsDailyCost::getIsConfirmed, true)
//                            .set(FundsDailyCost::getConfirmTime, LocalDateTime.now()));
//                }
//            }
//        }
//    }
//
//    private void sendMonthlyAIR(String yearAndMonth, String batchNumber) {
//        //实际利率法
//        MonthlyAIRListREQ airReq = new MonthlyAIRListREQ();
//        airReq.setYearAndMonth(yearAndMonth);
//        airReq.setPage(1);
////        airReq.setIsConfirmed(YesOrNoNumberEnum.YES.getCode());
//        airReq.setPageSize(Integer.MAX_VALUE);
//        airReq.setBatchNumber(batchNumber);
//        PageR<MonthlyAIRListRSP> monthlyAIRListRSPPageR = airList(airReq);
//        //实际利率法
//        if (ObjectUtil.isNotEmpty(monthlyAIRListRSPPageR) && ObjectUtil.isNotEmpty(monthlyAIRListRSPPageR.getList())) {
//            //contractId 实际利率
//            Map<Long, List<MonthlyAIRListRSP>> contractId2rsp = monthlyAIRListRSPPageR.getList().stream().collect(Collectors.groupingBy(MonthlyAIRListRSP::getContractId));
//            //projReviewId contract
//            List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.listByIds(contractId2rsp.keySet());
//            Map<Long, ContractBaseInfo> contractId2Bean = contractBaseInfos.stream().collect(Collectors.toMap(ContractBaseInfo::getId, e -> e, (a, b) -> a));
//            Map<Long, OrgDO> orgId2Dept = SpringContextHolder.getBean(OrgDOMapper.class).selectByIds(contractBaseInfos.stream().map(ContractBaseInfo::getBizDeptId).collect(Collectors.toList()), null).stream().collect(Collectors.toMap(OrgDO::getId, e -> e, (a, b) -> a));
//            Map<Long, Client> clientId2Bean = getBean(ClientService.class).listByIds(contractBaseInfos.stream().map(ContractBaseInfo::getClientId).collect(Collectors.toList())).stream().collect(Collectors.toMap(Client::getId, e -> e, (a, b) -> a));
//            monthlyAIRListRSPPageR.getList().forEach(air -> {
//                ContractBaseInfo contractBaseInfo = contractId2Bean.get(air.getContractId());
//                if (ObjectUtil.isNotEmpty(contractBaseInfo)) {
//                    monthlyManageConvert.sendAirAccountApplication(air, CQAccountApplicationTypeENUM.ACCRUE_INTEREST_ACTUAL_RATE, orgId2Dept.get(contractBaseInfo.getBizDeptId()), clientId2Bean.get(contractBaseInfo.getClientId()), yearAndMonth, contractBaseInfo);
//                }
//            });
//
//            /*proj2Contract.forEach((projReviewId, contracts) -> {
//                List<MonthlyAIRListRSP> allMonthlyAir = new ArrayList<>();
//                contracts.forEach((contract) -> allMonthlyAir.addAll(contractId2rsp.get(contract.getId())));//
//                monthlyManageConvert.sendAirAccountApplication(allMonthlyAir, CQAccountApplicationTypeENUM.ACCRUE_INTEREST_ACTUAL_RATE, orgId2Dept.get(contracts.get(0).getBizDeptId()), clientId2Bean.get(contracts.get(0).getClientId()), yearAndMonth);
//            });*/
//        }
//    }
//
//    private void sendMonthlyRP(String yearAndMonth, String batchNumber) {
//        MonthlyRPListREQ rpListREQ = new MonthlyRPListREQ();
//        rpListREQ.setYearAndMonth(yearAndMonth);
////        rpListREQ.setIsConfirmed(YesOrNoNumberEnum.YES.getCode());
//        rpListREQ.setPage(1);
//        rpListREQ.setPageSize(Integer.MAX_VALUE);
//        rpListREQ.setBatchNumber(batchNumber);
//        PageR<MonthlyRPListRSP> monthlyRPListRSPPageR = rpList(rpListREQ);
//        //剩余本金法
//        if (ObjectUtil.isNotEmpty(monthlyRPListRSPPageR) && ObjectUtil.isNotEmpty(monthlyRPListRSPPageR.getList())) {
//            //contractId 实际利率
//            Map<Long, List<MonthlyRPListRSP>> contractId2rsp = monthlyRPListRSPPageR.getList().stream().collect(Collectors.groupingBy(MonthlyRPListRSP::getContractId));
//            //projReviewId contract
//            List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.listByIds(contractId2rsp.keySet());
//            Map<Long, ContractBaseInfo> contractId2Bean = contractBaseInfos.stream().collect(Collectors.toMap(ContractBaseInfo::getId, e -> e, (a, b) -> a));
//            Map<Long, OrgDO> orgId2Dept = SpringContextHolder.getBean(OrgDOMapper.class).selectByIds(contractBaseInfos.stream().map(ContractBaseInfo::getBizDeptId).collect(Collectors.toList()), null).stream().collect(Collectors.toMap(OrgDO::getId, e -> e, (a, b) -> a));
//            Map<Long, Client> clientId2Bean = getBean(ClientService.class).listByIds(contractBaseInfos.stream().map(ContractBaseInfo::getClientId).collect(Collectors.toList())).stream().collect(Collectors.toMap(Client::getId, e -> e, (a, b) -> a));
//            monthlyRPListRSPPageR.getList().forEach(rp -> {
//                ContractBaseInfo contractBaseInfo = contractId2Bean.get(rp.getContractId());
//                if (ObjectUtil.isNotEmpty(contractBaseInfo)) {
//                    monthlyManageConvert.sendRPAccountApplication(rp, CQAccountApplicationTypeENUM.ACCRUE_INTEREST_REMAINING_PRINCIPAL, orgId2Dept.get(contractBaseInfo.getBizDeptId()), clientId2Bean.get(contractBaseInfo.getClientId()), yearAndMonth, contractBaseInfo);
//                }
//            });
//           /* proj2Contract.forEach((projReviewId, contracts) -> {
//                List<MonthlyRPListRSP> allMonthlyRP = new ArrayList<>();
//                contracts.forEach((contract) -> allMonthlyRP.addAll(contractId2rsp.get(contract.getId())));
//                monthlyManageConvert.sendRPAccountApplication(allMonthlyRP, CQAccountApplicationTypeENUM.ACCRUE_INTEREST_REMAINING_PRINCIPAL, orgId2Dept.get(contracts.get(0).getBizDeptId()), clientId2Bean.get(contracts.get(0).getClientId()), yearAndMonth);
//            });*/
//        }
//    }
//
//    private void sendMonthlyCost(String yearAndMonth, String batchNumber) {
//        //成本计提
//        MonthlyCostREQ req = new MonthlyCostREQ();
//        req.setYearAndMonth(yearAndMonth);
////        req.setIsConfirmed(YesOrNoNumberEnum.YES.getCode());
//        req.setPage(1);
//        req.setPageSize(Integer.MAX_VALUE);
//        req.setBatchNumber(batchNumber);
//        List<MonthlyCostRSP> monthlyCostRSPS = this.costList(req);
//        if (ObjectUtil.isNotEmpty(monthlyCostRSPS)) {
//            List<Long> dkFinancingIds = monthlyCostRSPS.stream().filter(e -> "DK".equalsIgnoreCase(e.getBusinessType())).map(MonthlyCostRSP::getFinancingId).collect(Collectors.toList());
//            List<Long> zrFinancingIds = monthlyCostRSPS.stream().filter(e -> "ZR".equalsIgnoreCase(e.getBusinessType())).map(MonthlyCostRSP::getFinancingId).collect(Collectors.toList());
//            Map<Long, FundFinancingBaseInfo> fundFinancingId2Bean = null;
//            if (CollectionUtil.isNotEmpty(dkFinancingIds)) {
//                fundFinancingId2Bean = financingBaseInfoService.listByIds(dkFinancingIds).stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, e -> e, (a, b) -> a));
//            }
//            Map<Long, FundDirectFinancingBaseInfo> fundDirectFinancingId2Bean = null;
//            if (CollectionUtil.isNotEmpty(zrFinancingIds)) {
//                fundDirectFinancingId2Bean = fundDirectFinancingBaseInfoService.listByIds(zrFinancingIds).stream().collect(Collectors.toMap(FundDirectFinancingBaseInfo::getId, e -> e, (a, b) -> a));
//            }
//            Map<Long, List<FundFinancingPledgeInfo>> dkPledgeMap = new HashMap<>();
//            Map<Long, List<FundDirectFinancingPledgeInfo>> zrPledgeMap = new HashMap<>();
//            List<Long> contractIds = new ArrayList<>();
//            if (ObjectUtil.isNotEmpty(dkFinancingIds)) {
//                List<FundFinancingPledgeInfo> list = getBean(FundFinancingPledgeInfoService.class).list(Wrappers.<FundFinancingPledgeInfo>lambdaQuery()
//                        .in(FundFinancingPledgeInfo::getFinancingId, dkFinancingIds));
//                dkPledgeMap = list.stream().collect(Collectors.groupingBy(FundFinancingPledgeInfo::getFinancingId));
//                contractIds.addAll(list.stream().map(FundFinancingPledgeInfo::getContractId).collect(Collectors.toList()));
//            }
//            if (ObjectUtil.isNotEmpty(zrFinancingIds)) {
//                List<FundDirectFinancingPledgeInfo> list = getBean(FundDirectFinancingPledgeInfoService.class).list(Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery()
//                        .in(FundDirectFinancingPledgeInfo::getFinancingId, zrFinancingIds));
//                zrPledgeMap = list.stream().collect(Collectors.groupingBy(FundDirectFinancingPledgeInfo::getFinancingId));
//                contractIds.addAll(list.stream().map(FundDirectFinancingPledgeInfo::getContractId).collect(Collectors.toList()));
//            }
//            //查询合同信息
//            Map<Long, ContractBaseInfo> contractId2Bean = new HashMap<>();
//            Map<Long, Client> clientId2Bean = new HashMap<>();
//            if (!contractIds.isEmpty()) {
//                contractId2Bean = contractBaseInfoMapper.selectBatchIds(contractIds).stream().collect(Collectors.toMap(ContractBaseInfo::getId, e -> e, (a, b) -> a));
//                clientId2Bean = getBean(ClientService.class).listByIds(contractId2Bean.values().stream().map(ContractBaseInfo::getClientId).collect(Collectors.toList())).stream().collect(Collectors.toMap(Client::getId, e -> e, (a, b) -> a));
//            }
//
//            List<Long> orgIds = new ArrayList<>();
//            if (ObjectUtil.isNotEmpty(fundFinancingId2Bean) && ObjectUtil.isNotEmpty(fundFinancingId2Bean.values())) {
//                orgIds.addAll(fundFinancingId2Bean.values().stream().map(FundFinancingBaseInfo::getDeptId).collect(Collectors.toSet()));
//            }
//            if (ObjectUtil.isNotEmpty(fundDirectFinancingId2Bean) && ObjectUtil.isNotEmpty(fundDirectFinancingId2Bean.values())) {
//                orgIds.addAll(fundDirectFinancingId2Bean.values().stream().map(FundDirectFinancingBaseInfo::getDeptId).collect(Collectors.toSet()));
//            }
//            Map<Long, OrgDO> orgId2Dept = SpringContextHolder.getBean(OrgDOMapper.class).selectByIds(orgIds, null).stream().collect(Collectors.toMap(OrgDO::getId, e -> e, (a, b) -> a));
//            //间融
//            for (MonthlyCostRSP monthlyCost : monthlyCostRSPS) {//间融
//                if ("DK".equalsIgnoreCase(monthlyCost.getBusinessType())) {
//                    FundFinancingBaseInfo baseInfo = fundFinancingId2Bean.get(monthlyCost.getFinancingId());
//                    if (ObjectUtil.isNotEmpty(baseInfo)) {
//                        List<FundFinancingPledgeInfo> fundFinancingPledgeInfos = dkPledgeMap.get(baseInfo.getId());
//                        if (ObjectUtil.isNotEmpty(fundFinancingPledgeInfos)) {
//                            ContractBaseInfo contractBaseInfo = contractId2Bean.get(fundFinancingPledgeInfos.get(0).getContractId());
//                            if (ObjectUtil.isNotEmpty(contractBaseInfo)) {
//                                Client client = clientId2Bean.get(contractBaseInfo.getClientId());
//                                monthlyManageConvert.sendCostList(monthlyCost, orgId2Dept.get(baseInfo.getDeptId()), yearAndMonth, client, contractBaseInfo);
//                            }
//                        }
//                    }
//                } else {
//                    FundDirectFinancingBaseInfo baseInfo = fundDirectFinancingId2Bean.get(monthlyCost.getFinancingId());
//                    if (ObjectUtil.isNotEmpty(baseInfo)) {
//                        List<FundDirectFinancingPledgeInfo> fundFinancingPledgeInfos = zrPledgeMap.get(baseInfo.getId());
//                        if (ObjectUtil.isNotEmpty(fundFinancingPledgeInfos)) {
//                            ContractBaseInfo contractBaseInfo = contractId2Bean.get(fundFinancingPledgeInfos.get(0).getContractId());
//                            if (ObjectUtil.isNotEmpty(contractBaseInfo)) {
//                                Client client = clientId2Bean.get(contractBaseInfo.getClientId());
//                                monthlyManageConvert.sendCostList(monthlyCost, orgId2Dept.get(baseInfo.getDeptId()), yearAndMonth, client, contractBaseInfo);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private void sendMonthlyStampDutyProj(String yearAndMonth, String batchNumber) {
//        //印花税 项目端
//        MonthlyStampDutyProjREQ projREQ = new MonthlyStampDutyProjREQ(yearAndMonth, YesOrNoNumberEnum.YES.getCode());
//        projREQ.setPage(1);
//        projREQ.setPageSize(Integer.MAX_VALUE);
//        projREQ.setBatchNumber(batchNumber);
//        PageR<MonthlyStampDutyProjRSP> monthlyStampDutyProjRSPPageR = this.projPage(projREQ);
//        if (ObjectUtil.isNotEmpty(monthlyStampDutyProjRSPPageR) && ObjectUtil.isNotEmpty(monthlyStampDutyProjRSPPageR.getList())) {
//            List<MonthlyStampDutyProjRSP> dutyProjRSPS = monthlyStampDutyProjRSPPageR.getList();
//            //合同信息
//            List<ContractBaseInfo> contractBaseInfos = contractBaseInfoMapper.selectBatchIds(dutyProjRSPS.stream().map(MonthlyStampDutyProjRSP::getContractId).collect(Collectors.toList()));
//            if (ObjectUtil.isNotEmpty(contractBaseInfos)) {
//                Map<Long, ContractBaseInfo> contractId2Bean = contractBaseInfos.stream().collect(Collectors.toMap(ContractBaseInfo::getId, e -> e, (a, b) -> a));
//                Map<Long, OrgDO> orgId2Dept = SpringContextHolder.getBean(OrgDOMapper.class).selectByIds(contractBaseInfos.stream().map(ContractBaseInfo::getBizDeptId).collect(Collectors.toList()), null).stream().collect(Collectors.toMap(OrgDO::getId, e -> e, (a, b) -> a));
//                Map<Long, Client> clientId2Bean = getBean(ClientService.class).listByIds(contractBaseInfos.stream().map(ContractBaseInfo::getClientId).collect(Collectors.toList())).stream().collect(Collectors.toMap(Client::getId, e -> e, (a, b) -> a));
//                dutyProjRSPS.forEach(dutyProjRSP -> {
//                    ContractBaseInfo contractBaseInfo = contractId2Bean.get(dutyProjRSP.getContractId());
//                    if (ObjectUtil.isNotEmpty(contractBaseInfo)) {
//                        monthlyManageConvert.sendStampDuty(dutyProjRSP, orgId2Dept.get(contractBaseInfo.getBizDeptId()), clientId2Bean.get(contractBaseInfo.getClientId()), yearAndMonth, contractBaseInfo);
//                    }
//                });
//            }
//        }
//    }
//
//    private void sendMonthlyStampDutyFin(String yearAndMonth, String batchNumber) {
//        //印花税 资产端 StampDutyTypeEnum
//        MonthlyStampDutyFinREQ stampDutyFinReq = new MonthlyStampDutyFinREQ();
//        stampDutyFinReq.setYearAndMonth(yearAndMonth);
////        stampDutyFinReq.setIsConfirmed(YesOrNoNumberEnum.YES.getCode());
//        stampDutyFinReq.setPage(1);
//        stampDutyFinReq.setPageSize(Integer.MAX_VALUE);
//        stampDutyFinReq.setBatchNumber(batchNumber);
//        PageR<MonthlyStampDutyFinRSP> monthlyStampDutyFinRSPPageR = this.finPage(stampDutyFinReq);
//        if (ObjectUtil.isNotEmpty(monthlyStampDutyFinRSPPageR) && ObjectUtil.isNotEmpty(monthlyStampDutyFinRSPPageR.getList())) {
//            List<MonthlyStampDutyFinRSP> dutyProjRSPS = monthlyStampDutyFinRSPPageR.getList();
//            //合同信息
//            List<Long> dkFinancingIds = dutyProjRSPS.stream().filter(e -> StampDutyTypeEnum.FIN_FIN.name().equalsIgnoreCase(e.getType())).map(MonthlyStampDutyFinRSP::getFinancingId).collect(Collectors.toList());
//            List<Long> zrFinancingIds = dutyProjRSPS.stream().filter(e -> StampDutyTypeEnum.FIN_DIRECT_FIN.name().equalsIgnoreCase(e.getType())).map(MonthlyStampDutyFinRSP::getFinancingId).collect(Collectors.toList());
//            Map<Long, FundFinancingBaseInfo> fundFinancingId2Bean = new HashMap<>();
//            Map<Long, FundDirectFinancingBaseInfo> fundDirectFinancingId2Bean = new HashMap<>();
//            Map<Long, List<FundFinancingPledgeInfo>> dkPledgeMap = new HashMap<>();
//            Map<Long, List<FundDirectFinancingPledgeInfo>> zrPledgeMap = new HashMap<>();
//            List<Long> contractIds = new ArrayList<>();
//            if (ObjectUtil.isNotEmpty(dkFinancingIds)) {
//                fundFinancingId2Bean = financingBaseInfoService.listByIds(dkFinancingIds).stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, e -> e, (a, b) -> a));
//                List<FundFinancingPledgeInfo> list = getBean(FundFinancingPledgeInfoService.class).list(Wrappers.<FundFinancingPledgeInfo>lambdaQuery()
//                        .in(FundFinancingPledgeInfo::getFinancingId, dkFinancingIds));
//                dkPledgeMap = list.stream().collect(Collectors.groupingBy(FundFinancingPledgeInfo::getFinancingId));
//                contractIds.addAll(list.stream().map(FundFinancingPledgeInfo::getContractId).collect(Collectors.toList()));
//            }
//            if (ObjectUtil.isNotEmpty(zrFinancingIds)) {
//                fundDirectFinancingId2Bean = fundDirectFinancingBaseInfoService.listByIds(zrFinancingIds).stream().collect(Collectors.toMap(FundDirectFinancingBaseInfo::getId, e -> e, (a, b) -> a));
//                List<FundDirectFinancingPledgeInfo> list = getBean(FundDirectFinancingPledgeInfoService.class).list(Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery()
//                        .in(FundDirectFinancingPledgeInfo::getFinancingId, zrFinancingIds));
//                zrPledgeMap = list.stream().collect(Collectors.groupingBy(FundDirectFinancingPledgeInfo::getFinancingId));
//                contractIds.addAll(list.stream().map(FundDirectFinancingPledgeInfo::getContractId).collect(Collectors.toList()));
//            }
//            //查询合同信息
//            Map<Long, ContractBaseInfo> contractId2Bean = new HashMap<>();
//            Map<Long, Client> clientId2Bean = new HashMap<>();
//            if (!contractIds.isEmpty()) {
//                contractId2Bean = contractBaseInfoMapper.selectBatchIds(contractIds).stream().collect(Collectors.toMap(ContractBaseInfo::getId, e -> e, (a, b) -> a));
//                clientId2Bean = getBean(ClientService.class).listByIds(contractId2Bean.values().stream().map(ContractBaseInfo::getClientId).collect(Collectors.toList())).stream().collect(Collectors.toMap(Client::getId, e -> e, (a, b) -> a));
//            }
//
//            List<Long> orgIds = new ArrayList<>();
//            if (ObjectUtil.isNotEmpty(fundFinancingId2Bean) && ObjectUtil.isNotEmpty(fundFinancingId2Bean.values())) {
//                orgIds.addAll(fundFinancingId2Bean.values().stream().map(FundFinancingBaseInfo::getDeptId).collect(Collectors.toSet()));
//            }
//            if (ObjectUtil.isNotEmpty(fundDirectFinancingId2Bean) && ObjectUtil.isNotEmpty(fundDirectFinancingId2Bean.values())) {
//                orgIds.addAll(fundDirectFinancingId2Bean.values().stream().map(FundDirectFinancingBaseInfo::getDeptId).collect(Collectors.toSet()));
//            }
//            Map<Long, OrgDO> orgId2Dept = SpringContextHolder.getBean(OrgDOMapper.class).selectByIds(orgIds, null).stream().collect(Collectors.toMap(OrgDO::getId, e -> e, (a, b) -> a));
//
//            for (MonthlyStampDutyFinRSP dutyProjRSP : dutyProjRSPS) {
//                ContractBaseInfo contractBaseInfo = null;
//                //融资
//                if (StampDutyTypeEnum.FIN_FIN.name().equalsIgnoreCase(dutyProjRSP.getType())) {
//                    List<FundFinancingPledgeInfo> fundFinancingPledgeInfos = dkPledgeMap.get(dutyProjRSP.getFinancingId());
//                    if (ObjectUtil.isNotEmpty(fundFinancingPledgeInfos)) {
//                        contractBaseInfo = contractId2Bean.get(fundFinancingPledgeInfos.get(0).getContractId());
//                    }
//                } else {
//                    List<FundDirectFinancingPledgeInfo> directFinancingPledgeInfo = zrPledgeMap.get(dutyProjRSP.getFinancingId());
//                    if (ObjectUtil.isNotEmpty(directFinancingPledgeInfo)) {
//                        contractBaseInfo = contractId2Bean.get(directFinancingPledgeInfo.get(0).getContractId());
//                    }
//                }
//                if (contractBaseInfo != null) {
//                    monthlyManageConvert.sendFinPage(dutyProjRSP, orgId2Dept.get(contractBaseInfo.getBizDeptId()), clientId2Bean.get(contractBaseInfo.getClientId()), yearAndMonth, contractBaseInfo);
//                }
//            }
//        }
//    }
//
//    private List<Long> getRecordValue(String value) {
//        List<Long> incomeIdList = new ArrayList<>();
//        try {
//            if (value != null) {
//                incomeIdList = JSON.parseArray(value, Long.class);
//            }
//        } catch (Exception e) {
//            return new ArrayList<>();
//        }
//        return incomeIdList;
//    }
//
//    public void validate(MonthlyCostValidateREQ req) {
//        String yearAndMonth = req.getYearAndMonth();
//        if (StringUtils.isEmpty(yearAndMonth)) {
//            throw new MithrasException("月份时间格式为空");
//        }
//        LocalDate localDate = LocalDateTimeUtil.parseDate(yearAndMonth, DatePattern.NORM_MONTH_PATTERN);
//        LocalDate interestStartDate = LocalDate.of(localDate.getYear(), localDate.getMonthValue(), 1);
//        LocalDate interestEndDate = LocalDate.of(localDate.getYear(), localDate.getMonthValue(), localDate.lengthOfMonth());
//        //期初日期
//        LambdaQueryWrapper<FundsDailyCost> query = Wrappers.lambdaQuery();
//        query.eq(FundsDailyCost::getItemText, BEGINNING_ITEM_TEXT)
//                .gt(FundsDailyCost::getFinancingAmount, 0);
//        List<FundsDailyCost> beginDayList = fundsDailyCostService.list(query);
//        boolean findCalDate = false;
//        for (FundsDailyCost beginDailyCost : beginDayList) {
//            Long financingId = beginDailyCost.getFinancingId();
//            LocalDate lastCostDate = getLastCostday(financingId, beginDailyCost.getType());
//            //期初和查询月份在一个月，并且无计算利息
//            if (lastCostDate == null
//                    && beginDailyCost.getInterestDate().plusDays(1).equals(interestStartDate)
//                    && beginDailyCost.getInterestDate().isBefore(interestEndDate)) {
//                findCalDate = true;
//            }
//            //期初在日期中间，并且无计算利息
//            if (lastCostDate == null
//                    && !beginDailyCost.getInterestDate().isBefore(interestStartDate)
//                    && beginDailyCost.getInterestDate().isBefore(interestEndDate)) {
//                findCalDate = true;
//            }
//
//            //已有计算利息
//            if (lastCostDate != null
//                    && interestStartDate.equals(lastCostDate.plusDays(1))) {
//                findCalDate = true;
//            }
//            if (localDate.getMonthValue() >= beginDailyCost.getInterestDate().getMonthValue()
//                    && lastCostDate != null && lastCostDate.getMonthValue() >= localDate.getMonthValue()) {
//                findCalDate = true;
//            }
//            if (findCalDate) {
//                break;
//            }
//        }
//        if (!findCalDate) {
//            throw new MithrasException(String.format("%d年%d月无成本计提日期", localDate.getYear(), localDate.getMonthValue()));
//        }
//    }
//
//
//    public List<MonthlyCostRSP> costList(MonthlyCostREQ req) {
//        String yearAndMonth = req.getYearAndMonth();
//        if (StringUtils.isEmpty(yearAndMonth)) {
//            throw new MithrasException("月份时间格式为空");
//        }
//        LocalDate localDate = LocalDateTimeUtil.parseDate(yearAndMonth, DatePattern.NORM_MONTH_PATTERN);
//        LocalDate interestEndDate = LocalDate.of(localDate.getYear(), localDate.getMonthValue(), localDate.lengthOfMonth());
//
//        List<MonthlyCostRSP> rspList = new ArrayList<>();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
//        Map<Long, List<MonthlyCostInfo>> financingIdNameMap = new HashMap<>();
//        LambdaQueryWrapper<FundsDailyCost> query = Wrappers.lambdaQuery();
//        query.eq(FundsDailyCost::getItemText, BEGINNING_ITEM_TEXT);
//        List<FundsDailyCost> todoList = fundsDailyCostService.list(query);
//        if (CollectionUtil.isEmpty(todoList)) {
//            log.error("执行每日成本计息为空");
//            return Collections.emptyList();
//        }
//        //返回增量期初+存量期初
//        List<FundsDailyCost> beginRes = getIncrementFunds(todoList);
//        //获取间融的融资信息
//        List<Long> financingIds = beginRes.stream().filter(f -> "DK".equals(f.getType())).map(FundsDailyCost::getFinancingId).collect(Collectors.toList());
//        final List<FundFinancingBaseInfo> fundFinancingBaseInfos = financingBaseInfoService.listByIds(financingIds);
//        Map<Long, Integer> map = fundFinancingBaseInfos.stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, FundFinancingBaseInfo::getInitialInterestReceivedOnce, (a, b) -> a));
//        Map<Long, FundFinancingBaseInfo> fundFinancingId2Bean = financingBaseInfoService.listByIds(beginRes.stream().filter(e -> "DK".equalsIgnoreCase(e.getType())).map(FundsDailyCost::getFinancingId).collect(Collectors.toList())).stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, e -> e, (a, b) -> a));
//        Map<Long, FundDirectFinancingBaseInfo> FundDirectFinancingId2Bean = fundDirectFinancingBaseInfoService.listByIds(beginRes.stream().filter(e -> "ZR".equalsIgnoreCase(e.getType())).map(FundsDailyCost::getFinancingId).collect(Collectors.toList())).stream().collect(Collectors.toMap(FundDirectFinancingBaseInfo::getId, e -> e, (a, b) -> a));
//        Set<Long> financingIdList = new HashSet<>();
//        for (FundsDailyCost base : beginRes) {
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
//            Long financingId = base.getFinancingId();
//            if ("DK".equalsIgnoreCase(type)) {
//                FundFinancingBaseInfo fundFinancingBaseInfo = fundFinancingId2Bean.get(base.getFinancingId());
//                if (Objects.isNull(fundFinancingBaseInfo)) {
//                    continue;
//                }
//                if (!financingIdNameMap.containsKey(financingId)) {
//                    financingIdNameMap.putIfAbsent(financingId, new ArrayList<MonthlyCostInfo>());
//                }
//                financingIdNameMap.get(financingId).add(new MonthlyCostInfo(fundFinancingBaseInfo.getOrganizationName(), "DK"));
//            } else if ("ZR".equalsIgnoreCase(type)) {
//                FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo = FundDirectFinancingId2Bean.get(base.getFinancingId());
//                if (Objects.isNull(fundDirectFinancingBaseInfo)) {
//                    continue;
//                }
//                if (!financingIdNameMap.containsKey(financingId)) {
//                    financingIdNameMap.putIfAbsent(financingId, new ArrayList<MonthlyCostInfo>());
//                }
//                financingIdNameMap.get(financingId).add(new MonthlyCostInfo(fundDirectFinancingBaseInfo.getProductName(), "ZR"));
//            }
//            financingIdList.add(financingId);
//        }
//
//        LambdaQueryWrapper<FundsDailyCost> wrapper = Wrappers.<FundsDailyCost>lambdaQuery()
//                .in(FundsDailyCost::getFinancingId, financingIdList)
//                .ne(FundsDailyCost::getItemText, BEGINNING_ITEM_TEXT)
//                .eq(FundsDailyCost::getDeleted, false)
////                .eq(FundsDailyCost::getIsConfirmed, false)
//                .eq(isNotNull(interestEndDate), FundsDailyCost::getInterestDate, interestEndDate)
//                .gt(FundsDailyCost::getFinancingAmount, 0)
//                .orderByDesc(FundsDailyCost::getInterestDate);
//        List<FundsDailyCost> fundsDailyCostList = fundsDailyCostService.list(wrapper);
//        if (fundsDailyCostList.isEmpty()) {
//            return new ArrayList<>();
//        }
//        Map<Long, List<FundsDailyCost>> fundMap = new HashMap<>();
//        Map<Long, List<FundsDailyCost>> fundDirectMap = new HashMap<>();
//        for (FundsDailyCost fundsDailyCost : fundsDailyCostList) {
//            if (fundsDailyCost.getType().equalsIgnoreCase("DK")) {
//                fundMap.putIfAbsent(fundsDailyCost.getFinancingId(), new ArrayList<>());
//                fundMap.get(fundsDailyCost.getFinancingId()).add(fundsDailyCost);
//            } else if (fundsDailyCost.getType().equalsIgnoreCase("ZR")) {
//                fundDirectMap.putIfAbsent(fundsDailyCost.getFinancingId(), new ArrayList<>());
//                fundDirectMap.get(fundsDailyCost.getFinancingId()).add(fundsDailyCost);
//            }
//        }
//        List<FundsDailyCost> res = new ArrayList<>();
//        for (Map.Entry<Long, List<FundsDailyCost>> entry : fundMap.entrySet()) {
//            List<FundsDailyCost> fundsDailyCosts = entry.getValue();
//            if (!fundsDailyCosts.isEmpty()) {
//                res.add(fundsDailyCosts.get(0));
//            }
//        }
//        for (Map.Entry<Long, List<FundsDailyCost>> entry : fundDirectMap.entrySet()) {
//            List<FundsDailyCost> fundsDailyCosts = entry.getValue();
//            if (!fundsDailyCosts.isEmpty()) {
//                res.add(fundsDailyCosts.get(0));
//            }
//        }
//
//        for (FundsDailyCost base : res) {
//            MonthlyCostRSP monthlyCostRSP = new MonthlyCostRSP();
//            monthlyCostRSP.setId(base.getId());
//            monthlyCostRSP.setYearAndMonth(formatter.format(base.getInterestDate()));
//            monthlyCostRSP.setBusinessType(base.getType());
//            monthlyCostRSP.setPropertyType(base.getPropertyType());
//            monthlyCostRSP.setFinancingId(base.getFinancingId());
//            List<MonthlyCostInfo> costInfoList = financingIdNameMap.get(base.getFinancingId());
//            if (costInfoList != null && !costInfoList.isEmpty()) {
//                for (MonthlyCostInfo monthlyCostInfo : costInfoList) {
//                    if (monthlyCostInfo.getType().equalsIgnoreCase(base.getType())) {
//                        monthlyCostRSP.setOrganizationName(monthlyCostInfo.getOrganizationName());
//                        break;
//                    }
//                }
//            }
//            if (StringUtils.isNotBlank(base.getPropertyType())) {
//                LeaseType leaseType = LeaseType.of(base.getPropertyType());
//                if (leaseType != null) {
//                    monthlyCostRSP.setPropertyTypeDisplay(ProjectBizType.ZL.display());
//                } else {
//                    ProjectBizType projectBizType = ProjectBizType.of(base.getPropertyType());
//                    if (projectBizType != null) {
//                        monthlyCostRSP.setPropertyTypeDisplay(projectBizType.display());
//                    }
//                }
//            }
//            if ("DK".equalsIgnoreCase(base.getType())) {
//                FundFinancingBaseInfo fundFinancingBaseInfo = fundFinancingId2Bean.get(base.getFinancingId());
//                monthlyCostRSP.setFinancingCode(fundFinancingBaseInfo.getFinancingCode());
//                monthlyCostRSP.setFinancingAmount(fundFinancingBaseInfo.getFinancingAmount());
//                monthlyCostRSP.setValueDate(fundFinancingBaseInfo.getActualLoanDate());
//                monthlyCostRSP.setLoanProperty(fundFinancingBaseInfo.getTimeLimitType());
//            } else if ("ZR".equalsIgnoreCase(base.getType())) {
//                FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo = FundDirectFinancingId2Bean.get(base.getFinancingId());
//                monthlyCostRSP.setFinancingCode(fundDirectFinancingBaseInfo.getFinancingCode());
//                monthlyCostRSP.setFinancingAmount(fundDirectFinancingBaseInfo.getFinancingAmount() * 10000);
//                monthlyCostRSP.setValueDate(fundDirectFinancingBaseInfo.getDurationFrom());
//                monthlyCostRSP.setLoanProperty(FundFinancingTimeLimitTypeEnum.BOND_PAYABLE.name());
//            }
//            monthlyCostRSP.setFinancingCost(base.getFinancingCost());
//            monthlyCostRSP.setTotalCapitalCost(base.getTotalCapitalCost());
//            monthlyCostRSP.setRemainingAmount(base.getFinancingAmount());
//            monthlyCostRSP.setFinancingRate(base.getFinancingRate());
//            monthlyCostRSP.setDailyRate(base.getDailyRate());
//            monthlyCostRSP.setIsConfirmed(base.getIsConfirmed());
//            monthlyCostRSP.setTotalCapitalCostAfterTax(base.getTotalCapitalCostAfterTax());
//            rspList.add(monthlyCostRSP);
//        }
//        return rspList;
//
//    }

//    @Deprecated
//    public List<MonthlyCostRSP> calculateDailyInterest(MonthlyCostREQ req, LocalDate startDate, LocalDate endDate) {
//
//        StopWatch st = new StopWatch();
//        st.start("准备");
//        Set<Long> financingIdList = new HashSet<>();
//        Map<Long, List<MonthlyCostInfo>> financingIdNameMap = new HashMap<>();
//        //存量
//        LambdaQueryWrapper<FundsDailyCost> query = Wrappers.lambdaQuery();
//        query.eq(FundsDailyCost::getItemText, BEGINNING_ITEM_TEXT);
//        List<FundsDailyCost> todoList = fundsDailyCostService.list(query);
//        if (CollectionUtil.isEmpty(todoList)) {
//            log.error("执行每日成本计息为空");
//            return Collections.emptyList();
//        }
//
//        //返回增量期初+存量期初
//        List<FundsDailyCost> beginRes = getIncrementFunds(todoList);
//        //获取间融的融资信息
//        List<Long> financingIds = beginRes.stream().filter(f -> "DK".equals(f.getType())).map(FundsDailyCost::getFinancingId).collect(Collectors.toList());
//        final List<FundFinancingBaseInfo> fundFinancingBaseInfos = financingBaseInfoService.listByIds(financingIds);
//        //key - 间融融资id ,value - 是否期初一次性收息 0-否 1-是
//        Map<Long, Integer> map = fundFinancingBaseInfos.stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, FundFinancingBaseInfo::getInitialInterestReceivedOnce, (a, b) -> a));
//        int availableProcessors = Runtime.getRuntime().availableProcessors();
//        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(availableProcessors);
//        st.stop();
//        st.start("建任务");
//        Map<Long, FundFinancingBaseInfo> fundFinancingId2Bean = financingBaseInfoService.listByIds(beginRes.stream().filter(e -> "DK".equalsIgnoreCase(e.getType())).map(FundsDailyCost::getFinancingId).collect(Collectors.toList())).stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, e -> e, (a, b) -> a));
//        Map<Long, FundDirectFinancingBaseInfo> FundDirectFinancingId2Bean = fundDirectFinancingBaseInfoService.listByIds(beginRes.stream().filter(e -> "ZR".equalsIgnoreCase(e.getType())).map(FundsDailyCost::getFinancingId).collect(Collectors.toList())).stream().collect(Collectors.toMap(FundDirectFinancingBaseInfo::getId, e -> e, (a, b) -> a));
//        List<Callable<Void>> tasks = new ArrayList<>();
//        //成本计提每日计算
//        for (FundsDailyCost fundsDailyCost : beginRes) {
//            //空的起息日判断
//            if (fundsDailyCost.getInterestDate() == null) {
//                continue;
//            }
//            //是否期初一次性收息=是 的融资，不参与计息计算。
//            if (Objects.nonNull(map.get(fundsDailyCost.getFinancingId())) && map.get(fundsDailyCost.getFinancingId()) == 1) {
//                continue;
//            }
//            Long financingId = fundsDailyCost.getFinancingId();
//            //成本计算最近一天
//            LocalDate lastCostDay = getLastCostday(financingId, fundsDailyCost.getType());
//            //只有期初那天记录-》前
//            if (lastCostDay == null
//                    && fundsDailyCost.getInterestDate().isBefore(startDate)
//                    && !fundsDailyCost.getInterestDate().plusDays(1).isEqual(startDate)) {
//                continue;
//            }
//            //只有期初那天记录-》后
//            if (fundsDailyCost.getInterestDate().isAfter(endDate)
//                    || fundsDailyCost.getInterestDate().isEqual(endDate)) {
//                continue;
//            }
//            //查询月份比最近成本计算月份晚2个月以上
//            if (lastCostDay != null
//                    && endDate.isAfter(lastCostDay)
//                    && ChronoUnit.MONTHS.between(lastCostDay, endDate) >= 2) {
//                continue;
//            }
//            //成本计算最近一天融资为0直接过滤
//            Long lastCostDayAmount = getLastCostDayAmount(financingId, fundsDailyCost.getType());
//            if (lastCostDayAmount <= 0 && lastCostDay != null) {
//                continue;
//            }
//
//            financingIdList.add(financingId);
//            try {
//                LocalDate interestStartDate = startDate;
//                String type = fundsDailyCost.getType();
//                // 判断是否已经有详情
//                if ("DK".equalsIgnoreCase(type)) {
//                    FundFinancingBaseInfo fundFinancingBaseInfo = fundFinancingId2Bean.get(fundsDailyCost.getFinancingId());
//                    if (Objects.isNull(fundFinancingBaseInfo)) {
//                        continue;
//                    }
//                    List<FundOrganization> organizationList = organizationService.getByFinancingId(fundFinancingBaseInfo.getId());
//                    if (!financingIdNameMap.containsKey(financingId)) {
//                        financingIdNameMap.putIfAbsent(financingId, new ArrayList<MonthlyCostInfo>());
//                    }
//                    financingIdNameMap.get(financingId).add(new MonthlyCostInfo(Optional.ofNullable(organizationList.get(0)).map(FundOrganization::getOrganizationName).orElse(null), "DK"));
//                    tasks.add(new ApiCallerFundCalTask(fundFinancingBaseInfo, interestStartDate, endDate));
//                } else if ("ZR".equalsIgnoreCase(type)) {
//                    FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo = FundDirectFinancingId2Bean.get(fundsDailyCost.getFinancingId());
//                    if (Objects.isNull(fundDirectFinancingBaseInfo)) {
//                        continue;
//                    }
//                    if (!financingIdNameMap.containsKey(financingId)) {
//                        financingIdNameMap.putIfAbsent(financingId, new ArrayList<MonthlyCostInfo>());
//                    }
//                    financingIdNameMap.get(financingId).add(new MonthlyCostInfo(fundDirectFinancingBaseInfo.getProductName(), "ZR"));
//                    tasks.add(new ApiCallerDirectFundCalTask(fundDirectFinancingBaseInfo, interestStartDate, endDate));
//                }
//            } catch (Exception e) {
//                log.error("执行每日成本计息发生异常[ftpInterestId:{}]", financingId, e);
//            }
//        }
//        st.stop();
//        st.start("执行任务");
//        try {
//            List<Future<Void>> results = fixedThreadPool.invokeAll(tasks);
//            for (Future<Void> result : results) {
//                result.get();
//            }
//            fixedThreadPool.shutdown();
//            while (true){
//                if (fixedThreadPool.isTerminated()){
//                    break;
//                }
//            }
//        } catch (InterruptedException | ExecutionException e) {
//            Thread.currentThread().interrupt();
//            log.error("执行每日成本计息发生异常", e);
//        }
//        st.stop();
//        st.start("查询");
//        LambdaQueryWrapper<FundsDailyCost> wrapper = Wrappers.<FundsDailyCost>lambdaQuery()
//                .in(FundsDailyCost::getFinancingId, financingIdList)
//                .eq(FundsDailyCost::getInterestDate, endDate)
//                .ne(FundsDailyCost::getItemText, BEGINNING_ITEM_TEXT)
//                .eq(req.getBatchNumber() != null, FundsDailyCost::getConfirmBatch, req.getBatchNumber())
//                .gt(FundsDailyCost::getFinancingAmount, 0)
//                .orderByDesc(FundsDailyCost::getType);
//        if (!req.isInterestPay()) {
//            wrapper.eq(FundsDailyCost::getIsConfirmed, ObjectUtil.isNotEmpty(req.getIsConfirmed()) ? req.getIsConfirmed() : YesOrNoNumberEnum.NO.getCode());
//        }
//        //返回每个融资id对应的月底最后一天成本计提
//        //com.baomidou.mybatisplus.extension.plugins.pagination.Page<FundsDailyCost> dataPage = fundsDailyCostService.getBaseMapper().selectPage(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(req.getPage(), req.getPageSize()), wrapper);
//        List<FundsDailyCost> fundsDailyCostList = fundsDailyCostService.list(wrapper);
//        List<MonthlyCostRSP> rspList = new ArrayList<>();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
//        st.stop();
//        st.start("循环");
//        fundsDailyCostList.forEach(base -> {
//            MonthlyCostRSP monthlyCostRSP = new MonthlyCostRSP();
//            monthlyCostRSP.setId(base.getId());
//            monthlyCostRSP.setYearAndMonth(formatter.format(base.getInterestDate()));
//            monthlyCostRSP.setBusinessType(base.getType());
//            monthlyCostRSP.setPropertyType(base.getPropertyType());
//            monthlyCostRSP.setFinancingId(base.getFinancingId());
//            List<MonthlyCostInfo> costInfoList = financingIdNameMap.get(base.getFinancingId());
//            if (costInfoList != null && !costInfoList.isEmpty()) {
//                for (MonthlyCostInfo monthlyCostInfo : costInfoList) {
//                    if (monthlyCostInfo.getType().equalsIgnoreCase(base.getType())) {
//                        monthlyCostRSP.setOrganizationName(monthlyCostInfo.getOrganizationName());
//                        break;
//                    }
//                }
//            }
//            if (StringUtils.isNotBlank(base.getPropertyType())) {
//                LeaseType leaseType = LeaseType.of(base.getPropertyType());
//                if (leaseType != null) {
//                    monthlyCostRSP.setPropertyTypeDisplay(ProjectBizType.ZL.display());
//                } else {
//                    ProjectBizType projectBizType = ProjectBizType.of(base.getPropertyType());
//                    if (projectBizType != null) {
//                        monthlyCostRSP.setPropertyTypeDisplay(projectBizType.display());
//                    }
//                }
//            }
//            if ("DK".equalsIgnoreCase(base.getType())) {
//                FundFinancingBaseInfo fundFinancingBaseInfo = fundFinancingId2Bean.get(base.getFinancingId());
//                monthlyCostRSP.setFinancingCode(fundFinancingBaseInfo.getFinancingCode());
//                monthlyCostRSP.setFinancingAmount(fundFinancingBaseInfo.getFinancingAmount());
//                monthlyCostRSP.setValueDate(fundFinancingBaseInfo.getActualLoanDate());
//                monthlyCostRSP.setLoanProperty(fundFinancingBaseInfo.getTimeLimitType());
//            } else if ("ZR".equalsIgnoreCase(base.getType())) {
//                FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo = FundDirectFinancingId2Bean.get(base.getFinancingId());
//                monthlyCostRSP.setFinancingCode(fundDirectFinancingBaseInfo.getFinancingCode());
//                monthlyCostRSP.setFinancingAmount(fundDirectFinancingBaseInfo.getFinancingAmount() * 10000);
//                monthlyCostRSP.setValueDate(fundDirectFinancingBaseInfo.getDurationFrom());
//                monthlyCostRSP.setLoanProperty(FundFinancingTimeLimitTypeEnum.BOND_PAYABLE.name());
//            }
//            monthlyCostRSP.setFinancingCost(base.getFinancingCost());
//            monthlyCostRSP.setTotalCapitalCost(base.getTotalCapitalCost());
//            monthlyCostRSP.setRemainingAmount(base.getFinancingAmount());
//            monthlyCostRSP.setFinancingRate(base.getFinancingRate());
//            monthlyCostRSP.setDailyRate(base.getDailyRate());
//            monthlyCostRSP.setIsConfirmed(base.getIsConfirmed());
//            monthlyCostRSP.setTotalCapitalCostAfterTax(base.getTotalCapitalCostAfterTax());
//            rspList.add(monthlyCostRSP);
//        });
//        st.stop();
//        st.start("最后");
////        if (!req.isInterestPay()) {
////            List<Long> ids = null;
////            if (fundsDailyCostList.size() > rspList.size()) {
////                List<FundsDailyCost> list1 = fundsDailyCostService.list(wrapper);
////                ids = list1.stream().map(FundsDailyCost::getId).collect(Collectors.toList());
////            } else {
////                ids = rspList.stream().map(MonthlyCostRSP::getId).collect(Collectors.toList());
////            }
////            record(MonthlyModuleTypeEnum.COST.name(), ids);
////            st.stop();
////            log.info(st.prettyPrint(TimeUnit.SECONDS));
////        }
//        return rspList;
//    }

    @Transactional(rollbackFor = Throwable.class)
    public List<MonthlyCostRSP> calculateDailyInterest2(MonthlyCostREQ req) {
        LocalDate targetDate = LocalDateTimeUtil.parseDate(req.getYearAndMonth(), DatePattern.NORM_MONTH_PATTERN);
        LocalDate interestStartDate = LocalDate.of(targetDate.getYear(), targetDate.getMonthValue(), 1);
        LocalDate interestEndDate = LocalDate.of(interestStartDate.getYear(), interestStartDate.getMonthValue(), interestStartDate.lengthOfMonth());
        List<FundsDailyCostMain> todoList = SpringUtil.getBean(FundsDailyCostMainService.class).listNotFinish();
        if (Objects.nonNull(req.getFinancingId())) {
            // 取对应数据
            todoList.removeIf(e -> !Objects.equals(e.getFinancingId(), req.getFinancingId()) || !Objects.equals(e.getFinancingType(), req.getFinancingType()));
        }
        Map<Long, List<MonthlyCostInfo>> financingIdNameMap = new HashMap<>();
        List<FundsDailyCost> zrFundsDailyCost = new ArrayList<>();
        List<FundsDailyCost> dkFundsDailyCost = new ArrayList<>();
        List<FundsDailyCost> addDailyCost = new ArrayList<>();
        List<FundsDailyCost> updateDailyCost = new ArrayList<>();
        Map<Long, FundFinancingBaseInfo> dkFinancingId2Bean = new HashMap<>();
        Map<Long, FundDirectFinancingBaseInfo> zrFinancingId2Bean = new HashMap<>();
        //存量
        // FIXME 数据量会随着融资数量增多而增多，需要考虑数据量变大时该怎么处理
        LambdaQueryWrapper<FundsDailyCost> query = Wrappers.lambdaQuery();
        query.ne(FundsDailyCost::getItemText, BEGINNING_ITEM_TEXT);
        query.ge(FundsDailyCost::getInterestDate, interestStartDate);
        query.le(FundsDailyCost::getInterestDate, interestEndDate);
        query.eq(ObjectUtil.isNotEmpty(req.getFinancingId()), FundsDailyCost::getFinancingId, req.getFinancingId());
        query.eq(ObjectUtil.isNotEmpty(req.getFinancingType()), FundsDailyCost::getType, req.getFinancingType());
        List<FundsDailyCost> oldFundsDailyCost = fundsDailyCostService.list(query);

//        //获取zr数据
//        zrFundsDailyCost = oldFundsDailyCost.stream().filter(e -> ObjectUtil.equals(e.getType(), ZR)).collect(Collectors.toList());
//        dkFundsDailyCost = oldFundsDailyCost.stream().filter(e -> ObjectUtil.equals(e.getType(), DK)).collect(Collectors.toList());
//        if(!justUpdate && CollectionUtil.isEmpty(oldFundsDailyCost)) {
//            FundsDailyCost fundsDailyCost = new FundsDailyCost();
//            fundsDailyCost.setFinancingId(req.getFinancingId());
//            fundsDailyCost.setType(req.getFinancingType());
//            //新增
//            if (ZR.equals(req.getFinancingType())) {
//                zrFundsDailyCost.add(fundsDailyCost);
//            } else {
//                dkFundsDailyCost.add(fundsDailyCost);
//            }
//        }
        if (CollectionUtil.isNotEmpty(oldFundsDailyCost)) {
            //获取zr数据
            zrFundsDailyCost = oldFundsDailyCost.stream().filter(e -> ObjectUtil.equals(e.getType(), ZR)).collect(Collectors.toList());
            dkFundsDailyCost = oldFundsDailyCost.stream().filter(e -> ObjectUtil.equals(e.getType(), DK)).collect(Collectors.toList());
            //financingIdNameMap.putAll(oldFundsDailyCost.stream().collect(Collectors.groupingBy(FundsDailyCost::getFinancingId)));
        }
//        if(justUpdate && CollectionUtil.isEmpty(oldFundsDailyCost)) {
//            FundsDailyCost fundsDailyCost = new FundsDailyCost();
//            fundsDailyCost.setFinancingId(req.getFinancingId());
//            fundsDailyCost.setType(req.getFinancingType());
//            //新增
//            if (ZR.equals(req.getFinancingType())) {
//                zrFundsDailyCost.add(fundsDailyCost);
//            } else {
//                dkFundsDailyCost.add(fundsDailyCost);
//            }
//        }
        Set<Long> directFinancingIds = todoList.stream().filter(e -> StrUtil.equals(e.getFinancingType(), ZR)).map(FundsDailyCostMain::getFinancingId).collect(Collectors.toSet());
        //处理直融
//        if (CollectionUtil.isNotEmpty(zrFundsDailyCost)) {
        if (CollectionUtil.isNotEmpty(directFinancingIds)) {
            Map<Long, List<FundsDailyCost>> oldFinancingKey2DailyCostMap = zrFundsDailyCost.stream().collect(Collectors.groupingBy(FundsDailyCost::getFinancingId));
            //获取融资对应的每天还款本金
            zrFinancingId2Bean.putAll(fundDirectFinancingBaseInfoService.listByIds(directFinancingIds).stream().collect(Collectors.toMap(FundDirectFinancingBaseInfo::getId, e -> e, (a, b) -> a)));
            //获取所有产品
            Map<Long, List<FundDirectFinancingProductDetail>> financingId2ProductDetails = fundDirectFinancingProductDetailService.listByBatchFinancingId(directFinancingIds).stream().collect(Collectors.groupingBy(FundDirectFinancingProductDetail::getFinancingId));

            // 由主表确定明细数据的资产类型
            Map<Long, FundsDailyCostMain> zrMainMap = todoList.stream().filter(e -> StrUtil.equals(e.getFinancingType(), "ZR")).collect(Collectors.toMap(FundsDailyCostMain::getFinancingId, e -> e, (a, b) -> b));
//            //取关联合同
//            Map<Long, List<FundDirectFinancingPledgeInfo>> mapByFinancings = fundDirectFinancingPledgeInfoService.getMapByFinancings(directFinancingIds);
//            Map<Long, String> financing2PropertyTypeMap = new HashMap<>();
            Map<Long, List<FundReceiptFlowDetail>> financingRepayDetail = fundReceiptRepayBaseInfoService.getBatchFinancingRepayDetail(directFinancingIds, FinancingTypeEnum.DIRECT.name(), FinanceCashFlowItemEnum.REPAY.name());
//
//            mapByFinancings.forEach((fid, pledgeInfos) -> {
//                if (CollectionUtil.isEmpty(pledgeInfos)) {
//                    return;
//                }
//                String s = null;
//                for(FundDirectFinancingPledgeInfo pledgeInfo : pledgeInfos) {
//                    if(ObjectUtil.isEmpty(pledgeInfo)) {
//                        continue;
//                    }
//                    if (s == null && ObjectUtil.isNotEmpty(pledgeInfo.getBizType())) {
//                        s = pledgeInfo.getBizType();
//                    }
//                    if(s!= null && ObjectUtil.isNotEmpty(pledgeInfo.getBizType()) && ObjectUtil.notEqual(s, pledgeInfo.getBizType())) {
//                        s = null;
//                    }
//                    financing2PropertyTypeMap.put(fid, s);
//                }
//            });

            directFinancingIds.forEach(financingId -> {
                FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo = zrFinancingId2Bean.get(financingId);
                if (StrUtil.equalsAny(fundDirectFinancingBaseInfo.getDirectFinancingType(), DirectFinancingType.ABN.name(), DirectFinancingType.ABS.name())) {
                    List<FundDirectFinancingProductDetail> financingProductDetails = financingId2ProductDetails.get(financingId);
                    //走abs
                    financingProductDetails.forEach(productDetail -> {
                        //获取日期 todo 需优化 目前数据不多
                        List<FundDirectFinancingRepayActualSplit> repayActualSplitList = fundDirectFinancingRepayActualSplitService.listByProductDetailId(productDetail.getId());
                        FundDirectFinancingBaseInfo financingBaseInfo = zrFinancingId2Bean.get(productDetail.getFinancingId());
                        List<FundsDailyCost> fundsDailyCosts = oldFinancingKey2DailyCostMap.get(productDetail.getFinancingId());
                        Map<LocalDate, Long> data2OldId = new HashMap<>();
                        if(CollectionUtil.isNotEmpty(fundsDailyCosts)) {
                            data2OldId.putAll(fundsDailyCosts.stream().filter(e -> Objects.equals(e.getFinancingProductId(), productDetail.getId())).filter(e -> ObjectUtil.isNotEmpty(e.getInterestDate())).collect(Collectors.toMap(FundsDailyCost::getInterestDate, FundsDailyCost::getId, (a, b) -> a)));
                        }
                        if (ObjectUtil.isEmpty(financingBaseInfo) || ObjectUtil.isEmpty(financingBaseInfo.getAverageCouponRate()) || CollectionUtil.isEmpty(repayActualSplitList)) {
                            return;
                        }
                        //构建明细
                        FundsDailyCost needFundsDailyCost = buildABSDirectFundsDailyCost(productDetail, repayActualSplitList, targetDate, zrMainMap.get(productDetail.getFinancingId()));
                        if (Objects.nonNull(needFundsDailyCost)) {
                            Long aLong = data2OldId.get(interestEndDate);
                            if (ObjectUtil.isNotEmpty(aLong)) {
                                needFundsDailyCost.setId(aLong);
                                updateDailyCost.add(needFundsDailyCost);
                            } else {
                                addDailyCost.add(needFundsDailyCost);
                            }
                        }
                    });
                } else {
                    //非abs/abn走收付款管理
                    List<FundReceiptFlowDetail> fundReceiptFlowDetails = financingRepayDetail.get(financingId);
                    List<FundsDailyCost> oldFundsDailyCosts = oldFinancingKey2DailyCostMap.get(financingId);
                    Map<LocalDate, Long> data2OldId = new HashMap<>();
                    if(CollectionUtil.isNotEmpty(oldFundsDailyCosts)) {
                        data2OldId.putAll(oldFundsDailyCosts.stream().filter(x -> ObjectUtil.isNotEmpty(x.getInterestDate())).collect(Collectors.toMap(FundsDailyCost::getInterestDate, FundsDailyCost::getId, (a, b) -> b)));
                    }
                    List<FundDirectFinancingRepayActual> repayActualList = SpringUtil.getBean(FundDirectFinancingRepayActualService.class).listByFinancingId(financingId);
                    repayActualList.sort(Comparator.comparing(FundDirectFinancingRepayActual::getRepayDate).reversed());
                    FundsDailyCost fundsDailyCost = buildFundsDailyCost(targetDate, LongUtil.other2Long(LongUtil.null2zero(fundDirectFinancingBaseInfo.getFinancingAmount()).toString()), fundReceiptFlowDetails, zrMainMap.get(financingId));
                    if (Objects.nonNull(fundsDailyCost)) {
                        Long aLong = data2OldId.get(interestEndDate);
                        if (ObjectUtil.isNotEmpty(aLong)) {
                            fundsDailyCost.setId(aLong);
                            updateDailyCost.add(fundsDailyCost);
                        } else {
                            addDailyCost.add(fundsDailyCost);
                        }
                    }
                }
            });

        }
        //处理间融
        Set<Long> indirectFinancingIds = todoList.stream().filter(e -> StrUtil.equals(e.getFinancingType(), DK)).map(FundsDailyCostMain::getFinancingId).collect(Collectors.toSet());
//        if(ObjectUtil.isNotEmpty(dkFundsDailyCost)) {
        if(ObjectUtil.isNotEmpty(indirectFinancingIds)) {
            //获取融资对应的每天还款本金
            Map<Long, List<FundsDailyCost>> oldFinancingKey2DailyCostMap = dkFundsDailyCost.stream().collect(Collectors.groupingBy(FundsDailyCost::getFinancingId));
            List<FundFinancingBaseInfo> financingBaseInfoList = financingBaseInfoService.listByIds(indirectFinancingIds);
            dkFinancingId2Bean.putAll(financingBaseInfoList.stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, e -> e, (a, b) -> b)));
            Map<Long, FundFinancingPlan> financingId2Plan = fundFinancingPlanService.getMapByFinancingIds(indirectFinancingIds);
            Map<Long, List<FundReceiptFlowDetail>> financingRepayDetail = fundReceiptRepayBaseInfoService.getBatchFinancingRepayDetail(indirectFinancingIds, FinancingTypeEnum.INDIRECT.name(), FinanceCashFlowItemEnum.REPAY.name());

            // 由主表确定明细数据的资产类型
            Map<Long, FundsDailyCostMain> dkMainMap = todoList.stream().filter(e -> StrUtil.equals(e.getFinancingType(), "DK")).collect(Collectors.toMap(FundsDailyCostMain::getFinancingId, e -> e, (a, b) -> b));
//            //取关联合同
//            Map<Long, List<FundFinancingPledgeInfo>> mapByFinancings = fundFinancingPledgeInfoService.getMapByFinancings(indirectFinancingIds);
//            Map<Long, String> financing2PropertyTypeMap = new HashMap<>();
//            mapByFinancings.forEach((fid, pledgeInfos) -> {
//                if (CollectionUtil.isEmpty(pledgeInfos)) {
//                    return;
//                }
//                String s = null;
//                for(FundFinancingPledgeInfo pledgeInfo : pledgeInfos) {
//                    if(ObjectUtil.isEmpty(pledgeInfo)) {
//                        continue;
//                    }
//                    if (s == null && ObjectUtil.isNotEmpty(pledgeInfo.getBizType())) {
//                        s = pledgeInfo.getBizType();
//                    }
//                    if(s!= null && ObjectUtil.isNotEmpty(pledgeInfo.getBizType()) && ObjectUtil.notEqual(s, pledgeInfo.getBizType())) {
//                        s = null;
//                    }
//                }
//                financing2PropertyTypeMap.put(fid, s);
//            });

            financingBaseInfoList.forEach(e -> {
                FundFinancingPlan fundFinancingPlan = financingId2Plan.get(e.getId());
                List<FundReceiptFlowDetail> fundReceiptFlowDetails = financingRepayDetail.get(e.getId());
                List<FundsDailyCost> oldFundsDailyCosts = oldFinancingKey2DailyCostMap.get(e.getId());
                Map<LocalDate, Long> data2OldId = new HashMap<>();
                if(CollectionUtil.isNotEmpty(oldFundsDailyCosts)) {
                    data2OldId.putAll(oldFundsDailyCosts.stream().filter(x -> ObjectUtil.isNotEmpty(x.getInterestDate())).collect(Collectors.toMap(FundsDailyCost::getInterestDate, FundsDailyCost::getId)));
                }
                List<FundFinancingRepayActual> repayActualList = SpringUtil.getBean(FundFinancingRepayActualService.class).listByFinancingId(e.getId());
                repayActualList.sort(Comparator.comparing(FundFinancingRepayActual::getRepayDate).reversed());
                FundsDailyCost fundsDailyCost = buildFundsDailyCost(targetDate, fundFinancingPlan.getFinancingAmount(), fundReceiptFlowDetails, dkMainMap.get(e.getId()));
                if (Objects.nonNull(fundsDailyCost)) {
                    Long aLong = data2OldId.get(interestEndDate);
                    if (ObjectUtil.isNotEmpty(aLong)) {
                        fundsDailyCost.setId(aLong);
                        updateDailyCost.add(fundsDailyCost);
                    } else {
                        addDailyCost.add(fundsDailyCost);
                    }
                }
            });
        }

        //保存
        if (CollectionUtil.isNotEmpty(addDailyCost)) {
            fundsDailyCostService.saveBatch(addDailyCost);
        }
        if (CollectionUtil.isNotEmpty(updateDailyCost)) {
            fundsDailyCostService.updateBatchById(updateDailyCost);
        }
        //刷新主表冗余数据
        SpringUtil.getBean(FundsDailyCostMainService.class).refreshTotalCapitalCost(targetDate);
        //原返回
        List<MonthlyCostRSP> rspList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        addDailyCost.addAll(updateDailyCost);
        addDailyCost.forEach(base -> {
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
                FundFinancingBaseInfo fundFinancingBaseInfo = dkFinancingId2Bean.get(base.getFinancingId());
                monthlyCostRSP.setFinancingCode(fundFinancingBaseInfo.getFinancingCode());
                monthlyCostRSP.setFinancingAmount(fundFinancingBaseInfo.getFinancingAmount());
                monthlyCostRSP.setValueDate(fundFinancingBaseInfo.getActualLoanDate());
                monthlyCostRSP.setLoanProperty(fundFinancingBaseInfo.getTimeLimitType());
            } else if ("ZR".equalsIgnoreCase(base.getType())) {
                FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo = zrFinancingId2Bean.get(base.getFinancingId());
                monthlyCostRSP.setFinancingCode(fundDirectFinancingBaseInfo.getFinancingCode());
                monthlyCostRSP.setFinancingAmount(fundDirectFinancingBaseInfo.getFinancingAmount());
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
        });
        return rspList;
    }

    //构建间融数据明细
    private FundsDailyCost buildFundsDailyCost(LocalDate targetDate, Long financingAmount, List<FundReceiptFlowDetail> financingRepayDetail, FundsDailyCostMain fundsDailyCostMain) {
        LocalDate beganDate = LocalDate.of(targetDate.getYear(), targetDate.getMonthValue(), 1);
        LocalDate endDate = LocalDate.of(targetDate.getYear(), targetDate.getMonthValue(), beganDate.lengthOfMonth());
        //获取区间内日期
        List<LocalDate> daysBetween = getDaysBetween(fundsDailyCostMain.getCarryInterestDate(), endDate);
        // 获取核销本金
        Map<LocalDate, Long> everyDayWriteOffPrincipalMap = getEveryDayWriteAmount(financingRepayDetail, daysBetween, BJ, fundsDailyCostMain);
        // 获取核销利息
        Map<LocalDate, Long> everyDayWriteOffInterestMap = getEveryDayWriteAmount(financingRepayDetail, daysBetween, LX, fundsDailyCostMain);
        //获取剩余本金
        Map<LocalDate, Long> everyDayRemainingPrincipal = getEveryDayRemainingPrincipal(financingRepayDetail, daysBetween, financingAmount, everyDayWriteOffPrincipalMap);
        //获取日利率
        BigDecimal dayFtpUpper = calculateFinancingRateDaily(Math.toIntExact(fundsDailyCostMain.getFinancingRate()));
        BigDecimal dayFtp = dayFtpUpper.divide(new BigDecimal("1000000"), 20, RoundingMode.HALF_UP);
        BigDecimal taxRate = BigDecimal.valueOf(0.06);
        // 当期计提利息
        BigDecimal financingCostBD = BigDecimal.ZERO;
        // 期末融资余额
        long financingBalanceAmount = 0L;
        // 当期实际还款本金
        long principalAmount = 0L;
        // 当期实际核销利息
        long interestAmount = 0L;
        // 按天计提
        LocalDate interestDate = beganDate;
        while (!interestDate.isAfter(endDate)) {
            financingBalanceAmount = Optional.ofNullable(everyDayRemainingPrincipal.get(interestDate)).orElse(0L);
            principalAmount = principalAmount + Optional.ofNullable(everyDayWriteOffPrincipalMap.get(interestDate)).orElse(0L);
            interestAmount = interestAmount + Optional.ofNullable(everyDayWriteOffInterestMap.get(interestDate)).orElse(0L);
            //本日剩余本金（元）*FTP收益日利率（%）
            financingCostBD = financingCostBD.add(BigDecimal.valueOf(financingBalanceAmount).multiply(dayFtp));
            interestDate = interestDate.plusDays(1);
        }
        if (financingBalanceAmount <= 0 && principalAmount <= 0 && interestAmount <= 0) {
            return null;
        }
        FundsDailyCost merge = new FundsDailyCost();
        merge.setMainId(fundsDailyCostMain.getId());
        merge.setFinancingId(fundsDailyCostMain.getFinancingId());
        merge.setInterestDate(LocalDate.of(targetDate.getYear(), targetDate.getMonthValue(), targetDate.lengthOfMonth()));
        merge.setItemText(merge.getInterestDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        merge.setFinancingAmount(Optional.ofNullable(everyDayRemainingPrincipal.get(endDate)).orElse(0L));
        merge.setPrincipleAmount(principalAmount);
        merge.setInterestAmount(interestAmount);
        merge.setFinancingRate(fundsDailyCostMain.getFinancingRate());
        merge.setDailyRate(dayFtpUpper.intValue());
        merge.setFinancingCost(financingCostBD.longValue());
        // 计算本年累计
        Long totalCostThisYear = fundsDailyCostService.getBaseMapper().sumCostByMainId(fundsDailyCostMain.getId(), LocalDate.of(targetDate.getYear(), 1, 1), beganDate.minusDays(1));
        merge.setTotalCapitalCost(Optional.ofNullable(totalCostThisYear).orElse(0L) + merge.getFinancingCost());
        merge.setType(fundsDailyCostMain.getFinancingType());
        merge.setIsConfirmed(YesOrNoNumberEnum.NO.getCode());
        // 间融-质押资产为保理 没有销项抵减，所以税率=0
        // 间融-质押资产为直租：13%
        // 其他：6%
        if (StrUtil.isNotBlank(fundsDailyCostMain.getLeaseType())) {
            if (StrUtil.equals(fundsDailyCostMain.getLeaseType(), LeaseType.zhi_zu.name())) {
                taxRate = BigDecimal.valueOf(0.13);
            }
            merge.setPropertyType(fundsDailyCostMain.getLeaseType());
        } else {
            if (StrUtil.equals(fundsDailyCostMain.getBizType(), ProjectBizType.BL.name())) {
                taxRate = BigDecimal.ZERO;
            }
            merge.setPropertyType(fundsDailyCostMain.getBizType());
        }
        // 计算税后金额
        merge.setTaxRate(taxRate);
        merge.setFinancingCostAfterTax(FinancialUtil.calculateAmountWithoutTax(merge.getFinancingCost(), taxRate).longValue());
        merge.setTotalCapitalCostAfterTax(FinancialUtil.calculateAmountWithoutTax(merge.getTotalCapitalCost(), taxRate).longValue());
        // 填充利息余额
        this.fillInterestBalance(fundsDailyCostMain, merge, everyDayWriteOffInterestMap, targetDate);
        // 填充钆差金额
        this.fillCostDiff(fundsDailyCostMain, merge, false, targetDate);
        return merge;
    }

    //获取间融每天的剩余本金
    private Map<LocalDate, Long> getEveryDayWriteAmount(List<FundReceiptFlowDetail> financingRepayDetail, List<LocalDate> everyDay, String subject, FundsDailyCostMain fundsDailyCostMain) {
        if (ObjectUtil.isEmpty(everyDay) || CollectionUtil.isEmpty(financingRepayDetail)) {
            return MapUtil.empty();
        }
        Map<LocalDate, Long> day2WriteOffAmountMap = new HashMap<>();
        if (Objects.equals(fundsDailyCostMain.getIsSameBiz(), YesOrNoNumberEnum.YES.getCode()) && StrUtil.equals(fundsDailyCostMain.getFinancingType(), DK)) {
            // 同业融资需要按照计划还款日期计算，且同业融资一定是间接融资
            Map<String, List<FundReceiptFlowDetail>> cashFlowCode2DetailMap = financingRepayDetail.stream().collect(Collectors.groupingBy(FundReceiptFlowDetail::getCashFlowCode));
            // 查询还款计划
            List<FundFinancingRepayActual> repayActualList = SpringUtil.getBean(FundFinancingRepayActualService.class).listByFinancingId(fundsDailyCostMain.getFinancingId());
            Map<LocalDate, List<FundFinancingRepayActual>> map = repayActualList.stream().collect(Collectors.groupingBy(FundFinancingRepayActual::getRepayDate));
            for (Map.Entry<LocalDate, List<FundFinancingRepayActual>> entry : map.entrySet()) {
                LocalDate day = entry.getKey();
                List<FundReceiptFlowDetail> detailList = new LinkedList<>();
                for (FundFinancingRepayActual repayActual : entry.getValue()) {
                    if (CollectionUtil.isNotEmpty(cashFlowCode2DetailMap.get(repayActual.getCashFlowCode()))) {
                        detailList.addAll(cashFlowCode2DetailMap.get(repayActual.getCashFlowCode()));
                    }
                }
                if (ObjectUtil.equals(BJ, subject)) {
                    day2WriteOffAmountMap.put(day, LongUtil.null2zero(day2WriteOffAmountMap.get(day)) + LongUtil.null2zero(detailList.stream().map(FundReceiptFlowDetail::getPrincipalAmount).filter(ObjectUtil::isNotEmpty).reduce(Long::sum).orElse(0L)));
                } else if (ObjectUtil.equals(LX, subject)) {
                    day2WriteOffAmountMap.put(day, LongUtil.null2zero(day2WriteOffAmountMap.get(day)) + LongUtil.null2zero(detailList.stream().map(FundReceiptFlowDetail::getInterestAmount).filter(ObjectUtil::isNotEmpty).reduce(Long::sum).orElse(0L)));
                } else {
                    day2WriteOffAmountMap.put(day, LongUtil.null2zero(day2WriteOffAmountMap.get(day)) + LongUtil.null2zero(detailList.stream().map(FundReceiptFlowDetail::getTotalAmount).filter(ObjectUtil::isNotEmpty).reduce(Long::sum).orElse(0L)));
                }
            }
        } else {
            // 非同业融资按照实际还款日期计算
            Map<LocalDate, List<FundReceiptFlowDetail>> date2ReceiptFlowDetail = financingRepayDetail.stream().filter(e -> ObjectUtil.isNotEmpty(e.getCashFlowDate())).collect(Collectors.groupingBy(FundReceiptFlowDetail::getCashFlowDate));
            for (LocalDate day : everyDay) {
                List<FundReceiptFlowDetail> fundReceiptFlowDetails = date2ReceiptFlowDetail.get(day);
                if (ObjectUtil.isNotEmpty(fundReceiptFlowDetails)) {
                    if (ObjectUtil.equals(BJ, subject)) {
                        day2WriteOffAmountMap.put(day, LongUtil.null2zero(day2WriteOffAmountMap.get(day)) + LongUtil.null2zero(fundReceiptFlowDetails.stream().map(FundReceiptFlowDetail::getPrincipalAmount).filter(ObjectUtil::isNotEmpty).reduce(Long::sum).orElse(0L)));
                    } else if (ObjectUtil.equals(LX, subject)) {
                        day2WriteOffAmountMap.put(day, LongUtil.null2zero(day2WriteOffAmountMap.get(day)) + LongUtil.null2zero(fundReceiptFlowDetails.stream().map(FundReceiptFlowDetail::getInterestAmount).filter(ObjectUtil::isNotEmpty).reduce(Long::sum).orElse(0L)));
                    } else {
                        day2WriteOffAmountMap.put(day, LongUtil.null2zero(day2WriteOffAmountMap.get(day)) + LongUtil.null2zero(fundReceiptFlowDetails.stream().map(FundReceiptFlowDetail::getTotalAmount).filter(ObjectUtil::isNotEmpty).reduce(Long::sum).orElse(0L)));
                    }
                }
            }
        }
        return day2WriteOffAmountMap;
    }

    //获取直融每天的剩余本金
    private Map<LocalDate, Long> getEveryDayRemainingPrincipal(List<FundReceiptFlowDetail> financingRepayDetail, List<LocalDate> everyDay, Long financingAmount, Map<LocalDate, Long> date2ReceiptFlowDetail) {
        if (ObjectUtil.isEmpty(everyDay) || ObjectUtil.isEmpty(financingAmount) || financingAmount <= 0) {
            return MapUtil.empty();
        }
        Map<LocalDate, Long> day2RemainingPrincipal = new HashMap<>();
        for (LocalDate day : everyDay) {
            financingAmount = financingAmount - LongUtil.null2zero(date2ReceiptFlowDetail.get(day));
            day2RemainingPrincipal.put(day, financingAmount);
        }
        return day2RemainingPrincipal;
    }

    //构建直融数据明细
    private FundsDailyCost buildABSDirectFundsDailyCost(FundDirectFinancingProductDetail financingProductDetail, List<FundDirectFinancingRepayActualSplit> repayActualSplitList, LocalDate targetDate, FundsDailyCostMain fundsDailyCostMain) {
        LocalDate beganDate = LocalDate.of(targetDate.getYear(), targetDate.getMonthValue(), 1);
        LocalDate endDate = LocalDate.of(targetDate.getYear(), targetDate.getMonthValue(), targetDate.lengthOfMonth());
        //获取区间内日期
        List<LocalDate> daysBetween = getDaysBetween(financingProductDetail.getValueDate(), endDate);
        // 获取相关数据备用
        repayActualSplitList.sort(Comparator.comparing(FundDirectFinancingRepayActualSplit::getPhase));
        Map<LocalDate, Long> day2PlanWriteOffPrincipal = repayActualSplitList.stream().filter(e -> Objects.nonNull(e.getPrincipalAmount())).collect(Collectors.toMap(FundDirectFinancingRepayActualSplit::getRepayDate, FundDirectFinancingRepayActualSplit::getPrincipalAmount, (a,b) -> a));
        List<FundDirectFinancingRepayActualSplitRecord> productRepayDetail = fundDirectFinancingRepayActualSplitRecordService.list(Wrappers.<FundDirectFinancingRepayActualSplitRecord>lambdaQuery().eq(FundDirectFinancingRepayActualSplitRecord::getProductDetailId, financingProductDetail.getId()));
        Map<String, LocalDate> cashFlowCode2RepayDate = repayActualSplitList.stream().collect(Collectors.toMap(FundDirectFinancingRepayActualSplit::getCashFlowCode, FundDirectFinancingRepayActualSplit::getRepayDate, (a, b) -> b));
        Map<LocalDate, Long> getDirectEveryDayWritePrincipal = getDirectEveryDayWriteAmount(productRepayDetail, daysBetween, cashFlowCode2RepayDate, "PRINCIPAL");
        Map<LocalDate, Long> getDirectEveryDayWriteInterest = getDirectEveryDayWriteAmount(productRepayDetail, daysBetween, cashFlowCode2RepayDate, "INTEREST");
        Map<LocalDate, Long> directEveryDayRemainingPrincipal = getDirectEveryDayRemainingPrincipal(day2PlanWriteOffPrincipal, daysBetween, financingProductDetail.getIssuanceAmount() * 10000, getDirectEveryDayWritePrincipal);
        //获取日利率
        long rate = Optional.ofNullable(financingProductDetail.getIssuanceRate()).orElse(0L);
        BigDecimal dayFtpUpper = calculateFinancingRateDaily(Math.toIntExact(rate));
        BigDecimal dayFtp = dayFtpUpper.divide(new BigDecimal("1000000"), 20, RoundingMode.HALF_UP);
        BigDecimal taxRate = BigDecimal.valueOf(0.06);
        // 当期计提利息
        BigDecimal financingCostBD = BigDecimal.ZERO;
        // 期末融资余额
        long financingBalanceAmount = 0L;
        // 当期实际还款本金
        long principalAmount = 0L;
        // 当期实际核销利息
        long interestAmount = 0L;
        // 按天计提
        LocalDate interestDate = beganDate;
        while (!interestDate.isAfter(endDate)) {
            financingBalanceAmount = Optional.ofNullable(directEveryDayRemainingPrincipal.get(interestDate)).orElse(0L);
            principalAmount = principalAmount + Optional.ofNullable(getDirectEveryDayWritePrincipal.get(interestDate)).orElse(0L);
            interestAmount = interestAmount + Optional.ofNullable(getDirectEveryDayWriteInterest.get(interestDate)).orElse(0L);
            //本日剩余本金（元）*FTP收益日利率（%）
            financingCostBD = financingCostBD.add(BigDecimal.valueOf(financingBalanceAmount).multiply(dayFtp));
            interestDate = interestDate.plusDays(1);
        }
        if (financingBalanceAmount <= 0 && principalAmount <= 0 && interestAmount <= 0) {
            return null;
        }
        FundsDailyCost merge = new FundsDailyCost();
        merge.setMainId(fundsDailyCostMain.getId());
        merge.setFinancingId(financingProductDetail.getFinancingId());
        merge.setFinancingProductId(financingProductDetail.getId());
        merge.setAbbreviation(financingProductDetail.getAbbreviation());
        merge.setInterestDate(LocalDate.of(targetDate.getYear(), targetDate.getMonthValue(), targetDate.lengthOfMonth()));
        merge.setItemText(merge.getInterestDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        merge.setFinancingAmount(Optional.ofNullable(directEveryDayRemainingPrincipal.get(endDate)).orElse(0L));
        merge.setPrincipleAmount(principalAmount);
        merge.setInterestAmount(interestAmount);
        merge.setFinancingRate(Math.toIntExact(rate));
        merge.setDailyRate(dayFtpUpper.intValue());
        merge.setFinancingCost(financingCostBD.longValue());
        // 计算本年累计
        Long totalCostThisYear = fundsDailyCostService.getBaseMapper().sumCostByMainId(fundsDailyCostMain.getId(), LocalDate.of(targetDate.getYear(), 1, 1), beganDate.minusDays(1));
        merge.setTotalCapitalCost(Optional.ofNullable(totalCostThisYear).orElse(0L) + merge.getFinancingCost());
        merge.setType(ZR);
        merge.setIsConfirmed(YesOrNoNumberEnum.NO.getCode());
        // 间融-质押资产为保理 没有销项抵减，所以税率=0
        // 间融-质押资产为直租：13%
        // 其他：6%
        if (StrUtil.isNotBlank(fundsDailyCostMain.getLeaseType())) {
            if (StrUtil.equals(fundsDailyCostMain.getLeaseType(), LeaseType.zhi_zu.name())) {
                taxRate = BigDecimal.valueOf(0.13);
            }
            merge.setPropertyType(fundsDailyCostMain.getLeaseType());
        } else {
            if (StrUtil.equals(fundsDailyCostMain.getBizType(), ProjectBizType.BL.name())) {
                taxRate = BigDecimal.ZERO;
            }
            merge.setPropertyType(fundsDailyCostMain.getBizType());
        }
        // 计算税后金额
        merge.setTaxRate(taxRate);
        merge.setFinancingCostAfterTax(FinancialUtil.calculateAmountWithoutTax(merge.getFinancingCost(), taxRate).longValue());
        merge.setTotalCapitalCostAfterTax(FinancialUtil.calculateAmountWithoutTax(merge.getTotalCapitalCost(), taxRate).longValue());
        // 填充利息余额
        this.fillInterestBalance(fundsDailyCostMain, merge, getDirectEveryDayWriteInterest, targetDate);
        // 填充钆差金额
        this.fillCostDiff(fundsDailyCostMain, merge, true, targetDate);
        return merge;
    }


    //获取直融每天的还款本金或者利息
    private Map<LocalDate, Long> getDirectEveryDayWriteAmount(List<FundDirectFinancingRepayActualSplitRecord> financingRepayDetail, List<LocalDate> everyDay, Map<String, LocalDate> cashFlowCode2RepayDate, String cashFlowItem) {
        if (ObjectUtil.isEmpty(everyDay) ) {
            return MapUtil.empty();
        }
        Map<LocalDate, List<FundDirectFinancingRepayActualSplitRecord>> date2ReceiptFlowDetail = new HashMap<>();
        financingRepayDetail.forEach(e -> {
            LocalDate localDate = cashFlowCode2RepayDate.get(e.getCashFlowCode());
            List<FundDirectFinancingRepayActualSplitRecord> orDefault = date2ReceiptFlowDetail.getOrDefault(localDate, new ArrayList<>());
            orDefault.add(e);
            date2ReceiptFlowDetail.put(localDate, orDefault);
        });

        Map<LocalDate, Long> day2WriteOffAmount = new HashMap<>();
        for (LocalDate day : everyDay) {
            List<FundDirectFinancingRepayActualSplitRecord> fundReceiptFlowDetails = date2ReceiptFlowDetail.get(day);
            if (ObjectUtil.isNotEmpty(fundReceiptFlowDetails)) {
                // 过滤出对应现金流
                fundReceiptFlowDetails.removeIf(e -> !StrUtil.equals(e.getCashFlowItem(), cashFlowItem));
                day2WriteOffAmount.put(day,  fundReceiptFlowDetails.stream().map(FundDirectFinancingRepayActualSplitRecord::getWriteOffAmount).filter(ObjectUtil::isNotEmpty).reduce(Long::sum).orElse(0L));
            }
        }
        return day2WriteOffAmount;
    }

    //获取直融每天的剩余本金
    private Map<LocalDate, Long> getDirectEveryDayRemainingPrincipal(Map<LocalDate, Long> day2PlanWriteOffPrincipal, List<LocalDate> everyDay, Long totalAmount,  Map<LocalDate, Long> day2ActualWriteOffPrincipal) {
        if (ObjectUtil.isEmpty(everyDay) || ObjectUtil.isEmpty(totalAmount) || totalAmount <= 0) {
            return MapUtil.empty();
        }
        Map<LocalDate, Long> day2RemainingPrincipal = new HashMap<>();
        long remainingPrincipal = totalAmount;
        for (LocalDate day : everyDay) {
            long writeOffPrincipal = 0L;
            Long actualWriteOffPrincipal = day2PlanWriteOffPrincipal.get(day);
            if (Objects.nonNull(actualWriteOffPrincipal)) {
                writeOffPrincipal = actualWriteOffPrincipal;
            } else {
                Long planWriteOffPrincipal = day2PlanWriteOffPrincipal.get(day);
                writeOffPrincipal = Optional.ofNullable(planWriteOffPrincipal).orElse(0L);
            }
            remainingPrincipal = remainingPrincipal - writeOffPrincipal;
            day2RemainingPrincipal.put(day, remainingPrincipal);
        }
        return day2RemainingPrincipal;
    }

    private void fillInterestBalance(FundsDailyCostMain fundsDailyCostMain, FundsDailyCost record, Map<LocalDate, Long> everyDayWriteOffInterestMap, LocalDate targetDate) {
        if (Objects.isNull(record)) {
            return;
        }
        // 计算期初和期末的计提利息余额
        FundsDailyCost lastMonth = fundsDailyCostService.getOne(
                Wrappers.<FundsDailyCost>lambdaQuery()
                        .eq(FundsDailyCost::getMainId, fundsDailyCostMain.getId())
                        .eq(Objects.nonNull(record.getFinancingProductId()), FundsDailyCost::getFinancingProductId, record.getFinancingProductId())
                        .eq(FundsDailyCost::getInterestDate, LocalDate.of(targetDate.getYear(), targetDate.getMonthValue(), 1).minusDays(1))
                        .orderByDesc(FundsDailyCost::getId)
                        .last(StringUtil.mysqlLimitOne())
        );
        long beginOfPeriodBalance = Optional.ofNullable(lastMonth).map(FundsDailyCost::getEndOfPeriodInterestBalance).orElse(0L);
        // 期末值 = 期初值 + 本月计提利息 - 本月实际核销利息
        long actualInterest = 0L;
        for (Map.Entry<LocalDate, Long> entry : everyDayWriteOffInterestMap.entrySet()) {
            if (entry.getKey().getYear() == targetDate.getYear() && entry.getKey().getMonthValue() == targetDate.getMonthValue()) {
                actualInterest += Optional.ofNullable(entry.getValue()).orElse(0L);
            }
        }
        long endOfPeriodBalance = beginOfPeriodBalance + record.getFinancingCost() - actualInterest;
        record.setBeginOfPeriodInterestBalance(beginOfPeriodBalance);
        record.setEndOfPeriodInterestBalance(endOfPeriodBalance);
    }

    public boolean isFinish(FundsDailyCostMain fundsDailyCostMain, FundsDailyCost fundsDailyCost, boolean isAbsAbn, LocalDate targetDate) {
        boolean isFinish = false;
        if (isAbsAbn) {
            List<FundDirectFinancingRepayActualSplit> cashFlowList = fundDirectFinancingRepayActualSplitService.list(
                    Wrappers.<FundDirectFinancingRepayActualSplit>lambdaQuery()
                            .eq(FundDirectFinancingRepayActualSplit::getFinancingId, fundsDailyCostMain.getFinancingId())
                            .eq(FundDirectFinancingRepayActualSplit::getProductDetailId, fundsDailyCost.getFinancingProductId())
                            .gt(FundDirectFinancingRepayActualSplit::getInterestAmount, 0)
                            .gt(FundDirectFinancingRepayActualSplit::getRepayDate, LocalDate.of(targetDate.getYear(), targetDate.getMonthValue(), targetDate.lengthOfMonth()))
            );
            if (CollectionUtil.isEmpty(cashFlowList)) {
                // 说明利息还清了
                isFinish = true;
            }
        } else {
            FundReceiptRepayBaseInfo fundReceiptRepayBaseInfo = fundReceiptRepayBaseInfoService.getOneByFinancingId(fundsDailyCostMain.getFinancingId(), StrUtil.equals(fundsDailyCostMain.getFinancingType(), ZR));
            if (Objects.isNull(fundReceiptRepayBaseInfo)) {
                log.error("计提应付利息-没有找到对应的还本付息主表[{}]", JSONUtil.toJsonStr(fundsDailyCostMain));
                return false;
            }
            // 查询还款计划
            List<FundReceiptFlowPlan> cashFlowList = SpringUtil.getBean(FundReceiptFlowPlanService.class).list(
                    Wrappers.<FundReceiptFlowPlan>lambdaQuery()
                            .eq(FundReceiptFlowPlan::getReceiptRepayId, fundReceiptRepayBaseInfo.getId())
                            .eq(FundReceiptFlowPlan::getCashFlowItem, FinanceCashFlowItemEnum.REPAY.name())
                            .gt(FundReceiptFlowPlan::getInterestAmount, 0)
                            .gt(FundReceiptFlowPlan::getCashFlowDate, LocalDate.of(targetDate.getYear(), targetDate.getMonthValue(), targetDate.lengthOfMonth()))
            );
            if (CollectionUtil.isEmpty(cashFlowList)) {
                // 说明利息还清了
                isFinish = true;
            }
        }
        return isFinish;
    }

    private void fillCostDiff(FundsDailyCostMain fundsDailyCostMain, FundsDailyCost fundsDailyCost, boolean isAbsAbn, LocalDate targetDate) {
        if (Objects.isNull(fundsDailyCost)) {
            return;
        }
        // 判断是否当月结清，是的话需要做计提利息的钆差处理（钆差金额 = 本月期末计提利息余额的相反数）
        // 剩余利息 = 0也视为结清，所以判断是否要走结清逻辑统一通过剩余利息是否为0来处理
        // 因为资金端实际核销不按照计划进行，所以无法通过计算得到剩余利息，和资金经理鲁莹沟通后，只要还本付息中对应日期之后没有利息要还则算作剩余利息为0
        if (this.isFinish(fundsDailyCostMain, fundsDailyCost, isAbsAbn, targetDate)) {
            long diff = -1 * Optional.ofNullable(fundsDailyCost.getEndOfPeriodInterestBalance()).orElse(0L);
            fundsDailyCost.setFinancingCostDiff(diff);
            // 期末计提利息余额为0
            fundsDailyCost.setEndOfPeriodInterestBalance(0L);
            fundsDailyCost.setFinancingCost(fundsDailyCost.getFinancingCost() + diff);
            fundsDailyCost.setTotalCapitalCost(fundsDailyCost.getTotalCapitalCost() + diff);
            fundsDailyCost.setFinancingCostAfterTax(FinancialUtil.calculateAmountWithoutTax(fundsDailyCost.getFinancingCost(), fundsDailyCost.getTaxRate()).longValue());
            fundsDailyCost.setTotalCapitalCostAfterTax(FinancialUtil.calculateAmountWithoutTax(fundsDailyCost.getTotalCapitalCost(), fundsDailyCost.getTaxRate()).longValue());
        }
    }

//    public class ApiCallerFundCalTask implements Callable<Void> {
//        private FundFinancingBaseInfo fundFinancingBaseInfo;
//        private LocalDate interestStartDate;
//        private LocalDate interestEndDate;
//
//        public ApiCallerFundCalTask(FundFinancingBaseInfo fundFinancingBaseInfo, LocalDate interestStartDate, LocalDate interestEndDate) {
//            this.fundFinancingBaseInfo = fundFinancingBaseInfo;
//            this.interestStartDate = interestStartDate;
//            this.interestEndDate = interestEndDate;
//        }
//
//        @Override
//        public Void call() throws Exception {
//            doFundCalculate(fundFinancingBaseInfo, interestStartDate, interestEndDate);
//            return null;
//        }
//    }

//    public class ApiCallerDirectFundCalTask implements Callable<Void> {
//        private FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo;
//        private LocalDate interestStartDate;
//        private LocalDate interestEndDate;
//
//        public ApiCallerDirectFundCalTask(FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo, LocalDate interestStartDate, LocalDate interestEndDate) {
//            this.fundDirectFinancingBaseInfo = fundDirectFinancingBaseInfo;
//            this.interestStartDate = interestStartDate;
//            this.interestEndDate = interestEndDate;
//        }
//
//        @Override
//        public Void call() throws Exception {
//            doDirectFundCalculate(fundDirectFinancingBaseInfo, interestStartDate, interestEndDate);
//            return null;
//        }
//    }


//    public void doFundCalculate(FundFinancingBaseInfo fundFinancingBaseInfo, LocalDate interestStartDate, LocalDate interestEndDate) {
//        LocalDate interestDate = interestStartDate;
//        while (!interestDate.isAfter(interestEndDate)) {
//            fundsDailyCostService.doFundsCalculate(fundFinancingBaseInfo, interestDate);
//            interestDate = interestDate.plusDays(1);
//        }
//    }

//    private void doDirectFundCalculate(FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo, LocalDate interestStartDate, LocalDate interestEndDate) {
//        LocalDate interestDate = interestStartDate;
//        while (!interestDate.isAfter(interestEndDate)) {
//            fundsDailyCostService.doDirectFundsCalculate(fundDirectFinancingBaseInfo, interestDate);
//            interestDate = interestDate.plusDays(1);
//        }
//    }

//    @Transactional
//    public List<FundsDailyCost> getIncrementFunds(List<FundsDailyCost> todoList) {
//        List<FundsDailyCost> fundsRes = new ArrayList<>();
//        //查询直融
//        LambdaQueryWrapper<FundDirectFinancingBaseInfo> directQuery = Wrappers.lambdaQuery();
//        directQuery.eq(FundDirectFinancingBaseInfo::getObsolete, false);
//        List<FundDirectFinancingBaseInfo> fundDirectFinancingBaseInfoList = fundDirectFinancingBaseInfoService.list(directQuery);
//        Set<Long> directFinancingFilterIds = new HashSet<>();
//        List<FundDirectFinancingBaseInfo> directRes = new ArrayList<>();
//        if (fundDirectFinancingBaseInfoList != null && !fundDirectFinancingBaseInfoList.isEmpty()) {
//            for (FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo : fundDirectFinancingBaseInfoList) {
//                for (FundsDailyCost fundsDailyCost : todoList) {
//                    if (fundDirectFinancingBaseInfo.getId().equals(fundsDailyCost.getFinancingId())
//                            && "ZR".equalsIgnoreCase(fundsDailyCost.getType())) {
//                        directFinancingFilterIds.add(fundDirectFinancingBaseInfo.getId());
//                    }
//                }
//            }
//            for (FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo : fundDirectFinancingBaseInfoList) {
//                boolean contains = false;
//                for (Long directFinancingFilterId : directFinancingFilterIds) {
//                    if (directFinancingFilterId.equals(fundDirectFinancingBaseInfo.getId())) {
//                        contains = true;
//                        break;
//                    }
//                }
//                if (!contains) {
//                    directRes.add(fundDirectFinancingBaseInfo);
//                }
//            }
//        }
//
//        //查询间融
//        LambdaQueryWrapper<FundFinancingBaseInfo> query = Wrappers.lambdaQuery();
//        query.eq(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name());
//        List<FundFinancingBaseInfo> fundFinancingBaseInfoList = financingBaseInfoService.list(query);
//        Set<Long> financingFilterIds = new HashSet<>();
//        List<FundFinancingBaseInfo> res = new ArrayList<>();
//        if (fundFinancingBaseInfoList != null && !fundFinancingBaseInfoList.isEmpty()) {
//            for (FundFinancingBaseInfo fundFinancingBaseInfo : fundFinancingBaseInfoList) {
//                for (FundsDailyCost fundsDailyCost : todoList) {
//                    if (fundFinancingBaseInfo.getId().equals(fundsDailyCost.getFinancingId())
//                            && "DK".equalsIgnoreCase(fundsDailyCost.getType())) {
//                        financingFilterIds.add(fundFinancingBaseInfo.getId());
//                    }
//                }
//            }
//            for (FundFinancingBaseInfo fundFinancingBaseInfo : fundFinancingBaseInfoList) {
//                boolean contains = false;
//                for (Long financingFilterId : financingFilterIds) {
//                    if (financingFilterId.equals(fundFinancingBaseInfo.getId())) {
//                        contains = true;
//                        break;
//                    }
//                }
//                if (!contains) {
//                    res.add(fundFinancingBaseInfo);
//                }
//            }
//        }
//
//        //直融入库
//        if (!directRes.isEmpty()) {
//            List<FundsDailyCost> directFundsDailyCostList = new ArrayList<>();
//            for (FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo : directRes) {
//                FundsDailyCost fundsDailyCost = new FundsDailyCost();
//                fundsDailyCost.setItemText(BEGINNING_ITEM_TEXT);
//                fundsDailyCost.setFinancingId(fundDirectFinancingBaseInfo.getId());
//                fundsDailyCost.setType("ZR");
//                fundsDailyCost.setFinancingAmount(fundDirectFinancingBaseInfo.getFinancingAmount());
//                fundsDailyCost.setInterestDate(fundDirectFinancingBaseInfo.getDurationFrom() != null ?
//                        fundDirectFinancingBaseInfo.getDurationFrom() : LocalDate.now());
//                directFundsDailyCostList.add(fundsDailyCost);
//            }
//            fundsRes.addAll(directFundsDailyCostList);
//            fundsDailyCostService.saveBatch(directFundsDailyCostList);
//        }
//
//        //间融入库
//        if (!res.isEmpty()) {
//            List<FundsDailyCost> fundsDailyCostList = new ArrayList<>();
//            for (FundFinancingBaseInfo fundFinancingBaseInfo : res) {
//                FundsDailyCost fundsDailyCost = new FundsDailyCost();
//                fundsDailyCost.setItemText(BEGINNING_ITEM_TEXT);
//                fundsDailyCost.setFinancingId(fundFinancingBaseInfo.getId());
//                fundsDailyCost.setType("DK");
//                fundsDailyCost.setFinancingAmount(fundFinancingBaseInfo.getFinancingAmount());
//                fundsDailyCost.setInterestDate(fundFinancingBaseInfo.getActualLoanDate() != null
//                        ? fundFinancingBaseInfo.getActualLoanDate() : LocalDate.now());
//                fundsDailyCostList.add(fundsDailyCost);
//            }
//            fundsRes.addAll(fundsDailyCostList);
//            fundsDailyCostService.saveBatch(fundsDailyCostList);
//        }
//        fundsRes.addAll(todoList);
//        return fundsRes;
//    }
//
//    private LocalDate getLastCostday(Long financingId, String type) {
//        FundsDailyCost lastDayFundsDailyCost = getLastDayFundsDailyCost(financingId, type);
//        return lastDayFundsDailyCost != null ? lastDayFundsDailyCost.getInterestDate() : null;
//    }

//    public FundsDailyCost getLastDayFundsDailyCost(Long financingId, String type) {
//        LambdaQueryWrapper<FundsDailyCost> costQuery = Wrappers.lambdaQuery();
//        costQuery.eq(FundsDailyCost::getFinancingId, financingId)
//                .eq(FundsDailyCost::getType, type)
//                .gt(FundsDailyCost::getFinancingAmount, 0)
//                .ne(FundsDailyCost::getItemText, BEGINNING_ITEM_TEXT)
//                .ne(FundsDailyCost::getItemText, DIFF_ITEM_TEXT);
//        List<FundsDailyCost> dailyCostList = fundsDailyCostService.list(costQuery);
//        if (dailyCostList != null && !dailyCostList.isEmpty()) {
//            List<FundsDailyCost> res = dailyCostList.stream().sorted(Comparator.comparing(FundsDailyCost::getInterestDate)
//                            .reversed())
//                    .collect(Collectors.toList());
//            return res.get(0);
//        }
//        return null;
//    }

//    private Long getLastCostDayAmount(Long financingId, String type) {
//        FundsDailyCost lastDayFundsDailyCost = getLastDayFundsDailyCost(financingId, type);
//        return lastDayFundsDailyCost != null ? lastDayFundsDailyCost.getFinancingAmount() : 0L;
//    }


//    private Map<Long, ContractRentActual> getRent(List<Long> contractId) {
//        List<ContractRentActual> list = contractRentActualService.list(Wrappers.<ContractRentActual>lambdaQuery()
//                .in(CollectionUtils.isNotEmpty(contractId), ContractRentActual::getContractId, contractId)
//                .le(ContractRentActual::getCashFlowDate, LocalDate.now())
//                .orderByDesc(ContractRentActual::getCashFlowPhase));
//        Map<Long, List<ContractRentActual>> collect = list.stream().collect(Collectors.groupingBy(ContractRentActual::getContractId));
//        if (CollectionUtils.isNotEmpty(collect)) {
//            return collect.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get(0)));
//        } else {
//            return new HashMap<>();
//        }
//    }
//
//    private Map<Long, Integer> getContractLpr(List<Long> contractId) {
//        List<ContractLeasePrice> leasePrice = leasePriceService.list(Wrappers.<ContractLeasePrice>lambdaQuery()
//                .in(CollectionUtils.isNotEmpty(contractId), ContractLeasePrice::getContractId, contractId));
//        List<ContractAocPrice> aocPrice = aocPriceService.list(Wrappers.<ContractAocPrice>lambdaQuery()
//                .in(CollectionUtils.isNotEmpty(contractId), ContractAocPrice::getContractId, contractId));
//        List<ContractFactoringPrice> factoringPrice = factoringPriceService.list(Wrappers.<ContractFactoringPrice>lambdaQuery()
//                .in(CollectionUtils.isNotEmpty(contractId), ContractFactoringPrice::getContractId, contractId));
//        Map<Long, Integer> map = new HashMap<>();
//        leasePrice.forEach(price -> {
//            map.put(price.getContractId(), Optional.ofNullable(price.getLprPercent()).orElse(0) + Optional.ofNullable(price.getLprAddPercent()).orElse(0));
//        });
//        aocPrice.forEach(price -> {
//            map.put(price.getContractId(), Optional.ofNullable(price.getLprPercent()).orElse(0) + Optional.ofNullable(price.getLprAddPercent()).orElse(0));
//        });
//        factoringPrice.forEach(price -> {
//            map.put(price.getContractId(), Optional.ofNullable(price.getLprPercent()).orElse(0) + Optional.ofNullable(price.getLprAddPercent()).orElse(0));
//        });
//        return map;
//    }

//    private void record(String type, List<Long> collect) {
//        if (ObjectUtil.isEmpty(AccountUtil.getLoginInfo())) {
//            return;
//        }
//        Long userId = AccountUtil.getLoginInfo().getId();
//        MonthlyUserRecord userRecord = userRecordMapper.selectOne(Wrappers.<MonthlyUserRecord>lambdaQuery()
//                .eq(MonthlyUserRecord::getUserId, userId)
//                .eq(MonthlyUserRecord::getType, type));
//        if (userRecord == null) {
//            MonthlyUserRecord record = new MonthlyUserRecord();
//            record.setValue(JSON.toJSONString(Optional.ofNullable(collect).orElse(Collections.emptyList())));
//            record.setUserId(userId);
//            record.setType(type);
//            userRecordMapper.insert(record);
//        } else {
//            userRecord.setValue(JSON.toJSONString(Optional.ofNullable(collect).orElse(Collections.emptyList())));
//            userRecordMapper.updateById(userRecord);
//        }
//    }

//    private String ensureType(String bizType, String leaseType) {
//        if (Objects.equals(ProjectBizType.BL.name(), bizType)) {
//            return ProjectBizType.BL.display();
//        } else if (Objects.equals(ProjectBizType.ZR.name(), bizType)) {
//            return ProjectBizType.ZR.display();
//        } else {
//            return Optional.ofNullable(LeaseType.of(leaseType)).map(LeaseType::display).orElse("");
//        }
//    }

    private List<LocalDate> getDaysBetween(LocalDate from, LocalDate to) {
        if (ObjectUtil.isEmpty(from) || ObjectUtil.isEmpty(to)) {
            return ListUtil.empty();
        }
        // 计算天数差
        long daysBetween = ChronoUnit.DAYS.between(from, to);
        // 生成日期列表
        List<LocalDate> dates = new ArrayList<>();
        for (int i = 0; i <= daysBetween; i++) {
            dates.add(from.plusDays(i));
        }
        return dates;
    }
}
