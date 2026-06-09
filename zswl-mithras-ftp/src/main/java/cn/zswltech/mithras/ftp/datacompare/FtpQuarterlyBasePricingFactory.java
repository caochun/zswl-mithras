package cn.zswltech.mithras.ftp.datacompare;

import cn.zswltech.mithras.dto.ftp.FtpQuarterlyBasePricingRsp;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewLeasePriceRSP;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.ftp.oldftp.mapper.lib.FtpQuarterlyBasePricingLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projreview.ProjReviewLeasePriceLibMapper;
import cn.zswltech.mithras.ftp.oldftp.model.FtpQuarterlyBasePricing;
import cn.zswltech.mithras.ftp.oldftp.model.FtpQuarterlyBasePricingLib;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewLeasePriceLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.ftp.oldftp.lib.handler.impl.quarterly.FtpQuarterlyBasePricingLibHandler;
import cn.zswltech.mithras.projectprocess.service.lib.projreview.handler.impl.ProjReviewLeasePriceLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("ftpQuarterlyBasePricing")
public class FtpQuarterlyBasePricingFactory implements EditdataCompareFactory {

    @Resource
    private FtpQuarterlyBasePricingLibMapper libMapper;
    @Resource
    private FtpQuarterlyBasePricingLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<FtpQuarterlyBasePricing, FtpQuarterlyBasePricingLib, FtpQuarterlyBasePricingRsp>(rsps, libMapper, handler, commonVersionMapper,"FTP_QUARTERLY_GUIDANCE", version);
    }
}