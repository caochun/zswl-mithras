package cn.zswltech.mithras.fund.versioning.financing.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.fund.financing.baseinfo.FundFinancingBaseInfoDetailRSP;
import cn.zswltech.mithras.fund.versioning.financing.FundFinancingDetailConverter;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingLibModelEnum;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingBaseInfoLib;
import cn.zswltech.mithras.fund.versioning.financing.handler.FundFinancingAbstractLibHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

/**
 * @author dingqi
 * @date 2023/2/21
 * @description
 */
@Component
public class FundFinancingBaseInfoLibHandler extends FundFinancingAbstractLibHandler<FundFinancingBaseInfoLib, FundFinancingBaseInfo, FundFinancingBaseInfoDetailRSP> {
    @Resource
    private FundFinancingDetailConverter fundFinancingDetailConverter;

    @Override
    protected FundFinancingBaseInfoLib entity2Lib(FundFinancingBaseInfo f) {
        return BeanUtil.copyProperties(f, FundFinancingBaseInfoLib.class);
    }

    @Override
    protected FundFinancingBaseInfo lib2Entity(FundFinancingBaseInfoLib t) {
        return BeanUtil.copyProperties(t, FundFinancingBaseInfoLib.class);
    }

    @Override
    protected FundFinancingBaseInfoDetailRSP lib2Rsp(FundFinancingBaseInfoLib f) {
        return fundFinancingDetailConverter.convertBaseInfoToDetailRSP(this.actualLib2Entity(f), true);
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
        set.add("approvalStatus");
        set.add("financingStatus");
        //set.add("actualLoanDate");
        return set;
    }

    @Override
    public FundFinancingLibModelEnum getSubModule() {
        return FundFinancingLibModelEnum.BASE_INFO;
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
