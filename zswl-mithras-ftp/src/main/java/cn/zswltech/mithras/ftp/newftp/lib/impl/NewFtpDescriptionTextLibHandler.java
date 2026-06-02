package cn.zswltech.mithras.ftp.newftp.lib.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.newftp.NewFtpDescriptionTextListRsp;
import cn.zswltech.mithras.ftp.newftp.enums.NewFtpSubModule;
import cn.zswltech.mithras.ftp.newftp.lib.NewFtpLibAbstractHandler;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpDescriptionTextDraft;
import cn.zswltech.mithras.ftp.newftp.model.lib.NewFtpDescriptionTextLib;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/24 11:45
 */
@Component
public class NewFtpDescriptionTextLibHandler extends NewFtpLibAbstractHandler<NewFtpDescriptionTextLib, NewFtpDescriptionTextDraft, NewFtpDescriptionTextListRsp> {
    @Override
    protected NewFtpDescriptionTextLib entity2Lib(NewFtpDescriptionTextDraft f) {
        return BeanUtil.copyProperties(f, NewFtpDescriptionTextLib.class);
    }

    @Override
    protected NewFtpDescriptionTextDraft lib2Entity(NewFtpDescriptionTextLib t) {
        return BeanUtil.copyProperties(t, NewFtpDescriptionTextDraft.class);
    }

    @Override
    protected NewFtpDescriptionTextListRsp lib2Rsp(NewFtpDescriptionTextLib f) {
        return BeanUtil.copyProperties(f, NewFtpDescriptionTextListRsp.class);
    }

    @Override
    public NewFtpSubModule getSubModule() {
        return NewFtpSubModule.DESCRIPTION_TEXT;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }
}
