package cn.zswltech.mithras.service.factory.file.impl;

import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingMaterialsEnum;
import cn.zswltech.mithras.service.factory.file.AbstractFileListProvider;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 融资管理
 *
 * @author wangchuanhao
 * @date 2023/2/6 2:18 PM
 */
@Component
public class FundDirectFinancingFileListProvider extends AbstractFileListProvider {

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.FUND_DIRECT_FINANCING;
    }

    @Override
    protected int getGroupFileSort(FileListRSP rsp) {
        return Optional.ofNullable(FundFinancingMaterialsEnum.getByName(rsp.getMaterialsType())).map(FundFinancingMaterialsEnum::getSort).orElse(Integer.MAX_VALUE);
    }

}
