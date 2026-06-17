package cn.zswltech.mithras.liquidity.controller.risk;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.liquidityrisk.*;
import cn.zswltech.mithras.api.liquidityrisk.CapitalFlowSettingAPI;
import cn.zswltech.mithras.liquidity.application.risk.CapitalFlowSettingApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class CapitalFlowSettingController implements CapitalFlowSettingAPI {
    @Resource
    private CapitalFlowSettingApplicationService capitalFlowSettingApplicationService;

    @Override
    public R<Void> setting(CapitalFlowSettingReq req) {
        return capitalFlowSettingApplicationService.setting(req);
    }

    @Override
    public R<CapitalFlowSettingRsp> detail(CapitalFlowSettingDetailReq req) {
        return capitalFlowSettingApplicationService.detail(req);
    }
}
