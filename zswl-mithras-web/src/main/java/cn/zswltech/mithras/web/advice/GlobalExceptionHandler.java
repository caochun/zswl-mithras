package cn.zswltech.mithras.web.advice;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.exception.FlowException;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.system.mapper.SystemSwitchMapper;
import cn.zswltech.mithras.system.mapper.model.SystemSwitch;
import cn.zswltech.mithras.system.mapper.model.SystemUserOperateLog;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.exception.LackDataException;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.system.event.SystemSwitchRefreshEvent;
import cn.zswltech.mithras.system.audit.application.SystemUserOperateLogService;
import cn.zswltech.mithras.foundation.util.StringUtil;
import cn.zswltech.mithras.foundation.async.ThreadPoolUtil;
import cn.zswltech.mithras.validation.ControllerMissParamException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.api.FlowableException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.core.MethodParameter;
import org.springframework.core.NamedThreadLocal;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static cn.hutool.extra.spring.SpringUtil.getProperty;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * @author junke
 */
@Slf4j
@ResponseBody
@ControllerAdvice(basePackages = "cn.zswltech.mithras")
public class GlobalExceptionHandler implements RequestBodyAdvice, ResponseBodyAdvice, ApplicationListener<SystemSwitchRefreshEvent> {

    private static final String STORE_OPERATE_LOG_SWITCH_KEY = "STORE_OPERATE_LOG";
    private static final ConcurrentHashMap<String, Object> localCache = new ConcurrentHashMap<>();
    private static final List<String> IGNORE_URLS = ListUtil.of("/api/ok");

    private static final NamedThreadLocal<String> requestDataHolder = new NamedThreadLocal<>("requestDataHolder");

    private static Logger reqLogger = LoggerFactory.getLogger("reqLogger");


