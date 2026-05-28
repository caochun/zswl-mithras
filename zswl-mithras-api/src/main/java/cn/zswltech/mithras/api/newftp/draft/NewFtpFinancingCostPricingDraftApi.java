package cn.zswltech.mithras.api.newftp.draft;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.newftp.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author yangxiong
 * @date 2024/3/29/15:24
 * @description
 */
@Api(tags = "FTP-融资成本定价编辑区API")
public interface NewFtpFinancingCostPricingDraftApi {

    @ApiOperation(value = "融资成本编辑区查询接口")
    @PostMapping(path = "/new/ftp/financing/cost/draft/detail")
    R<PageR<NewFtpFinancingCostPricingListRSP>> draftList(@RequestBody @Valid NewFtpCommonDetailReq req);

    @ApiOperation("融资成本定价刷新")
    @PostMapping("/new/ftp/financing/cost/pricing/draft/flash")
    R<Void> flashDraft(@RequestBody @Valid NewFtpFinancingCostPricingFlashREQ req);

    @ApiOperation("修改编辑区融资成本定价")
    @PostMapping("/new/ftp/financing/cost/pricing/draft/modify")
    R<Void> modify(@RequestBody @Valid NewFtpFinancingCostPricingModifyREQ req);
}
