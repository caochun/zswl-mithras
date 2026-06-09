package cn.zswltech.mithras.application.adapter.system.fund;

import cn.zswltech.mithras.fund.application.FundOrganizationNamePort;
import cn.zswltech.mithras.system.user.Id2NameService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Map;

@Component
public class FundOrganizationNamePortAdapter implements FundOrganizationNamePort {

    @Resource
    private Id2NameService id2NameService;

    @Override
    public Map<Long, String> sysUserId2Name(Collection<Long> userIds) {
        return id2NameService.sysUserId2Name(userIds);
    }
}
