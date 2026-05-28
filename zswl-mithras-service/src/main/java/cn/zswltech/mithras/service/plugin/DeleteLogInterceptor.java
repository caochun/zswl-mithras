package cn.zswltech.mithras.service.plugin;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.service.SystemDatabaseRecordService;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.mapper.SystemSwitchMapper;
import cn.zswltech.mithras.service.mapper.model.SystemDatabaseRecord;
import cn.zswltech.mithras.service.mapper.model.SystemSwitch;
import cn.zswltech.mithras.service.service.Listener.SystemSwitchRefreshEvent;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2024/2/19
 * @description
 */
@Slf4j
@Intercepts({@Signature(method = "update", type = Executor.class, args = {MappedStatement.class, Object.class})})
public class DeleteLogInterceptor implements Interceptor, ApplicationListener<SystemSwitchRefreshEvent> {
    private static final String FUNCTION_DELETE_BY_ID = "deleteById";
    private static final String FUNCTION_DELETE_BATCH_IDS = "deleteBatchIds";
    private static final String FUNCTION_DELETE = "delete";

    private static final String SYSTEM_SWITCH_CODE = "STORE_DELETE_RECORD";

    private static final ConcurrentHashMap<String, Object> localCache = new ConcurrentHashMap<>();

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (!this.needStoreDeleteRecord()) {
            return invocation.proceed();
        }
        MappedStatement ms = null;
        List<SystemDatabaseRecord> toInsertList = null;
        try {
            ms = (MappedStatement) invocation.getArgs()[0];
            Object parameter = invocation.getArgs()[1];
            if (Objects.nonNull(ms) && SqlCommandType.DELETE == ms.getSqlCommandType()) {
                toInsertList = this.prepare(ms, parameter);
            }
        } catch (Exception e) {
            log.error("记录物理删除原始数据发生异常, 但不影响正常逻辑[{}]", Optional.ofNullable(ms).map(MappedStatement::getId).orElse(null), e);
        }
        Object result = invocation.proceed();
        // 切面逻辑正常的话保存日志操作，如果后续事务操作存在失败，理论上会回滚操作记录数据
        if (CollectionUtil.isNotEmpty(toInsertList)) {
            SpringUtil.getBean(SystemDatabaseRecordService.class).saveBatch(toInsertList);
        }
        return result;
    }

    private boolean needStoreDeleteRecord() {
        try {
            return Objects.equals(localCache.get(SYSTEM_SWITCH_CODE), YesOrNoNumberEnum.YES.getCode());
        } catch (Exception e) {
            log.error("获取系统配置[存储删除记录]发生异常，默认为不保存进行后续处理", e);
        }
        return false;
    }

    private List<SystemDatabaseRecord> prepare(MappedStatement ms, Object parameter) {
        Long currentUserId = Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null);
        String originalParameterStr = null;
        if (Objects.nonNull(parameter)) {
            originalParameterStr = parameter instanceof Number ? parameter.toString() : JSONUtil.toJsonStr(parameter);
        }
        log.info("发生物理删除操作, functionId:{}, args:{}, userId:{}", ms.getId(), originalParameterStr, currentUserId);
        // 取出表名判断是否需要保存删除记录
        String originSql = ms.getBoundSql(parameter).getSql();
        final String tableName = this.extractTablesFromSql(originSql);
        if (StrUtil.isBlank(tableName)) {
            return Collections.emptyList();
        }
        if (tableName.toLowerCase().startsWith("act")) {
            // 审批流的表数据不记录
            return Collections.emptyList();
        }
        if (tableName.toLowerCase().startsWith("cr")) {
            // 征信报送的表数据不记录
            return Collections.emptyList();
        }
        // TODO 需要根据删除方法定制查询方法，理论上后续不会有新的自定义删除方法，如果有，需要在这里加入对应查询逻辑
        String functionId = this.ensureFunctionId(ms);
        List<Map<String, Object>> queryResult = null;
        switch (functionId) {
            case FUNCTION_DELETE_BY_ID: {
                queryResult = this.queryBeforeDeleteById(ms, parameter);
                break;
            }
            case FUNCTION_DELETE_BATCH_IDS: {
                queryResult = this.queryBeforeDeleteBatchIds(ms, parameter);
                break;
            }
            case FUNCTION_DELETE: {
                queryResult = this.queryBeforeDelete(ms, parameter);
                break;
            }
        }
        if (Objects.isNull(queryResult) || CollectionUtil.isEmpty(queryResult)) {
            return Collections.emptyList();
        }
        // 保存原始数据
        String originParameter = originalParameterStr;
        String operation = ms.getSqlCommandType().name();
        String batchSeq = LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_MS_PATTERN);
        return queryResult.stream().map(e -> {
            SystemDatabaseRecord record = new SystemDatabaseRecord();
            record.setOperation(operation);
            record.setOperatorId(currentUserId);
            record.setBatchSequence(batchSeq);
            record.setOriginSql(originSql);
            record.setOriginParameter(originParameter);
            record.setTableName(StrUtil.isBlank(tableName) ? "unknown" : tableName);
            record.setRowData(JSONUtil.toJsonStr(e));
            return record;
        }).collect(Collectors.toList());
    }

    private String ensureFunctionId(MappedStatement ms) {
        int index = ms.getId().lastIndexOf(".");
        return ms.getId().substring(index + 1);
    }

    private String extractTablesFromSql(String sql) {
        List<String> tables = new ArrayList<>();
        // 定义正则表达式模式，用于匹配表名
        Pattern pattern = Pattern.compile("FROM\\s+(\\w+)");
        Matcher matcher = pattern.matcher(sql);
        while (matcher.find()) {
            tables.add(matcher.group(1));
        }
        if (tables.size() == 0) {
            return null;
        }
        return tables.get(0);
    }

    private List<Map<String, Object>> queryBeforeDeleteById(MappedStatement mappedStatement, Object parameter) {
        String originSql = mappedStatement.getBoundSql(parameter).getSql();
        String querySql = "SELECT * " + originSql.substring(6);
        return jdbcTemplate.queryForList(querySql, parameter);
    }

    private List<Map<String, Object>> queryBeforeDeleteBatchIds(MappedStatement mappedStatement, Object parameter) {
        String originSql = mappedStatement.getBoundSql(parameter).getSql();
        String querySql = "SELECT * " + originSql.substring(6);
        if (parameter instanceof MapperMethod.ParamMap) {
            Object v = ((MapperMethod.ParamMap<?>) parameter).get("param1");
            if (Objects.nonNull(v) && v instanceof Collection) {
                Object[] arr = ((Collection) v).toArray();
                return jdbcTemplate.queryForList(querySql, arr);
            }
        }
        return null;
    }

    private List<Map<String, Object>> queryBeforeDelete(MappedStatement mappedStatement, Object parameter) {
        String originSql = mappedStatement.getBoundSql(parameter).getSql();
        String querySql = "SELECT * " + originSql.substring(6);
        if (parameter instanceof MapperMethod.ParamMap) {
            Object v = ((MapperMethod.ParamMap<?>) parameter).get("param1");
            if (Objects.nonNull(v) && (v instanceof QueryWrapper || v instanceof LambdaQueryWrapper)) {
                JSONObject jsonObject = JSONUtil.parseObj(v);
                Map valueMap = jsonObject.get("paramNameValuePairs", Map.class);
                if (CollectionUtil.isNotEmpty(valueMap)) {
                    Object[] valueArray = new Object[valueMap.size()];
                    for (int i = 0; i < valueMap.size(); i++) {
                        // 不需要考虑集合参数，集合参数会被打散到MPGENVAL中
                        // 比如参数是[1,2,3]，在map中会被打散成MPGENVAL1、MPGENVAL2和MPGENVAL3中
                        valueArray[i] = valueMap.get("MPGENVAL" + (i + 1));
                    }
                    return jdbcTemplate.queryForList(querySql, valueArray);
                }
            }
        }
        return null;
    }

    @Override
    public void onApplicationEvent(SystemSwitchRefreshEvent systemSwitchRefreshEvent) {
        LambdaQueryWrapper<SystemSwitch> query = Wrappers.<SystemSwitch>lambdaQuery().eq(SystemSwitch::getCode, SYSTEM_SWITCH_CODE);
        query.last(StringUtil.mysqlLimitOne());
        SystemSwitch result = SpringUtil.getBean(SystemSwitchMapper.class).selectOne(query);
        log.info("刷新配置参数本地缓存[存储物理删除日志: {}]", JSONUtil.toJsonStr(result));
        if (Objects.isNull(result)) {
            localCache.put(SYSTEM_SWITCH_CODE, YesOrNoNumberEnum.NO.getCode());
        } else {
            localCache.put(SYSTEM_SWITCH_CODE, result.getValue());
        }
    }
}
