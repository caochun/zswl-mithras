package cn.zswltech.mithras.service.service.ftp;

import cn.zswltech.mithras.service.mapper.ftp.FtpQuarterlyCustomerPrincipalPricingMapper;
import cn.zswltech.mithras.service.mapper.model.ftp.FtpQuarterlyBasePricing;
import cn.zswltech.mithras.service.mapper.model.ftp.FtpQuarterlyCustomerPrincipalPricing;
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
