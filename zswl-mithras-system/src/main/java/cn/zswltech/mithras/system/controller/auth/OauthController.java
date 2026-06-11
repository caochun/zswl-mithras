package cn.zswltech.mithras.system.controller.auth;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.biz.service.SystemConfigService;
import cn.zswltech.gruul.biz.service.impl.UserServiceImpl;
import cn.zswltech.gruul.common.result.Response;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.common.util.MD5Util;
import cn.zswltech.gruul.dao.dal.entity.SystemConfigDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.api.auth.OAuthApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.AuthUserInfoRSP;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author dingqi
 * @date 2022/9/15
 * @description
 */
@RestController
@Slf4j
public class OauthController implements OAuthApi {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private UserServiceImpl userServiceAPI;

    @Resource
    private SystemConfigService systemConfigService;
    @Resource
    HttpServletRequest httpServletRequest;

    private static final String OAUTH_SECRET = "OAUTH_SECRET";
    private static final long TOKEN_EXPIRE_TIME = 60 * 30;

    @Override
    public R<String> authorize(@RequestHeader("token") String token) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            log.info("token:{}", token);
            // 将JSON解析为Map结构
            Map<String, String> map = null;
            map = mapper.readValue(token, Map.class);
            // 提取目标值
            String qjtAc = map.get("_qjt_ac_");
            String code = MD5Util.MD5(qjtAc);
            redisTemplate.opsForValue().set(code, qjtAc, TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);
            return R.ok(code);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return R.fail("授权失败");

    }

    @Override
    public Map<String, String> token( Map<String, String> param) {
        log.info("param:{}", param);
        String clientIdList = param.get("client_id");
        String clientSecretList = param.get("client_secret");
        String codeList = param.get("code");
        String redirect_uri = param.get("redirect_uri");

        if (ObjectUtil.isEmpty(clientIdList) || ObjectUtil.isEmpty(clientSecretList)) {
            throw new MithrasException("请先配置授权密钥");
        }
        if (ObjectUtil.isEmpty(codeList)) {
            throw new MithrasException("请先登录");
        }
        Response<SystemConfigDO> specifyRiskManager = systemConfigService.getConfig(OAUTH_SECRET);
        if (specifyRiskManager.isSuccess()) {
            String configValue = specifyRiskManager.getData().getConfigValue();
            Map<String, String> stringListMap = JSON.parseObject(configValue, new TypeReference<Map<String, String>>() {
            });
            if (!ObjectUtil.equals(stringListMap.get(clientIdList), clientSecretList)) {
                throw new MithrasException("请先配置授权密钥");
            }
        } else {
            throw new MithrasException("请先配置授权密钥");
        }
        String accessToken = redisTemplate.opsForValue().get(codeList);
        redirect_uri = redirect_uri + "?access_token=" + accessToken;
        Map<String, String> map = new HashMap();
        map.put("access_token", accessToken);
        map.put("token_type", "Bearer");
        map.put("expires_in", String.valueOf(TOKEN_EXPIRE_TIME));
        map.put("refresh_token", accessToken);
        return map;
    }

    @Override
    public Map<String, String> userinfo( Map<String, String> params) {
        log.info("accessToken:{}", params);
        String accessToken = params.get("access_token");
        if (accessToken == null) {
            throw new MithrasException("请先登录");
        }
        AccountVO loginInfo = AccountUtil.getLoginInfo(accessToken);
        UserVO data = userServiceAPI.getUser(loginInfo.getAccount()).getData();
        if (data == null) {
            throw new MithrasException("请先登录");
        }
        AuthUserInfoRSP rsp = BeanUtil.copyProperties(data, AuthUserInfoRSP.class);
        rsp.setPhone(userServiceAPI.getRealPhone(data.getId()));
        Map<String, String> map = new HashMap<>();
        map.put("phone", rsp.getPhone());
        map.put("account", rsp.getAccount());
        return map;
    }


}
