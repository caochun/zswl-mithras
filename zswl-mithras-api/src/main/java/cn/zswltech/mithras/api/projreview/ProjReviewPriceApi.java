package cn.zswltech.mithras.api.projreview;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewIRRSaveREQ;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailREQ;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailRSP;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceModifyREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@Api(tags = "项目评审-报价方案复合接口")
public interface ProjReviewPriceApi {

    @ApiOperation("项目评审修改报价方案")
    @PostMapping("/proj/review/price/modify")
    R<Void> modify(@RequestBody @Valid ProjReviewPriceModifyREQ req);

    @ApiOperation("项目评审查询报价方案")
    @PostMapping("/proj/review/price/detail")
    R<ProjReviewPriceDetailRSP> detail(@RequestBody @Valid ProjReviewPriceDetailREQ req);

    @ApiOperation("项目评审保存IRR")
    @PostMapping("/proj/review/price/irr/save")
    R<Void> saveIrrPercent(@RequestBody @Valid ProjReviewIRRSaveREQ req);
}
