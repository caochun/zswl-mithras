package cn.zswltech.mithras.api.gungnirApi;

import cn.hutool.json.JSONObject;
import cn.zswltech.mithras.dto.gungnir.GungnirLoginREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.validation.Valid;
import java.util.Map;

/**
 *
 **/
@Api(tags = "调用三方(风控)接口返回结果")
public interface GungnirApi {

    @ApiOperation("第三方取盐")
    @PostMapping("/gungnirApi/user/getAuthCode")
    JSONObject getAuthCode(@RequestBody @Valid GungnirLoginREQ loginREQ);

    @ApiOperation("第三方登录")
    @PostMapping("/gungnirApi/user/login")
    JSONObject userLogin(@RequestHeader Map<String, String> headers, @RequestBody @Valid GungnirLoginREQ loginREQ);

}


