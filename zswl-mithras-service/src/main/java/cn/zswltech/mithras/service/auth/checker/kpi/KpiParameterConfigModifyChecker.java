package cn.zswltech.mithras.service.auth.checker.kpi;

import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.service.SysUserService;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2023/2/14
 * @description
 */
@Component
public class KpiParameterConfigModifyChecker implements IDataAuthChecker {
    @Resource
    private SysUserService sysUserService;

    @Override
    public boolean check(BusinessModuleEnum businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        // 目前暂定管理员有编辑权限
        boolean isAdmin = sysUserService.currentUserIsSpecificJob(JobEnum.admin.name());
        if (!isAdmin) {
            throw new AuthCheckException("只允许管理员操作");
        }
        return true;
    }
}
