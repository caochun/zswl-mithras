package cn.zswltech.mithras.service.auth.checker.payment;

import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.service.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.system.user.SysUserService;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * @author dingqi
 * @date 2023/7/4
 * @description
 */
@Component
public class PaymentDisableAuthChecker implements IDataAuthChecker {
    @Resource
    private SysUserService sysUserService;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        boolean hit = sysUserService.currentUserIsSpecificJob(JobEnum.operationmanagementagent.name());
        if (!hit) {
            throw new AuthCheckException("仅运营管理部经办人可操作");
        }
        return true;
    }

    @Override
    public boolean checkBatch(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Collection<Long> keyIds, Object[] args) {
        boolean hit = sysUserService.currentUserIsSpecificJob(JobEnum.operationmanagementagent.name());
        if (!hit) {
            throw new AuthCheckException("仅运营管理部经办人可操作");
        }
        return true;
    }
}
