package cn.zswltech.mithras.application.orchestration.facade.liquidity.risk;

import cn.zswltech.mithras.liquidity.application.liquidityrisk.CapitalFlowSettingApplicationService;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.liquidityrisk.CapitalFlowSettingDetailReq;
import cn.zswltech.mithras.dto.liquidityrisk.CapitalFlowSettingReq;
import cn.zswltech.mithras.dto.liquidityrisk.CapitalFlowSettingRsp;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.liquidity.application.auth.LiquidityRiskModifyMainAuthChecker;
import cn.zswltech.mithras.liquidity.application.auth.LiquidityRiskViewMainAuthChecker;
import cn.zswltech.mithras.application.orchestration.liquidity.risk.CapitalFlowSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @create: 2023-05-15
 **/

@Slf4j
@Service
public class CapitalFlowSettingFacade implements CapitalFlowSettingApplicationService {

    @Resource
    private CapitalFlowSettingService capitalFlowSettingService;

    @Override
//    @DataAuthCheck(checkerClass = LiquidityRiskModifyMainAuthChecker.class, businessModule = "VIRTUAL_LIQUIDITY_RISK")
    public R<Void> setting(@Valid CapitalFlowSettingReq req) {
        capitalFlowSettingService.setting(req);
        return R.ok();
    }
    @Override
//    @DataAuthCheck(checkerClass = LiquidityRiskViewMainAuthChecker.class, businessModule = "VIRTUAL_LIQUIDITY_RISK")
    public R<CapitalFlowSettingRsp> detail(@Valid CapitalFlowSettingDetailReq req) {
        return R.ok(capitalFlowSettingService.detail(req));
    }
}
