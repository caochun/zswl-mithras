package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyDeductionListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.newftp.lib.impl.NewFtpMonthlyDeductionLibHandler;
import cn.zswltech.mithras.service.service.newftp.mapper.lib.NewFtpMonthlyDeductionLibMapper;
import cn.zswltech.mithras.service.service.newftp.model.draft.NewFtpMonthlyDeductionDraft;
import cn.zswltech.mithras.service.service.newftp.model.lib.NewFtpMonthlyDeductionLib;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("newFtpDeduction")
public class NewFtpMonthlyDeductionFactory implements EditdataCompareFactory {

    @Resource
    private NewFtpMonthlyDeductionLibMapper libMapper;
    @Resource
    private NewFtpMonthlyDeductionLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<NewFtpMonthlyDeductionDraft, NewFtpMonthlyDeductionLib, NewFtpMonthlyDeductionListRSP>(rsps, libMapper, handler, commonVersionMapper, BusinessModuleEnum.NEW_FTP_GUIDANCE.name(), version);
    }
}