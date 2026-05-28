package cn.zswltech.mithras.api.payment;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.payment.version.PaymentEffectREQ;
import cn.zswltech.mithras.api.payment.version.PaymentVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/19 10:20
 */
@Api(tags = "付款管理-多版本管理接口")
public interface PaymentVersionApi {
    
    @ApiOperation("提交审批")
    @PostMapping("/payment/effect")
    R<Void> effect(@RequestBody @Valid PaymentEffectREQ req);

    @ApiOperation("版本表列")
    @PostMapping("/payment/version/list")
    R<PageR<CommonVersionListRSP>> list(@RequestBody @Valid CommonVersionListREQ req);

    @ApiOperation("版本比较详情（与上一版本比较）")
    @PostMapping("/payment/compare/preVersion")
    R<CommonVersionDiffRSP> comparePreVersion(@RequestBody @Valid PaymentVersionDiffREQ req);

    @ApiOperation(value = "提交审批-检验客户存在舆情")
    @PostMapping("/payment/check/client/opinion")
    R<Boolean> checkClientOpinion(@RequestBody @Valid PaymentEffectREQ req);

    @ApiOperation(value = "提交审批-检验项目利润分配")
    @PostMapping("/payment/check/finance/project/distribution")
    R<Boolean> checkProjectDistribution(@RequestBody @Valid PaymentEffectREQ req);
}
