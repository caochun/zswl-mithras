package cn.zswltech.mithras.payment.application.checker;

import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.foundation.auth.DataAuthProcessBatchGuard;
import cn.zswltech.mithras.foundation.auth.DataAuthProcessGuard;
import cn.zswltech.mithras.foundation.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.port.CurrentUserJobResolver;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/7/4
 * @description
 */
@Component
public class PaymentModifyAuthChecker implements IDataAuthChecker {
    @Resource
    private DataAuthProcessGuard dataAuthProcessGuard;
    @Resource
    private DataAuthProcessBatchGuard dataAuthProcessBatchGuard;
    @Resource
    private CurrentUserJobResolver currentUserJobResolver;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        if (Objects.isNull(keyId)) {
            throw new AuthCheckException("id不能为空");
        }
        boolean hit = currentUserJobResolver.currentUserIsSpecificJob(JobEnum.operationmanagementagent.name());
        if (!hit) {
            throw new AuthCheckException("仅运营管理部经办人可操作");
        }
        dataAuthProcessGuard.check(businessModule, keyId);
        return true;
    }

    @Override
    public boolean checkBatch(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Collection<Long> keyIds, Object[] args) {
        if (CollectionUtils.isEmpty(keyIds)) {
            throw new AuthCheckException("id列表不能为空");
        }
        boolean hit = currentUserJobResolver.currentUserIsSpecificJob(JobEnum.operationmanagementagent.name());
        if (!hit) {
            throw new AuthCheckException("仅运营管理部经办人可操作");
        }
        dataAuthProcessBatchGuard.check(businessModule, keyIds);
        return true;
    }
}
