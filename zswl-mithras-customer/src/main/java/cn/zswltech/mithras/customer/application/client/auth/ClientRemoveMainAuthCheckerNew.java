package cn.zswltech.mithras.customer.application.client.auth;


import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.foundation.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.foundation.auth.DataAuthProcessGuard;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.customer.application.client.ClientOccupyGuard;
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
public class ClientRemoveMainAuthCheckerNew implements IDataAuthChecker {

    @Resource
    private DataAuthProcessGuard dataAuthProcessRule;
    @Resource
    private ClientOccupyGuard clientService;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        if (Objects.isNull(keyId)) {
            throw new AuthCheckException("id不能为空");
        }
        clientService.checkClientOccupy(keyId);
        dataAuthProcessRule.check(businessModule, keyId);
        return true;
    }

}
