package cn.zswltech.mithras.application.orchestration.document.file.impl;

import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.application.orchestration.document.file.AbstractFileListProvider;
import org.springframework.stereotype.Component;

/**
 * 租赁物清单-评估机构
 */
@Component
public class LeaseAppraisalDataListFileProvider extends AbstractFileListProvider {

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.LEASE_APPRAISAL_DATA_LIST;
    }

}
