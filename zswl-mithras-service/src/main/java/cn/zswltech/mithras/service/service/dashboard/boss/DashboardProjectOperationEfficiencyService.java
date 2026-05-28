package cn.zswltech.mithras.service.service.dashboard.boss;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.zswltech.gruul.common.constant.OrgConstants;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.dashboard.boss.OperationEfficiencyDetailListRSP;
import cn.zswltech.mithras.dto.dashboard.boss.OperationEfficiencyStatisticsListRSP;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.service.enums.dashboard.BossDashboardGuanYuanDataSourceKeyEnum;
import cn.zswltech.mithras.service.enums.riskcontrol.RiskControlIndustryClassify;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.dashboard.guanyuandata.boss.OperationEfficiencyStageDTO;
import cn.zswltech.sleipnir.toolkit.enums.GuanYuanFilterTypeEnum;
import cn.zswltech.sleipnir.toolkit.request.guanyuan.GuanYuanDSRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author yangxiong
 * @date 2024/5/16/14:40
 * @description 项目运营效率
 */
@Slf4j
@Service
public class DashboardProjectOperationEfficiencyService extends GuanYuanBasicService {
    private static final List<String> STAGE_LIST = ListUtil.of("访客", "立项", "尽调", "评审", "评审结束未创建合同", "签约", "付款", "投放");
    private static final String QUERY_PUBLIC = "PUBLIC";
    private static final String QUERY_INDUSTRY = "INDUSTRY";

    @Resource
    private OrgDOMapper orgDOMapper;

    public List<OperationEfficiencyDetailListRSP> listEfficiencyGroupByDept(String queryType) {
        // 查找业务部门
        List<OrgDO> orgList = orgDOMapper.queryAll();
        orgList.removeIf(e -> !Objects.equals(e.getType(), OrgConstants.BUSINESS_DEPT));
        orgList.removeIf(e -> !Objects.equals(e.getLevel(), 2));
        if (CollectionUtil.isEmpty(orgList)) {
            return Collections.emptyList();
        }
        // 查询数据
        Map<String, List<OperationEfficiencyStageDTO>> dataMap = this.getDataMap(queryType);
        // 返回参数
        List<OperationEfficiencyDetailListRSP> result = new ArrayList<>(orgList.size());
        for (OrgDO orgDO : orgList) {
            OperationEfficiencyDetailListRSP rsp = new OperationEfficiencyDetailListRSP();
            rsp.setDeptName(orgDO.getName());
            List<OperationEfficiencyStatisticsListRSP> innerList = new ArrayList<>(STAGE_LIST.size());
            for (String stage : STAGE_LIST) {
                List<OperationEfficiencyStageDTO> list = Optional.ofNullable(dataMap.get(stage)).orElse(Collections.emptyList());
                List<OperationEfficiencyStageDTO> listBelongOrg = list.stream().filter(e -> Objects.nonNull(e.getBizDeptId())).filter(e -> Objects.equals(e.getBizDeptId(), orgDO.getId())).collect(Collectors.toList());
                innerList.add(this.convert(stage, listBelongOrg));
            }
            rsp.setDetail(innerList);
            result.add(rsp);
        }
        return result;
    }

    public List<OperationEfficiencyStatisticsListRSP> listEfficiency(String queryType) {
        // 取数据
        Map<String, List<OperationEfficiencyStageDTO>> dataMap = this.getDataMap(queryType);
        // 返回参数
        List<OperationEfficiencyStatisticsListRSP> result = new ArrayList<>(STAGE_LIST.size());
        for (String stage : STAGE_LIST) {
            result.add(this.convert(stage, dataMap.get(stage)));
        }
        return result;
    }

