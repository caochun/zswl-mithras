package cn.zswltech.mithras.ftp.oldftp.lib.handler.impl.quarterly;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyBasePricingRsp;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyMonthPricingRsp;
import cn.zswltech.mithras.ftp.oldftp.enums.FtpQuarterlyInfoModule;
import cn.zswltech.mithras.ftp.oldftp.model.FtpQuarterlyBasePricing;
import cn.zswltech.mithras.ftp.oldftp.model.FtpQuarterlyMonthPricing;
import cn.zswltech.mithras.ftp.oldftp.model.FtpQuarterlyMonthPricingLib;
import cn.zswltech.mithras.ftp.oldftp.lib.handler.AbstractFtpQuarterlyLibHandler;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/11 11:41
 */
@Service
public class FtpQuarterlyMonthPricingLibHandler
        extends AbstractFtpQuarterlyLibHandler<FtpQuarterlyMonthPricingLib, FtpQuarterlyMonthPricing, FtpQuarterlyMonthPricingRsp> {
    @Override
    protected FtpQuarterlyMonthPricingLib entity2Lib(FtpQuarterlyMonthPricing f) {
        return BeanUtil.copyProperties(f, FtpQuarterlyMonthPricingLib.class);
    }

    @Override
    protected FtpQuarterlyMonthPricing lib2Entity(FtpQuarterlyMonthPricingLib t) {
        return BeanUtil.copyProperties(t, FtpQuarterlyMonthPricing.class);
    }

    @Override
    protected FtpQuarterlyMonthPricingRsp lib2Rsp(FtpQuarterlyMonthPricingLib f) {
        FtpQuarterlyMonthPricingRsp rsp = BeanUtil.copyProperties(f, FtpQuarterlyMonthPricingRsp.class);
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public FtpQuarterlyInfoModule getSubModule() {
        return FtpQuarterlyInfoModule.MONTH_PRICING;
    }
}
