package cn.zswltech.mithras.service.factory.file.impl;

import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.groupcreditreview.GroupCreditReviewMaterialsEnum;
import cn.zswltech.mithras.service.factory.file.AbstractFileListProvider;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 集团授信评审
 *
 * @author wangchuanhao
 * @date 2023/2/6 2:02 PM
 */
@Component
public class GroupCreditReviewFileListProvider extends AbstractFileListProvider {

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.GROUP_CREDIT_REVIEW;
    }

    @Override
    protected int getGroupFileSort(FileListRSP rsp) {
        return Optional.ofNullable(GroupCreditReviewMaterialsEnum.getByName(rsp.getMaterialsType())).map(GroupCreditReviewMaterialsEnum::getSort).orElse(Integer.MAX_VALUE);
    }

}
