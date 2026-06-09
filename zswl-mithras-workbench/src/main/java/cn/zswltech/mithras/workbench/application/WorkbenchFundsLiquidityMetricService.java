package cn.zswltech.mithras.workbench.application;

import cn.zswltech.mithras.dto.workbench.WorkbenchMetricReq;
import cn.zswltech.mithras.dto.workbench.chart.LineBarChartValueVO;
import cn.zswltech.mithras.dto.workbench.chart.sub.ChartBaseDataVO;
import cn.zswltech.mithras.dto.workbench.chart.sub.ChartDataVO;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.workbench.infrastructure.persistence.mapper.WorkbenchFundsLiquidityMetricMapper;
import cn.zswltech.mithras.workbench.infrastructure.persistence.mapper.model.WorkbenchFundsLiquidityMetric;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 工作台-资金流动性分析指标
 * @date 2023-05-09
 */
@Service
public class WorkbenchFundsLiquidityMetricService
        extends ServiceImpl<WorkbenchFundsLiquidityMetricMapper, WorkbenchFundsLiquidityMetric> {

    private static final String WRITTEN_OFF = "WRITTEN_OFF";
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Resource
    private WorkbenchFundsLiquidityPort fundsLiquidityPort;

    public LineBarChartValueVO fundsLiquidityChartRealTime(WorkbenchMetricReq req) {
        LocalDate thisMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate start = thisMonth.minusMonths(6);
        LocalDate end = thisMonth.plusMonths(6).with(TemporalAdjusters.lastDayOfMonth());
        LocalDate iter = start;
        Set<String> monthSet = new HashSet<>();
        while (iter.isBefore(end)) {
            monthSet.add(month(iter));
            iter = iter.plusMonths(1);
        }

        List<WorkbenchFundsLiquidityCollection> collections = fundsLiquidityPort.listRentCollections(start, end);
        Map<String, List<WorkbenchFundsLiquidityCollection>> collectionMonthGroup = collections.stream()
                .collect(Collectors.groupingBy(collection -> month(collection.getPlanCollectionDate())));
        List<ChartBaseDataVO> rentReceivables = new ArrayList<>();
        List<ChartBaseDataVO> actualRepayment = new ArrayList<>();

        collectionMonthGroup.forEach((month, collectionBaseInfos) -> {
            ChartBaseDataVO rentReceivable = new ChartBaseDataVO();
            rentReceivable.setName(month);
            rentReceivable.setUnitDisplay("万元");
            rentReceivable.setValue(collectionBaseInfos.stream()
                    .map(WorkbenchFundsLiquidityCollection::getPlanCollectionAmount)
                    .map(LongUtil::null2zero).map(BigDecimal::new)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP).toString());
            rentReceivables.add(rentReceivable);

            ChartBaseDataVO actualRepaymentData = new ChartBaseDataVO();
            actualRepaymentData.setName(month);
            rentReceivable.setUnitDisplay("万元");
            actualRepaymentData.setValue(collectionBaseInfos.stream()
                    .map(WorkbenchFundsLiquidityCollection::getCollectionAmount)
                    .map(LongUtil::null2zero).map(BigDecimal::new)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP).toString());
            actualRepayment.add(actualRepaymentData);
        });
        HashSet<String> tmpMonthSet = new HashSet<>(monthSet);
        tmpMonthSet.removeAll(collectionMonthGroup.keySet());
        tmpMonthSet.forEach(item -> {
            rentReceivables.add(new ChartBaseDataVO(item, "0", null, null));
            actualRepayment.add(new ChartBaseDataVO(item, "0", null, null));
        });
        rentReceivables.sort(Comparator.comparing(ChartBaseDataVO::getName));
        actualRepayment.sort(Comparator.comparing(ChartBaseDataVO::getName));
        List<ChartDataVO> data = new ArrayList<>();
        ChartDataVO rentReceivableData = new ChartDataVO("应收租金", "line", rentReceivables);
        ChartDataVO actualRepaymentData = new ChartDataVO("实收租金", "line", actualRepayment);
        data.add(rentReceivableData);
        data.add(actualRepaymentData);

        List<WorkbenchFundsLiquidityRepayCashFlow> repayCashFlows = fundsLiquidityPort.listRepayCashFlows(start, end);
        Map<String, List<WorkbenchFundsLiquidityRepayCashFlow>> repayMonthGroup = repayCashFlows.stream()
                .collect(Collectors.groupingBy(lib -> month(lib.getRepayDate())));
        List<ChartBaseDataVO> payableFunds = new ArrayList<>();
        List<ChartBaseDataVO> paidUpFunds = new ArrayList<>();
        repayMonthGroup.forEach((month, libs) -> {
            ChartBaseDataVO payableFund = new ChartBaseDataVO();
            payableFund.setUnitDisplay("万元");
            payableFund.setName(month);
            payableFund.setValue(libs.stream().map(WorkbenchFundsLiquidityRepayCashFlow::getRepayAmount)
                    .map(LongUtil::null2zero).map(BigDecimal::new)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP).toString());
            payableFunds.add(payableFund);

            ChartBaseDataVO paidUpFund = new ChartBaseDataVO();
            paidUpFund.setUnitDisplay("万元");
            paidUpFund.setName(month);
            paidUpFund.setValue(libs.stream()
                    .filter(lib -> WRITTEN_OFF.equals(lib.getWriteOffState()))
                    .map(WorkbenchFundsLiquidityRepayCashFlow::getRepayAmount)
                    .map(LongUtil::null2zero).map(BigDecimal::new)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP).toString());
            paidUpFunds.add(paidUpFund);
        });
        tmpMonthSet = new HashSet<>(monthSet);
        tmpMonthSet.removeAll(repayMonthGroup.keySet());
        tmpMonthSet.forEach(item -> {
            payableFunds.add(new ChartBaseDataVO(item, "0", null, null));
            paidUpFunds.add(new ChartBaseDataVO(item, "0", null, null));
        });
        payableFunds.sort(Comparator.comparing(ChartBaseDataVO::getName));
        paidUpFunds.sort(Comparator.comparing(ChartBaseDataVO::getName));
        ChartDataVO payableFundsData = new ChartDataVO("应还资金", "line", payableFunds);
        ChartDataVO paidUpFundsData = new ChartDataVO("实还资金", "line", paidUpFunds);
        data.add(payableFundsData);
        data.add(paidUpFundsData);

        return new LineBarChartValueVO("资金流动性分析", data);
    }

    private String month(LocalDate date) {
        return MONTH_FORMATTER.format(date);
    }
}
