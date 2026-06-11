package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.fund.enums.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingTimeLimitTypeEnum;
import cn.zswltech.mithras.fund.mapper.lib.financing.FinancingQueryDto;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingBaseInfoLib;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description: 融资期限类型为短期的 保理融资/流动资金贷款/项目贷款 的期末融资融资余额加总 （亿）
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_004Calculator extends FinancingBalanceCalculator {
    @Override
    public String metricCode() {
        return "FCM_004";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        return super.cal(dateTime);
    }


    @Override
    public Set<Long> getFinancingIds(LocalDate dateTime) {
        List<FundFinancingBaseInfo> fundFinancingBaseInfos = financingBaseInfoMapper.selectList(Wrappers.<FundFinancingBaseInfo>lambdaQuery()
                .in(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.EFFECT.name(),
                        FundFinancingStatusEnum.CARRY_INTEREST.name(), FundFinancingStatusEnum.SETTLE.name())
                .in(FundFinancingBaseInfo::getBusinessType, Arrays.asList(FundFinancingBizTypeEnum.FACTORING_FINANCING.name(),
                        FundFinancingBizTypeEnum.WORKING_CAPITAL_LOAN.name(), FundFinancingBizTypeEnum.PROJECT_LOAN.name()))
                .eq(FundFinancingBaseInfo::getTimeLimitType, FundFinancingTimeLimitTypeEnum.SHORT_TERM_LOAN.name()));
        if (fundFinancingBaseInfos.isEmpty()) {
            return Collections.emptySet();
        }
        Map<Long, FundFinancingBaseInfo> baseInfoMap = fundFinancingBaseInfos.stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, Function.identity(), (v1, v2) -> v1));
        return baseInfoMap.values().stream().map(FundFinancingBaseInfo::getId).collect(Collectors.toSet());
    }
}
