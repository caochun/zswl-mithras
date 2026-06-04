package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.fund.domain.enums.DirectFinancingType;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingRepayActual;
import cn.zswltech.mithras.service.fund.direct.mapper.FundDirectFinancingBaseInfoMapper;
import cn.zswltech.mithras.service.fund.direct.mapper.FundDirectFinancingRepayActualMapper;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description: 业务类型为ABS时所有融资期限类型  的期末融资融资余额加总 （亿）
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_002Calculator implements FinancialCloudMetricCalculator {

    @Resource
    private FundDirectFinancingBaseInfoMapper fundDirectFinancingBaseInfoMapper;

    @Resource
    private FundDirectFinancingRepayActualMapper fundDirectFinancingRepayActualMapper;

    @Override

    public String metricCode() {
        return "FCM_002";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        List<FundDirectFinancingBaseInfo> directFinancingBaseInfoList = fundDirectFinancingBaseInfoMapper.selectList(Wrappers.<FundDirectFinancingBaseInfo>lambdaQuery()
                .in(FundDirectFinancingBaseInfo::getDirectFinancingType, DirectFinancingType.ABS.name(), DirectFinancingType.ABN.name()));
        if (ObjectUtil.isEmpty(directFinancingBaseInfoList)) {
            return BigDecimal.ZERO;
        }
        Set<Long> absFinancingIds = directFinancingBaseInfoList.stream().map(FundDirectFinancingBaseInfo::getId).collect(Collectors.toSet());
        List<FundDirectFinancingRepayActual> list = fundDirectFinancingRepayActualMapper.selectList(Wrappers.<FundDirectFinancingRepayActual>lambdaQuery()
                .in(FundDirectFinancingRepayActual::getFinancingId, absFinancingIds)
                .le(FundDirectFinancingRepayActual::getRepayDate, dateTime.with(TemporalAdjusters.lastDayOfMonth()))
                .orderByDesc(FundDirectFinancingRepayActual::getPhase));
        if (list.isEmpty()) {
            return BigDecimal.ZERO;
        }
        Map<Long, List<FundDirectFinancingRepayActual>> collect = list.stream().collect(Collectors.groupingBy(FundDirectFinancingRepayActual::getFinancingId));

        return collect.values().stream()
                .map(v -> v.get(0).getRemainingPrincipleAmount())
                .map(LongUtil::null2zero).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
