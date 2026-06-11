package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.capital.enums.FinanceCashFlowItemEnum;
import cn.zswltech.mithras.fund.mapper.financing.FundFinancingBaseInfoMapper;
import cn.zswltech.mithras.fund.mapper.receiptrepay.FundReceiptFlowDetailMapper;
import cn.zswltech.mithras.fund.mapper.receiptrepay.FundReceiptRepayBaseInfoMapper;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptRepayBaseInfo;
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
    private FundReceiptFlowDetailMapper receiptFlowDetailMapper;
    @Resource
    private FundReceiptRepayBaseInfoMapper receiptRepayBaseInfoMapper;
    @Resource
    protected FundFinancingBaseInfoMapper financingBaseInfoMapper;

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
        List<FundReceiptRepayBaseInfo> receiptRepayBaseInfos = receiptRepayBaseInfoMapper.selectList(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                .in(FundReceiptRepayBaseInfo::getFinancingId, financingIds)
                .isNull(FundReceiptRepayBaseInfo::getFinancingType));
        if (CollUtil.isEmpty(receiptRepayBaseInfos)) {
            return BigDecimal.ZERO;
        }

        List<FundReceiptFlowDetail> detailList = receiptFlowDetailMapper.selectList(Wrappers.<FundReceiptFlowDetail>lambdaQuery()
                .le(FundReceiptFlowDetail::getCashFlowDate, end)
                .eq(FundReceiptFlowDetail::getCashFlowItem, FinanceCashFlowItemEnum.REPAY.name())
                .in(FundReceiptFlowDetail::getReceiptRepayId, receiptRepayBaseInfos.stream().map(FundReceiptRepayBaseInfo::getId).collect(Collectors.toList())));

        return receiptRepayBaseInfos.stream().map(FundReceiptRepayBaseInfo::getFinancingAmount)
                .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)
                .subtract(detailList.stream().map(FundReceiptFlowDetail::getPrincipalAmount)
                        .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add));
    }
}
