package cn.zswltech.mithras.application.orchestration.document.file.impl;

import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.contract.enums.overdue.CollectionActionFileType;
import cn.zswltech.mithras.application.orchestration.document.file.AbstractFileListProvider;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/11/6 14:43
 */
@Component
public class CollectionActionFileListProvider extends AbstractFileListProvider {
    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.OVERDUE_COLLECTION_ACTION;
    }

    @Override
    protected int getGroupFileSort(FileListRSP rsp) {
        return Optional.of(CollectionActionFileType.valueOf(rsp.getMaterialsType())).map(CollectionActionFileType::getSort).orElse(Integer.MAX_VALUE);
    }
}
