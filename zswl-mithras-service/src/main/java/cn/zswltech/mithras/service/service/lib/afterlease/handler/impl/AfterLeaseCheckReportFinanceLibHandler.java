package cn.zswltech.mithras.service.service.lib.afterlease.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportFinanceRSP;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckReportFinance;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckReportFinanceLib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.lib.afterlease.handler.AfterLeaseCheckReportLibAbstractHandler;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2022/12/15
 * @description
 */
@Component
public class AfterLeaseCheckReportFinanceLibHandler extends AfterLeaseCheckReportLibAbstractHandler<NewAfterLeaseCheckReportFinanceLib, NewAfterLeaseCheckReportFinance, AfterLeaseCheckReportFinanceRSP> {
    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }

    @Override
    protected NewAfterLeaseCheckReportFinanceLib entity2Lib(NewAfterLeaseCheckReportFinance f) {
        return BeanUtil.copyProperties(f, NewAfterLeaseCheckReportFinanceLib.class);
    }

    @Override
    protected NewAfterLeaseCheckReportFinance lib2Entity(NewAfterLeaseCheckReportFinanceLib t) {
        return BeanUtil.copyProperties(t, NewAfterLeaseCheckReportFinance.class);
    }

    @Override
    protected AfterLeaseCheckReportFinanceRSP lib2Rsp(NewAfterLeaseCheckReportFinanceLib f) {
        throw new MithrasException("暂不支持功能");
    }
}
