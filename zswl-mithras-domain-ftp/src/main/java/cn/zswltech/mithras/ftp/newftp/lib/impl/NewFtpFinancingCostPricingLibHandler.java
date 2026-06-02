package cn.zswltech.mithras.ftp.newftp.lib.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.newftp.NewFtpFinancingCostPricingListRSP;
import cn.zswltech.mithras.ftp.newftp.enums.NewFtpSubModule;
import cn.zswltech.mithras.ftp.newftp.lib.NewFtpLibAbstractHandler;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpFinancingCostPricingDraft;
import cn.zswltech.mithras.ftp.newftp.model.lib.NewFtpFinancingCostPricingLib;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/11 16:56
 */
@Service
public class NewFtpFinancingCostPricingLibHandler
        extends NewFtpLibAbstractHandler<NewFtpFinancingCostPricingLib, NewFtpFinancingCostPricingDraft, NewFtpFinancingCostPricingListRSP> {

    @Override
    public Set<String> compareIgnoreFieldNames() {
        HashSet<String> fields = new HashSet<>();
        return fields;
    }
    @Override
    protected NewFtpFinancingCostPricingLib entity2Lib(NewFtpFinancingCostPricingDraft f) {
        return BeanUtil.copyProperties(f, NewFtpFinancingCostPricingLib.class);
    }

    @Override
    protected NewFtpFinancingCostPricingDraft lib2Entity(NewFtpFinancingCostPricingLib t) {
        return BeanUtil.copyProperties(t, NewFtpFinancingCostPricingDraft.class);
    }

    @Override
    protected NewFtpFinancingCostPricingListRSP lib2Rsp(NewFtpFinancingCostPricingLib f) {
        //todo 待实现
        return BeanUtil.copyProperties(f, NewFtpFinancingCostPricingListRSP.class);
    }

    @Override
    public NewFtpSubModule getSubModule() {
        return NewFtpSubModule.FINANCING_COST_PRICING;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }

}
