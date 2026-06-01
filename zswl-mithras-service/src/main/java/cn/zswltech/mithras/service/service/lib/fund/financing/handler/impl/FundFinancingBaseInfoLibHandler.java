package cn.zswltech.mithras.service.service.lib.fund.financing.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.fund.financing.baseinfo.FundFinancingBaseInfoDetailRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingLibModelEnum;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfoLib;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.service.service.lib.fund.financing.handler.FundFinancingAbstractLibHandler;
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
    private FundFinancingBaseInfoService financingBaseInfoService;

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
        return financingBaseInfoService.convertToDetailRSP(this.actualLib2Entity(f), true);
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
    public BusinessModuleEnum businessModuleEnum() {
        return BusinessModuleEnum.FUND_FINANCING;
    }
}
