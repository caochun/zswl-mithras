package cn.zswltech.mithras.api.payment;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.payment.dto.FtpPriceCheckREQ;
import cn.zswltech.mithras.api.payment.dto.FtpPriceListREQ;
import cn.zswltech.mithras.api.payment.dto.FtpPriceListRSP;
import cn.zswltech.mithras.api.payment.dto.FtpPriceUpdateREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author yangxiong
 * @date 2024/8/12/16:49
 * @description
 */
@Deprecated
@Api(tags = "ftp价格表")
@RequestMapping("/ftp/price")
public interface FtpPriceApi {

    @ApiOperation(value = "ftp价格表列表")
    @PostMapping(path = "/list")
    R<PageR<FtpPriceListRSP>> pageList(@RequestBody FtpPriceListREQ req);

    @ApiOperation(value = "修改ftp价格表记录")
    @PostMapping(path = "/update")
    R<Void> update(@RequestBody @Valid FtpPriceUpdateREQ req);

    @ApiOperation(value = "根据日期修改数据前置校验")
    @PostMapping(path = "/check")
    R<Boolean> check(@RequestBody @Valid FtpPriceCheckREQ req);
}
