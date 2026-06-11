package cn.zswltech.mithras.foundation.version;

import cn.zswltech.mithras.foundation.persistence.dto.ChangeDTO;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;

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
