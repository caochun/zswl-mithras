package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.service.mapper.model.liquiditymanage.AccountBalanceBaseInfo;
import cn.zswltech.mithras.service.service.liquiditymanage.AccountBalanceBaseInfoService;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

/**
 * @description: 未来六月还款额 还本付息-从当月起，还本付息应付日期为未来六个月（不包括当月）的应还金额加总（取账户余额表）
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Slf4j
@Component
public class FCM_129Calculator implements FinancialCloudMetricCalculator {

    @Resource
    private AccountBalanceBaseInfoService accountBalanceBaseInfoService;

    @Override
    public String metricCode() {
        return "FCM_129";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        LocalDate start = dateTime.with(firstDayOfMonth()).plusMonths(1);
        LocalDate end = start.with(lastDayOfMonth()).plusMonths(5);
        // 查询数据
        List<AccountBalanceBaseInfo> infoList = accountBalanceBaseInfoService.list(Wrappers.<AccountBalanceBaseInfo>lambdaQuery()
                .ge(AccountBalanceBaseInfo::getDate, start)
                .le(AccountBalanceBaseInfo::getDate, end));
        if (infoList.isEmpty()) {
            log.error("FCM_129:查询账户余额表数据为空");
            return BigDecimal.ZERO;
        }

        // 计算 有编辑字段取编辑字段（还本付息）
        return infoList.stream().map(e -> {
            return new BigDecimal(LongUtil.null2zero(e.getRepayEditAmount() != null ? e.getRepayEditAmount() : e.getRepayAmount()));
        }).reduce(BigDecimal::add).get();
    }
}
