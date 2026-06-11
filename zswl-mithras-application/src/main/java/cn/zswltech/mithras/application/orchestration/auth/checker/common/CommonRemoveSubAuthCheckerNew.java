package cn.zswltech.mithras.application.orchestration.auth.checker.common;


import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.foundation.auth.checker.AuthHelper;
import cn.zswltech.mithras.foundation.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.application.orchestration.auth.rule.DataAuthSponsorUserRule;
import cn.zswltech.mithras.application.orchestration.auth.rule.DataAuthProcessRule;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 子表数据删除权限验证
 * 可用于需转一层才能拿到主表id的
 *
 * @author wangchuanhao
 * @date 2022/7/22 12:05 AM
 */
@Component
@Slf4j
public class CommonRemoveSubAuthCheckerNew implements IDataAuthChecker {
    @Resource
    private DataAuthSponsorUserRule dataAuthSponsorUserRule;
    @Resource
    private DataAuthProcessRule dataAuthProcessRule;
    @Resource
    private AuthHelper authHelper;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        if (Objects.isNull(keyId)) {
            throw new AuthCheckException("id不能为空");
        }
        // 从数据库子表中获取主表id
        Long mainId = authHelper.getMainIdFromSubTable(businessModule, helperMapperClass, keyId);
        // 权限校验
        dataAuthSponsorUserRule.check(businessModule, mainId);
        dataAuthProcessRule.check(businessModule, mainId);
        return true;
    }

}
