package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingTimeLimitTypeEnum;
import cn.zswltech.mithras.fund.model.financing.FundFinancingBaseInfo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description: 融资期限类型为长期的 保理融资/流动资金贷款/项目贷款  （亿）
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_001Calculator extends FinancingBalanceCalculator {

    @Override
    public String metricCode() {
        return "FCM_001";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        return super.cal(dateTime);
    }


    @Override
    public Set<Long> getFinancingIds(LocalDate dateTime) {
        List<FundFinancingBaseInfo> fundFinancingBaseInfos = financingBaseInfoMapper.selectList(Wrappers.<FundFinancingBaseInfo>lambdaQuery()
                .in(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.EFFECT.name(), FundFinancingStatusEnum.CARRY_INTEREST.name(), FundFinancingStatusEnum.SETTLE.name())
                .in(FundFinancingBaseInfo::getBusinessType, Arrays.asList(FundFinancingBizTypeEnum.FACTORING_FINANCING.name(),
                        FundFinancingBizTypeEnum.WORKING_CAPITAL_LOAN.name(), FundFinancingBizTypeEnum.PROJECT_LOAN.name()))
                .eq(FundFinancingBaseInfo::getTimeLimitType, FundFinancingTimeLimitTypeEnum.LONG_TERM_LOAN.name()));
        if (CollUtil.isEmpty(fundFinancingBaseInfos)) {
            return Collections.emptySet();
        }
        Map<Long, FundFinancingBaseInfo> baseInfoMap = fundFinancingBaseInfos.stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, Function.identity(), (v1, v2) -> v1));
        return baseInfoMap.values().stream().map(FundFinancingBaseInfo::getId).collect(Collectors.toSet());
    }
}
