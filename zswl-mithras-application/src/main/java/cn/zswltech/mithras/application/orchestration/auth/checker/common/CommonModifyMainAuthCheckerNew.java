package cn.zswltech.mithras.application.orchestration.auth.checker.common;


import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.foundation.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.application.orchestration.auth.rule.DataAuthSponsorUserBatchRule;
import cn.zswltech.mithras.application.orchestration.auth.rule.DataAuthSponsorUserRule;
import cn.zswltech.mithras.application.orchestration.auth.rule.DataAuthProcessBatchRule;
import cn.zswltech.mithras.application.orchestration.auth.rule.DataAuthProcessRule;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/7/28
 * @description 主表修改权限校验
 */
@Component
public class CommonModifyMainAuthCheckerNew implements IDataAuthChecker {

    @Resource
    private DataAuthSponsorUserRule dataAuthSponsorUserRule;
    @Resource
    private DataAuthProcessRule dataAuthProcessRule;
    @Resource
    private DataAuthSponsorUserBatchRule dataAuthSponsorUserBatchRule;
    @Resource
    private DataAuthProcessBatchRule dataAuthProcessBatchRule;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        if (Objects.isNull(keyId)) {
            throw new AuthCheckException("id不能为空");
        }
        dataAuthSponsorUserRule.check(businessModule, keyId);
        dataAuthProcessRule.check(businessModule, keyId);
        return true;
    }

    public boolean appCheck(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args, Long createdBy) {
        if (Objects.isNull(keyId)) {
            throw new AuthCheckException("id不能为空");
        }
        dataAuthSponsorUserRule.appCheck(businessModule, keyId, createdBy);
        dataAuthProcessRule.appCheck(businessModule, keyId, createdBy);
        return true;
    }

    @Override
    public boolean checkBatch(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Collection<Long> keyIds, Object[] args) {
        if (CollectionUtils.isEmpty(keyIds)) {
            throw new AuthCheckException("id列表不能为空");
        }
        dataAuthSponsorUserBatchRule.check(businessModule, keyIds);
        dataAuthProcessBatchRule.check(businessModule, keyIds);
        return true;
    }

}
