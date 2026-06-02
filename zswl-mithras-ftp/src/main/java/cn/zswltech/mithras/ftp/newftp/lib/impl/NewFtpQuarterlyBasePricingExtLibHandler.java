package cn.zswltech.mithras.ftp.newftp.lib.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.newftp.NewFtpQuarterlyBasePricingExtDraftDetailRSP;
import cn.zswltech.mithras.ftp.newftp.enums.NewFtpSubModule;
import cn.zswltech.mithras.ftp.newftp.lib.NewFtpLibAbstractHandler;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpQuarterlyBasePricingExtDraft;
import cn.zswltech.mithras.ftp.newftp.model.lib.NewFtpQuarterlyBasePricingExtLib;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2025/7/17
 * @description
 */
@Component
public class NewFtpQuarterlyBasePricingExtLibHandler extends NewFtpLibAbstractHandler<NewFtpQuarterlyBasePricingExtLib, NewFtpQuarterlyBasePricingExtDraft, NewFtpQuarterlyBasePricingExtDraftDetailRSP> {
    @Override
    public NewFtpSubModule getSubModule() {
        return NewFtpSubModule.QUARTERLY_PRICING_EXT;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }

    @Override
    protected NewFtpQuarterlyBasePricingExtLib entity2Lib(NewFtpQuarterlyBasePricingExtDraft f) {
        return BeanUtil.copyProperties(f, NewFtpQuarterlyBasePricingExtLib.class);
    }

    @Override
    protected NewFtpQuarterlyBasePricingExtDraft lib2Entity(NewFtpQuarterlyBasePricingExtLib t) {
        return BeanUtil.copyProperties(t, NewFtpQuarterlyBasePricingExtDraft.class);
    }

    @Override
    protected NewFtpQuarterlyBasePricingExtDraftDetailRSP lib2Rsp(NewFtpQuarterlyBasePricingExtLib f) {
        return BeanUtil.copyProperties(f, NewFtpQuarterlyBasePricingExtDraftDetailRSP.class);
    }
}
