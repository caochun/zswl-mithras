package cn.zswltech.mithras.api.bridge;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SystemUserAuthCodeREQ;
import cn.zswltech.mithras.dto.SystemUserAuthCodeRSP;
import cn.zswltech.mithras.dto.SystemUserLoginREQ;
import cn.zswltech.mithras.dto.SystemUserLoginRSP;
import cn.zswltech.mithras.dto.app.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author junke
 */
@Api(value = "跨域接口", tags = "跨域接口")
public interface BridgeApi {


    @ApiOperation("登录接口校验")
    @PostMapping(path = "/birdge/user/auth")
    R<SystemUserAuthCodeRSP> getAuthCode(@RequestBody @Valid SystemUserAuthCodeREQ req);


    @ApiOperation("登录接口登陆")
    @PostMapping(path = "/birdge/user/login")
    R<SystemUserLoginRSP> login(@RequestBody @Valid SystemUserLoginREQ req);
}
