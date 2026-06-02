package cn.zswltech.mithras.service.job;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpFinancingCostPricingConfig;
import cn.zswltech.mithras.service.service.newftp.service.config.NewFtpFinancingCostPricingConfigService;
import cn.zswltech.mithras.service.service.newftp.service.config.NewFtpGuaranteeCostPricingConfigService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

/**
 * @author dingqi
 * @date 2023/5/23
 * @description
 */
@Slf4j
@Component
public class NewFtpJob {

    @Resource
    private NewFtpFinancingCostPricingConfigService financingCostPricingConfigService;

    @Resource
    private NewFtpGuaranteeCostPricingConfigService guaranteeCostPricingConfigService;

    /**
     * 融资成本定价
     */
    @XxlJob("calculateFtpFinancingCostPricing")
    public void calculateFtpFinancingCostPricing() {
        try {
            log.info("calculateFtpFinancingCostPricing job began");
            NewFtpFinancingCostPricingConfig configServiceOne = financingCostPricingConfigService.getOne(Wrappers.<NewFtpFinancingCostPricingConfig>lambdaQuery()
                    .orderByDesc(NewFtpFinancingCostPricingConfig::getMonth)
                    .last(StringUtil.mysqlLimitOne()));
            LocalDate month = configServiceOne.getMonth().plusMonths(1);
            while (!month.equals(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()))) {
                financingCostPricingConfigService.add(month);
                month = month.plusMonths(1);
            }

        } catch (Exception e) {
            log.error("calculateFtpFinancingCostPricing job error", e);
        }
    }

    /**
     * 担保成本定价
     */
    @XxlJob("calculateFtpGuaranteeCostPricing")
    public void calculateFtpGuaranteeCostPricing() {
        try {
            log.info("calculateFtpGuaranteeCostPricing job began");
            LocalDate dataTime = LocalDate.now();
            String jobParam = XxlJobHelper.getJobParam();
            if (ObjectUtil.isNotEmpty(jobParam)) {
                dataTime = LocalDate.parse(jobParam, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        .with(TemporalAdjusters.firstDayOfMonth());
            }
            guaranteeCostPricingConfigService.addConfig(dataTime.minusMonths(1));
        } catch (Exception e) {
            log.error("calculateFtpGuaranteeCostPricing job error", e);
        }
    }
}
