package cn.zswltech.mithras.application.orchestration.document.file.impl;

import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.application.orchestration.document.file.AbstractFileListProvider;
import org.springframework.stereotype.Component;

/**
 * 保单缓存模块
 */
@Component
public class PolicyTmpListProvider extends AbstractFileListProvider {

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.POLICY_TMP;
    }

}
