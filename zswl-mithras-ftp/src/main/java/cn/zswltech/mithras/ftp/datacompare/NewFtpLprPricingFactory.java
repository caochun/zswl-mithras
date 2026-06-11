package cn.zswltech.mithras.ftp.datacompare;

import cn.zswltech.mithras.dto.newftp.NewFtpDetailLprPricingListRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
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
        return new DefaultDataCompare<NewFtpLprPricingDraft, NewFtpLprPricingLib, NewFtpDetailLprPricingListRSP>(rsps, libMapper, handler, commonVersionMapper, "NEW_FTP_GUIDANCE", version);
    }
}