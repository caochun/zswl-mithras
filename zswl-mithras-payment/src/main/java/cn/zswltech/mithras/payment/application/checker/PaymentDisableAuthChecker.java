package cn.zswltech.mithras.payment.application.checker;

import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.foundation.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.port.CurrentUserJobResolver;
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
    private CurrentUserJobResolver currentUserJobResolver;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        boolean hit = currentUserJobResolver.currentUserIsSpecificJob(JobEnum.operationmanagementagent.name());
        if (!hit) {
            throw new AuthCheckException("仅运营管理部经办人可操作");
        }
        return true;
    }

    @Override
    public boolean checkBatch(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Collection<Long> keyIds, Object[] args) {
        boolean hit = currentUserJobResolver.currentUserIsSpecificJob(JobEnum.operationmanagementagent.name());
        if (!hit) {
            throw new AuthCheckException("仅运营管理部经办人可操作");
        }
        return true;
    }
}
