package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.liquiditymanage.mapper.model.AccountBalanceBaseInfo;
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
 * @description: 未来六月回款额 租金计划-从当月起，租金应收日期为未来六个月（不包括当月）的计划收款金额（不区分租金、核销状态）加总（取账户余额表）
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Slf4j
@Component
public class FCM_130Calculator implements FinancialCloudMetricCalculator {

    @Resource
    private AccountBalanceBaseInfoService accountBalanceBaseInfoService;

    @Override
    public String metricCode() {
        return "FCM_130";
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

        // 租金应收日期为未来六个月（不包括当月）的计划收款金额
        return infoList.stream().map(e -> new BigDecimal(LongUtil.null2zero(e.getRentReflowAmount()))).reduce(BigDecimal::add).get();
    }
}
