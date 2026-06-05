package cn.zswltech.mithras.service.application.payment;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.payment.application.FtpPriceApplicationService;
import cn.zswltech.mithras.api.payment.dto.FtpPriceCheckREQ;
import cn.zswltech.mithras.api.payment.dto.FtpPriceListREQ;
import cn.zswltech.mithras.api.payment.dto.FtpPriceListRSP;
import cn.zswltech.mithras.api.payment.dto.FtpPriceUpdateREQ;
import cn.zswltech.mithras.service.others.MithrasException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author yangxiong
 * @date 2024/8/12/17:15
 * @description
 */
@Deprecated
@Slf4j
@Service
public class FtpPriceFacade implements FtpPriceApplicationService {

//    @Resource
//    private FtpPriceInfoService ftpPriceInfoService;

    @Override
    public R<PageR<FtpPriceListRSP>> pageList(FtpPriceListREQ req) {
//        return R.ok(ftpPriceInfoService.pageList(req));
        throw new MithrasException("暂不支持的操作");
    }

    @Override
    public R<Void> update(FtpPriceUpdateREQ req) {
//        ftpPriceInfoService.updateRecord(req);
//        return R.ok();
        throw new MithrasException("暂不支持的操作");
    }

    @Override
    public R<Boolean> check(FtpPriceCheckREQ req) {
//        return R.ok(ftpPriceInfoService.check(req));
        throw new MithrasException("暂不支持的操作");
    }
}
