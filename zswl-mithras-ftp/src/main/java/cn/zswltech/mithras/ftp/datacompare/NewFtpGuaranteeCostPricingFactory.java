package cn.zswltech.mithras.ftp.datacompare;

import cn.zswltech.mithras.dto.newftp.NewFtpGuaranteeCostPricingListRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.ftp.newftp.lib.impl.NewFtpGuaranteeCostPricingLibHandler;
import cn.zswltech.mithras.ftp.newftp.mapper.lib.NewFtpGuaranteeCostPricingLibMapper;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpGuaranteeCostPricingDraft;
import cn.zswltech.mithras.ftp.newftp.model.lib.NewFtpGuaranteeCostPricingLib;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("guaranteeCostPricing")
public class NewFtpGuaranteeCostPricingFactory implements EditdataCompareFactory {

    @Resource
    private NewFtpGuaranteeCostPricingLibMapper libMapper;
    @Resource
    private NewFtpGuaranteeCostPricingLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<NewFtpGuaranteeCostPricingDraft, NewFtpGuaranteeCostPricingLib, NewFtpGuaranteeCostPricingListRSP>(rsps, libMapper, handler, commonVersionMapper, "NEW_FTP_GUIDANCE", version);
    }
}