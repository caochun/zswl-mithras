package cn.zswltech.mithras.api.newftp.config;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.PageReq;
import cn.zswltech.mithras.dto.newftp.NewFtpFinancingCostPricingFlashREQ;
import cn.zswltech.mithras.dto.newftp.NewFtpFinancingCostPricingListRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpFinancingCostPricingModifyREQ;
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
@Api(tags = "FTP-融资成本定价配置API")
public interface NewFtpFinancingCostPricingConfigApi {

    @ApiOperation("融资成本定价刷新")
    @PostMapping("/new/ftp/financing/cost/pricing/flash")
    R<Void> flash(@RequestBody @Valid NewFtpFinancingCostPricingFlashREQ req);

    @ApiOperation("融资成本定价列表")
    @PostMapping("/new/ftp/financing/cost/pricing/list")
    R<PageR<NewFtpFinancingCostPricingListRSP>> financingCostList(@RequestBody @Valid PageReq req);

    @ApiOperation("修改融资成本定价")
    @PostMapping("/new/ftp/financing/cost/pricing/modify")
    R<Void> modify(@RequestBody @Valid NewFtpFinancingCostPricingModifyREQ req);
}
