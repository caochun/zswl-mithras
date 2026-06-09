package cn.zswltech.mithras.application.adapter.system.projlifecycle;

import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.projlifecycle.application.ProjLifecycleAuthPort;
import cn.zswltech.mithras.system.user.SysUserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class ProjLifecycleAuthPortAdapter implements ProjLifecycleAuthPort {

    @Resource
    private SysUserService sysUserService;

    @Override
    public Long currentUserId() {
        return AccountUtil.getLoginInfo().getId();
    }

    @Override
    public List<Long> canViewDeptIds() {
        return sysUserService.canViewDeptIds();
    }
}
