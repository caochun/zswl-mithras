package cn.zswltech.mithras.ftp.newftp.datacompare;

import cn.zswltech.mithras.dto.newftp.NewFtpDetailTreasuryBondYieldListRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.ftp.newftp.lib.impl.NewFtpShiborInterestRateLibHandler;
import cn.zswltech.mithras.ftp.newftp.mapper.lib.NewFtpShiborInterestRateLibMapper;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpShiborInterestRateDraft;
import cn.zswltech.mithras.ftp.newftp.model.lib.NewFtpShiborInterestRateLib;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("shiborInterestRate")
public class NewFtpShiborInterestRateFactory implements EditdataCompareFactory {

    @Resource
    private NewFtpShiborInterestRateLibMapper libMapper;
    @Resource
    private NewFtpShiborInterestRateLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<NewFtpShiborInterestRateDraft, NewFtpShiborInterestRateLib, NewFtpDetailTreasuryBondYieldListRSP>(rsps, libMapper, handler, commonVersionMapper, "NEW_FTP_GUIDANCE", version);
    }
}