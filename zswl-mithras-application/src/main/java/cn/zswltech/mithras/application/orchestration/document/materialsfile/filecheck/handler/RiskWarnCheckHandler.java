package cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.handler;

import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.FileModuleCheck;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @create: 2023-03-09
 **/

@Component
public class RiskWarnCheckHandler extends FileModuleCheck {


    @Override
    public void checkUpload(String moduleKey, Long mainId, String materialsType) {
    }

    @Override
    public void checkDownload(String moduleKey, Long mainId, List<Long> fileIds) {
    }

    @Override
    public void checkRemove(String moduleKey, List<Long> fileIds) {
    }

    @Override
    public void checkList(String moduleKey, Long fileId) {
    }

    @Override
    public void checkRemove(String moduleKey, Long fileId) {
    }

    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.RISK_WARN.name();
    }
}
