package cn.zswltech.mithras.fund.application.lib.financing.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.fund.financing.earlysettle.FundFinancingEarlySettlePlanRSP;
import cn.zswltech.mithras.fund.application.lib.financing.FundFinancingDetailConverter;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingLibModelEnum;
import cn.zswltech.mithras.fund.model.financing.FundFinancingEarlySettlePlan;
import cn.zswltech.mithras.fund.model.financing.FundFinancingEarlySettlePlanLib;
import cn.zswltech.mithras.fund.application.lib.financing.handler.FundFinancingAbstractLibHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2023/2/23
 * @description
 */
@Component
public class FundFinancingEarlySettlePlanLibHandler extends FundFinancingAbstractLibHandler<FundFinancingEarlySettlePlanLib, FundFinancingEarlySettlePlan, FundFinancingEarlySettlePlanRSP> {
    @Resource
    private FundFinancingDetailConverter fundFinancingDetailConverter;

    @Override
    protected FundFinancingEarlySettlePlanLib entity2Lib(FundFinancingEarlySettlePlan f) {
        return BeanUtil.copyProperties(f, FundFinancingEarlySettlePlanLib.class);
    }

    @Override
    protected FundFinancingEarlySettlePlan lib2Entity(FundFinancingEarlySettlePlanLib t) {
        return BeanUtil.copyProperties(t, FundFinancingEarlySettlePlan.class);
    }

    @Override
    protected FundFinancingEarlySettlePlanRSP lib2Rsp(FundFinancingEarlySettlePlanLib f) {
        return fundFinancingDetailConverter.convertEarlySettlePlanToRSP(this.actualLib2Entity(f), f.getFinancingId());
    }

    @Override
    public FundFinancingLibModelEnum getSubModule() {
        return FundFinancingLibModelEnum.EARLYSETTLEPLAN;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }

    @Override
    protected String businessModuleName() {
        return "FUND_FINANCING";
    }
}
