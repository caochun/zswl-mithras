package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.capital.domain.enums.FinanceCashFlowItemEnum;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptFlowDetailService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/4/24 18:54
 */
public abstract class FinancingBalanceCalculator implements FinancialCloudMetricCalculator {

    @Resource
    private FundReceiptFlowDetailService receiptFlowDetailService;
    @Resource
    private FundReceiptRepayBaseInfoService receiptRepayBaseInfoService;
    @Resource
    protected FundFinancingBaseInfoService financingBaseInfoService;

    /**
     * 获取需统计的融资id
     *
     * @return
     */
    public abstract Set<Long> getFinancingIds(LocalDate dateTime);

    public BigDecimal cal(LocalDate dateTime) {
        // 结束日为当月最后一天
        LocalDate end = dateTime.with(TemporalAdjusters.lastDayOfMonth());
        Set<Long> financingIds = getFinancingIds(dateTime);
        if (financingIds.isEmpty()) {
            return BigDecimal.ZERO;
        }
        // 这里改从还本付息拿数据
        List<FundReceiptRepayBaseInfo> receiptRepayBaseInfos = receiptRepayBaseInfoService.list(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                .in(FundReceiptRepayBaseInfo::getFinancingId, financingIds)
                .isNull(FundReceiptRepayBaseInfo::getFinancingType));
        if (CollUtil.isEmpty(receiptRepayBaseInfos)) {
            return BigDecimal.ZERO;
        }

        List<FundReceiptFlowDetail> detailList = receiptFlowDetailService.list(Wrappers.<FundReceiptFlowDetail>lambdaQuery()
                .le(FundReceiptFlowDetail::getCashFlowDate, end)
                .eq(FundReceiptFlowDetail::getCashFlowItem, FinanceCashFlowItemEnum.REPAY.name())
                .in(FundReceiptFlowDetail::getReceiptRepayId, receiptRepayBaseInfos.stream().map(FundReceiptRepayBaseInfo::getId).collect(Collectors.toList())));

        return receiptRepayBaseInfos.stream().map(FundReceiptRepayBaseInfo::getFinancingAmount)
                .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)
                .subtract(detailList.stream().map(FundReceiptFlowDetail::getPrincipalAmount)
                        .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add));
    }
}
