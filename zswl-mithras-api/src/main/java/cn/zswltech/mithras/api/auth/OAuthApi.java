package cn.zswltech.mithras.api.auth;

import cn.zswltech.mithras.api.common.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;


/**
 * @date 2022/9/15
 * @description oauth权限相关接口
 */
@Api(tags = "权限相关接口-oauth")
public interface OAuthApi {


    @ApiOperation("获取权限认证code")
    @GetMapping("/oauth/authorize")
    R<String> authorize(@RequestHeader("token") String token);

    @ApiOperation("code换取token")
    @RequestMapping("/oauth/token")
    Map<String, String> token(@RequestParam Map<String, String> params);

    @ApiOperation("token换取用户信息")
    @RequestMapping("/userinfo")
    Map<String, String> userinfo(@RequestParam Map<String, String> params);

}
