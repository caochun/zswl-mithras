package cn.zswltech.mithras.service.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.biz.service.LoginService;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.result.MSG;
import cn.zswltech.gruul.common.result.Response;
import cn.zswltech.gruul.common.util.RequestUtil;
import cn.zswltech.gruul.common.util.StringUtil;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.service.config.AppConfigProperties;
import cn.zswltech.mithras.service.config.OcrConfigProperties;
import cn.zswltech.mithras.service.util.DESUtil;
import cn.zswltech.mithras.service.util.HttpClientUtil;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.google.gson.JsonObject;
import liquibase.pro.packaged.J;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.Security;

@RestController
public class SysUserController {
    private static final Logger logger = LoggerFactory.getLogger(SysUserController.class);
    @Value("${datasharehost.validate.url:http://newportal.cncico.com/sso/proxyValidate}")
    private String dataShareValidHostUrl;
    @Value("${dashboard.sso.service}")
    private String dashboardSSOService;


    @Value("${portalhost.validate.url:http://newportal.cncico.com/sso/proxyValidate}")
    private String portalHostValUrl;
    @Resource
    private UserService userService;
    @Resource
    private LoginService loginService;
    @Resource
    private AppConfigProperties appConfigProperties;


    @PostMapping("/user/portal/app/ssoLogin")
    public Response portalSsoLogin(@RequestBody SsoAppRequestBody req,
                                   HttpServletRequest request, HttpServletResponse response) {
        String loginId = req.getLoginId();
        String password = appConfigProperties.getPassword();
        if (StringUtils.isBlank(password)) {
            logger.info("配置文件读取密钥为空");
        }
        if (StringUtils.isBlank(loginId)) {
            logger.info(" loginId is null ");
            return Response.error(MSG.req_error_param);
        }
        try {
            String decode = DESUtil.decode(loginId, password);
            if (StringUtils.isBlank(decode)) {
                logger.info("解密结果为空");
                return Response.error("解密结果为空");
            }
            String phone = getPhone(decode);
            if (StringUtils.isBlank(phone)) {
                logger.info("[portalSsoLogin]:user does not exist, phone={}", phone);
                return Response.error("此登录账号解密为空");
            }
            logger.info("[portalSsoLogin]:phone={}", phone);
            UserDO queryDo = new UserDO();
            queryDo.setPhone(phone);
            UserDO userDO = userService.selectOne(queryDo);
            if (ObjectUtils.isEmpty(userDO)) {
                //账户不存在，但是提示模糊一点
                logger.info("[portalSsoLogin]:user does not exist, phone={}", phone);
                return Response.error("请联系融租易系统管理员将您的集团信息录入系统");
            }
            Response<Map<String, Object>> loginResponse =
                    loginService.userLogin(response, userDO, RequestUtil.getIpAddr(request));
            return loginResponse;
        } catch (Exception e) {
            logger.error("[portalSsoLogin]: loginId={} --- error={}", loginId, e.getMessage());
            return Response.error(MSG.req_error_account_not_exist);
        }
    }


