package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.ftp.FtpQuarterlyBasePricingRsp;
import cn.zswltech.mithras.dto.ftp.FtpQuarterlyMonthPricingRsp;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.ftp.FtpQuarterlyBasePricingLibMapper;
import cn.zswltech.mithras.service.mapper.lib.ftp.FtpQuarterlyMonthPricingLibMapper;
import cn.zswltech.mithras.service.mapper.model.ftp.FtpQuarterlyBasePricing;
import cn.zswltech.mithras.service.mapper.model.ftp.FtpQuarterlyBasePricingLib;
import cn.zswltech.mithras.service.mapper.model.ftp.FtpQuarterlyMonthPricing;
import cn.zswltech.mithras.service.mapper.model.ftp.FtpQuarterlyMonthPricingLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.lib.ftp.handler.impl.quarterly.FtpQuarterlyBasePricingLibHandler;
import cn.zswltech.mithras.service.service.lib.ftp.handler.impl.quarterly.FtpQuarterlyMonthPricingLibHandler;
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
        return new DefaultDataCompare<FtpQuarterlyMonthPricing, FtpQuarterlyMonthPricingLib, FtpQuarterlyMonthPricingRsp>(rsps, libMapper, handler, commonVersionMapper,BusinessModuleEnum.FTP_QUARTERLY_GUIDANCE.name(), version);
    }
}