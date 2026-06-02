package cn.zswltech.mithras.ftp.lib.handler.impl.monthly;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.ftp.FtpMonthlyPricingRsp;
import cn.zswltech.mithras.ftp.enums.FtpMonthlyInfoModule;
import cn.zswltech.mithras.ftp.model.FtpMonthlyPricing;
import cn.zswltech.mithras.ftp.model.FtpMonthlyPricingLib;
import cn.zswltech.mithras.ftp.lib.handler.AbstractFtpMonthlyLibHandler;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/11 17:00
 */
@Service
public class FtpMonthlyPricingLibHandler
        extends AbstractFtpMonthlyLibHandler<FtpMonthlyPricingLib, FtpMonthlyPricing, FtpMonthlyPricingRsp> {
    @Override
    protected FtpMonthlyPricingLib entity2Lib(FtpMonthlyPricing f) {
        return BeanUtil.copyProperties(f, FtpMonthlyPricingLib.class);
    }

    @Override
    protected FtpMonthlyPricing lib2Entity(FtpMonthlyPricingLib t) {
        return BeanUtil.copyProperties(t, FtpMonthlyPricing.class);
    }

    @Override
    protected FtpMonthlyPricingRsp lib2Rsp(FtpMonthlyPricingLib f) {
        FtpMonthlyPricingRsp rsp = BeanUtil.copyProperties(f, FtpMonthlyPricingRsp.class);
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public FtpMonthlyInfoModule getSubModule() {
        return FtpMonthlyInfoModule.PRICING;
    }
}
