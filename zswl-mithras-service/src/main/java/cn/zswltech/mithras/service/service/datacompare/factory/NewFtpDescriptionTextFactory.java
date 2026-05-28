package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.newftp.NewFtpDescriptionTextListRsp;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.newftp.lib.impl.NewFtpDescriptionTextLibHandler;
import cn.zswltech.mithras.service.service.newftp.mapper.lib.NewFtpDescriptionTextLibMapper;
import cn.zswltech.mithras.service.service.newftp.model.draft.NewFtpDescriptionTextDraft;
import cn.zswltech.mithras.service.service.newftp.model.lib.NewFtpDescriptionTextLib;
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
        return new DefaultDataCompare<NewFtpDescriptionTextDraft, NewFtpDescriptionTextLib, NewFtpDescriptionTextListRsp>(rsps, libMapper, handler, commonVersionMapper, BusinessModuleEnum.NEW_FTP_GUIDANCE.name(), version);
    }
}