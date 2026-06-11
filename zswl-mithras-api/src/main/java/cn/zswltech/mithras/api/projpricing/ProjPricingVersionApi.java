package cn.zswltech.mithras.api.projpricing;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.projpricing.ProjPricingEffectREQ;
import cn.zswltech.mithras.dto.projpricing.ProjPricingVersionDiffREQ;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingCompareREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;


@Api(tags = "项目定价-版本管理接口")
public interface ProjPricingVersionApi {

    /**
     * 定价信息生效（或提交审批）
     * @param req
     * @return
     */
    @ApiOperation("定价信息生效（或提交审批）")
    @PostMapping("/proj/pricing/effect")
    R<Void> effect(@RequestBody @Valid ProjPricingEffectREQ req);

    /**
     * 定价信息版本表列
     * @param req
     * @return
     */
    @ApiOperation("定价信息版本表列")
    @PostMapping("/proj/pricing/version/list")
    R<PageR<CommonVersionListRSP>> list(@RequestBody @Valid CommonVersionListREQ req);

    /**
     * 定价信息版本比较详情（与上一版本比较）
     * @param req
     * @return
     */
    @ApiOperation("定价信息版本比较详情（与上一版本比较）")
    @PostMapping("/proj/pricing/compare/preVersion")
    R<CommonVersionDiffRSP> comparePreVersion(@RequestBody @Valid ProjPricingVersionDiffREQ req);


    @ApiOperation("项目定价-项目评审 基本信息-对比")
    @PostMapping("/proj/pricing/review/base/info/detail/compare")
    R<Map<String, DiffValue>> projPricingReviewBaseInfoCompare(@RequestBody @Valid ProjPricingCompareREQ req);

    @ApiOperation("项目定价-项目评审 报价方案-对比")
    @PostMapping("/proj/pricing/review/price/detail/compare")
    R<Map<String, DiffValue>> projPricingReviewPriceCompare(@RequestBody @Valid ProjPricingCompareREQ req);

    @ApiOperation("项目定价-项目评审 现金流计划表-对比")
    @PostMapping("/proj/pricing/review/cashflowplan/list/compare")
    R<List<Map<String, DiffValue>>> projPricingReviewCashFlowPlanCompare(@RequestBody @Valid ProjPricingCompareREQ req);


}