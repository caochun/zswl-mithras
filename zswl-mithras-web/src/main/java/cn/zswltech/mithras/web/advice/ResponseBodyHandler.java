package cn.zswltech.mithras.web.advice;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.foundation.cache.RedisDistLock;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.enums.CacheEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractRemindRecord;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractRemindRecordService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author dingqi
 * @date 2022/9/5
 * @description
 */
@Slf4j
@Deprecated
//@ControllerAdvice
public class ResponseBodyHandler implements ResponseBodyAdvice<Object> {
    private static final String CACHE_NO_TOAST = "no toast";

    private static final Set<String> IGNORE = new HashSet<>();

    static {
        IGNORE.add("cn.zswltech.mithras.application.orchestration.facade.contract.ContractVersionFacade#startRent");
    }

    @Resource
    private RedisDistLock redisDistLock;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractRemindRecordService contractRemindRecordService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Object beforeBodyWrite(Object o, @NotNull MethodParameter methodParameter, @NotNull MediaType mediaType, @NotNull Class aClass, @NotNull ServerHttpRequest serverHttpRequest, @NotNull ServerHttpResponse serverHttpResponse) {
        try {
            if (o instanceof R<?>) {
                R<?> r = (R<?>) o;
                if (!r.isSuccess()) {
                    return o;
                }
                AccountVO accountVO = AccountUtil.getLoginInfo();
                if (Objects.isNull(accountVO)) {
                    // 未登录直接返回
                    return o;
                }
                String text = this.getCachedToastText(accountVO.getId());
                if (StrUtil.isBlank(text)) {
                    return o;
                }
                r.setToast(text);
            }
            return o;
        } catch (Exception e) {
            Class<?> clz = methodParameter.getDeclaringClass();
            Method method = methodParameter.getMethod();
            String methodName = null;
            if (Objects.nonNull(method)) {
                methodName = method.getName();
            }
            log.error("ResponseBody后置处理发生异常[o: {}, clz: {}, method: {}]", JSONUtil.toJsonStr(o), clz.getName(), methodName, e);
            return o;
        }
    }

    @Override
    public boolean supports(@NotNull MethodParameter methodParameter, @NotNull Class aClass) {
        // 未登录不需要处理
        AccountVO accountVO = AccountUtil.getLoginInfo();
        if (Objects.isNull(accountVO)) {
            // 未登录直接返回
            return false;
        }
        Class<?> clz = methodParameter.getDeclaringClass();
        // 忽略全局异常处理器
        boolean isException = clz.isAssignableFrom(GlobalExceptionHandler.class);
        if (isException) {
            return false;
        }
        Method method = methodParameter.getMethod();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(clz.getName());
        if (Objects.nonNull(method)) {
            stringBuilder.append("#").append(method.getName());
        }
        String key = stringBuilder.toString();
        boolean ignore = IGNORE.contains(key);
        if (ignore) {
            log.info("{} 为忽略接口，不执行Http响应体的加工处理逻辑", key);
        }
        return !ignore;
    }

    private String getCachedToastText(Long userId) {
        String lockKey = CacheEnum.getCacheKey(CacheEnum.CONTRACT_RENT_REMIND_LOCK, userId.toString());
        boolean lock = redisDistLock.tryLock(lockKey, CacheEnum.CONTRACT_RENT_REMIND_LOCK.getExpire() + 1, CacheEnum.CONTRACT_RENT_REMIND_LOCK.getExpire());
        if (!lock) {
            throw new MithrasException(ResultMsg.CONCURRENT_OPERATION);
        }
        try {
            String cacheKey = CacheEnum.getCacheKey(CacheEnum.GLOBAL_TOAST_TEXT, userId.toString());
            String cacheValue = stringRedisTemplate.opsForValue().get(cacheKey);
            if (StrUtil.isNotBlank(cacheValue)) {
                if (Objects.equals(cacheValue, CACHE_NO_TOAST)) {
                    return null;
                } else {
                    return cacheValue;
                }
            }
            String text = this.insureToastText(userId);
            if (StrUtil.isBlank(text)) {
                stringRedisTemplate.opsForValue().set(cacheKey, CACHE_NO_TOAST, CacheEnum.GLOBAL_TOAST_TEXT.getExpire(), TimeUnit.MILLISECONDS);
            } else {
                stringRedisTemplate.opsForValue().set(cacheKey, text, CacheEnum.GLOBAL_TOAST_TEXT.getExpire(), TimeUnit.MILLISECONDS);
            }
            return text;
        } finally {
            redisDistLock.unlock(lockKey);
        }
    }

    private String insureToastText(Long userId) {
        List<ContractRemindRecord> contractRemindRecordList = contractRemindRecordService.listByContractCreator(userId);
        if (CollectionUtil.isEmpty(contractRemindRecordList)) {
            return null;
        }
        List<String> textList = new ArrayList<>(contractRemindRecordList.size());
        for (ContractRemindRecord contractRemindRecord : contractRemindRecordList) {
            // 如果合同已经处于起租流程中则忽略
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractRemindRecord.getContractId());
            if (Objects.equals(contractBaseInfo.getContractProcessStatus(), ContractProcessStatusEnum.START_RENT_COMMIT.name())) {
                continue;
            }
            String text = String.format(GlobalConstants.CONTRACT_TODO_TEMPLATE, contractRemindRecord.getContractCode(), contractRemindRecord.getPaymentCode());
            textList.add(text);
        }
        return CharSequenceUtil.join("\n", textList);
    }
}