    @ExceptionHandler(FlowException.class)
    public R<Void> flowException(FlowException exception) {
        log.error("flowException, loginUserId:{}, uri:{}, body:{}",
                Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null),
                requestUri(),
                Optional.ofNullable(requestDataHolder.get()).orElse(requestData()),
                exception);
        return R.fail(exception.getMessage());
    }

    @ExceptionHandler(FlowableException.class)
    public R<Void> flowException(FlowableException exception) {
        log.error("flowException, loginUserId:{}, uri:{}, body:{}",
                Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null),
                requestUri(),
                Optional.ofNullable(requestDataHolder.get()).orElse(requestData()),
                exception);
        return R.fail(exception.getCause().getMessage());
    }

    @ExceptionHandler(AuthCheckException.class)
    public R<Void> authCheckException(AuthCheckException e) {
        log.warn("authCheckException, loginUserId:{}, uri:{}, body:{}",
                Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null),
                requestUri(),
                Optional.ofNullable(requestDataHolder.get()).orElse(requestData()),
                e);
        return R.fail(String.format("权限校验失败: %s", e.getMessage()));
    }

    @ExceptionHandler(MithrasException.class)
    public R<Void> mithrasException(MithrasException exception) {
        log.warn("mithrasException, loginUserId:{}, uri:{}, body:{}",
                Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null),
                requestUri(),
                Optional.ofNullable(requestDataHolder.get()).orElse(requestData()),
                exception);
        if (exception.getCode() == 400) {
            return R.fail(exception.getMessage());
        }else {
            return R.customFail(exception.getCode(), exception.getMessage());
        }
    }

    @ExceptionHandler(LackDataException.class)
    public R<Void> lackDataException(LackDataException exception) {
        log.error("lackDataException, loginUserId:{}, uri:{}, body:{}",
                Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null),
                requestUri(),
                Optional.ofNullable(requestDataHolder.get()).orElse(requestData()),
                exception);
        return R.fail(String.format("数据不完整:%s，请完善数据后继续操作", exception.getMessage()));
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R<Void> httpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("httpMessageNotReadableException, loginUserId:{}, uri:{}, body:{}",
                Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null),
                requestUri(),
                Optional.ofNullable(requestDataHolder.get()).orElse(requestData()),
                ex);
        if (ex.getCause() instanceof InvalidFormatException) {
            return R.fail("参数类型转换错误");
        } else {
            return R.fail("参数body错误:" + ex.getMessage());
        }
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public R<Void> httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        log.error("httpMediaTypeNotSupportedException, loginUserId:{}, uri:{}, body:{}",
                Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null),
                requestUri(),
                Optional.ofNullable(requestDataHolder.get()).orElse(requestData()),
                ex);
        return R.fail("HttpMediaType不支持");
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public R<Void> maxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        log.error("MaxUploadSizeExceededException, loginUserId:{}, uri:{}, body:{}",
                Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null),
                requestUri(),
                Optional.ofNullable(requestDataHolder.get()).orElse(requestData()),
                ex);
        return R.fail("支持最大的上传文件的大小为：" + getProperty("spring.servlet.multipart.max-file-size"));
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<Void> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.error("httpRequestMethodNotSupportedException, loginUserId:{}, uri:{}, body:{}",
                Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null),
                requestUri(),
                Optional.ofNullable(requestDataHolder.get()).orElse(requestData()),
                ex);
        return R.fail("method不支持");
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    public R<Void> bindException(Exception ex) {
        log.warn("bindException, loginUserId:{}, uri:{}, body:{}",
                Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null),
                requestUri(),
                Optional.ofNullable(requestDataHolder.get()).orElse(requestData()),
                ex);
        BindingResult bindResult;
        if (ex instanceof BindException) {
            bindResult = ((BindException) ex).getBindingResult();
        } else {
            bindResult = ((MethodArgumentNotValidException) ex).getBindingResult();
        }
        ;
        List<FieldError> fieldErrors = bindResult.getFieldErrors();
        if (ObjectUtil.isNull(bindResult.getTarget())) {
            return R.fail("参数错误");
        } else {
            return R.fail(generateMsg(bindResult.getTarget().getClass(), fieldErrors));
        }
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(ServletRequestBindingException.class)
    public R<Void> servletRequestBindingException(ServletRequestBindingException ex) {
        log.warn("servletRequestBindingException, loginUserId:{}, uri:{}, body:{}",
                Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null),
                requestUri(),
                Optional.ofNullable(requestDataHolder.get()).orElse(requestData()),
                ex);
        return R.fail(ex.getMessage());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({MissingServletRequestParameterException.class})
    public R<Void> missingServletRequestParameterException(MissingServletRequestParameterException ex) {
        log.warn("missingServletRequestParameterException, loginUserId:{}, uri:{}, body:{}",
                Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null),
                requestUri(),
                Optional.ofNullable(requestDataHolder.get()).orElse(requestData()),
                ex);
        return R.fail("缺少必要参数:" + ex.getParameterName());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({ControllerMissParamException.class})
    public R<Void> controllerMissParamException(ControllerMissParamException ex) {
        log.warn("controllerMissParamException, loginUserId:{}, uri:{}, body:{}",
                Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null),
                requestUri(),
                Optional.ofNullable(requestDataHolder.get()).orElse(requestData()),
                ex);
        return R.fail("缺少必要参数:" + ex.getParam());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({ConstraintViolationException.class})
    public R<Void> constraintViolationException(ConstraintViolationException ex) {
        log.error("constraintViolationException, loginUserId:{}, uri:{}, body:{}",
                Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null),
                requestUri(),
                Optional.ofNullable(requestDataHolder.get()).orElse(requestData()),
                ex);
        String message = ex.getMessage();
        Iterator<ConstraintViolation<?>> iterator = ex.getConstraintViolations().iterator();
        while (iterator.hasNext()) {
            ConstraintViolation<?> next = iterator.next();
            message = next.getMessageTemplate();
        }
        return R.fail(message);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({DuplicateKeyException.class})
    public R<Void> duplicateKeyException(DuplicateKeyException ex) {
        log.error("duplicateKeyException, loginUserId:{}, uri:{}, body:{}",
                Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null),
                requestUri(),
                Optional.ofNullable(requestDataHolder.get()).orElse(requestData()),
                ex);
        return R.fail("插入数据造成unique索引重复");
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public R<Void> handleError(Throwable ex) {
        log.error("handleError, loginUserId:{}, uri:{}, body:{}",
                Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null),
                requestUri(),
                Optional.ofNullable(requestDataHolder.get()).orElse(requestData()),
                ex);
        return R.fail();
    }

    private String generateMsg(Class<?> targetClz, List<FieldError> fieldErrors) {
        StringBuilder sb = new StringBuilder("参数错误：");
        sb.append("{");
        for (FieldError error : fieldErrors) {
            String fieldDesc = fieldDesc(targetClz, error.getField());
            sb.append(fieldDesc).append(":").append(error.getDefaultMessage()).append(" ");
        }
        sb.append("}");
        return sb.toString();
    }

    private String fieldDesc(Class<?> clz, String fieldName) {
        try {
            Field declaredField = ReflectUtil.getField(clz, fieldName);
            ApiModelProperty annotation = declaredField.getAnnotation(ApiModelProperty.class);
            if (null != annotation) {
                return annotation.value();
            }
        } catch (Exception ignored) {
            log.warn("find bind result field desc failed. {}", ignored.getMessage());
        }
        return fieldName;
    }

    private String requestData() {
        try {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (null == requestAttributes) {
                return "unknown request uri";
            }
            HttpServletRequest request = requestAttributes.getRequest();
            return JSON.toJSONString(request.getParameterMap());
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private String requestUri() {
        try {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (null == requestAttributes) {
                return "unknown request uri";
            }
            HttpServletRequest request = requestAttributes.getRequest();
            return request.getRequestURI() + (StringUtils.isBlank(request.getQueryString()) ? "" : "?" + request.getQueryString());
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return inputMessage;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        requestDataHolder.set(JSON.toJSONString(body));
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        logOperation();
        requestDataHolder.remove();
        return body;
    }

    private void logOperation() {
        try {
            JSONObject jo = new JSONObject();
            jo.put("uri", requestUri());
            jo.put("user", Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getAccount).orElse(null));
            jo.put("body", Optional.ofNullable(requestDataHolder.get()).orElse(requestData()));
            reqLogger.info(JSONUtil.toJsonPrettyStr(jo));
            // 持久化到数据库
            if (Objects.equals(localCache.get(STORE_OPERATE_LOG_SWITCH_KEY), YesOrNoNumberEnum.NO.getCode())) {
                return;
            }
            // TODO 比较好的做法是从ES查数据，先落数据库应付等保检查吧
            final String url = requestUri();
            if (IGNORE_URLS.contains(url)) {
                return;
            }
            final Long userId = Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null);
            final String reqData = Optional.ofNullable(requestDataHolder.get()).orElse(requestData());
            // 异步保存到数据库
            ThreadPoolUtil.getCommonPool().execute(() -> {
                try {
                    SystemUserOperateLog systemUserOperateLog = new SystemUserOperateLog();
                    systemUserOperateLog.setUrl(url);
                    systemUserOperateLog.setUserId(userId);
                    if (Objects.nonNull(userId)) {
                        String userName = SpringUtil.getBean(Id2NameService.class).sysUserId2NameSingle(userId);
                        systemUserOperateLog.setUserName(userName);
                    }
                    systemUserOperateLog.setReqData(reqData);
                    SpringUtil.getBean(SystemUserOperateLogService.class).save(systemUserOperateLog);
                } catch (Exception e) {
                    log.error("持久化用户操作日志发生异常，不影响正常业务逻辑[url:{}, userId:{}]", url, userId, e);
                }
            });
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void onApplicationEvent(@NotNull SystemSwitchRefreshEvent event) {
        LambdaQueryWrapper<SystemSwitch> query = Wrappers.<SystemSwitch>lambdaQuery().eq(SystemSwitch::getCode, STORE_OPERATE_LOG_SWITCH_KEY);
        query.last(StringUtil.mysqlLimitOne());
        SystemSwitch result = SpringUtil.getBean(SystemSwitchMapper.class).selectOne(query);
        log.info("刷新配置参数本地缓存[持久化操作日志: {}]", JSONUtil.toJsonStr(result));
        if (Objects.isNull(result)) {
            localCache.put(STORE_OPERATE_LOG_SWITCH_KEY, YesOrNoNumberEnum.NO.getCode());
        } else {
            localCache.put(STORE_OPERATE_LOG_SWITCH_KEY, result.getValue());
        }
    }
}
