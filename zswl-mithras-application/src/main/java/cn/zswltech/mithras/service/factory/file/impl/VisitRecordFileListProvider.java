package cn.zswltech.mithras.service.factory.file.impl;

import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.factory.file.AbstractFileListProvider;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2025/9/8
 * @description
 */
@Component
public class VisitRecordFileListProvider extends AbstractFileListProvider {
    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.VISIT_RECORD;
    }
}
