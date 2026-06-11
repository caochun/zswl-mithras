package cn.zswltech.mithras.ftp.datacompare;

import cn.zswltech.mithras.dto.ftp.FtpQuarterlyBasePricingRsp;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyMonthPricingRsp;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.ftp.oldftp.mapper.lib.FtpQuarterlyBasePricingLibMapper;
import cn.zswltech.mithras.ftp.oldftp.mapper.lib.FtpQuarterlyMonthPricingLibMapper;
import cn.zswltech.mithras.ftp.oldftp.model.FtpQuarterlyBasePricing;
import cn.zswltech.mithras.ftp.oldftp.model.FtpQuarterlyBasePricingLib;
import cn.zswltech.mithras.ftp.oldftp.model.FtpQuarterlyMonthPricing;
import cn.zswltech.mithras.ftp.oldftp.model.FtpQuarterlyMonthPricingLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.ftp.oldftp.lib.handler.impl.quarterly.FtpQuarterlyBasePricingLibHandler;
import cn.zswltech.mithras.ftp.oldftp.lib.handler.impl.quarterly.FtpQuarterlyMonthPricingLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("ftpQuarterlyMonthPricing")
public class FtpQuarterlyMonthPricingFactory implements EditdataCompareFactory {

    @Resource
    private FtpQuarterlyMonthPricingLibMapper libMapper;
    @Resource
    private FtpQuarterlyMonthPricingLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<FtpQuarterlyMonthPricing, FtpQuarterlyMonthPricingLib, FtpQuarterlyMonthPricingRsp>(rsps, libMapper, handler, commonVersionMapper,"FTP_QUARTERLY_GUIDANCE", version);
    }
}