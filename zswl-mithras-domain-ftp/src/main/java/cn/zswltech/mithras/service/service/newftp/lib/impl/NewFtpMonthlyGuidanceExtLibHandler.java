package cn.zswltech.mithras.service.service.newftp.lib.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyGuidanceExtDraftDetailRSP;
import cn.zswltech.mithras.service.enums.newftp.NewFtpSubModule;
import cn.zswltech.mithras.service.service.newftp.lib.NewFtpLibAbstractHandler;
import cn.zswltech.mithras.service.service.newftp.model.draft.NewFtpMonthlyGuidanceExtDraft;
import cn.zswltech.mithras.service.service.newftp.model.lib.NewFtpMonthlyGuidanceExtLib;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/24 10:31
 */
@Component
public class NewFtpMonthlyGuidanceExtLibHandler extends NewFtpLibAbstractHandler<NewFtpMonthlyGuidanceExtLib, NewFtpMonthlyGuidanceExtDraft, NewFtpMonthlyGuidanceExtDraftDetailRSP> {
    @Override
    protected NewFtpMonthlyGuidanceExtLib entity2Lib(NewFtpMonthlyGuidanceExtDraft f) {
        return BeanUtil.copyProperties(f, NewFtpMonthlyGuidanceExtLib.class);
    }

    @Override
    protected NewFtpMonthlyGuidanceExtDraft lib2Entity(NewFtpMonthlyGuidanceExtLib t) {
        return BeanUtil.copyProperties(t, NewFtpMonthlyGuidanceExtDraft.class);
    }

    @Override
    protected NewFtpMonthlyGuidanceExtDraftDetailRSP lib2Rsp(NewFtpMonthlyGuidanceExtLib f) {
        return BeanUtil.copyProperties(f, NewFtpMonthlyGuidanceExtDraftDetailRSP.class);
    }

    @Override
    public NewFtpSubModule getSubModule() {
        return NewFtpSubModule.MONTHLY_GUIDANCE_EXT;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }
}
