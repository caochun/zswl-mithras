package cn.zswltech.mithras.foundation.persistence.plugin;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.defaults.DefaultSqlSession;

import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * 操作人 拦截器
 * 使用时在实体类上标注@AutoAuditEntity注解
 * 如果该次操作不覆盖更新操作人，则再通过threadlocal透传一个值过来打标
 *
 * @author wangchuanhao
 * @date 2022/6/20 10:54 PM
 */
@Slf4j
@Intercepts({@Signature(method = "update", type = Executor.class, args = {MappedStatement.class, Object.class})})
public class AuditDataInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 如果取不到登陆用户 就不做用户的处理
        AccountVO loginUser = AccountUtil.getLoginInfo();
        if (Objects.isNull(loginUser)) {
            return invocation.proceed();
        }

        Object[] args = invocation.getArgs();
        SqlCommandType sqlCommandType = null;
        
        for (Object object : args) {
            // 从MappedStatement参数中获取到操作类型
            if (object instanceof MappedStatement) {
                MappedStatement ms = (MappedStatement) object;
                sqlCommandType = ms.getSqlCommandType();
                log.debug("操作类型： {}", sqlCommandType);
                continue;
            } else if (object instanceof BaseModel) {
                //  判断参数是否是BaseModel类型 一个参数
                setAuditVal(sqlCommandType, object, loginUser.getId());
            } else if (object instanceof MapperMethod.ParamMap) {
                // 兼容MyBatis的updateByExampleSelective(record, example);
                log.debug("mybatis arg: {}", object);
                @SuppressWarnings("unchecked")
                MapperMethod.ParamMap<Object> parasMap = (MapperMethod.ParamMap<Object>) object;
                if (parasMap.containsKey("param1")) {
                    setAuditVal(sqlCommandType, parasMap.get("param1"), loginUser.getId());
                }
                if (parasMap.containsKey("record")) {
                    setAuditVal(sqlCommandType, parasMap.get("record"), loginUser.getId());
                }

            } else if (object instanceof DefaultSqlSession.StrictMap) {
                // 兼容批量插入
                log.debug("mybatis arg: {}", object);
                @SuppressWarnings("unchecked")
                DefaultSqlSession.StrictMap<List<Object>> map = (DefaultSqlSession.StrictMap<List<Object>>) object;
                String key = "collection";
                if (!map.containsKey(key)) {
                    continue;
                }
                List<Object> objs = map.get(key);
                for (Object obj : objs) {
                    setAuditVal(sqlCommandType, obj, loginUser.getId());
                }
            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    /**
     * 判断是否处理
     * @return
     */
    private boolean needRecordAuditInfo(Object obj) {
        AutoAuditEntity autoAuditEntity = obj.getClass().getAnnotation(AutoAuditEntity.class);
        if (autoAuditEntity == null) {
            return false;
        }
        return true;
    }

    /**
     * 设置审批人
     * @param commandType
     * @param obj
     */
    private void setAuditVal(SqlCommandType commandType, Object obj, Long curUserId) {
        if (Objects.isNull(obj) || !needRecordAuditInfo(obj)) {
            return;
        }
        if (obj instanceof BaseModel) {
            if (SqlCommandType.INSERT.equals(commandType)) {
                if (Objects.isNull(((BaseModel) obj).getCreateBy())) {
                    ((BaseModel) obj).setCreateBy(curUserId);
                }
                if (Objects.isNull(((BaseModel) obj).getUpdateBy())) {
                    ((BaseModel) obj).setUpdateBy(curUserId);
                }
            } else if (SqlCommandType.UPDATE.equals(commandType)) {
                if (Objects.isNull(((BaseModel) obj).getUpdateBy())) {
                    ((BaseModel) obj).setUpdateBy(curUserId);
                }
            }
        }
    }
}