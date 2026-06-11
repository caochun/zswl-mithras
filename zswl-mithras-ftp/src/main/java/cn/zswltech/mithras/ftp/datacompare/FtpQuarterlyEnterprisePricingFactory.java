package cn.zswltech.mithras.ftp.datacompare;

import cn.zswltech.mithras.dto.ftp.FtpQuarterlyEnterprisePricingRsp;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.ftp.oldftp.mapper.lib.FtpQuarterlyEnterprisePricingLibMapper;
import cn.zswltech.mithras.ftp.oldftp.model.FtpQuarterlyEnterprisePricing;
import cn.zswltech.mithras.ftp.oldftp.model.FtpQuarterlyEnterprisePricingLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.ftp.oldftp.lib.handler.impl.quarterly.FtpQuarterlyEnterprisePricingLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("ftpQuarterlyEnterprisePricing")
public class FtpQuarterlyEnterprisePricingFactory implements EditdataCompareFactory {

    @Resource
    private FtpQuarterlyEnterprisePricingLibMapper libMapper;
    @Resource
    private FtpQuarterlyEnterprisePricingLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<FtpQuarterlyEnterprisePricing, FtpQuarterlyEnterprisePricingLib, FtpQuarterlyEnterprisePricingRsp>(rsps, libMapper, handler, commonVersionMapper,"FTP_QUARTERLY_GUIDANCE", version);
    }
}