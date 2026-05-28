package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.service.enums.fund.DirectFinancingType;
import cn.zswltech.mithras.service.enums.fund.financing.FinancingTypeEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingRepayActual;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingRepayActualService;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_007Calculator implements FinancialCloudMetricCalculator {

    @Resource
    private FundDirectFinancingBaseInfoService fundDirectFinancingBaseInfoService;
    @Resource
    private FundReceiptRepayBaseInfoService repayBaseInfoService;

    @Override
    public String metricCode() {
        return "FCM_007";
    }


    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        List<FundDirectFinancingBaseInfo> list = fundDirectFinancingBaseInfoService.list(Wrappers.<FundDirectFinancingBaseInfo>lambdaQuery()
                .in(FundDirectFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.EFFECT.name(), FundFinancingStatusEnum.CARRY_INTEREST.name(), FundFinancingStatusEnum.SETTLE.name())
                .eq(FundDirectFinancingBaseInfo::getDirectFinancingType, DirectFinancingType.ABS.name()));

        if (CollUtil.isEmpty(list)) {
            return BigDecimal.ZERO;
        }
        // 这里不能取融资金额，需要使用剩余本金
        Map<Long, Long> repayPrincipalMap = repayBaseInfoService.queryRemainingAmount(list.stream().map(FundDirectFinancingBaseInfo::getId).collect(Collectors.toList()), FinancingTypeEnum.DIRECT);
        if (repayPrincipalMap.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // 开始加权
        BigDecimal res = BigDecimal.ZERO;
        BigDecimal total = repayPrincipalMap.values().stream().map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        for (FundDirectFinancingBaseInfo financing : list) {
            res = new BigDecimal(LongUtil.null2zero(repayPrincipalMap.get(financing.getId())))
                    .multiply(new BigDecimal(LongUtil.null2zero(financing.getComprehensiveFinancingCost())))
                    .divide(total, 4, RoundingMode.HALF_UP)
                    .add(res);
        }
        return res.setScale(2, RoundingMode.HALF_UP);
    }
}
