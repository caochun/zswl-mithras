package cn.zswltech.mithras.service.auth.aop;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.auth.checker.AuthHelper;
import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 数据修改拦截
 *
 * @author wangchuanhao
 * @date 2022/7/21 2:40 PM
 */
@Aspect
@Component
public class DataAuthCheckAdvice {

    @Resource
    private AuthHelper authHelper;

    @Pointcut("@annotation(cn.zswltech.mithras.service.auth.aop.DataAuthCheck)")
    public void authCheck() {

    }

    @Before("authCheck() && @annotation(dataAuthCheck)")
    public void doBefore(JoinPoint joinPoint, DataAuthCheck dataAuthCheck) {
        Object[] args = joinPoint.getArgs();
        Object key = null;
        if (DataAuthCheck.ParamType.NO != dataAuthCheck.paramType()) {
            Object arg = args[dataAuthCheck.paramIndex()];
            if(arg instanceof List){
                if(ObjectUtil.isEmpty(arg) || ((List<?>) arg).size() == 0){
                    throw new AuthCheckException("指定参数为NULL");
                }
                arg = ((List<?>) arg).get(0);
            }
            if (Objects.isNull(arg)) {
                throw new AuthCheckException("指定参数为NULL");
            }
            if (DataAuthCheck.ParamType.DIRECT.equals(dataAuthCheck.paramType())) {
                key = arg;
            } else {
                if (StringUtils.isNotBlank(dataAuthCheck.keyFieldName())) {
                    key = authHelper.extractObjectByExpression(arg, dataAuthCheck.keyFieldName());
                }
            }
        }
        IDataAuthChecker dataAuthChecker = SpringContextHolder.getBean(dataAuthCheck.checkerClass());
        Class<? extends BaseMapper>[] classes = dataAuthCheck.mapperClass();
        for (Class<? extends BaseMapper> aClass : classes) {
            if (key == null) {
                dataAuthChecker.check(dataAuthCheck.businessModule(), aClass, null, args);
            } else {
                if (key instanceof Long) {
                    dataAuthChecker.check(dataAuthCheck.businessModule(), aClass, (Long) key, args);
                } else if (key instanceof String) {
                    String s = (String) key;
                    if (!NumberUtil.isLong(s)) {
                        throw new AuthCheckException("数据主Key类型错误");
                    }
                    dataAuthChecker.check(dataAuthCheck.businessModule(), aClass, Long.parseLong(s), args);
                } else if (key instanceof Collection) {
                    dataAuthChecker.checkBatch(dataAuthCheck.businessModule(), aClass, (Collection) key, args);
                } else {
                    throw new AuthCheckException("未定义的数据主Key类型");
                }
            }
        }

    }

}
