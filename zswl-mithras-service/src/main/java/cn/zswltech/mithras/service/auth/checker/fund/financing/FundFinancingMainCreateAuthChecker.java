package cn.zswltech.mithras.service.auth.checker.fund.financing;

import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.system.service.SysUserService;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2023/2/24
 * @description
 */
@Component
public class FundFinancingMainCreateAuthChecker implements IDataAuthChecker {
    @Resource
    private SysUserService sysUserService;

    @Override
    public boolean check(BusinessModuleEnum businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        // FIXME 只有资金部门的人才能创建融资 但是目前组织架构还未调整 先用岗位来替代一下
        boolean canCreate = sysUserService.currentUserIsSpecificJob(JobEnum.moneymanager.name(), JobEnum.moneymanagerleader.name());
        if (!canCreate) {
            throw new AuthCheckException("仅资金部门员工可以创建");
        }
        return true;
    }
}
