package cn.zswltech.mithras.customer.application.client.auth;


import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.foundation.auth.checker.AuthHelper;
import cn.zswltech.mithras.foundation.auth.checker.DefaultMainIdInspector;
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
 * @date 2022/7/28
 * @description 子表修改权限校验
 */
@Component
public class ClientModifySubAuthCheckerNew implements IDataAuthChecker {

    @Resource
    private DataAuthProcessGuard dataAuthProcessRule;
    @Resource
    private AuthHelper authHelper;
    @Resource
    private DefaultMainIdInspector defaultMainIdInspector;
    @Resource
    private ClientOccupyGuard clientService;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        if (Objects.isNull(keyId)) {
            throw new AuthCheckException("id不能为空");
        }
        // 从数据库子表中获取主表id
        Long mainId = authHelper.getMainIdFromSubTable(businessModule, helperMapperClass, keyId);
        if (args.length > 0) {
            // 此处做一个额外的校验 禁止更新子表中的主表id 兼容之前各处都没处理参数中主表id的问题
            defaultMainIdInspector.inspect(businessModule, args[0], mainId);
        }
        // 权限校验
        //dataAuthSponsorUserRule.check(businessModule, mainId);
        clientService.checkClientOccupy(mainId);
        dataAuthProcessRule.check(businessModule, mainId);
        return true;
    }

}
