package cn.zswltech.mithras.application.orchestration.auth.checker.common;


import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.foundation.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.application.orchestration.auth.rule.DataAuthSponsorUserRule;
import cn.zswltech.mithras.application.orchestration.auth.rule.DataAuthProcessRule;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/7/27
 * @description 主表删除权限校验
 */
@Component
public class CommonRemoveMainAuthCheckerNew implements IDataAuthChecker {

    @Resource
    private DataAuthSponsorUserRule dataAuthSponsorUserRule;
    @Resource
    private DataAuthProcessRule dataAuthProcessRule;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        if (Objects.isNull(keyId)) {
            throw new AuthCheckException("id不能为空");
        }
        dataAuthSponsorUserRule.check(businessModule, keyId);
        dataAuthProcessRule.check(businessModule, keyId);
        return true;
    }

}
