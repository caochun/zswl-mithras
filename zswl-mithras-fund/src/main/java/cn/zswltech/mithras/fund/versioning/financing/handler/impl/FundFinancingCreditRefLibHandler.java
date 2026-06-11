package cn.zswltech.mithras.fund.versioning.financing.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.fund.financing.FundFinancingCreditRefDetailRSP;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingLibModelEnum;
import cn.zswltech.mithras.fund.model.financing.FundFinancingCreditRef;
import cn.zswltech.mithras.fund.model.financing.FundFinancingCreditRefLib;
import cn.zswltech.mithras.fund.versioning.financing.handler.FundFinancingAbstractLibHandler;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;


@Component
public class FundFinancingCreditRefLibHandler extends FundFinancingAbstractLibHandler<FundFinancingCreditRefLib, FundFinancingCreditRef, FundFinancingCreditRefDetailRSP> {

    @Override
    protected FundFinancingCreditRefLib entity2Lib(FundFinancingCreditRef f) {
        return BeanUtil.copyProperties(f, FundFinancingCreditRefLib.class);
    }

    @Override
    protected FundFinancingCreditRef lib2Entity(FundFinancingCreditRefLib t) {
        return BeanUtil.copyProperties(t, FundFinancingCreditRef.class);
    }

    @Override
    protected FundFinancingCreditRefDetailRSP lib2Rsp(FundFinancingCreditRefLib f) {
        return BeanUtil.copyProperties(f, FundFinancingCreditRefDetailRSP.class);
    }

    @Override
    public String libMainIdFieldName() {
        return "origin_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "id";
    }

    @Override
    public Set<String> compareIgnoreFieldNames() {
        Set<String> set = new HashSet<>();
        return set;
    }

    @Override
    public FundFinancingLibModelEnum getSubModule() {
        return FundFinancingLibModelEnum.FINANCING_CREDIT_REF;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }
}
