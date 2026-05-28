package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.ftp.FtpMonthlyPricingRsp;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.ftp.FtpMonthlyPricingLibMapper;
import cn.zswltech.mithras.service.mapper.model.ftp.FtpMonthlyPricing;
import cn.zswltech.mithras.service.mapper.model.ftp.FtpMonthlyPricingLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.lib.ftp.handler.impl.monthly.FtpMonthlyPricingLibHandler;
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
        return new DefaultDataCompare<FtpMonthlyPricing, FtpMonthlyPricingLib, FtpMonthlyPricingRsp>(rsps, libMapper, handler, commonVersionMapper,BusinessModuleEnum.FTP_MONTHLY_GUIDANCE.name(), version);
    }
}