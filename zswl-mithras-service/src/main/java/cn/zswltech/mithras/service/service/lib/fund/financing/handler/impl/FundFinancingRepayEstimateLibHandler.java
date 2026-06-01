package cn.zswltech.mithras.service.service.lib.fund.financing.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.fund.financing.repay.FundFinancingRepayActualListRSP;
import cn.zswltech.mithras.dto.fund.financing.repay.FundFinancingRepayEstimateListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingLibModelEnum;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingRepayEstimate;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingRepayEstimateLib;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingRepayEstimateService;
import cn.zswltech.mithras.service.service.lib.fund.financing.handler.FundFinancingAbstractLibHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/2/21
 * @description
 */
@Component
public class FundFinancingRepayEstimateLibHandler extends FundFinancingAbstractLibHandler<FundFinancingRepayEstimateLib, FundFinancingRepayEstimate, FundFinancingRepayEstimateListRSP> {
    @Resource
    private FundFinancingRepayEstimateService financingRepayEstimateService;

    @Override
    public Set<String> compareIgnoreFieldNames() {
        HashSet<String> fields = new HashSet<>();
        return fields;
    }

    @Override
    protected FundFinancingRepayEstimateLib entity2Lib(FundFinancingRepayEstimate f) {
        return BeanUtil.copyProperties(f, FundFinancingRepayEstimateLib.class);
    }

    @Override
    protected FundFinancingRepayEstimate lib2Entity(FundFinancingRepayEstimateLib t) {
        return BeanUtil.copyProperties(t, FundFinancingRepayEstimate.class);
    }

    @Override
    protected FundFinancingRepayEstimateListRSP lib2Rsp(FundFinancingRepayEstimateLib f) {
        if (ObjectUtil.isEmpty(f)) {
            return new FundFinancingRepayActualListRSP();
        }
        FundFinancingRepayEstimateListRSP rsp = new FundFinancingRepayEstimateListRSP();
        BeanUtil.copyProperties(actualLib2Entity(f), rsp);
        rsp.setRepayDate(LocalDateTimeUtil.format(f.getRepayDate(), DatePattern.NORM_DATE_PATTERN));
        return rsp;
    }

    @Override
    protected List<FundFinancingRepayEstimateListRSP> lib2RspList(List<FundFinancingRepayEstimateLib> libList) {
        if (CollectionUtil.isEmpty(libList)) {
            return Collections.emptyList();
        }
        List<FundFinancingRepayEstimate> entityList = libList.stream().map(this::actualLib2Entity).collect(Collectors.toList());
        return financingRepayEstimateService.convertToRSPList(entityList);
    }
    @Override
    public FundFinancingLibModelEnum getSubModule() {
        return FundFinancingLibModelEnum.REPAY_ESTIMATE;
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
