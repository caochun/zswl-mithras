package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingBaseInfo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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
public class FCM_008Calculator extends AverageFinancingCostCalculator {


    @Override
    public String metricCode() {
        return "FCM_008";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        return super.cal(dateTime);
    }

    @Override
    protected List<FundFinancingBaseInfo> getFinancings(LocalDate dateTime) {
        List<FundFinancingBaseInfo> fundFinancingBaseInfos = financingBaseInfoMapper.selectList(Wrappers.<FundFinancingBaseInfo>lambdaQuery()
                .in(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.EFFECT.name(), FundFinancingStatusEnum.CARRY_INTEREST.name(), FundFinancingStatusEnum.SETTLE.name())
                .in(FundFinancingBaseInfo::getBusinessType, FundFinancingBizTypeEnum.LETTER_OF_CREDIT.name()));
        Map<Long, FundFinancingBaseInfo> baseInfoMap = fundFinancingBaseInfos.stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, Function.identity(), (v1, v2) -> v1));
        return new ArrayList<>(baseInfoMap.values());
    }
}
