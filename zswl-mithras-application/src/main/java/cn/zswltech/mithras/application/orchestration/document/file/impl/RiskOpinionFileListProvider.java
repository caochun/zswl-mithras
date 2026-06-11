package cn.zswltech.mithras.application.orchestration.document.file.impl;

import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.payment.enums.LendingMaterialType;
import cn.zswltech.mithras.application.orchestration.document.file.AbstractFileListProvider;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 付款模块
 *
 * @author wangchuanhao
 * @date 2023/2/6 1:37 PM
 */
@Component
public class RiskOpinionFileListProvider extends AbstractFileListProvider {

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.RISK_OPINION;
    }

    @Override
    protected int getGroupFileSort(FileListRSP rsp) {
        return Optional.ofNullable(LendingMaterialType.getByName(rsp.getMaterialsType())).map(LendingMaterialType::getSort).orElse(Integer.MAX_VALUE);
    }

}
