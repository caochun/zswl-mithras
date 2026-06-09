package cn.zswltech.mithras.service.factory.file.impl;

import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.factory.file.AbstractFileListProvider;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2025/3/14
 * @description
 */
@Component
public class NewFtpGuidanceFileListProvider extends AbstractFileListProvider {
    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.NEW_FTP_GUIDANCE;
    }
}
