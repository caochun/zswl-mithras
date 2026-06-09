package cn.zswltech.mithras.application.adapter.system.basedata;

import cn.zswltech.mithras.basedata.application.BaseDataBankAccountUserPort;
import cn.zswltech.mithras.system.user.Id2NameService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;

@Component
public class BaseDataBankAccountUserPortAdapter implements BaseDataBankAccountUserPort {

    @Resource
    private Id2NameService id2NameService;

    @Override
    public Map<Long, String> sysUserId2Name(Set<Long> userIds) {
        return id2NameService.sysUserId2Name(userIds);
    }
}
