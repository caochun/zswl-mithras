package cn.zswltech.mithras.ftp.datacompare;

import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyDeductionListRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.ftp.newftp.lib.impl.NewFtpMonthlyDeductionLibHandler;
import cn.zswltech.mithras.ftp.newftp.mapper.lib.NewFtpMonthlyDeductionLibMapper;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpMonthlyDeductionDraft;
import cn.zswltech.mithras.ftp.newftp.model.lib.NewFtpMonthlyDeductionLib;
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
        return new DefaultDataCompare<NewFtpMonthlyDeductionDraft, NewFtpMonthlyDeductionLib, NewFtpMonthlyDeductionListRSP>(rsps, libMapper, handler, commonVersionMapper, "NEW_FTP_GUIDANCE", version);
    }
}