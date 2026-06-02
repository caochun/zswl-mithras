package cn.zswltech.mithras.ftp.service;

import cn.zswltech.mithras.ftp.mapper.FtpQuarterlyCustomerPrincipalPricingMapper;
import cn.zswltech.mithras.ftp.model.FtpQuarterlyBasePricing;
import cn.zswltech.mithras.ftp.model.FtpQuarterlyCustomerPrincipalPricing;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/9 16:04
 */
@Service
public class FtpQuarterlyCustomerPrincipalPricingService
        extends ServiceImpl<FtpQuarterlyCustomerPrincipalPricingMapper, FtpQuarterlyCustomerPrincipalPricing> {
    public List<FtpQuarterlyCustomerPrincipalPricing> selectByGuidanceId(Long guidanceId) {
        return baseMapper.selectList(Wrappers.<FtpQuarterlyCustomerPrincipalPricing>lambdaQuery().eq(FtpQuarterlyCustomerPrincipalPricing::getGuidanceId, guidanceId));
    }
}
