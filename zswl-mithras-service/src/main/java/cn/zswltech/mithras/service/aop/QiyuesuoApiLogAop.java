package cn.zswltech.mithras.service.aop;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.service.mapper.QiyuesuoInvokeLogMapper;
import cn.zswltech.mithras.service.mapper.model.QiyuesuoInvokeLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author bigbear
 * @date 2024/12/11 19:42
 * @description
 */
@Slf4j
@Aspect
@Component
public class QiyuesuoApiLogAop {

    @Resource
    private QiyuesuoInvokeLogMapper qiyuesuoInvokeLogMapper;

    @Pointcut("@annotation(cn.zswltech.mithras.third.qiyuesuo.infrastructure.client.annotation.QiyuesuoApiLog)")
    public void writeLog() {
    }

    @Around("writeLog()")
    public Object doBefore(ProceedingJoinPoint pjp) throws Throwable {
        MDC.put("traceId", System.currentTimeMillis() + RandomUtil.randomString(3));
        // 获取方法请求参数
        Object[] args = pjp.getArgs();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append("第").append(i + 1).append("个参数: ").append(JSONUtil.toJsonStr(args[i])).append(",\n");
        }
        log.info(sb.toString());
        Object object = null;
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            object = pjp.proceed();
            stopWatch.stop();
            log.info("调用接口：{}，耗时={}", pjp.getSignature().getDeclaringTypeName() + "#" + pjp.getSignature().getName(), stopWatch.prettyPrint(TimeUnit.SECONDS));
        } catch (Throwable e) {
            // 将日志入库
            AccountVO loginInfo = AccountUtil.getLoginInfo();
            CompletableFuture.runAsync(() -> {
                QiyuesuoInvokeLog qiyuesuoInvokeLog = new QiyuesuoInvokeLog();
                qiyuesuoInvokeLog.setCreateBy(loginInfo.getId());
                qiyuesuoInvokeLog.setUpdateBy(loginInfo.getId());
                qiyuesuoInvokeLog.setCreateTime(LocalDateTime.now());
                qiyuesuoInvokeLog.setUpdateTime(LocalDateTime.now());
                qiyuesuoInvokeLog.setMethod(pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName());
                qiyuesuoInvokeLog.setParam(sb.toString());
                qiyuesuoInvokeLog.setResult(JSONUtil.toJsonStr(e.getMessage()));
                qiyuesuoInvokeLogMapper.insert(qiyuesuoInvokeLog);
            });
            throw e;
        }
        // 将日志入库
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        Object finalObject = object;
        CompletableFuture.runAsync(() -> {
            // 构建日志
            QiyuesuoInvokeLog qiyuesuoInvokeLog = new QiyuesuoInvokeLog();
            qiyuesuoInvokeLog.setCreateBy(loginInfo.getId());
            qiyuesuoInvokeLog.setUpdateBy(loginInfo.getId());
            qiyuesuoInvokeLog.setCreateTime(LocalDateTime.now());
            qiyuesuoInvokeLog.setUpdateTime(LocalDateTime.now());
            qiyuesuoInvokeLog.setMethod(pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName());
            qiyuesuoInvokeLog.setParam(sb.toString());
            qiyuesuoInvokeLog.setResult(JSONUtil.toJsonStr(finalObject));
            qiyuesuoInvokeLogMapper.insert(qiyuesuoInvokeLog);
        });
        return object;
    }

}