    @PostMapping("/user/portal/ssoLogin")
    public Response portalSsoLogin(@RequestBody SsoRequestBody req,
                                   HttpServletRequest request, HttpServletResponse response) {
        String ticket = req.getTicket();
        String service = req.getService();
        if (StringUtils.isBlank(ticket)) {
            logger.info(" ticket is null ");
            return Response.error(MSG.req_error_param);
        }
        if (StringUtils.isBlank(service)) {
            logger.info(" service is null ");
            return Response.error(MSG.req_error_param);
        }
        try {
            String mainCode = null;
            // 通过 ticket 和 service 获取 用户信息
            Map<String, Object> param = new HashMap<>();
            param.put("ticket", ticket);
            param.put("service", service);
            String result = HttpClientUtil.connectPostHttps(portalHostValUrl, param);
//            String result = "<cas:serviceResponse\n" +
//                    "    xmlns:cas='http://www.yale.edu/tp/cas'>\n" +
//                    "    <cas:authenticationSuccess>\n" +
//                    "        <cas:user>15757119095</cas:user>\n" +
//                    "        <cas:attributes>\n" +
//                    "            <cas:birthday></cas:birthday>\n" +
//                    "            <cas:subcompanyid>12</cas:subcompanyid>\n" +
//                    "            <cas:loginid>15757119095</cas:loginid>\n" +
//                    "            <cas:workcode>testmaincode</cas:workcode>\n" +
//                    "            <cas:sex>0</cas:sex>\n" +
//                    "            <cas:departmentid>26</cas:departmentid>\n" +
//                    "            <cas:mobile></cas:mobile>\n" +
//                    "            <cas:systemlanguage>7</cas:systemlanguage>\n" +
//                    "            <cas:telephone></cas:telephone>\n" +
//                    "            <cas:managerid></cas:managerid>\n" +
//                    "            <cas:countryid>1</cas:countryid>\n" +
//                    "            <cas:lastname>王坤</cas:lastname>\n" +
//                    "            <cas:assistantid></cas:assistantid>\n" +
//                    "            <cas:password>8DDCFF3A80F4189CA1C9D4D902C3C909</cas:password>\n" +
//                    "            <cas:id>118</cas:id>\n" +
//                    "            <cas:email></cas:email>\n" +
//                    "            <cas:status>0</cas:status>\n" +
//                    "        </cas:attributes>\n" +
//                    "    </cas:authenticationSuccess>\n" +
//                    "</cas:serviceResponse>";
            logger.info("[portalSsoLogin]: ticket={} --- service={} --- result={}", ticket, service, result);
            // doom4j 解析 result
            Document doc = DocumentHelper.parseText(result);
            Element root = doc.getRootElement();
            Element authenticationFailure = root.element("authenticationFailure");
            // 校验ticket失败逻辑
            if (!ObjectUtils.isEmpty(authenticationFailure)) {
                logger.error("[portalSsoLogin]: ticket={} --- service={} --- authenticationFailure={}", ticket, service, authenticationFailure.getTextTrim());
                return Response.error(MSG.req_error_account_not_exist);
            }
            // 校验ticket成功逻辑
            Element authenticationSuccess = root.element("authenticationSuccess");
            if (ObjectUtil.isNotEmpty(authenticationSuccess)) {
                Element attributes = authenticationSuccess.element("attributes");
                if (!ObjectUtils.isEmpty(attributes)) {
                    mainCode = attributes.element("workcode").getTextTrim();
                }
            }
            // 用户登录
            if (StringUtil.isBlank(mainCode)) {
                logger.info("[portalSsoLogin]:workcode={}", mainCode);
                //账户不存在，但是提示模糊一点
                return Response.error(MSG.req_error_account_not_exist);
            }
            logger.info("[portalSsoLogin]:workcode={}", mainCode);
            UserDO queryDo = new UserDO();
            queryDo.setMainCode(mainCode);
            UserDO userDO = userService.selectOne(queryDo);
            if (ObjectUtils.isEmpty(userDO)) {
                //账户不存在，但是提示模糊一点
                logger.info("[portalSsoLogin]:user does not exist, workcode={}", mainCode);
                return Response.error("请联系融租易系统管理员将您的集团信息录入系统");
            }
            Response<Map<String, Object>> loginResponse =
                    loginService.userLogin(response, userDO, RequestUtil.getIpAddr(request));
            if (loginResponse.isSuccess()) {
                // set http cookie, 使用utf-8编码
                // 修改下 重定向的路径
                loginResponse.getData().put("url", "/lifeCycle/projectLifeCycle");
            }
            return loginResponse;
        } catch (Exception e) {
            logger.error("[portalSsoLogin]: ticket={} --- service={} --- error={}", ticket, service, e.getMessage());
            return Response.error(MSG.req_error_account_not_exist);
        }
    }

