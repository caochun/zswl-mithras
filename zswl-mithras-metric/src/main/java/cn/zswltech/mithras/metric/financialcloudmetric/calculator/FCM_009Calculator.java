package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.fund.enums.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingTimeLimitTypeEnum;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingBaseInfo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_009Calculator extends AverageFinancingCostCalculator {


    @Override
    public String metricCode() {
        return "FCM_009";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        return super.cal(dateTime);
    }

    @Override
    protected List<FundFinancingBaseInfo> getFinancings(LocalDate dateTime) {
        List<FundFinancingBaseInfo> fundFinancingBaseInfos = financingBaseInfoMapper.selectList(Wrappers.<FundFinancingBaseInfo>lambdaQuery()
                .in(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.EFFECT.name(),
                        FundFinancingStatusEnum.CARRY_INTEREST.name(), FundFinancingStatusEnum.SETTLE.name())
                .in(FundFinancingBaseInfo::getBusinessType, Arrays.asList(FundFinancingBizTypeEnum.FACTORING_FINANCING.name(),
                        FundFinancingBizTypeEnum.WORKING_CAPITAL_LOAN.name(), FundFinancingBizTypeEnum.PROJECT_LOAN.name()))
                .eq(FundFinancingBaseInfo::getTimeLimitType, FundFinancingTimeLimitTypeEnum.SHORT_TERM_LOAN.name()));
        Map<Long, FundFinancingBaseInfo> baseInfoMap = fundFinancingBaseInfos.stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, Function.identity(), (v1, v2) -> v1));
        return new ArrayList<>(baseInfoMap.values());
    }
}
