package cn.zswltech.mithras.ftp.lib.handler.impl.quarterly;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyBasePricingRsp;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyCustomerPrincipalPricingRsp;
import cn.zswltech.mithras.ftp.enums.FtpQuarterlyInfoModule;
import cn.zswltech.mithras.ftp.model.FtpQuarterlyBasePricingLib;
import cn.zswltech.mithras.ftp.model.FtpQuarterlyCustomerPrincipalPricing;
import cn.zswltech.mithras.ftp.model.FtpQuarterlyCustomerPrincipalPricingLib;
import cn.zswltech.mithras.ftp.lib.handler.AbstractFtpQuarterlyLibHandler;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/11 11:37
 */
@Service
public class FtpQuarterlyCustomerPrincipalPricingHandler
        extends AbstractFtpQuarterlyLibHandler<FtpQuarterlyCustomerPrincipalPricingLib, FtpQuarterlyCustomerPrincipalPricing, FtpQuarterlyCustomerPrincipalPricingRsp> {
    @Override
    protected FtpQuarterlyCustomerPrincipalPricingLib entity2Lib(FtpQuarterlyCustomerPrincipalPricing f) {
        return BeanUtil.copyProperties(f, FtpQuarterlyCustomerPrincipalPricingLib.class);
    }

    @Override
    protected FtpQuarterlyCustomerPrincipalPricing lib2Entity(FtpQuarterlyCustomerPrincipalPricingLib t) {
        return BeanUtil.copyProperties(t, FtpQuarterlyCustomerPrincipalPricing.class);
    }

    @Override
    protected FtpQuarterlyCustomerPrincipalPricingRsp lib2Rsp(FtpQuarterlyCustomerPrincipalPricingLib f) {
        FtpQuarterlyCustomerPrincipalPricingRsp rsp = BeanUtil.copyProperties(f, FtpQuarterlyCustomerPrincipalPricingRsp.class);
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public FtpQuarterlyInfoModule getSubModule() {
        return FtpQuarterlyInfoModule.CUSTOMER_PRICING;
    }
}
