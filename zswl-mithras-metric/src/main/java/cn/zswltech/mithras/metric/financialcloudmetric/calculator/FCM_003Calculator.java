package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingTimeLimitTypeEnum;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.lib.financing.FinancingQueryDto;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingBaseInfoLib;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description: 业务类型为信用证时所有融资期限类型 的期末融资融资余额加总 （亿）
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_003Calculator extends FinancingBalanceCalculator {

    @Override
    public String metricCode() {
        return "FCM_003";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        return super.cal(dateTime);
    }


    @Override
    public Set<Long> getFinancingIds(LocalDate dateTime) {
        List<FundFinancingBaseInfo> fundFinancingBaseInfos = financingBaseInfoMapper.selectList(Wrappers.<FundFinancingBaseInfo>lambdaQuery()
                .in(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.EFFECT.name(), FundFinancingStatusEnum.CARRY_INTEREST.name(), FundFinancingStatusEnum.SETTLE.name())
                .eq(FundFinancingBaseInfo::getBusinessType, FundFinancingBizTypeEnum.LETTER_OF_CREDIT.name()));
        if (CollUtil.isEmpty(fundFinancingBaseInfos)) {
            return Collections.emptySet();
        }
        Map<Long, FundFinancingBaseInfo> baseInfoMap = fundFinancingBaseInfos.stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, Function.identity(), (v1, v2) -> v1));
        return baseInfoMap.values().stream().map(FundFinancingBaseInfo::getId).collect(Collectors.toSet());
    }
}
