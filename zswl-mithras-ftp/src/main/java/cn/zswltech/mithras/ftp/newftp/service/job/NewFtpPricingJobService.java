package cn.zswltech.mithras.ftp.newftp.service.job;

import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpFinancingCostPricingConfig;

import java.time.LocalDate;

public interface NewFtpPricingJobService {

    NewFtpFinancingCostPricingConfig latestFinancingCostPricingConfig();

    void addFinancingCostPricingConfig(LocalDate localDate);

    void addGuaranteeCostPricingConfig(LocalDate localDate);
}
