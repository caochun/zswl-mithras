package cn.zswltech.mithras.service.service.materialsfile.filecheck.handler;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.service.materialsfile.filecheck.FileModuleCheck;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/3/22 14:14
 */
@Component
public class AnnouncementCheckHandler extends FileModuleCheck {
    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.ANNOUNCEMENT.name();
    }

    @Override
    public void checkUpload(String moduleKey, Long mainId, String materialsType) {

    }



    @Override
    public void checkRemove(String moduleKey, Long fileId) {

    }

    @Override
    public void checkRemove(String moduleKey, List<Long> fileIds) {

    }
}
