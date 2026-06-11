package cn.zswltech.mithras.ftp.datacompare;

import cn.zswltech.mithras.dto.ftp.FtpMonthlyPricingRsp;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.ftp.oldftp.mapper.lib.FtpMonthlyPricingLibMapper;
import cn.zswltech.mithras.ftp.oldftp.model.FtpMonthlyPricing;
import cn.zswltech.mithras.ftp.oldftp.model.FtpMonthlyPricingLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.ftp.oldftp.lib.handler.impl.monthly.FtpMonthlyPricingLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("ftpMonthlyPricing")
public class FtpMonthlyPricingFactory implements EditdataCompareFactory {

    @Resource
    private FtpMonthlyPricingLibMapper libMapper;
    @Resource
    private FtpMonthlyPricingLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<FtpMonthlyPricing, FtpMonthlyPricingLib, FtpMonthlyPricingRsp>(rsps, libMapper, handler, commonVersionMapper,"FTP_MONTHLY_GUIDANCE", version);
    }
}