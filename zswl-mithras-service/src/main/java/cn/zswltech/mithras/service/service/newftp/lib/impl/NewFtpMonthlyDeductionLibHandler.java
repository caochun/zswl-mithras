package cn.zswltech.mithras.service.service.newftp.lib.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyDeductionListRSP;
import cn.zswltech.mithras.service.enums.newftp.NewFtpSubModule;
import cn.zswltech.mithras.service.service.newftp.lib.NewFtpLibAbstractHandler;
import cn.zswltech.mithras.service.service.newftp.model.draft.NewFtpMonthlyDeductionDraft;
import cn.zswltech.mithras.service.service.newftp.model.lib.NewFtpMonthlyDeductionLib;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/24 10:23
 */
@Component
public class NewFtpMonthlyDeductionLibHandler extends NewFtpLibAbstractHandler<NewFtpMonthlyDeductionLib, NewFtpMonthlyDeductionDraft, NewFtpMonthlyDeductionListRSP> {
    @Override
    protected NewFtpMonthlyDeductionLib entity2Lib(NewFtpMonthlyDeductionDraft f) {
        return BeanUtil.copyProperties(f, NewFtpMonthlyDeductionLib.class);
    }

    @Override
    protected NewFtpMonthlyDeductionDraft lib2Entity(NewFtpMonthlyDeductionLib t) {
        return BeanUtil.copyProperties(t, NewFtpMonthlyDeductionDraft.class);
    }

    @Override
    protected NewFtpMonthlyDeductionListRSP lib2Rsp(NewFtpMonthlyDeductionLib f) {
        return BeanUtil.copyProperties(f, NewFtpMonthlyDeductionListRSP.class);
    }

    @Override
    public NewFtpSubModule getSubModule() {
        return NewFtpSubModule.MONTHLY_DEDUCTION;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }
}
