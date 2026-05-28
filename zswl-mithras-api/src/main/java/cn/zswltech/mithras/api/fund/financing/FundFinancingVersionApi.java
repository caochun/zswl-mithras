package cn.zswltech.mithras.api.fund.financing;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.fund.financing.SingleFinancingIdREQ;
import cn.zswltech.mithras.dto.fund.financing.version.FundFinancingEffectREQ;
import cn.zswltech.mithras.dto.fund.financing.version.FundFinancingSubmitREQ;
import cn.zswltech.mithras.dto.fund.financing.version.FundFinancingVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @ClassName FundFinancingVersionApi
 * @Description 融资管理生效，变更相关
 * @Author jackerhe
 * @Date 2023/2/21 3:15 下午
 * @Version 1.0
 **/
@Api(tags = "融资管理-数据版本（审批流）相关接口")
@RequestMapping(path = "/fund/financing")
public interface FundFinancingVersionApi {
    @ApiOperation("融资确认（生效）")
    @PostMapping(path = "/effect")
    R<String> effect(@RequestBody @Valid FundFinancingSubmitREQ singlePkREQ);

    @ApiOperation("融资生效（起息）")
    @PostMapping(path = "/carryinterest")
    R<Void> carryInterest(@RequestBody @Valid FundFinancingEffectREQ req);

    @ApiOperation("贷后变更-LPR调整-确认")
    @PostMapping(path = "/change/lpr/confirm")
    R<Void> confirmChangeLpr(@RequestBody @Valid SingleFinancingIdREQ req);

    @ApiOperation("贷后变更-提前结清-提交审批")
    @PostMapping(path = "/change/earlysettle/submit")
    R<String> submitEarlySettle(@RequestBody @Valid SingleFinancingIdREQ req);

    @ApiOperation("取消变更")
    @PostMapping(path = "/change/cancel")
    R<Void> cancelChange(@RequestBody @Valid SingleFinancingIdREQ req);

    /**
     * 融资管理版本表列
     * @param req
     * @return
     */
    @ApiOperation("融资管理版本列表")
    @PostMapping("/version/list")
    R<PageR<CommonVersionListRSP>> list(@RequestBody @Valid CommonVersionListREQ req);

    /**
     * 融资管理版本比较详情（与上一版本比较）
     * @param req
     * @return
     */
    @ApiOperation("融资管理版本比较详情（与上一版本比较）")
    @PostMapping("/compare/preVersion")
    R<CommonVersionDiffRSP> comparePreVersion(@RequestBody @Valid FundFinancingVersionDiffREQ req);

}
