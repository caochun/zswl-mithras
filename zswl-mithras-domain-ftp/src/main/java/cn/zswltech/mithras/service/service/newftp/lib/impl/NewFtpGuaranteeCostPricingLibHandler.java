package cn.zswltech.mithras.service.service.newftp.lib.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.newftp.NewFtpGuaranteeCostPricingListRSP;
import cn.zswltech.mithras.service.enums.newftp.NewFtpSubModule;
import cn.zswltech.mithras.service.service.newftp.lib.NewFtpLibAbstractHandler;
import cn.zswltech.mithras.service.service.newftp.model.draft.NewFtpGuaranteeCostPricingDraft;
import cn.zswltech.mithras.service.service.newftp.model.lib.NewFtpGuaranteeCostPricingLib;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/11 16:56
 */
@Service
public class NewFtpGuaranteeCostPricingLibHandler
        extends NewFtpLibAbstractHandler<NewFtpGuaranteeCostPricingLib, NewFtpGuaranteeCostPricingDraft, NewFtpGuaranteeCostPricingListRSP> {

    @Override
    public Set<String> compareIgnoreFieldNames() {
        HashSet<String> fields = new HashSet<>();
        return fields;
    }
    @Override
    protected NewFtpGuaranteeCostPricingLib entity2Lib(NewFtpGuaranteeCostPricingDraft f) {
        return BeanUtil.copyProperties(f, NewFtpGuaranteeCostPricingLib.class);
    }

    @Override
    protected NewFtpGuaranteeCostPricingDraft lib2Entity(NewFtpGuaranteeCostPricingLib t) {
        return BeanUtil.copyProperties(t, NewFtpGuaranteeCostPricingDraft.class);
    }

    @Override
    protected NewFtpGuaranteeCostPricingListRSP lib2Rsp(NewFtpGuaranteeCostPricingLib f) {
        return BeanUtil.copyProperties(f, NewFtpGuaranteeCostPricingListRSP.class);
    }

    @Override
    public NewFtpSubModule getSubModule() {
        return NewFtpSubModule.GUARANTEE_COST_PRICING;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }

}
