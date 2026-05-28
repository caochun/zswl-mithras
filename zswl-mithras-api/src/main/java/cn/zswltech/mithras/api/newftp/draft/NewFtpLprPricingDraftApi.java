package cn.zswltech.mithras.api.newftp.draft;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.newftp.NewFtpCommonDetailReq;
import cn.zswltech.mithras.dto.newftp.NewFtpDetailLprPricingListRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpLprPricingModifyREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author yangxiong
 * @date 2024/3/29/15:22
 * @description
 */
@Api(tags = "FTP-LPR定价编辑区API")
public interface NewFtpLprPricingDraftApi {

    @ApiOperation("详情页-LPR列表")
    @PostMapping("/new/ftp/detail/lpr/pricing")
    R<PageR<NewFtpDetailLprPricingListRSP>> lprPricingList(@RequestBody @Valid NewFtpCommonDetailReq req);

    @ApiOperation("修改LPR定价")
    @PostMapping("/new/ftp/lpr/pricing/modify")
    R<Void> modify(@RequestBody @Valid NewFtpLprPricingModifyREQ req);
}
