package cn.zswltech.mithras.service.auth.checker.implnew;


import cn.zswltech.mithras.service.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.service.auth.checker.AuthHelper;
import cn.zswltech.mithras.service.auth.checker.DefaultMainIdInspector;
import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.service.auth.rule.DataAuthSponsorUserRule;
import cn.zswltech.mithras.service.auth.rule.DataAuthProcessRule;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.others.AuthCheckException;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/7/28
 * @description 子表修改权限校验
 */
@Component
public class CommonModifySubAuthCheckerNew implements IDataAuthChecker {

    @Resource
    private DataAuthSponsorUserRule dataAuthSponsorUserRule;
    @Resource
    private DataAuthProcessRule dataAuthProcessRule;
    @Resource
    private AuthHelper authHelper;
    @Resource
    private DefaultMainIdInspector defaultMainIdInspector;

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
        dataAuthSponsorUserRule.check(businessModule, mainId);
        dataAuthProcessRule.check(businessModule, mainId);
        return true;
    }

}
