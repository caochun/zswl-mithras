package cn.zswltech.mithras.payment.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.payment.FtpPriceApi;
import cn.zswltech.mithras.api.payment.dto.FtpPriceCheckREQ;
import cn.zswltech.mithras.api.payment.dto.FtpPriceListREQ;
import cn.zswltech.mithras.api.payment.dto.FtpPriceListRSP;
import cn.zswltech.mithras.api.payment.dto.FtpPriceUpdateREQ;
import cn.zswltech.mithras.payment.application.FtpPriceApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class FtpPriceController implements FtpPriceApi {
    @Resource
    private FtpPriceApplicationService ftpPriceApplicationService;

    @Override
    public R<PageR<FtpPriceListRSP>> pageList(FtpPriceListREQ req) {
        return ftpPriceApplicationService.pageList(req);
    }

    @Override
    public R<Void> update(FtpPriceUpdateREQ req) {
        return ftpPriceApplicationService.update(req);
    }

    @Override
    public R<Boolean> check(FtpPriceCheckREQ req) {
        return ftpPriceApplicationService.check(req);
    }

}
