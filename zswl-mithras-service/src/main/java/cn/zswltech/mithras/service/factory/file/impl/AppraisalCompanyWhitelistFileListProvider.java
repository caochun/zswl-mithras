package cn.zswltech.mithras.service.factory.file.impl;

import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.factory.file.AbstractFileListProvider;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2025/9/5
 * @description
 */
@Component
public class AppraisalCompanyWhitelistFileListProvider extends AbstractFileListProvider {
    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.APPRAISAL_COMPANY_WHITELIST;
    }
}
