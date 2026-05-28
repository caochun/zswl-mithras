package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyGuidanceExtDraftDetailRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.newftp.lib.impl.NewFtpMonthlyGuidanceExtLibHandler;
import cn.zswltech.mithras.service.service.newftp.mapper.lib.NewFtpMonthlyGuidanceExtLibMapper;
import cn.zswltech.mithras.service.service.newftp.model.draft.NewFtpMonthlyGuidanceExtDraft;
import cn.zswltech.mithras.service.service.newftp.model.lib.NewFtpMonthlyGuidanceExtLib;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("newFtpMonthlyGuidanceExt")
public class NewFtpMonthlyGuidanceExtFactory implements EditdataCompareFactory {

    @Resource
    private NewFtpMonthlyGuidanceExtLibMapper libMapper;
    @Resource
    private NewFtpMonthlyGuidanceExtLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<NewFtpMonthlyGuidanceExtDraft, NewFtpMonthlyGuidanceExtLib, NewFtpMonthlyGuidanceExtDraftDetailRSP>(rsps, libMapper, handler, commonVersionMapper, BusinessModuleEnum.NEW_FTP_GUIDANCE.name(), version);
    }
}