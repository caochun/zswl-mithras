package cn.zswltech.mithras.system.infrastructure.aop;

import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/11/14
 * @description
 */
@Slf4j
@Order(-10086)
@Component
@Aspect
public class XxlJobErrorLogAspect {
    @Around("@annotation(com.xxl.job.core.handler.annotation.XxlJob)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String jobName = null;
        try {
            Signature signature = proceedingJoinPoint.getSignature();
            if (signature instanceof MethodSignature) {
                MethodSignature methodSignature = (MethodSignature) signature;
                XxlJob xxlJob = methodSignature.getMethod().getAnnotation(XxlJob.class);
                if (Objects.nonNull(xxlJob)) {
                    jobName = xxlJob.value();
                }
            }
        } catch (Throwable e) {
            log.error("获取jobName失败", e);
        }
        try {
            return proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            log.error("XxlJob任务执行异常[jobName:{}]", jobName, e);
            throw e;
        }
    }
}
