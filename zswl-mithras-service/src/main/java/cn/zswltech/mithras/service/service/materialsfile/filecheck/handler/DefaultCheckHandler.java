package cn.zswltech.mithras.service.service.materialsfile.filecheck.handler;


import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.service.materialsfile.filecheck.FileModuleCheck;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 默认校验
 * @author: jackerhe
 * @date: 2023/2/8 4:45 下午
 **/
@Component
public class DefaultCheckHandler extends FileModuleCheck {


    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.DEFAULT.name();
    }

    @Override
    public void checkUpload(String moduleKey, Long mainId, String materialsType) {
        super.checkUpload(moduleKey, mainId, materialsType);
    }



    @Override
    public void checkRemove(String moduleKey, Long fileId) {
        super.checkRemove(moduleKey, fileId);
    }

    @Override
    public void checkList(String moduleKey, Long fileId) {
        super.checkList(moduleKey, fileId);
    }

    @Override
    public void checkRemove(String moduleKey, List<Long> fileIds) {
        super.checkRemove(moduleKey, fileIds);
    }

    @Override
    public void checkDownload(String moduleKey, Long mainId, List<Long> fileId) {
        super.checkDownload(moduleKey, mainId, fileId);
    }
}
