package cn.zswltech.mithras.service.auth.checker.payment;

import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.service.auth.rule.DataAuthProcessBatchRule;
import cn.zswltech.mithras.service.auth.rule.DataAuthProcessRule;
import cn.zswltech.mithras.service.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.system.user.SysUserService;
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
    private DataAuthProcessRule dataAuthProcessRule;
    @Resource
    private DataAuthProcessBatchRule dataAuthProcessBatchRule;
    @Resource
    private SysUserService sysUserService;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        if (Objects.isNull(keyId)) {
            throw new AuthCheckException("id不能为空");
        }
        boolean hit = sysUserService.currentUserIsSpecificJob(JobEnum.operationmanagementagent.name());
        if (!hit) {
            throw new AuthCheckException("仅运营管理部经办人可操作");
        }
        dataAuthProcessRule.check(businessModule, keyId);
        return true;
    }

    @Override
    public boolean checkBatch(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Collection<Long> keyIds, Object[] args) {
        if (CollectionUtils.isEmpty(keyIds)) {
            throw new AuthCheckException("id列表不能为空");
        }
        boolean hit = sysUserService.currentUserIsSpecificJob(JobEnum.operationmanagementagent.name());
        if (!hit) {
            throw new AuthCheckException("仅运营管理部经办人可操作");
        }
        dataAuthProcessBatchRule.check(businessModule, keyIds);
        return true;
    }
}
