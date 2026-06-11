package cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.handler;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.FileModuleCheck;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/9/24
 * @description
 */
@Component
public class ClientTransformCheckHandler extends FileModuleCheck {
    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.CLIENT_TRANSFER.name();
    }

    @Override
    public void checkUpload(String moduleKey, Long mainId , String materialsType){

    }



    @Override
    public void checkList(String moduleKey, Long mainId){

    }

    @Override
    public void checkRemove(String moduleKey, Long fileId){

    }

    @Override
    public void checkRemove(String moduleKey, List<Long> fileIds){

    }

    @Override
    public void checkDownload(String moduleKey, Long mainId, List<Long> fileIds){

    }
}
