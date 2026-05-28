package cn.zswltech.mithras.api.bridge;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SystemUserAuthCodeREQ;
import cn.zswltech.mithras.dto.SystemUserAuthCodeRSP;
import cn.zswltech.mithras.dto.SystemUserLoginREQ;
import cn.zswltech.mithras.dto.SystemUserLoginRSP;
import cn.zswltech.mithras.dto.app.*;
import cn.zswltech.mithras.dto.client.client.ClientAppPlanQueryREQ;
import cn.zswltech.mithras.dto.client.client.ClientAppPlanQueryRSP;
import cn.zswltech.mithras.dto.client.client.ClientAppProjQueryREQ;
import cn.zswltech.mithras.dto.client.client.ClientAppProjQueryRSP;
import cn.zswltech.mithras.dto.file.AppFileUploadREQ;
import cn.zswltech.mithras.dto.file.FileUploadRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

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