    private OperationEfficiencyStatisticsListRSP convert(String stage, List<OperationEfficiencyStageDTO> sourceList) {
        OperationEfficiencyStatisticsListRSP rsp = new OperationEfficiencyStatisticsListRSP();
        rsp.setStageName(stage);
        if (CollectionUtil.isEmpty(sourceList)) {
            return rsp;
        }
        LocalDate now = LocalDate.now();
        LocalDateTime thisMonthStart = LocalDateTime.of(now.getYear(), now.getMonthValue(), 1, 0, 0, 0);
        LocalDateTime thisMonthEnd = LocalDateTime.of(now.getYear(), now.getMonthValue(), now.lengthOfMonth(), 23, 59, 59);
        LocalDateTime lastMonthStart = thisMonthStart.minusMonths(1);
        LocalDateTime lastMonthEnd = thisMonthEnd.minusMonths(1);
        LocalDateTime lastYearMonthStart = thisMonthStart.minusYears(1);
        LocalDateTime lastYearMonthEnd = thisMonthEnd.minusYears(1);
        int workdaysTotal = 0;
        int workdaysThisMonth = 0;
        int quantityThisMonth = 0;
        int workdaysLastMonth = 0;
        int quantityLastMonth = 0;
        int workdaysLastYearMonth = 0;
        int quantityLastYearMonth = 0;
        long moneyAmountThisMonth = 0L;
        for (OperationEfficiencyStageDTO operationEfficiencyStage : sourceList) {
            LocalDateTime targetDateTime = operationEfficiencyStage.getStageEndTime();
            workdaysTotal += Optional.ofNullable(operationEfficiencyStage.getWorkdays()).orElse(0);
            if (Objects.nonNull(targetDateTime) && !targetDateTime.isBefore(thisMonthStart) && !targetDateTime.isAfter(thisMonthEnd)) {
                workdaysThisMonth += Optional.ofNullable(operationEfficiencyStage.getWorkdays()).orElse(0);
                moneyAmountThisMonth += Optional.ofNullable(operationEfficiencyStage.getMoneyAmount()).orElse(0L);
                quantityThisMonth++;
            }
            if (Objects.nonNull(targetDateTime) && !targetDateTime.isBefore(lastMonthStart) && !targetDateTime.isAfter(lastMonthEnd)) {
                workdaysLastMonth += Optional.ofNullable(operationEfficiencyStage.getWorkdays()).orElse(0);
                quantityLastMonth++;
            }
            if (Objects.nonNull(targetDateTime) && !targetDateTime.isBefore(lastYearMonthStart) && !targetDateTime.isAfter(lastYearMonthEnd)) {
                workdaysLastYearMonth += Optional.ofNullable(operationEfficiencyStage.getWorkdays()).orElse(0);
                quantityLastYearMonth++;
            }
        }
        BigDecimal avg = BigDecimal.valueOf(workdaysTotal).divide(BigDecimal.valueOf(sourceList.size()), 2, RoundingMode.HALF_UP);
        BigDecimal avgThisMonth = null;
        if (quantityThisMonth != 0) {
            avgThisMonth = BigDecimal.valueOf(workdaysThisMonth).divide(BigDecimal.valueOf(quantityThisMonth), 2, RoundingMode.HALF_UP);
            rsp.setAverageThisMonth(new ValueUnitDTO(avgThisMonth.toPlainString(), "天"));
        }
        BigDecimal avgLastMonth = null;
        if (quantityLastMonth != 0) {
            avgLastMonth = BigDecimal.valueOf(workdaysLastMonth).divide(BigDecimal.valueOf(quantityLastMonth), 2, RoundingMode.HALF_UP);
        }
        BigDecimal avgLastYearMonth = null;
        if (quantityLastYearMonth != 0) {
            avgLastYearMonth = BigDecimal.valueOf(workdaysLastYearMonth).divide(BigDecimal.valueOf(quantityLastYearMonth), 2, RoundingMode.HALF_UP);
        }
        rsp.setAverage(new ValueUnitDTO(avg.toPlainString(), "天"));
        if (Objects.nonNull(avgThisMonth) && Objects.nonNull(avgLastMonth) && avgLastMonth.compareTo(BigDecimal.ZERO) != 0) {
            rsp.setChainRatio(new ValueUnitDTO(avgThisMonth.subtract(avgLastMonth).divide(avgLastMonth, 20, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_UP).toPlainString(), "%"));
        }
        if (Objects.nonNull(avgThisMonth) && Objects.nonNull(avgLastYearMonth) && avgLastYearMonth.compareTo(BigDecimal.ZERO) != 0) {
            rsp.setYearOnYearBasis(new ValueUnitDTO(avgThisMonth.subtract(avgLastYearMonth).divide(avgLastYearMonth, 20, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_UP).toPlainString(), "%"));
        }
        rsp.setQuantity(new ValueUnitDTO(String.valueOf(quantityThisMonth), "个"));
        rsp.setFinanceAmount(new ValueUnitDTO(BigDecimal.valueOf(moneyAmountThisMonth).divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP).toPlainString(), "万元"));
        return rsp;
    }

    private Map<String, List<OperationEfficiencyStageDTO>> getDataMap(String queryType) {
        CompletableFuture<List<OperationEfficiencyStageDTO>> lxList = CompletableFuture.supplyAsync(() -> queryFromGuanYuan(BossDashboardGuanYuanDataSourceKeyEnum.OperationEfficiencyStageLX, this.build(queryType), OperationEfficiencyStageDTO.class));
        CompletableFuture<List<OperationEfficiencyStageDTO>> psList = CompletableFuture.supplyAsync(() -> queryFromGuanYuan(BossDashboardGuanYuanDataSourceKeyEnum.OperationEfficiencyStagePS, this.build(queryType), OperationEfficiencyStageDTO.class));
        CompletableFuture<List<OperationEfficiencyStageDTO>> psjswcjhtList = CompletableFuture.supplyAsync(() -> queryFromGuanYuan(BossDashboardGuanYuanDataSourceKeyEnum.OperationEfficiencyStagePSJSWCJHT, this.build(queryType), OperationEfficiencyStageDTO.class));
        CompletableFuture<List<OperationEfficiencyStageDTO>> qyList = CompletableFuture.supplyAsync(() -> queryFromGuanYuan(BossDashboardGuanYuanDataSourceKeyEnum.OperationEfficiencyStageQY, this.build(queryType), OperationEfficiencyStageDTO.class));
        CompletableFuture<List<OperationEfficiencyStageDTO>> fkList = CompletableFuture.supplyAsync(() -> queryFromGuanYuan(BossDashboardGuanYuanDataSourceKeyEnum.OperationEfficiencyStageFK, this.build(queryType), OperationEfficiencyStageDTO.class));
        CompletableFuture<List<OperationEfficiencyStageDTO>> tfList = CompletableFuture.supplyAsync(() -> queryFromGuanYuan(BossDashboardGuanYuanDataSourceKeyEnum.OperationEfficiencyStageTF, this.build(queryType), OperationEfficiencyStageDTO.class));
        Map<String, List<OperationEfficiencyStageDTO>> map = new HashMap<>();
        try {
            map.put("立项", lxList.get());
            map.put("评审", psList.get());
            map.put("评审结束未创建合同", psjswcjhtList.get());
            map.put("签约", qyList.get());
            map.put("付款", fkList.get());
            map.put("投放", tfList.get());
        } catch (Exception e) {
            log.error("异步取数据发生异常", e);
            throw new MithrasException("数据获取异常");
        }
        return map;
    }

    private GuanYuanDSRequest.Body build(String queryType) {
        GuanYuanDSRequest.Body body = new GuanYuanDSRequest.Body();
        if (Objects.equals(QUERY_PUBLIC, queryType)) {
            GuanYuanDSRequest.Filter filter = new GuanYuanDSRequest.Filter();
            filter.setName("risk_control_industry_classify");
            filter.setFilterType(GuanYuanFilterTypeEnum.IN.name());
            filter.setFilterValue(ListUtil.of(RiskControlIndustryClassify.PUBLIC_UTILITIES.name(), RiskControlIndustryClassify.CIVIL_CONSUMPTION.name(), RiskControlIndustryClassify.TRAVEL.name()));
            body.setFilters(Collections.singletonList(filter));
        }
        if (Objects.equals(QUERY_INDUSTRY, queryType)) {
            GuanYuanDSRequest.Filter filter = new GuanYuanDSRequest.Filter();
            filter.setName("risk_control_industry_classify");
            filter.setFilterType(GuanYuanFilterTypeEnum.NI.name());
            filter.setFilterValue(ListUtil.of(RiskControlIndustryClassify.PUBLIC_UTILITIES.name(), RiskControlIndustryClassify.CIVIL_CONSUMPTION.name(), RiskControlIndustryClassify.TRAVEL.name()));
            body.setFilters(Collections.singletonList(filter));
        }
        return body;
    }
}
