package cn.zswltech.mithras.service.controller.gungnir;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.gungnirApi.GungnirApi;
import cn.zswltech.mithras.dto.gungnir.GungnirLoginREQ;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.util.HttpClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 浙商金控登录接口
 **/
@RestController
public class GungnirController implements GungnirApi {

    @Value("${gungnir-url}")
    private String gungnirUrl;


    @Override
    public JSONObject getAuthCode(GungnirLoginREQ req) {
        String url = gungnirUrl+"/gungnirApi/user/getAuthCode";
        Map<String, Object> param = new HashMap<>();
        param.put("account", req.getAccount());
        param.put("password", req.getPassword());
        String responseResult = HttpClientUtil.connectPostHttps(url, param);
        if (StringUtils.isBlank(responseResult)) {
            throw new MithrasException("authcode返回为空");
        }
        JSONObject responseJson = JSONUtil.parseObj(responseResult);
        return responseJson;
    }

    @Override
    public JSONObject userLogin(Map<String, String> headers, GungnirLoginREQ req) {
        String token = headers.get("token");
        if(StringUtils.isBlank(token)) {
            throw new MithrasException("token不能为空");
        }
        Map<String, String> headersMap = new HashMap<>();
        headersMap.put("token", token);
        String url = gungnirUrl+"/gungnirApi/user/login";
        Map<String, Object> param = new HashMap<>();
        param.put("account", req.getAccount());
        param.put("password", req.getPassword());
        String responseResult = HttpClientUtil.connectPostHttps(url, param,headersMap);
        if (StringUtils.isBlank(responseResult)) {
            throw new MithrasException("userLogin返回为空");
        }
        JSONObject responseJson = JSONUtil.parseObj(responseResult);
        return responseJson;
    }
}
