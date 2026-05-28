package cn.zswltech.mithras.api.riskcontrol;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.riskcontrol.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author zhaozhengkang
 * @description 预警监控管理
 * @date 2023-02-08
 */
@Api(tags = "风控管理-预警监控管理-接口")
@RequestMapping("/risk/control/strategy")
public interface RiskControlStrategyApi {

    @ApiOperation("修改预警监控指标")
    @PostMapping("/modify")
    R<Void> modify(@RequestBody @Valid RiskControlStrategyModifyReq req);

    @ApiOperation("预警监控指标列表")
    @PostMapping("/list")
    R<PageR<RiskControlStrategyListRsp>> list(@RequestBody @Valid RiskControlStrategyListReq req);

    @ApiOperation("预警监控指标详情")
    @PostMapping("/detail")
    R<RiskControlStrategyDetailRsp> detail(@RequestBody @Valid RiskControlStrategyDetailReq req);

    @ApiOperation("立项拦截")
    @PostMapping("/proreview/intercept")
    R<InterceptRsp> projEstablishIntercept(@RequestBody @Valid ProjEstablishInterceptReq req);

    @ApiOperation("付款申请拦截")
    @PostMapping("/paymentapply/intercept")
    R<InterceptRsp> paymentApplyIntercept(@RequestBody @Valid PaymentApplyInterceptReq req);

    @ApiOperation("快照重计算")
    @PostMapping("/snapshot/recalculate")
    R<Void> recalculate(@RequestBody @Valid RiskControlStrategyCalReq req);


}