package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.service.service.lib.CommonVersionService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
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
    private CommonVersionMapper commonVersionMapper;
    @Resource
    private PaymentActualDetailService actualDetailService;

    @Override
    public String metricCode() {
        return "FCM_084";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        LocalDate start = dateTime.with(TemporalAdjusters.firstDayOfYear());
        LocalDate end = dateTime.with(TemporalAdjusters.lastDayOfMonth());
        //有实际投放的新增客户====》20250715变更逻辑：历史未投放过的都算为新增客户
        List<Long> existClientIdList = actualDetailService.list(Wrappers.<PaymentActualDetail>lambdaQuery()
                        .le(PaymentActualDetail::getPaidInDate, start))
                .stream().map(PaymentActualDetail::getClientId).collect(Collectors.toList());

        List<PaymentActualDetail> detailList = actualDetailService.list(Wrappers.<PaymentActualDetail>lambdaQuery()
                .notIn(PaymentActualDetail::getClientId, existClientIdList)
                .ge(PaymentActualDetail::getPaidInDate, start)
                .le(PaymentActualDetail::getPaidInDate, end));
        if (CollUtil.isEmpty(detailList)) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(detailList.stream().map(PaymentActualDetail::getClientId).distinct().count());
    }
}
