package cn.zswltech.mithras.application.orchestration.adapter.customer;

import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.customer.application.client.ClientUserDeptPort;
import cn.zswltech.mithras.system.user.SysUserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class ClientUserDeptPortAdapter implements ClientUserDeptPort {
    @Resource
    private SysUserService sysUserService;

    @Override
    public List<OrgDO> getSpecificUserDeptList(Long userId) {
        return sysUserService.getSpecificUserDeptList(userId);
    }
}
