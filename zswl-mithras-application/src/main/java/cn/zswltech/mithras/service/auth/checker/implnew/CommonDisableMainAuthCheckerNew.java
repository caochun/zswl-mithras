package cn.zswltech.mithras.service.auth.checker.implnew;


import cn.zswltech.mithras.service.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.service.auth.rule.DataAuthSponsorUserBatchRule;
import cn.zswltech.mithras.service.auth.rule.DataAuthSponsorUserRule;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.others.AuthCheckException;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/7/28
 * @description 主表 关闭数据校验
 * 仅校验主办逻辑 不校验流程逻辑 在流程中关闭数据后自动取消流程
 */
@Component
public class CommonDisableMainAuthCheckerNew implements IDataAuthChecker {

    @Resource
    private DataAuthSponsorUserRule dataAuthSponsorUserRule;
    @Resource
    private DataAuthSponsorUserBatchRule dataAuthSponsorUserBatchRule;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        if (Objects.isNull(keyId)) {
            throw new AuthCheckException("id不能为空");
        }
        dataAuthSponsorUserRule.check(businessModule, keyId);
        return true;
    }

    @Override
    public boolean checkBatch(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Collection<Long> keyIds, Object[] args) {
        if (CollectionUtils.isEmpty(keyIds)) {
            throw new AuthCheckException("id列表不能为空");
        }
        dataAuthSponsorUserBatchRule.check(businessModule, keyIds);
        return true;
    }
}
