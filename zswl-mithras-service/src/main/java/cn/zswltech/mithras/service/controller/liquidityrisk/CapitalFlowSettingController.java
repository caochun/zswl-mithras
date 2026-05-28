package cn.zswltech.mithras.service.controller.liquidityrisk;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.liquidityrisk.CapitalFlowSettingAPI;
import cn.zswltech.mithras.dto.liquidityrisk.CapitalFlowSettingDetailReq;
import cn.zswltech.mithras.dto.liquidityrisk.CapitalFlowSettingReq;
import cn.zswltech.mithras.dto.liquidityrisk.CapitalFlowSettingRsp;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.liquidityrisk.LiquidityRiskModifyMainAuthChecker;
import cn.zswltech.mithras.service.auth.checker.liquidityrisk.LiquidityRiskViewMainAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.service.liquidityrisk.CapitalFlowSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @create: 2023-05-15
 **/

@Slf4j
@RestController
public class CapitalFlowSettingController implements CapitalFlowSettingAPI {

    @Resource
    private CapitalFlowSettingService capitalFlowSettingService;

    @Override
//    @DataAuthCheck(checkerClass = LiquidityRiskModifyMainAuthChecker.class, businessModule = BusinessModuleEnum.VIRTUAL_LIQUIDITY_RISK)
    public R<Void> setting(@Valid CapitalFlowSettingReq req) {
        capitalFlowSettingService.setting(req);
        return R.ok();
    }
    @Override
//    @DataAuthCheck(checkerClass = LiquidityRiskViewMainAuthChecker.class, businessModule = BusinessModuleEnum.VIRTUAL_LIQUIDITY_RISK)
    public R<CapitalFlowSettingRsp> detail(@Valid CapitalFlowSettingDetailReq req) {
        return R.ok(capitalFlowSettingService.detail(req));
    }
}
