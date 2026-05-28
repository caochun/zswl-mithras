package cn.zswltech.mithras.api.newftp.config;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.PageReq;
import cn.zswltech.mithras.dto.newftp.NewFtpTreasuryBondYieldListRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpTreasuryBondYieldPricingListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author yangxiong
 * @date 2024/3/29/15:28
 * @description
 */
@Api(tags = "FTP-十年期国债配置API")
public interface NewFtpTreasuryBondYieldConfigApi {

    @ApiOperation("10年期国债收益率列表")
    @PostMapping("/new/ftp/treasury/bond/yield/list")
    R<PageR<NewFtpTreasuryBondYieldListRSP>> list(@RequestBody @Valid PageReq req);

    @ApiOperation("10年期国债收益率定价列表")
    @PostMapping("/new/ftp/treasury/bond/yield/listpricing")
    R<PageR<NewFtpTreasuryBondYieldPricingListRSP>> listprincing(@RequestBody @Valid PageReq req);
}
