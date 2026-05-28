package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.payment.WriteOffStatus;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentActualDetail;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description: 新增客户投放金额（万元） 当年新增客户付款核销的实付金额之和（付款核销的实付日期在当年）
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_085Calculator implements FinancialCloudMetricCalculator {
    
    @Resource
    private PaymentActualDetailService actualDetailService;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public String metricCode() {
        return "FCM_085";
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
        return detailList.stream().map(PaymentActualDetail::getPaidInAmount)
                .map(LongUtil::null2zero).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
