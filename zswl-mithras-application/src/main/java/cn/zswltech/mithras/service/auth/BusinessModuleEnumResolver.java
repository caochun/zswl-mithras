package cn.zswltech.mithras.service.auth;

import cn.zswltech.mithras.service.auth.DataAuthBusinessModule;

import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import org.springframework.stereotype.Component;

@Component
public class BusinessModuleEnumResolver implements DataAuthBusinessModuleResolver {

    @Override
    public DataAuthBusinessModule resolve(String moduleName) {
        return BusinessModuleEnum.of(moduleName);
    }
}
