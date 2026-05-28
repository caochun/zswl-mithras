package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetricValue;
import cn.zswltech.mithras.metric.financialcloudmetric.service.FinancialCloudMetricValueService;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

/**
 * @description: 人均创利 累计利润/有效人数  万元/人 ==》改：指标：利润总额/在职员工数量（手工维护在职员工数量，根据手工维护的值，计算指标）
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_126Calculator extends FinancialReportRelatedCalculator {

    @Resource(name = "userServiceAPI")
    private UserService userService;
    @Resource
    private FinancialCloudMetricValueService metricValueService;

    @Override
    public String metricCode() {
        return "FCM_126";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {

        Example example = new Example(UserDO.class);
        example.createCriteria().andEqualTo("status", 0)
                .andNotIn("account", Arrays.asList("admin", "readonly"));
        int count = userService.selectCountByExample(example);
        FinancialCloudMetricValue fcm119 = metricValueService.getMetricValue("FCM_119", dateTime);
        if (Objects.isNull(fcm119) || Objects.isNull(fcm119.getValue())) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(fcm119.getMetricValue())
                .divide(new BigDecimal(count), 4, RoundingMode.HALF_UP);
    }
}
