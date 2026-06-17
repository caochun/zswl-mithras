package cn.zswltech.mithras.application.orchestration.workflow.flow.file.impl;

import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.application.orchestration.workflow.flow.file.IFileHandler;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/13 17:38
 */
@Component
public class FtpMonthlyFileHandlerImpl implements IFileHandler {
    @Resource
    private MaterialsListService materialsListService;
    @Override
    public void uploadCheck(ProcessResp processResp, String materialsType, String taskId) {
        IFileHandler.super.uploadCheck(processResp, materialsType, taskId);
    }

    @Override
    public void removeCheck(ProcessResp processResp, MaterialsList materialsList, String taskId) {
        IFileHandler.super.removeCheck(processResp, materialsList, taskId);
    }

    @Override
    public List<MaterialsList> listFile(ProcessResp processResp, List<String> materialsTypeList) {
        return materialsListService.list(BusinessModuleEnum.NEW_FTP_GUIDANCE.name(), materialsTypeList, Collections.singletonList(Long.valueOf(processResp.getBusinessKey())));
    }

    @Override
    public BusinessModuleEnum businessModule() {
        return BusinessModuleEnum.NEW_FTP_GUIDANCE;
    }

    @Override
    public String convertMaterialsType(String materialsType) {
        return null;
    }
}
