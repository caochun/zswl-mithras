package cn.zswltech.mithras.service.service.ftp;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.mapper.model.ftp.*;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import liquibase.pro.packaged.F;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @description excel数据处理
* @author zhaozhengkang
* @date 2023-01-09
*/
@Service
public class FtpQuarterlyPricingService {

    @Resource
    private FtpQuarterlyBasePricingService basePricingService;
    @Resource
    private FtpQuarterlyMonthPricingService monthPricingService;
    @Resource
    private FtpQuarterlyEnterprisePricingService enterprisePricingService;
    @Resource
    private FtpQuarterlyCustomerPrincipalPricingService customerPricingService;

    @Transactional(rollbackFor = Throwable.class)
    public void saveBasePricings(Long guidanceId, List<FtpQuarterlyBasePricing> pricings) {
        if(ObjectUtil.isEmpty(pricings)){
            return;
        }
        List<FtpQuarterlyBasePricing> dbPricings = basePricingService.selectByGuidanceId(guidanceId);
        if(ObjectUtil.isEmpty(dbPricings)){
            basePricingService.saveBatch(pricings);
        }else {
            Map<String, FtpQuarterlyBasePricing> dbPricingMap = new HashMap<>();
            dbPricings.forEach(pricing -> {
                if(ObjectUtil.isNotEmpty(pricing.getSite())) {
                    dbPricingMap.put(pricing.getSite(), pricing);
                }
            });
            Map<String, FtpQuarterlyBasePricing> pricingMap =  new HashMap<>();
            pricings.forEach(pricing -> {
                if(ObjectUtil.isNotEmpty(pricing.getSite())) {
                    pricingMap.put(pricing.getSite(), pricing);
                }
            });
            dbPricingMap.forEach((site, dbPricing) -> {
                FtpQuarterlyBasePricing newPricing = pricingMap.get(site);
                dbPricing.setPercentValue(newPricing.getPercentValue());
            });
            basePricingService.updateBatchById(dbPricingMap.values());
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void saveMonthPricings(Long guidanceId, List<FtpQuarterlyMonthPricing> pricings) {
        if(ObjectUtil.isEmpty(pricings)){
            return;
        }
        List<FtpQuarterlyMonthPricing> dbPricings = monthPricingService.selectByGuidanceId(guidanceId);
        if(ObjectUtil.isEmpty(dbPricings)){
            monthPricingService.saveBatch(pricings);
        }else {
            Map<String, FtpQuarterlyMonthPricing> dbPricingMap = new HashMap<>();
            dbPricings.forEach(pricing -> {
                if(ObjectUtil.isNotEmpty(pricing.getSite())) {
                    dbPricingMap.put(pricing.getSite(), pricing);
                }
            });
            Map<String, FtpQuarterlyMonthPricing> pricingMap =  new HashMap<>();
            pricings.forEach(pricing -> {
                if(ObjectUtil.isNotEmpty(pricing.getSite())) {
                    pricingMap.put(pricing.getSite(), pricing);
                }
            });
            dbPricingMap.forEach((site, dbPricing) -> {
                FtpQuarterlyMonthPricing newPricing = pricingMap.get(site);
                dbPricing.setPercentValue(newPricing.getPercentValue());
            });
            monthPricingService.updateBatchById(dbPricingMap.values());
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void saveCustomerPricings(Long guidanceId, List<FtpQuarterlyCustomerPrincipalPricing> pricings) {
        if(ObjectUtil.isEmpty(pricings)){
            return;
        }
        List<FtpQuarterlyCustomerPrincipalPricing> dbPricings = customerPricingService.selectByGuidanceId(guidanceId);
        if(ObjectUtil.isEmpty(dbPricings)){
            customerPricingService.saveBatch(pricings);
        }else {
            Map<String, FtpQuarterlyCustomerPrincipalPricing> dbPricingMap = new HashMap<>();
            dbPricings.forEach(pricing -> {
                if(ObjectUtil.isNotEmpty(pricing.getSite())) {
                    dbPricingMap.put(pricing.getSite(), pricing);
                }
            });
            Map<String, FtpQuarterlyCustomerPrincipalPricing> pricingMap =  new HashMap<>();
            pricings.forEach(pricing -> {
                if(ObjectUtil.isNotEmpty(pricing.getSite())) {
                    pricingMap.put(pricing.getSite(), pricing);
                }
            });
            dbPricingMap.forEach((site, dbPricing) -> {
                FtpQuarterlyCustomerPrincipalPricing newPricing = pricingMap.get(site);
                dbPricing.setPercentValue(newPricing.getPercentValue());
            });
            customerPricingService.updateBatchById(dbPricingMap.values());
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void saveEnterprisePricings(Long guidanceId, List<FtpQuarterlyEnterprisePricing> pricings) {
        if(ObjectUtil.isEmpty(pricings)){
            return;
        }
        List<FtpQuarterlyEnterprisePricing> dbPricings = enterprisePricingService.selectByGuidanceId(guidanceId);
        if(ObjectUtil.isEmpty(dbPricings)){
            enterprisePricingService.saveBatch(pricings);
        }else {
            Map<String, FtpQuarterlyEnterprisePricing> dbPricingMap = new HashMap<>();
            dbPricings.forEach(pricing -> {
                if(ObjectUtil.isNotEmpty(pricing.getSite())) {
                    dbPricingMap.put(pricing.getSite(), pricing);
                }
            });
            Map<String, FtpQuarterlyEnterprisePricing> pricingMap =  new HashMap<>();
            pricings.forEach(pricing -> {
                if(ObjectUtil.isNotEmpty(pricing.getSite())) {
                    pricingMap.put(pricing.getSite(), pricing);
                }
            });
            dbPricingMap.forEach((site, dbPricing) -> {
                FtpQuarterlyEnterprisePricing newPricing = pricingMap.get(site);
                dbPricing.setPercentValue(newPricing.getPercentValue());
            });
            enterprisePricingService.updateBatchById(dbPricingMap.values());
        }
    }
}