package cn.zswltech.mithras.ftp.datacompare;

import cn.zswltech.mithras.dto.newftp.NewFtpDetailTreasuryBondYieldListRSP;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.ftp.newftp.lib.impl.NewFtpTreasuryBondYieldLibHandler;
import cn.zswltech.mithras.ftp.newftp.mapper.lib.NewFtpTreasuryBondYieldLibMapper;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpTreasuryBondYieldDraft;
import cn.zswltech.mithras.ftp.newftp.model.lib.NewFtpTreasuryBondYieldLib;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("treasuryBondYield")
public class NewFtpTreasuryBondYieldFactory implements EditdataCompareFactory {

    @Resource
    private NewFtpTreasuryBondYieldLibMapper libMapper;
    @Resource
    private NewFtpTreasuryBondYieldLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<NewFtpTreasuryBondYieldDraft, NewFtpTreasuryBondYieldLib, NewFtpDetailTreasuryBondYieldListRSP>(rsps, libMapper, handler, commonVersionMapper, "NEW_FTP_GUIDANCE", version);
    }
}