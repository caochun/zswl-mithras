package cn.zswltech.mithras.service.factory.file.impl;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.dto.file.FileListREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.factory.file.AbstractFileListProvider;
import cn.zswltech.mithras.service.others.MithrasException;
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
