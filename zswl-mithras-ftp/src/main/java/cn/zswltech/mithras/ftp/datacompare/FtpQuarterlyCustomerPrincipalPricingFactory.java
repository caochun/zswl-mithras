package cn.zswltech.mithras.ftp.datacompare;

import cn.zswltech.mithras.dto.ftp.FtpQuarterlyCustomerPrincipalPricingRsp;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.ftp.oldftp.mapper.lib.FtpQuarterlyCustomerPrincipalPricingLibMapper;
import cn.zswltech.mithras.ftp.oldftp.model.FtpQuarterlyCustomerPrincipalPricing;
import cn.zswltech.mithras.ftp.oldftp.model.FtpQuarterlyCustomerPrincipalPricingLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.ftp.oldftp.lib.handler.impl.quarterly.FtpQuarterlyCustomerPrincipalPricingHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("ftpQuarterlyCustomerPrincipalPricing")
public class FtpQuarterlyCustomerPrincipalPricingFactory implements EditdataCompareFactory {

    @Resource
    private FtpQuarterlyCustomerPrincipalPricingLibMapper libMapper;
    @Resource
    private FtpQuarterlyCustomerPrincipalPricingHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<FtpQuarterlyCustomerPrincipalPricing, FtpQuarterlyCustomerPrincipalPricingLib, FtpQuarterlyCustomerPrincipalPricingRsp>(rsps, libMapper, handler, commonVersionMapper,"FTP_QUARTERLY_GUIDANCE", version);
    }
}