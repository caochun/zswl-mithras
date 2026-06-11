package cn.zswltech.mithras.ftp.datacompare;

import cn.zswltech.mithras.dto.newftp.NewFtpDescriptionTextListRsp;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.ftp.newftp.lib.impl.NewFtpDescriptionTextLibHandler;
import cn.zswltech.mithras.ftp.newftp.mapper.lib.NewFtpDescriptionTextLibMapper;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpDescriptionTextDraft;
import cn.zswltech.mithras.ftp.newftp.model.lib.NewFtpDescriptionTextLib;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("newFtpDescriptionText")
public class NewFtpDescriptionTextFactory implements EditdataCompareFactory {

    @Resource
    private NewFtpDescriptionTextLibMapper libMapper;
    @Resource
    private NewFtpDescriptionTextLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<NewFtpDescriptionTextDraft, NewFtpDescriptionTextLib, NewFtpDescriptionTextListRsp>(rsps, libMapper, handler, commonVersionMapper, "NEW_FTP_GUIDANCE", version);
    }
}