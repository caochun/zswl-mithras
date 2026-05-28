package cn.zswltech.mithras.service.factory.file.impl;

import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.groupcreditestablish.GroupCreditEstablishMaterialsEnum;
import cn.zswltech.mithras.service.factory.file.AbstractFileListProvider;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 集团授信立项
 *
 * @author wangchuanhao
 * @date 2023/2/6 2:02 PM
 */
@Component
public class GroupCreditEstablishFileListProvider extends AbstractFileListProvider {

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.GROUP_CREDIT_ESTABLISH;
    }

    @Override
    protected int getGroupFileSort(FileListRSP rsp) {
        return Optional.ofNullable(GroupCreditEstablishMaterialsEnum.getByName(rsp.getMaterialsType())).map(GroupCreditEstablishMaterialsEnum::getSort).orElse(Integer.MAX_VALUE);
    }

}
