package cn.zswltech.mithras.service.service.lib;

import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.MaterialsListLib;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import cn.zswltech.mithras.service.mapper.tag.ILib;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件比对申明
 */
public interface FileCompareDeclaration {

    default ChangeDTO fileCheckActualChange(List<? extends IEntity> versionList, List<? extends IEntity> draftList) {
        ChangeDTO changeDTO = new ChangeDTO();
        changeDTO.setChangeFlag(false);
        changeDTO.setNeedApprovalChangeFlag(false);

        List<Long> versionFileIds = versionList.stream().map(IEntity::getId).collect(Collectors.toList());
        List<Long> draftFileIds = draftList.stream().map(IEntity::getId).collect(Collectors.toList());

        if (versionFileIds.size() != draftFileIds.size()) {
            changeDTO.setChangeFlag(true);
            changeDTO.setNeedApprovalChangeFlag(true);
            return changeDTO;
        }
        if (!versionFileIds.containsAll(draftFileIds) || !draftFileIds.containsAll(versionFileIds)) {
            changeDTO.setChangeFlag(true);
            changeDTO.setNeedApprovalChangeFlag(true);
            return changeDTO;
        }
        return changeDTO;

    }
}
