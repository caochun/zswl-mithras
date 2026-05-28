package cn.zswltech.mithras.api.projreview;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.projestablish.ProjEstablishEffectREQ;
import cn.zswltech.mithras.dto.projestablish.version.ProjEstablishVersionDiffREQ;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingCompareREQ;
import cn.zswltech.mithras.dto.projreview.*;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewCompareREQ;
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

/**
 * 项目评审-版本管理接口
 * @description
 * @date 2022-07-19
 */
@Api(tags = "项目评审-版本管理接口")
public interface ProjReviewVersionApi {
    @ApiOperation("检查评审客户是否缺少材料")
    @PostMapping("/proj/review/client/materials/check")
    R<ClientMaterialsLackInfoRSP> checkClientMaterials(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("检查评审相关评级信息")
    @PostMapping("/proj/review/rating/check")
    R<ProjReviewRatingCheckRSP> checkRatingInfo(@RequestBody @Valid SinglePkREQ req);

    /**
     * 评审信息生效（或提交审批）
     * @param req
     * @return
     */
    @ApiOperation("评审信息生效（或提交审批）")
    @PostMapping("/proj/review/effect")
    R<Void> effect(@RequestBody @Valid ProjReviewEffectREQ req);

    /**
     * 评审信息版本表列
     * @param req
     * @return
     */
    @ApiOperation("评审信息版本表列")
    @PostMapping("/proj/review/version/list")
    R<PageR<CommonVersionListRSP>> list(@RequestBody @Valid CommonVersionListREQ req);

    /**
     * 评审信息版本比较详情（与上一版本比较）
     * @param req
     * @return
     */
    @ApiOperation("评审信息版本比较详情（与上一版本比较）")
    @PostMapping("/proj/review/compare/preVersion")
    R<CommonVersionDiffRSP> comparePreVersion(@RequestBody @Valid ProjReviewVersionDiffREQ req);


    @ApiOperation("项目评审-项目定价- 基本信息-对比")
    @PostMapping("/proj/review/pricing/base/info/detail/compare")
    R<Map<String, DiffValue>> projReviewPricingBaseInfoCompare(@RequestBody @Valid ProjReviewCompareREQ req);

    @ApiOperation("项目评审-项目定价- 报价方案-对比")
    @PostMapping("/proj/review/pricing/price/detail/compare")
    R<Map<String, DiffValue>> projReviewPricingPriceCompare(@RequestBody @Valid ProjReviewCompareREQ req);

    @ApiOperation("项目评审-项目定价- 现金流计划表-对比")
    @PostMapping("/proj/review/pricing/cashflowplan/list/compare")
    R<List<Map<String, DiffValue>>> projReviewPricingCashFlowPlanCompare(@RequestBody @Valid ProjReviewCompareREQ req);
}