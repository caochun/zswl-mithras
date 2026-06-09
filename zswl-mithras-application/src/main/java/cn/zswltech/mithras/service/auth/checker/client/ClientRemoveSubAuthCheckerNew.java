package cn.zswltech.mithras.service.auth.checker.client;


import cn.zswltech.mithras.service.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.service.auth.checker.AuthHelper;
import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.service.auth.rule.DataAuthProcessRule;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.service.client.ClientService;
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
public class ClientRemoveSubAuthCheckerNew implements IDataAuthChecker {

    @Resource
    private DataAuthProcessRule dataAuthProcessRule;
    @Resource
    private AuthHelper authHelper;
    @Resource
    private ClientService clientService;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        if (Objects.isNull(keyId)) {
            throw new AuthCheckException("id不能为空");
        }
        // 从数据库子表中获取主表id
        Long mainId = authHelper.getMainIdFromSubTable(businessModule, helperMapperClass, keyId);
        // 权限校验
        clientService.checkClientOccupy(mainId);
        dataAuthProcessRule.check(businessModule, mainId);
        return true;
    }

}
