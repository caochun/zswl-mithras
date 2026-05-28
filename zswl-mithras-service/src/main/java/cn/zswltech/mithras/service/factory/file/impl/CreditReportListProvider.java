package cn.zswltech.mithras.service.factory.file.impl;

import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.factory.file.AbstractFileListProvider;
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
