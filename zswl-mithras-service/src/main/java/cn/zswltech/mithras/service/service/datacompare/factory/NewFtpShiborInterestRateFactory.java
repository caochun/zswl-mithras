package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.newftp.NewFtpDetailTreasuryBondYieldListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.newftp.lib.impl.NewFtpShiborInterestRateLibHandler;
import cn.zswltech.mithras.service.service.newftp.mapper.lib.NewFtpShiborInterestRateLibMapper;
import cn.zswltech.mithras.service.service.newftp.model.draft.NewFtpShiborInterestRateDraft;
import cn.zswltech.mithras.service.service.newftp.model.lib.NewFtpShiborInterestRateLib;
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
        return new DefaultDataCompare<NewFtpShiborInterestRateDraft, NewFtpShiborInterestRateLib, NewFtpDetailTreasuryBondYieldListRSP>(rsps, libMapper, handler, commonVersionMapper, BusinessModuleEnum.NEW_FTP_GUIDANCE.name(), version);
    }
}