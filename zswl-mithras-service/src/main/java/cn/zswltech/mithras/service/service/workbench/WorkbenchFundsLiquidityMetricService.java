package cn.zswltech.mithras.service.service.workbench;

import cn.zswltech.mithras.dto.workbench.WorkbenchMetricReq;
import cn.zswltech.mithras.dto.workbench.chart.LineBarChartValueVO;
import cn.zswltech.mithras.dto.workbench.chart.sub.ChartBaseDataVO;
import cn.zswltech.mithras.dto.workbench.chart.sub.ChartDataVO;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.fund.domain.enums.receiptrepay.CashFlowState;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.workbench.infrastructure.persistence.mapper.model.WorkbenchFundsLiquidityMetric;
import cn.zswltech.mithras.workbench.infrastructure.persistence.mapper.WorkbenchFundsLiquidityMetricMapper;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayCashFlowService;
import cn.zswltech.mithras.service.util.DateUtil;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 工作台-资金流动性分析指标
 * @date 2023-05-09
 */
@Service
public class WorkbenchFundsLiquidityMetricService
        extends ServiceImpl<WorkbenchFundsLiquidityMetricMapper, WorkbenchFundsLiquidityMetric> {

    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private FundReceiptRepayCashFlowService repayCashFlowService;

    public LineBarChartValueVO fundsLiquidityChartRealTime(WorkbenchMetricReq req) {
        LocalDate thisMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate start = thisMonth.minusMonths(6);
        LocalDate end = thisMonth.plusMonths(6).with(TemporalAdjusters.lastDayOfMonth());
        LocalDate iter = start;
        Set<String> monthSet = new HashSet<>();
        while (iter.isBefore(end)) {
            monthSet.add(DateUtil.getMonthStr(iter));
            iter = iter.plusMonths(1);
        }
        //收付款
        List<CollectionBaseInfo> collections = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .between(CollectionBaseInfo::getPlanCollectionDate, start, end)
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name()));
        Map<String, List<CollectionBaseInfo>> collectionMonthGroup = collections.stream()
                .collect(Collectors.groupingBy(collectionBaseInfo ->
                        DateUtil.getMonthStr(collectionBaseInfo.getPlanCollectionDate())));
        List<ChartBaseDataVO> rentReceivables = new ArrayList<>();
        List<ChartBaseDataVO> actualRepayment = new ArrayList<>();

        collectionMonthGroup.forEach((month, collectionBaseInfos) -> {
            ChartBaseDataVO rentReceivable = new ChartBaseDataVO();
            rentReceivable.setName(month);
            rentReceivable.setUnitDisplay("万元");
            rentReceivable.setValue(collectionBaseInfos.stream()
                    .map(CollectionBaseInfo::getPlanCollectionAmount)
                    .map(LongUtil::null2zero).map(BigDecimal::new)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP).toString());
            rentReceivables.add(rentReceivable);

            ChartBaseDataVO actualRepaymentData = new ChartBaseDataVO();
            actualRepaymentData.setName(month);
            rentReceivable.setUnitDisplay("万元");
            actualRepaymentData.setValue(collectionBaseInfos.stream()
                    .map(CollectionBaseInfo::getCollectionAmount)
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
        //资金
        List<FundReceiptRepayCashFlow> repayCashFlows = repayCashFlowService.list(
                Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                        .le(FundReceiptRepayCashFlow::getRepayDate, end)
                        .ge(FundReceiptRepayCashFlow::getRepayDate, start));
        Map<String, List<FundReceiptRepayCashFlow>> repayMonthGroup = repayCashFlows.stream().collect(Collectors.groupingBy(lib ->
                DateUtil.getMonthStr(lib.getRepayDate())));
        List<ChartBaseDataVO> payableFunds = new ArrayList<>();
        List<ChartBaseDataVO> paidUpFunds = new ArrayList<>();
        repayMonthGroup.forEach((month, libs) -> {
            ChartBaseDataVO payableFund = new ChartBaseDataVO();
            payableFund.setUnitDisplay("万元");
            payableFund.setName(month);
            payableFund.setValue(libs.stream().map(FundReceiptRepayCashFlow::getRepayAmount)
                    .map(LongUtil::null2zero).map(BigDecimal::new)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP).toString());
            payableFunds.add(payableFund);

            ChartBaseDataVO paidUpFund = new ChartBaseDataVO();
            paidUpFund.setUnitDisplay("万元");
            paidUpFund.setName(month);
            paidUpFund.setValue(libs.stream()
                    .filter(lib -> lib.getWriteOffState().equals(CashFlowState.WRITTEN_OFF.name()))
                    .map(FundReceiptRepayCashFlow::getRepayAmount)
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

}