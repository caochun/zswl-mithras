package cn.zswltech.mithras.ftp.lib.handler.impl.quarterly;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyGuidanceRsp;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyMonthPricingRsp;
import cn.zswltech.mithras.ftp.enums.FtpQuarterlyInfoModule;
import cn.zswltech.mithras.ftp.model.FtpQuarterlyGuidance;
import cn.zswltech.mithras.ftp.model.FtpQuarterlyGuidanceLib;
import cn.zswltech.mithras.ftp.model.FtpQuarterlyMonthPricing;
import cn.zswltech.mithras.ftp.model.FtpQuarterlyMonthPricingLib;
import cn.zswltech.mithras.ftp.lib.handler.AbstractFtpQuarterlyLibHandler;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/11 15:36
 */
@Service
public class FtpQuarterlyGuidanceLibHandler
        extends AbstractFtpQuarterlyLibHandler<FtpQuarterlyGuidanceLib, FtpQuarterlyGuidance, FtpQuarterlyGuidanceRsp> {
    @Override
    protected FtpQuarterlyGuidanceLib entity2Lib(FtpQuarterlyGuidance f) {
        return BeanUtil.copyProperties(f, FtpQuarterlyGuidanceLib.class);
    }

    @Override
    protected FtpQuarterlyGuidance lib2Entity(FtpQuarterlyGuidanceLib t) {
        return BeanUtil.copyProperties(t, FtpQuarterlyGuidance.class);
    }

    @Override
    protected FtpQuarterlyGuidanceRsp lib2Rsp(FtpQuarterlyGuidanceLib f) {
        FtpQuarterlyGuidanceRsp rsp = BeanUtil.copyProperties(f, FtpQuarterlyGuidanceRsp.class);
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public FtpQuarterlyInfoModule getSubModule() {
        return FtpQuarterlyInfoModule.GUIDANCE;
    }
    @Override
    public String libMainIdFieldName() {
        return "origin_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "id";
    }
}
