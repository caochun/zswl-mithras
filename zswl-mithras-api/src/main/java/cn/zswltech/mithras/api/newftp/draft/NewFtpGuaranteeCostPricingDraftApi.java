package cn.zswltech.mithras.api.newftp.draft;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.newftp.NewFtpCommonDetailReq;
import cn.zswltech.mithras.dto.newftp.NewFtpDetailReq;
import cn.zswltech.mithras.dto.newftp.NewFtpGuaranteeCostPricingListRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpGuaranteeCostPricingModifyREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author yangxiong
 * @date 2024/3/29/15:25
 * @description
 */
@Api(tags = "FTP-担保成本编辑区API")
public interface NewFtpGuaranteeCostPricingDraftApi {

    @ApiOperation("修改担保成本定价")
    @PostMapping("/new/ftp/guarantee/cost/pricing/draft/modify")
    R<Void> modifyDraft(@RequestBody @Valid NewFtpGuaranteeCostPricingModifyREQ req);

    @ApiOperation("编辑-担保成本定价列表")
    @PostMapping("/new/ftp/guarantee/cost/pricing/draft/list")
    R<PageR<NewFtpGuaranteeCostPricingListRSP>> listDraft(@RequestBody @Valid NewFtpCommonDetailReq req);

    @ApiOperation("编辑-修改担保成本刷新")
    @PostMapping("/new/ftp/guarantee/cost/pricing/draft/flash")
    R<Void> flashDraft(@RequestBody @Valid NewFtpDetailReq req);
}
