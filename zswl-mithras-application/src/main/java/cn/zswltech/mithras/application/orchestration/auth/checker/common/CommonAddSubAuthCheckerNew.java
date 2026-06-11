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
 * @date 2022/7/28
 * @description 子表数据添加校验
 */
@Component
public class CommonAddSubAuthCheckerNew implements IDataAuthChecker {

    @Resource
    private DataAuthSponsorUserRule dataAuthSponsorUserRule;
    @Resource
    private DataAuthProcessRule dataAuthProcessRule;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        if (Objects.isNull(keyId)) {
            throw new AuthCheckException("主表id不能为空");
        }
        // 对于子表数据的新增从业务上来看就是对一个主表数据的编辑修改，通用的逻辑是创建人可以进行修改，所以子表数据的添加校验是否主表创建人
        dataAuthSponsorUserRule.check(businessModule, keyId);
        dataAuthProcessRule.check(businessModule, keyId);
        return true;
    }

}
