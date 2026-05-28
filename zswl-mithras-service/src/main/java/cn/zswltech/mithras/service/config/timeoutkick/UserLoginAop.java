package cn.zswltech.mithras.service.config.timeoutkick;

import cn.zswltech.gruul.common.result.Response;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @description: 登录成功后置处理, 将ac存入redis, 并设置过期时间
 * @author: zhaozhengkang
 * @date: 2023/7/14 09:39
 */
@Component
@Aspect
@Slf4j
public class UserLoginAop {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${mithras.login.timeout}")
    private long timeout;

    @Pointcut("execution(* cn.zswltech.gruul.biz.service.LoginService.userLogin(..))")
    public void pointcut() {

    }

    @AfterReturning(value = "pointcut()", returning = "methodResult")
    public void afterReturning(JoinPoint point, Object methodResult) {
        log.info("登录接口后置处理....");
        Response<Map> res = (Response<Map>) methodResult;
        if (res.isSuccess()) {
            Map<String, Object> data = res.getData();
            String redisKey = (String) data.get("_qjt_ac_");
            redisTemplate.opsForValue()
                    .set(TimeoutKickConstant.REDIS_KEY_PREFIX + redisKey, TimeoutKickConstant.MEANINGLESS_VALUE,
                            timeout, TimeUnit.MINUTES);
        }
    }


}
