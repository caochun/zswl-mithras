package cn.zswltech.mithras.foundation.auth.aop;

import cn.zswltech.gruul.common.util.ShaUtil;
import cn.zswltech.mithras.foundation.auth.aop.ThirdAuthCheck;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 *
 * @author: 三方调用接口校验合法性
 * @date: 2022/10/17 4:13 下午
 **/
@Aspect
@Component
public class ThirdAuthCheckAdvice {
    @Pointcut("@annotation(cn.zswltech.mithras.foundation.auth.aop.ThirdAuthCheck)")
    public void authCheck() {
    }

    private static final String SECRET = "secret";

    private static final String RANDOM = "random";

    private static final String TIMESTAMP = "timestamp";

    private static final String PRIVATE_KEY = "6460520c-7520-4be4-a913-8ed596706ac6";

    @Before("authCheck() && @annotation(thirdAuthCheck)")
    public void doBefore(JoinPoint joinPoint, ThirdAuthCheck thirdAuthCheck) {
        Object[] args = joinPoint.getArgs();
        Object arg = args[0];
        String secret = getFieldValueByName(arg, SECRET);
        String random = getFieldValueByName(arg, RANDOM);
        String timestamp = getFieldValueByName(arg, TIMESTAMP);
        if(!secret.equals(ShaUtil.shaEncode(PRIVATE_KEY + timestamp + random))){
            throw new MithrasException("三方认证失败，请检查密钥或联系管理员");
        }
    }

    private String getFieldValueByName(Object o,String fieldName) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[] {});
            String value = method.invoke(o, new Object[] {}).toString();
            return value;
        } catch (Exception e) {
            throw new MithrasException("获取" + fieldName + "失败" + e);
        }
    }


}
