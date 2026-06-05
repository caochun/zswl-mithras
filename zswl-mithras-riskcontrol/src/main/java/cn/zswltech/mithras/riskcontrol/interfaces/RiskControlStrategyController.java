package cn.zswltech.mithras.riskcontrol.interfaces;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.riskcontrol.RiskControlStrategyApi;
import cn.zswltech.mithras.dto.riskcontrol.*;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategyApplicationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author zhaozhengkang
 * @description 风控管理-预警监控管理
 * @date 2023-02-08
 */
@RestController
public class RiskControlStrategyController implements RiskControlStrategyApi {

    @Resource
    private RiskControlStrategyApplicationService riskControlStrategyService;

    @Override
    public R<Void> modify(RiskControlStrategyModifyReq req) {
        riskControlStrategyService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<RiskControlStrategyListRsp>> list(RiskControlStrategyListReq req) {
        return R.ok(riskControlStrategyService.list(req));
    }

    @Override
    public R<RiskControlStrategyDetailRsp> detail(RiskControlStrategyDetailReq req) {
        return R.ok(riskControlStrategyService.detail(req));
    }

    @Override
    public R<InterceptRsp> projEstablishIntercept(ProjEstablishInterceptReq req) {
        return R.ok(riskControlStrategyService.projEstablishIntercept(req));
    }

    @Override
    public R<InterceptRsp> paymentApplyIntercept(PaymentApplyInterceptReq req) {
        return R.ok(riskControlStrategyService.paymentApplyIntercept(req));
    }

    @Override
    public R<Void> recalculate(RiskControlStrategyCalReq req) {
        riskControlStrategyService.recalculate(req);
        return R.ok();
    }

    @GetMapping("/test")
    public void test(@RequestParam("metricCode") String metricCode) {
        riskControlStrategyService.test(metricCode);
    }

}
