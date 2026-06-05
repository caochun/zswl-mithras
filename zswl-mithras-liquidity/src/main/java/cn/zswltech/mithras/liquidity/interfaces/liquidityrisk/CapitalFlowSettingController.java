package cn.zswltech.mithras.liquidity.interfaces.liquidityrisk;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.liquidityrisk.*;
import javax.validation.Valid;
import java.time.LocalDate;
import cn.zswltech.mithras.api.liquidityrisk.CapitalFlowSettingAPI;
import cn.zswltech.mithras.liquidity.application.liquidityrisk.CapitalFlowSettingApplicationService;
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
