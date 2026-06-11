package cn.zswltech.mithras.dashboard.application.boss;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dto.dashboard.boss.MonthCollectStatisticsListREQ;
import cn.zswltech.mithras.dto.dashboard.boss.MonthCollectStatisticsListRSP;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.dashboard.enums.DashBoardQueryTypeEnum;
import cn.zswltech.mithras.kpi.enums.BelongTypeEnum;
import cn.zswltech.mithras.kpi.enums.BusinessTypeEnum;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.contract.mapper.contract.ContractIncomeSharingMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractIncomeSharing;
import cn.zswltech.mithras.kpi.mapper.model.PerformanceBaseInfo;
import cn.zswltech.mithras.kpi.mapper.model.PerformanceMainInfo;
import cn.zswltech.mithras.kpi.mapper.model.PerformanceRecordInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.kpi.application.performance.KpiPerformanceBaseInfoService;
import cn.zswltech.mithras.kpi.application.performance.KpiPerformanceMainInfoService;
import cn.zswltech.mithras.kpi.application.performance.KpiPerformanceRecordInfoService;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author bigbear
 * @date 2024/10/23 09:55
 * @description
 */
@Slf4j
@Service
public class BusinessMonthCollectService implements BusinessMonthCollectApplicationService {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ContractIncomeSharingMapper contractIncomeSharingMapper;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private KpiPerformanceMainInfoService performanceMainInfoService;
    @Resource
    private KpiPerformanceBaseInfoService performanceBaseInfoService;
    @Resource
    private KpiPerformanceRecordInfoService performanceRecordInfoService;

