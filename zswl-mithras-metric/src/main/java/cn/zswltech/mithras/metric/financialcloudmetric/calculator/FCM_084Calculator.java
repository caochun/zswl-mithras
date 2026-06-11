package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.payment.mapper.PaymentActualDetailMapper;
import cn.zswltech.mithras.payment.mapper.model.PaymentActualDetail;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: 新增客户数（户） 有实际付款核销且客户创建在本年的客户数
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_084Calculator implements FinancialCloudMetricCalculator {

    @Resource
    private PaymentActualDetailMapper actualDetailMapper;

    @Override
    public String metricCode() {
        return "FCM_084";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        LocalDate start = dateTime.with(TemporalAdjusters.firstDayOfYear());
        LocalDate end = dateTime.with(TemporalAdjusters.lastDayOfMonth());
        //有实际投放的新增客户====》20250715变更逻辑：历史未投放过的都算为新增客户
        List<Long> existClientIdList = actualDetailMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                        .le(PaymentActualDetail::getPaidInDate, start))
                .stream().map(PaymentActualDetail::getClientId).collect(Collectors.toList());

        List<PaymentActualDetail> detailList = actualDetailMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                .notIn(PaymentActualDetail::getClientId, existClientIdList)
                .ge(PaymentActualDetail::getPaidInDate, start)
                .le(PaymentActualDetail::getPaidInDate, end));
        if (CollUtil.isEmpty(detailList)) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(detailList.stream().map(PaymentActualDetail::getClientId).distinct().count());
    }
}
