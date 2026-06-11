package cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.handler;

import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.FileModuleCheck;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author bigbear
 * @date 2024/12/11 15:16
 * @description
 */
@Component
public class ContractTextManageCheckHandler extends FileModuleCheck {
    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.CONTRACT_TEXT_MANAGE.name();
    }

    @Override
    public void checkUpload(String moduleKey, Long mainId, String materialsType) {

    }

//    @Override
//    public void afterUploadHandle(Long mainId, Long fileId) {
//
//    }

    @Override
    public void checkRemove(String moduleKey, Long fileId) {

    }

//    @Override
//    public void afterRemoveHandle(Long mainId) {
//
//    }

    @Override
    public void checkRemove(String moduleKey, List<Long> fileIds) {

    }

    @Override
    public void checkList(String moduleKey, Long mainId) {

    }

    @Override
    public void checkDownload(String moduleKey, Long mainId, List<Long> fileIds) {

    }

    @Override
    public void checkTemplateDownload(String moduleKey, String templateId) {

    }

    @Override
    public String getWatermarkSting() {
        return super.getWatermarkSting();
    }
}
