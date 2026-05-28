package cn.zswltech.mithras.service.config.timeoutkick;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.api.common.R;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @description: 拦截业务接口，判断登录信息是否失效，失效则返回登录失效信息，前端跳转到登录页
 * @author: zhaozhengkang
 * @date: 2023/7/13 10:09
 */
@WebFilter(filterName = "PermissionFilter")
@Configuration
@Slf4j
public class PermissionFilter implements Filter {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${mithras.login.timeout}")
    private long timeout;

    private final Set<String> DO_NOT_HANDLE_URLS = new HashSet<>(Arrays.asList("/user/getAuthCode",
            "/user/login", "/user/dashboard/ssoLogin"));

    @Override
    public void doFilter(ServletRequest req,
                         ServletResponse res,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (DO_NOT_HANDLE_URLS.contains(request.getRequestURI())) {
            chain.doFilter(req, res);
            return;
        }

        // readOnly用户不做登录失效校验
        UserDO readonly = Optional.ofNullable(getBean(UserService.class).queryByAccount("readonly")).orElse(new UserDO());
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        if (loginInfo == null || Objects.equals(loginInfo.getId(), readonly.getId())) {
            chain.doFilter(req, res);
            return;
        }

        String token = request.getHeader(TimeoutKickConstant.TOKEN_HEADER_KEY);
        if (ObjectUtil.isNotEmpty(token)) {
            JSONObject jsonObject = JSON.parseObject(token);
            Object redisKey = jsonObject.get(TimeoutKickConstant.ACCESS_KEY_NAME);
            if (ObjectUtil.isNotEmpty(redisKey)) {
                String s = redisTemplate.opsForValue().get(TimeoutKickConstant.REDIS_KEY_PREFIX + redisKey);
                if (ObjectUtil.isEmpty(s)) {
                    returnJson(response, request, JSON.toJSONString(R.fail(401027, "登录信息已失效,请重新登录")));
                    return;
                } else {
                    redisTemplate.opsForValue()
                            .set(TimeoutKickConstant.REDIS_KEY_PREFIX + redisKey, TimeoutKickConstant.MEANINGLESS_VALUE, timeout, TimeUnit.MINUTES);
                }
            }
        }
        chain.doFilter(req, res);
    }


    private void returnJson(HttpServletResponse response, HttpServletRequest request, String json) {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "OPTIONS, POST, GET, PATCH, DELETE, PUT");
        response.setHeader("Access-Control-Max-Age", "86400");
        response.setHeader("Access-Control-Allow-Headers", "*");
        try {
            writer = response.getWriter();
            writer.print(json);

        } catch (IOException e) {
            log.error("response error", e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
