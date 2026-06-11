package cn.zswltech.mithras.application.orchestration.document.file.impl;

import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.application.orchestration.document.file.AbstractFileListProvider;
import org.springframework.stereotype.Component;

/**
 * @author bigbear
 */
@Component
public class CreditReportListProvider extends AbstractFileListProvider {
    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.CREDIT_REPORT;
    }
}
