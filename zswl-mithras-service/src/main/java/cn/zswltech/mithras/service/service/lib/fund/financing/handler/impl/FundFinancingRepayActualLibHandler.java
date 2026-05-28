package cn.zswltech.mithras.service.service.lib.fund.financing.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.fund.financing.repay.FundFinancingRepayActualListRSP;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingLibModelEnum;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingRepayActual;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingRepayActualLib;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingRepayActualService;
import cn.zswltech.mithras.service.service.lib.fund.financing.handler.FundFinancingAbstractLibHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/2/21
 * @description
 */
@Component
public class FundFinancingRepayActualLibHandler extends FundFinancingAbstractLibHandler<FundFinancingRepayActualLib, FundFinancingRepayActual, FundFinancingRepayActualListRSP> {
    @Resource
    private FundFinancingRepayActualService financingRepayActualService;

    @Override
    protected FundFinancingRepayActualLib entity2Lib(FundFinancingRepayActual f) {
        return BeanUtil.copyProperties(f, FundFinancingRepayActualLib.class);
    }

    @Override
    protected FundFinancingRepayActual lib2Entity(FundFinancingRepayActualLib t) {
        return BeanUtil.copyProperties(t, FundFinancingRepayActual.class);
    }

    @Override
    protected FundFinancingRepayActualListRSP lib2Rsp(FundFinancingRepayActualLib f) {
        if (ObjectUtil.isEmpty(f)) {
            return new FundFinancingRepayActualListRSP();
        }
        FundFinancingRepayActualListRSP rsp = new FundFinancingRepayActualListRSP();
        BeanUtil.copyProperties(actualLib2Entity(f), rsp);
        rsp.setRepayDate(LocalDateTimeUtil.format(f.getRepayDate(), DatePattern.NORM_DATE_PATTERN));
        return rsp;
    }

    @Override
    protected List<FundFinancingRepayActualListRSP> lib2RspList(List<FundFinancingRepayActualLib> libList) {
        if (CollectionUtil.isEmpty(libList)) {
            return Collections.emptyList();
        }
        List<FundFinancingRepayActual> entityList = libList.stream().map(this::actualLib2Entity).collect(Collectors.toList());
        return financingRepayActualService.convertToRSPList(entityList);
    }

    @Override
    public FundFinancingLibModelEnum getSubModule() {
        return FundFinancingLibModelEnum.REPAY_ACTUAL;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }
}
