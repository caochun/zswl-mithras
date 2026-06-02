package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.newftp.NewFtpDetailLprPricingListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.ftp.newftp.lib.impl.NewFtpLprPricingLibHandler;
import cn.zswltech.mithras.ftp.newftp.mapper.lib.NewFtpLprPricingLibMapper;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpLprPricingDraft;
import cn.zswltech.mithras.ftp.newftp.model.lib.NewFtpLprPricingLib;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("lprPricing")
public class NewFtpLprPricingFactory implements EditdataCompareFactory {

    @Resource
    private NewFtpLprPricingLibMapper libMapper;
    @Resource
    private NewFtpLprPricingLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<NewFtpLprPricingDraft, NewFtpLprPricingLib, NewFtpDetailLprPricingListRSP>(rsps, libMapper, handler, commonVersionMapper, BusinessModuleEnum.NEW_FTP_GUIDANCE.name(), version);
    }
}