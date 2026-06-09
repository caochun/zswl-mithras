package cn.zswltech.mithras.application.adapter.workflow;

import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.workflow.application.flow.port.CurrentUserBizDeptResolver;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class CurrentUserBizDeptResolverAdapter implements CurrentUserBizDeptResolver {
    @Resource
    private SysUserService sysUserService;

    @Override
    public OrgDO currentUserBizDept() {
        return sysUserService.currentUserBizDept();
    }
}
