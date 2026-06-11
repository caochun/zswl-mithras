package cn.zswltech.mithras.kpi.application.auth;

import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.foundation.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.port.AdminAuthResolver;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 绩效参数配置没有业务主键和流程态，按系统管理员权限校验配置修改入口。
 */
@Component
public class KpiParameterConfigModifyChecker implements IDataAuthChecker {

    @Resource
    private AdminAuthResolver adminAuthResolver;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        if (!adminAuthResolver.adminAuth()) {
            throw new AuthCheckException("非管理员无权修改绩效参数配置");
        }
        return true;
    }
}
