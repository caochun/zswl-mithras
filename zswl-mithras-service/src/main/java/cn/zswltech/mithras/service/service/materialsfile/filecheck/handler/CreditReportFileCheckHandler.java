package cn.zswltech.mithras.service.service.materialsfile.filecheck.handler;

import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.service.materialsfile.filecheck.FileModuleCheck;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author dingqi
 * @date 2025/10/31
 * @description
 */
@Component
public class CreditReportFileCheckHandler extends FileModuleCheck {
    @Override
    public void checkUpload(String moduleKey, Long mainId, String materialsType) {
        // 不校验
    }

    @Override
    public void checkRemove(String moduleKey, Long fileId) {
        // 不校验
    }

    @Override
    public void checkRemove(String moduleKey, List<Long> fileIds) {
        // 不校验
    }

    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.CREDIT_REPORT_SELECT.name();
    }

    @Override
    public void checkList(String moduleKey, Long mainId) {
        // 不校验
    }

    @Override
    public void checkDownload(String moduleKey, Long mainId, List<Long> fileIds) {
        // 不校验
    }

    @Override
    public void checkTemplateDownload(String moduleKey, String templateId) {
        // 不校验
    }
}
