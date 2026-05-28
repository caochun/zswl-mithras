package cn.zswltech.mithras.service.service.lib.ftp.handler.impl.quarterly;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyBasePricingRsp;
import cn.zswltech.mithras.dto.projestablish.priceaoc.ProjEstablishAocPriceRSP;
import cn.zswltech.mithras.service.enums.ftp.FtpQuarterlyInfoModule;
import cn.zswltech.mithras.service.mapper.model.ftp.FtpQuarterlyBasePricing;
import cn.zswltech.mithras.service.mapper.model.ftp.FtpQuarterlyBasePricingLib;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishAocPrice;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishAocPriceLib;
import cn.zswltech.mithras.service.service.lib.ftp.handler.AbstractFtpQuarterlyLibHandler;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/11 11:30
 */
@Service
public class FtpQuarterlyBasePricingLibHandler
        extends AbstractFtpQuarterlyLibHandler<FtpQuarterlyBasePricingLib, FtpQuarterlyBasePricing, FtpQuarterlyBasePricingRsp> {
    @Override
    public FtpQuarterlyInfoModule getSubModule() {
        return FtpQuarterlyInfoModule.BASE_PRICING;
    }

    @Override
    protected FtpQuarterlyBasePricingLib entity2Lib(FtpQuarterlyBasePricing f) {
        return BeanUtil.copyProperties(f, FtpQuarterlyBasePricingLib.class);
    }

    @Override
    protected FtpQuarterlyBasePricing lib2Entity(FtpQuarterlyBasePricingLib t) {
        return BeanUtil.copyProperties(t, FtpQuarterlyBasePricing.class);
    }

    @Override
    protected FtpQuarterlyBasePricingRsp lib2Rsp(FtpQuarterlyBasePricingLib f) {
        FtpQuarterlyBasePricingRsp rsp = BeanUtil.copyProperties(f, FtpQuarterlyBasePricingRsp.class);
        rsp.setId(f.getOriginId());
        return rsp;
    }
}
