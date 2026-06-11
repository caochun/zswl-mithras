package cn.zswltech.mithras.application.orchestration.document.file.impl;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.dto.file.FileListREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.application.orchestration.document.file.AbstractFileListProvider;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/9/23
 * @description
 */
@Component
public class ClientTransformFileListProvider extends AbstractFileListProvider {
    @Override
    public List<Pair<String, List<FileListRSP>>> listGroup(FileListREQ req) {
        throw new MithrasException("暂不支持的功能");
    }

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.CLIENT_TRANSFER;
    }
}
