package cn.zswltech.mithras.service.factory.file.impl;

import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.projectprocess.enums.projestablish.ProjEstablishMaterialsEnum;
import cn.zswltech.mithras.service.factory.file.AbstractFileListProvider;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 立项模块
 *
 * @author wangchuanhao
 * @date 2023/2/6 11:26 AM
 */
@Component
public class ProjEstablishFileListProvider extends AbstractFileListProvider {

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.PROJ_ESTABLISH;
    }

    @Override
    protected int getGroupFileSort(FileListRSP rsp) {
        return Optional.ofNullable(ProjEstablishMaterialsEnum.getByName(rsp.getMaterialsType())).map(ProjEstablishMaterialsEnum::getSort).orElse(Integer.MAX_VALUE);
    }

}
