package cn.zswltech.mithras.application.adapter.system.basedata;

import cn.zswltech.mithras.basedata.application.job.BaseDataJobUserPort;
import cn.zswltech.mithras.system.user.SysUserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Component
public class BaseDataJobUserPortAdapter implements BaseDataJobUserPort {

    @Resource
    private SysUserService sysUserService;

    @Override
    public List<Long> jobUsers(Set<String> jobCodes) {
        return sysUserService.jobUsers(jobCodes);
    }

    @Override
    public List<Long> queryJobUserIds(String jobCode) {
        return sysUserService.queryJobUserIds(jobCode);
    }
}
