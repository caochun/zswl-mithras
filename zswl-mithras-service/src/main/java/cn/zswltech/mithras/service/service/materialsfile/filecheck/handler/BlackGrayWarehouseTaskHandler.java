package cn.zswltech.mithras.service.service.materialsfile.filecheck.handler;

import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.service.materialsfile.filecheck.FileModuleCheck;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @create: 2023-03-09
 **/

@Component
public class BlackGrayWarehouseTaskHandler extends FileModuleCheck {

    @Override
    public void checkUpload(String moduleKey, Long mainId , String materialsType) {
    }

    @Override
    public void checkDownload(String moduleKey, Long mainId, List<Long> fileIds) {
    }

    @Override
    public void checkRemove(String moduleKey, List<Long> fileIds) {
    }

        @Override
    public void checkList(String moduleKey, Long mainId) {
    }

    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.BLACK_GRAY_WAREHOUSE_TASK.name();
    }
}