    @PostMapping("/user/dashboard/ssoLogin")
    @ResponseBody
    public Response ssoLogin(@RequestBody DashBoardSsoRequestBody req,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        String ticket = req.getTicket();
        if (StringUtils.isBlank(ticket)) {
            logger.info(" ticket is null ");
            return Response.error(MSG.req_error_param);
        }
        try {
            // 通过 ticket 和 token 获取 用户信息
            Map<String, Object> params = new HashMap<>();
            params.put("ticket", ticket);
            params.put("service", dashboardSSOService);

            String xml = HttpClientUtil.invokePost(dataShareValidHostUrl, params, null);
            logger.info("驾驶舱跳转至融租易单点登录, ticket={},, service={},泛微返回报文是{}", ticket, dashboardSSOService, xml);
            UserDO ssoLoginUserDO = this.parseWeaverXml(xml);
            if (ssoLoginUserDO.getMainCode() == null) {
                logger.warn("未能从泛微sso返回结果获取用户工号，登录结束");
                return Response.error(MSG.permission_menu_error);
            }
            
//            // 测试
//            UserDO ssoLoginUserDO = new UserDO();
//            ssoLoginUserDO.setMainCode("20065272");

            String mainCode = ssoLoginUserDO.getMainCode();
            // 用户登录
            if (StringUtil.isBlank(mainCode)) {
                logger.info("[dashboardSsoLogin]:workcode={}", mainCode);
                //账户不存在，但是提示模糊一点
                return Response.error(MSG.permission_menu_error);
            }
            logger.info("[dashboardSsoLogin]:workcode={}", mainCode);
            UserDO queryDo = new UserDO();
            queryDo.setMainCode(mainCode);
            UserDO userDO = userService.selectOne(queryDo);
            if (ObjectUtils.isEmpty(userDO)) {
                //账户不存在，但是提示模糊一点
                logger.info("[dashboardSsoLogin]:user does not exist, workcode={}", mainCode);
                return Response.error("请联系融租易系统管理员将您的集团信息录入系统");
            }
            Response<Map<String, Object>> loginResponse =
                    loginService.userLogin(response, userDO, RequestUtil.getIpAddr(request));
            if (loginResponse.isSuccess()) {
                // 修改下 重定向的路径
                loginResponse.getData().put("url", "/lifeCycle/projectLifeCycle");
            }
            return loginResponse;
        } catch (Exception e) {
            logger.error("登录失败", e);
        }
        return Response.error(MSG.req_error_login_failed);
    }

    private UserDO parseWeaverXml(String xml) {
        UserDO userDO = new UserDO();
        Document document;
        try {
            document = DocumentHelper.parseText(xml);
        } catch (DocumentException e) {
            logger.error("sso xml报文解析失败", e);
            return userDO;
        }
        Element root = document.getRootElement();
        Iterator a = root.elementIterator();
        if (a.hasNext()) {
            Element success = (Element) a.next();
            Iterator attributes = success.elementIterator("attributes");
            while (attributes.hasNext()) {
                Element attribute = (Element) attributes.next();
//                weaverUser.setUserId(Integer.parseInt(attribute.elementText("id")));
                userDO.setMainCode(attribute.elementText("workcode"));
                userDO.setUserName(attribute.elementText("lastname"));
                userDO.setPhone(attribute.elementText("mobile"));
            }
        }
        return userDO;
    }

    @Data
    static class SsoRequestBody {
        private String ticket;
        private String service;
    }

    @Data
    static class DashBoardSsoRequestBody {
        private String ticket;
        private String token;
    }

    @Data
    static class SsoAppRequestBody {
        private String loginId;
    }

    private String getPhone(String response) {
        try {
            String rsp = response.trim();
            JSONObject jsonObject = new JSONObject(rsp);
            String loginId = jsonObject.getString("loginid");
            if (StringUtils.isNotBlank(loginId)) {
                return loginId;
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
