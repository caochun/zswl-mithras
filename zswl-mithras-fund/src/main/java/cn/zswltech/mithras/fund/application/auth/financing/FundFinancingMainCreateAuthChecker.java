package cn.zswltech.mithras.fund.application.auth.financing;

import cn.zswltech.mithras.foundation.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.port.CurrentUserJobResolver;
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
    private CurrentUserJobResolver currentUserJobResolver;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        // FIXME 只有资金部门的人才能创建融资 但是目前组织架构还未调整 先用岗位来替代一下
        boolean canCreate = currentUserJobResolver.currentUserIsSpecificJob(JobEnum.moneymanager.name(), JobEnum.moneymanagerleader.name());
        if (!canCreate) {
            throw new AuthCheckException("仅资金部门员工可以创建");
        }
        return true;
    }
}
