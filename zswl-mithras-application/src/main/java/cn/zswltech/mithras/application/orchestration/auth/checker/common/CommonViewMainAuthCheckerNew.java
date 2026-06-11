package cn.zswltech.mithras.application.orchestration.auth.checker.common;


import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.foundation.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.application.orchestration.auth.rule.DataAuthViewRule;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;


/**
 * @author dingqi
 * @date 2022/7/27
 * @description 主表数据查看权限校验
 */
@Component
public class CommonViewMainAuthCheckerNew implements IDataAuthChecker {

    @Resource
    private DataAuthViewRule dataAuthViewRule;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        if (Objects.isNull(keyId)) {
            throw new AuthCheckException("id不能为空");
        }
        dataAuthViewRule.check(businessModule, keyId);
        return true;
    }

}
