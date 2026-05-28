package cn.zswltech.mithras.service.auth.checker.client;

import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.service.auth.rule.DataAuthProcessRule;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.service.client.ClientService;
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
public class ClientAddSubAuthCheckerNew implements IDataAuthChecker {


    @Resource
    private DataAuthProcessRule dataAuthProcessRule;
    @Resource
    private ClientService clientService;

    @Override
    public boolean check(BusinessModuleEnum businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        if (Objects.isNull(keyId)) {
            throw new AuthCheckException("主表id不能为空");
        }
        clientService.checkClientOccupy(keyId);
        dataAuthProcessRule.check(businessModule, keyId);
        return true;
    }

}
