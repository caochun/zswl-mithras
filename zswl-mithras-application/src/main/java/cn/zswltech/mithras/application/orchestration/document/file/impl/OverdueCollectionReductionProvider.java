package cn.zswltech.mithras.application.orchestration.document.file.impl;

import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.application.orchestration.document.file.AbstractFileListProvider;
import org.springframework.stereotype.Component;


@Component
public class OverdueCollectionReductionProvider extends AbstractFileListProvider {

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.OVERDUE_COLLECTION_REDUCTION;
    }

}
