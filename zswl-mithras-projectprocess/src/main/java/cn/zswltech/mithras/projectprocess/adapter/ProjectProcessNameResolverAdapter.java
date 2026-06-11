package cn.zswltech.mithras.projectprocess.adapter;

import cn.zswltech.mithras.projectprocess.application.support.ProjectProcessNameResolver;
import cn.zswltech.mithras.foundation.port.ClientNameResolver;
import cn.zswltech.mithras.foundation.port.DeptNameResolver;
import cn.zswltech.mithras.foundation.port.UserNameResolver;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Map;

@Component
public class ProjectProcessNameResolverAdapter implements ProjectProcessNameResolver {

    @Resource
    private ClientNameResolver clientNameResolver;
    @Resource
    private UserNameResolver userNameResolver;
    @Resource
    private DeptNameResolver deptNameResolver;

    @Override
    public String clientId2NameSingle(Long clientId) {
        return clientNameResolver.clientId2NameSingle(clientId);
    }

    @Override
    public Map<Long, String> clientId2Name(Collection<Long> clientIds) {
        return clientNameResolver.clientId2Name(clientIds);
    }

    @Override
    public Map<Long, String> sysUserId2Name(Collection<Long> userIds) {
        return userNameResolver.sysUserId2Name(userIds);
    }

    @Override
    public Map<Long, String> deptId2Name(Collection<Long> deptIds) {
        return deptNameResolver.deptId2Name(deptIds);
    }
}
