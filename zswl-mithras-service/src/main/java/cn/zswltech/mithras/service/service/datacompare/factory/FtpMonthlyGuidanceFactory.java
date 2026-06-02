package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.ftp.FtpMonthlyGuidanceDetailRsp;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.ftp.oldftp.mapper.lib.FtpMonthlyGuidanceLibMapper;
import cn.zswltech.mithras.ftp.oldftp.model.FtpMonthlyGuidance;
import cn.zswltech.mithras.ftp.oldftp.model.FtpMonthlyGuidanceLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;

import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.ftp.oldftp.lib.handler.impl.monthly.FtpMonthlyGuidanceLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("ftpMonthlyGuidance")
public class FtpMonthlyGuidanceFactory implements EditdataCompareFactory {

    @Resource
    private FtpMonthlyGuidanceLibMapper libMapper;
    @Resource
    private FtpMonthlyGuidanceLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<FtpMonthlyGuidance, FtpMonthlyGuidanceLib, FtpMonthlyGuidanceDetailRsp>(rsps, libMapper, handler, commonVersionMapper,BusinessModuleEnum.FTP_MONTHLY_GUIDANCE.name(), version);
    }
}