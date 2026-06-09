package cn.zswltech.mithras.service.service.materialsfile.filecheck.handler;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.service.materialsfile.filecheck.FileModuleCheck;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description:
 * @author: ZZKen
 * @date: 2024/11/26 20:39
 */
@Component
public class LitigationRegistrationCheckHandler extends FileModuleCheck {
    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.LITIGATION_REGISTRATION.name();
    }

    @Override
    public void checkUpload(String moduleKey, Long mainId , String materialsType){
        super.checkUpload(moduleKey, mainId, materialsType);
    }


    @Override
    public void checkList(String moduleKey, Long mainId){

    }

    @Override
    public void checkRemove(String moduleKey, Long fileId){
        super.checkRemove(moduleKey, fileId);
    }

    @Override
    public void checkRemove(String moduleKey, List<Long> fileIds){
        super.checkRemove(moduleKey, fileIds);
    }

    @Override
    public void checkDownload(String moduleKey, Long mainId, List<Long> fileIds){

    }
}
