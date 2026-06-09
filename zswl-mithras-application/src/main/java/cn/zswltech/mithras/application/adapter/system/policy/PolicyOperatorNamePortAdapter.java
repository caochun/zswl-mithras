package cn.zswltech.mithras.application.adapter.system.policy;

import cn.zswltech.mithras.policy.application.PolicyOperatorNamePort;
import cn.zswltech.mithras.system.user.Id2NameService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Map;

@Component
public class PolicyOperatorNamePortAdapter implements PolicyOperatorNamePort {

    @Resource
    private Id2NameService id2NameService;

    @Override
    public Map<Long, String> sysUserId2Name(Collection<Long> userIds) {
        return id2NameService.sysUserId2Name(userIds);
    }
}