    /**
     * 月度收入统计
     */
    public List<MonthCollectStatisticsListRSP> getMonthCollectStatisticsList(MonthCollectStatisticsListREQ req) {
        log.info("业务月度收入统计 parameters = {}", req);
        // 当前默认展示当年数据
        req.setQueryDateFrom(LocalDate.now().withDayOfYear(1).toString());
        req.setQueryDateTo(LocalDate.now().toString());
        // 找到当年的收入分摊表信息
        List<ContractIncomeSharing> contractIncomeSharingList = contractIncomeSharingMapper.selectList(Wrappers.<ContractIncomeSharing>lambdaQuery()
                .between(ContractIncomeSharing::getIncomeDate, req.getQueryDateFrom(), req.getQueryDateTo()));
        if (CollUtil.isEmpty(contractIncomeSharingList)) {
            log.error("管理工作台-业务月度收入统计 未找到本年符合条件的数据");
            return Collections.emptyList();
        }
        Map<Long, ContractBaseInfo> contractBaseInfoMap = contractBaseInfoService.listByIds(contractIncomeSharingList.stream().map(ContractIncomeSharing::getContractId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(ContractBaseInfo::getId, Function.identity(), (k1, k2) -> k1));
        // 将浙江业务部和公用事业业务部找到
        Long zjDept = sysUserService.getOrgIdByCode("JCSSYWB");
        Long ggDept = sysUserService.getOrgIdByCode("GGSY");
        List<Long> zjAndGgDeptList = CollUtil.newArrayList(zjDept, ggDept);
        // 1、首先按照月份分组
        Map<Integer, Map<Long, List<ContractIncomeSharing>>> map = new HashMap<>();
        for (int i = 1; i < 13; i++) {
            int currentMonth = i;
            List<ContractIncomeSharing> monthSharingList = contractIncomeSharingList.stream()
                    .filter(item -> item.getIncomeDate().getMonthValue() == currentMonth)
                    .collect(Collectors.toList());
            if (CollUtil.isEmpty(monthSharingList)) {
                continue;
            }
            Map<Long, List<ContractIncomeSharing>> deptSharingMap = new HashMap<>();
            // 2、根据合同的风控行业分类确认具体是什么类型的数据
            monthSharingList.stream().collect(Collectors.groupingBy(ContractIncomeSharing::getContractId))
                    .forEach((contractId, incomeSharingList) -> {
                        ContractBaseInfo contractBaseInfo = contractBaseInfoMap.get(contractId);
                        if (Objects.isNull(contractBaseInfo)) {
                            throw MithrasException.newException("合同信息不存在");
                        }
                        List<ContractIncomeSharing> existList = Optional.ofNullable(deptSharingMap.get(contractBaseInfo.getBizDeptId())).orElse(CollUtil.newArrayList());
                        if (!CharSequenceUtil.equals(req.getType(), DashBoardQueryTypeEnum.ALL.name())) {
                            String type = this.getType(contractBaseInfo, zjAndGgDeptList);
                            if (CharSequenceUtil.equals(type, DashBoardQueryTypeEnum.INDUSTRY_CATEGORY.name())
                                    && CharSequenceUtil.equals(req.getType(), DashBoardQueryTypeEnum.INDUSTRY_CATEGORY.name())) {
                                existList.addAll(incomeSharingList);
                            } else if (CharSequenceUtil.equals(type, DashBoardQueryTypeEnum.PUBLIC_CATEGORY.name())
                                    && CharSequenceUtil.equals(req.getType(), DashBoardQueryTypeEnum.PUBLIC_CATEGORY.name())) {
                                existList.addAll(incomeSharingList);
                            } else {
                                return;
                            }
                        } else {
                            existList.addAll(incomeSharingList);
                        }
                        deptSharingMap.put(contractBaseInfo.getBizDeptId(), existList);
                    });
            map.put(currentMonth, deptSharingMap);
        }
        // 找到所有部门的业绩目标备用
        PerformanceMainInfo performanceMainInfo = performanceMainInfoService.getOne(Wrappers.<PerformanceMainInfo>lambdaQuery()
                .eq(PerformanceMainInfo::getYear, LocalDate.now().getYear())
                .eq(PerformanceMainInfo::getStatus, YesOrNoNumberEnum.YES.getCode())
                .last(StringUtil.mysqlLimitOne()));
        if (Objects.isNull(performanceMainInfo)) {
            throw MithrasException.newException("未找到" + LocalDate.now().getYear() + "年业绩目标");
        }
        String businessType = BusinessTypeEnum.DEPT_TOTAL.name();
        if (req.getType().equals(DashBoardQueryTypeEnum.INDUSTRY_CATEGORY.name())) {
            businessType = BusinessTypeEnum.INDUSTRY.name();
        } else if (req.getType().equals(DashBoardQueryTypeEnum.PUBLIC_CATEGORY.name())) {
            businessType = BusinessTypeEnum.PLATFORM.name();
        }
        PerformanceBaseInfo performanceBaseInfo = performanceBaseInfoService.getOne(Wrappers.<PerformanceBaseInfo>lambdaQuery()
                .eq(PerformanceBaseInfo::getMainId, performanceMainInfo.getId())
                .eq(PerformanceBaseInfo::getBelongType, BelongTypeEnum.COMPANY.name())
                .eq(CharSequenceUtil.isNotBlank(businessType), PerformanceBaseInfo::getBusinessType, businessType)
                .isNull(PerformanceBaseInfo::getBelongDeptId)
                .last(StringUtil.mysqlLimitOne()));
        Map<Integer, PerformanceRecordInfo> performanceRecordMap = performanceRecordInfoService.list(Wrappers.<PerformanceRecordInfo>lambdaQuery()
                        .eq(PerformanceRecordInfo::getPerformanceId, performanceBaseInfo.getId()))
                .stream().collect(Collectors.toMap(PerformanceRecordInfo::getMonth, Function.identity(), (a, b) -> a));
        // 3、对数据进行统计
        List<MonthCollectStatisticsListRSP> result = CollUtil.newArrayList();
        Map<Long, String> deptId2NameMap = id2NameService.deptId2Name(map.values().stream().map(Map::keySet).flatMap(Collection::stream).collect(Collectors.toList()));
        map.forEach((month, deptSharingMap) -> {
            MonthCollectStatisticsListRSP rsp = MonthCollectStatisticsListRSP.builder()
                    .incomeMonth(LocalDate.now().withMonth(month).format(DateTimeFormatter.ofPattern(DatePattern.NORM_MONTH_PATTERN)))
                    .build();
            List<MonthCollectStatisticsListRSP.MonthCollectStatisticsDetail> monthCollectStatisticsDetailList = CollUtil.newArrayList();
            AtomicReference<Long> monthIncomeAmount = new AtomicReference<>(0L);
            deptSharingMap.forEach((deptId, incomeSharingList) -> {
                MonthCollectStatisticsListRSP.MonthCollectStatisticsDetail statisticsDetail = MonthCollectStatisticsListRSP.MonthCollectStatisticsDetail.builder()
                        .deptId(deptId)
                        .deptName(deptId2NameMap.get(deptId))
                        .build();
                // 填充数据
                monthIncomeAmount.set(monthIncomeAmount.get() + incomeSharingList.stream().mapToLong(ContractIncomeSharing::getIncomeWithoutTax).summaryStatistics().getSum());
                BigDecimal decimal = BigDecimal.valueOf(monthIncomeAmount.get()).divide(BigDecimal.valueOf(1000000000000L), 2, RoundingMode.HALF_UP);
                statisticsDetail.setIncomeAmount(new ValueUnitDTO(decimal.toPlainString(), "亿元"));
                monthCollectStatisticsDetailList.add(statisticsDetail);
            });
            BigDecimal decimal = BigDecimal.valueOf(performanceRecordMap.get(month).getTargetAmount()).divide(BigDecimal.valueOf(1000000000000L), 2, RoundingMode.HALF_UP);
            rsp.setIncomeTarget(new ValueUnitDTO(decimal.toPlainString(), "亿元"));
            rsp.setMonthCollectStatisticsDetailList(monthCollectStatisticsDetailList);
            result.add(rsp);
        });
        result.forEach(obj -> {
            // 单独设置累计收入完成率
            LocalDate currentMonth = LocalDateTimeUtil.parseDate(obj.getIncomeMonth(), DateTimeFormatter.ofPattern(DatePattern.NORM_MONTH_PATTERN));
            BigDecimal incomeTarget = BigDecimal.ZERO;
            BigDecimal incomeAmount = BigDecimal.ZERO;
            for (MonthCollectStatisticsListRSP rsp : result) {
                LocalDate month = LocalDateTimeUtil.parseDate(rsp.getIncomeMonth(), DateTimeFormatter.ofPattern(DatePattern.NORM_MONTH_PATTERN));
                if (!month.isAfter(currentMonth)) {
                    // 将收入目标做累加
                    incomeAmount = rsp.getMonthCollectStatisticsDetailList().stream().map(MonthCollectStatisticsListRSP.MonthCollectStatisticsDetail::getIncomeAmount)
                            .map(ValueUnitDTO::getValue).map(BigDecimal::new).map(v -> v.multiply(BigDecimal.valueOf(1000000000000L)))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    incomeTarget = incomeAmount.add(new BigDecimal(rsp.getIncomeTarget().getValue()).multiply(BigDecimal.valueOf(1000000000000L)));
                }
            }
            obj.setIncomeCompletionRate(new ValueUnitDTO(incomeAmount.divide(incomeTarget, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP).toPlainString(), "%"));
        });
        return result;
    }

    public String getType(ContractBaseInfo contractBaseInfo, List<Long> zjAndGgDeptList) {
        // 浙江业务部和公共事业业务部比较特殊需要特殊处理
        String type = "";
        if (zjAndGgDeptList.contains(contractBaseInfo.getBizDeptId())) {
            // 找到对应的行业分类
            String riskControlIndustryClassify = contractBaseInfo.getRiskControlIndustryClassify();
            if (CharSequenceUtil.isBlank(riskControlIndustryClassify)) {
                log.error("合同【{}】不存在行业分类", contractBaseInfo.getContractCode());
                throw MithrasException.newException("合同信息不存在行业分类");
            }
            if (CharSequenceUtil.equalsAny(riskControlIndustryClassify, RiskControlIndustryClassify.CIVIL_CONSUMPTION.name(),
                    RiskControlIndustryClassify.TRAVEL.name(), RiskControlIndustryClassify.PUBLIC_UTILITIES.name())) {
                // 公用事业
                type = DashBoardQueryTypeEnum.PUBLIC_CATEGORY.name();
            } else {
                type = DashBoardQueryTypeEnum.INDUSTRY_CATEGORY.name();
            }
        } else {
            type = DashBoardQueryTypeEnum.INDUSTRY_CATEGORY.name();
        }
        return type;
    }
}
