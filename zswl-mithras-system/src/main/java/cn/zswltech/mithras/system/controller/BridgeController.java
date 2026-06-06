package cn.zswltech.mithras.system.controller;

import cn.zswltech.mithras.api.bridge.BridgeApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SystemUserAuthCodeREQ;
import cn.zswltech.mithras.dto.SystemUserAuthCodeRSP;
import cn.zswltech.mithras.dto.SystemUserLoginREQ;
import cn.zswltech.mithras.dto.SystemUserLoginRSP;
import cn.zswltech.mithras.system.application.bridge.api.BridgeApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
public class BridgeController implements BridgeApi {

    @Resource
    private BridgeApplicationService bridgeApplicationService;

    @Override
    public R<SystemUserAuthCodeRSP> getAuthCode(@Valid SystemUserAuthCodeREQ req) {
        return bridgeApplicationService.getAuthCode(req);
    }

    @Override
    public R<SystemUserLoginRSP> login(@Valid SystemUserLoginREQ req) {
        return bridgeApplicationService.login(req);
    }
}
