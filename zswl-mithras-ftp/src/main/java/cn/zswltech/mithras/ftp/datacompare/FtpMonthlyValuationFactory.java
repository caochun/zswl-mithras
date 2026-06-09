package cn.zswltech.mithras.ftp.datacompare;

import cn.zswltech.mithras.dto.ftp.FtpMonthlyValuationRsp;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.ftp.oldftp.mapper.lib.FtpMonthlyValuationLibMapper;
import cn.zswltech.mithras.ftp.oldftp.model.FtpMonthlyValuation;
import cn.zswltech.mithras.ftp.oldftp.model.FtpMonthlyValuationLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.ftp.oldftp.lib.handler.impl.monthly.FtpMonthlyValuationLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("ftpMonthlyValuation")
public class FtpMonthlyValuationFactory implements EditdataCompareFactory {

    @Resource
    private FtpMonthlyValuationLibMapper libMapper;
    @Resource
    private FtpMonthlyValuationLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<FtpMonthlyValuation, FtpMonthlyValuationLib, FtpMonthlyValuationRsp>(rsps, libMapper, handler, commonVersionMapper,"FTP_MONTHLY_GUIDANCE", version);
    }
}