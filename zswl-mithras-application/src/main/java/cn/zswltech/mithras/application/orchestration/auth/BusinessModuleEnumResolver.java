package cn.zswltech.mithras.application.orchestration.auth;

import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModuleResolver;
import org.springframework.stereotype.Component;

@Component
public class BusinessModuleEnumResolver implements DataAuthBusinessModuleResolver {

    @Override
    public DataAuthBusinessModule resolve(String moduleName) {
        return BusinessModuleEnum.of(moduleName);
    }
}
