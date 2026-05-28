package cn.zswltech.mithras.api.projpricing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingIRRSaveREQ;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingPriceDetailREQ;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingPriceDetailRSP;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingPriceModifyREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@Api(tags = "项目定价-报价方案复合接口")
public interface ProjPricingPriceApi {

    @ApiOperation("项目定价修改报价方案")
    @PostMapping("/proj/pricing/price/modify")
    R<Void> modify(@RequestBody @Valid ProjPricingPriceModifyREQ req);

    @ApiOperation("项目定价查询报价方案")
    @PostMapping("/proj/pricing/price/detail")
    R<ProjPricingPriceDetailRSP> detail(@RequestBody @Valid ProjPricingPriceDetailREQ req);

    @ApiOperation("项目定价保存IRR")
    @PostMapping("/proj/pricing/price/irr/save")
    R<Void> saveIrrPercent(@RequestBody @Valid ProjPricingIRRSaveREQ req);
}
