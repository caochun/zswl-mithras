package cn.zswltech.mithras.ftp.lib.handler.impl.quarterly;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyCustomerPrincipalPricingRsp;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyEnterprisePricingRsp;
import cn.zswltech.mithras.ftp.enums.FtpQuarterlyInfoModule;
import cn.zswltech.mithras.ftp.model.FtpQuarterlyBasePricing;
import cn.zswltech.mithras.ftp.model.FtpQuarterlyEnterprisePricing;
import cn.zswltech.mithras.ftp.model.FtpQuarterlyEnterprisePricingLib;
import cn.zswltech.mithras.ftp.lib.handler.AbstractFtpQuarterlyLibHandler;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/11 11:42
 */
@Service
public class FtpQuarterlyEnterprisePricingLibHandler
        extends AbstractFtpQuarterlyLibHandler<FtpQuarterlyEnterprisePricingLib, FtpQuarterlyEnterprisePricing, FtpQuarterlyEnterprisePricingRsp> {
    @Override
    protected FtpQuarterlyEnterprisePricingLib entity2Lib(FtpQuarterlyEnterprisePricing f) {
        return BeanUtil.copyProperties(f, FtpQuarterlyEnterprisePricingLib.class);
    }

    @Override
    protected FtpQuarterlyEnterprisePricing lib2Entity(FtpQuarterlyEnterprisePricingLib t) {
        return BeanUtil.copyProperties(t, FtpQuarterlyEnterprisePricing.class);
    }

    @Override
    protected FtpQuarterlyEnterprisePricingRsp lib2Rsp(FtpQuarterlyEnterprisePricingLib f) {
        FtpQuarterlyEnterprisePricingRsp rsp = BeanUtil.copyProperties(f, FtpQuarterlyEnterprisePricingRsp.class);
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public FtpQuarterlyInfoModule getSubModule() {
        return FtpQuarterlyInfoModule.ENTERPRISE_PRICING;
    }
}
