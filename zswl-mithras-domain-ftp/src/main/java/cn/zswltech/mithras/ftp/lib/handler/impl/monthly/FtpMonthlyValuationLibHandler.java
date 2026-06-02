package cn.zswltech.mithras.ftp.lib.handler.impl.monthly;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.ftp.FtpMonthlyValuationRsp;
import cn.zswltech.mithras.ftp.enums.FtpMonthlyInfoModule;
import cn.zswltech.mithras.ftp.model.FtpMonthlyValuation;
import cn.zswltech.mithras.ftp.model.FtpMonthlyValuationLib;
import cn.zswltech.mithras.ftp.lib.handler.AbstractFtpMonthlyLibHandler;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/11 17:01
 */
@Service
public class FtpMonthlyValuationLibHandler extends AbstractFtpMonthlyLibHandler<FtpMonthlyValuationLib, FtpMonthlyValuation, FtpMonthlyValuationRsp> {
    @Override
    protected FtpMonthlyValuationLib entity2Lib(FtpMonthlyValuation f) {
        return BeanUtil.copyProperties(f, FtpMonthlyValuationLib.class);
    }

    @Override
    protected FtpMonthlyValuation lib2Entity(FtpMonthlyValuationLib t) {
        return BeanUtil.copyProperties(t, FtpMonthlyValuation.class);
    }

    @Override
    protected FtpMonthlyValuationRsp lib2Rsp(FtpMonthlyValuationLib f) {
        FtpMonthlyValuationRsp rsp = BeanUtil.copyProperties(f, FtpMonthlyValuationRsp.class);
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public FtpMonthlyInfoModule getSubModule() {
        return FtpMonthlyInfoModule.VALUATION;
    }
}
