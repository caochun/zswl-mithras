package cn.zswltech.mithras.api.newftp.config;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.PageReq;
import cn.zswltech.mithras.dto.newftp.NewFtpShiborInterestRateListRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpShiborInterestRatePricingListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author yangxiong
 * @date 2024/3/29/15:17
 * @description
 */
@Api(tags = "FTP-一年期 shibor 利率配置API")
public interface NewFtpShiborInterestRateConfigApi {

    @ApiOperation("1年期SHIBOR利率列表")
    @PostMapping("/new/ftp/shibor/interest/rate/list")
    R<PageR<NewFtpShiborInterestRateListRSP>> list(@RequestBody @Valid PageReq req);

    @ApiOperation("一年期shibor利率定价列表")
    @PostMapping("/new/ftp/shibor/interest/rate/listprincing")
    R<PageR<NewFtpShiborInterestRatePricingListRSP>> listprincing(@RequestBody @Valid PageReq req);
}
