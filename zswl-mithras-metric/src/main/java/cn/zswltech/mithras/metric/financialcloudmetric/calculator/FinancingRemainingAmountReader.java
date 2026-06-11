package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.capital.enums.FinanceCashFlowItemEnum;
import cn.zswltech.mithras.fund.enums.financing.FinancingTypeEnum;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.fund.mapper.receiptrepay.FundReceiptFlowDetailMapper;
import cn.zswltech.mithras.fund.mapper.receiptrepay.FundReceiptRepayBaseInfoMapper;
import cn.zswltech.mithras.fund.mapper.receiptrepay.FundReceiptRepayCashFlowMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FinancingRemainingAmountReader {

    @Resource
    private FundReceiptRepayBaseInfoMapper repayBaseInfoMapper;
    @Resource
    private FundReceiptRepayCashFlowMapper repayCashFlowMapper;
    @Resource
    private FundReceiptFlowDetailMapper receiptFlowDetailMapper;

    public Map<Long, Long> queryRemainingAmount(Collection<Long> financingIdList, FinancingTypeEnum financingTypeEnum) {
        if (CollectionUtil.isEmpty(financingIdList)) {
            return Collections.emptyMap();
        }

        LambdaQueryWrapper<FundReceiptRepayBaseInfo> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.select(FundReceiptRepayBaseInfo::getId);
        queryWrapper.in(FundReceiptRepayBaseInfo::getFinancingId, financingIdList);
        switch (financingTypeEnum) {
            case DIRECT:
                queryWrapper.eq(FundReceiptRepayBaseInfo::getFinancingType, FinancingTypeEnum.DIRECT.name());
                break;
            case INDIRECT:
                queryWrapper.isNull(FundReceiptRepayBaseInfo::getFinancingType);
                break;
            default:
                break;
        }
        List<FundReceiptRepayBaseInfo> receiptRepayList = repayBaseInfoMapper.selectList(queryWrapper);
        List<Long> receiptRepayIdList = receiptRepayList.stream().map(FundReceiptRepayBaseInfo::getId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(receiptRepayIdList)) {
            return Collections.emptyMap();
        }

        List<FundReceiptRepayCashFlow> cashFlowList = repayCashFlowMapper.selectList(Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                .in(FundReceiptRepayCashFlow::getReceiptRepayId, receiptRepayIdList)
                .ne(FundReceiptRepayCashFlow::getPhase, 0));
        if (CollectionUtil.isEmpty(cashFlowList)) {
            return Collections.emptyMap();
        }

        Map<String, List<FundReceiptFlowDetail>> flowDetailMap = Optional.ofNullable(receiptFlowDetailMapper.selectList(Wrappers.<FundReceiptFlowDetail>lambdaQuery()
                        .in(FundReceiptFlowDetail::getCashFlowCode, cashFlowList.stream().map(FundReceiptRepayCashFlow::getCashFlowCode).collect(Collectors.toList()))
                        .eq(FundReceiptFlowDetail::getCashFlowItem, FinanceCashFlowItemEnum.REPAY.name())))
                .orElse(new ArrayList<>()).stream().collect(Collectors.groupingBy(FundReceiptFlowDetail::getCashFlowCode));
        for (FundReceiptRepayCashFlow repayCashFlow : cashFlowList) {
            List<FundReceiptFlowDetail> flowDetailList = flowDetailMap.get(repayCashFlow.getCashFlowCode());
            if (CollectionUtil.isEmpty(flowDetailList)) {
                continue;
            }
            long alreadyWriteOffAmount = flowDetailList.stream()
                    .filter(f -> Objects.nonNull(f.getPrincipalAmount()))
                    .mapToLong(FundReceiptFlowDetail::getPrincipalAmount)
                    .sum();
            repayCashFlow.setPrincipleAmount(Optional.ofNullable(repayCashFlow.getPrincipleAmount()).orElse(0L) - alreadyWriteOffAmount);
        }
        return cashFlowList.stream().collect(Collectors.groupingBy(FundReceiptRepayCashFlow::getFinancingId,
                Collectors.summingLong(e -> Objects.isNull(e.getPrincipleAmount()) ? 0 : e.getPrincipleAmount())));
    }
}
