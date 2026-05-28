package cn.zswltech.mithras.service.factory.file.impl;

import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FundDirectFinancingMaterialsEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingMaterialsEnum;
import cn.zswltech.mithras.service.factory.file.AbstractFileListProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 资金-归档
 *
 * @author wangchuanhao
 * @date 2023/2/6 2:18 PM
 */
@Component
public class FundFilingMaterialsFileListProvider extends AbstractFileListProvider {

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.FUND_FILING;
    }

    @Override
    protected int getGroupFileSort(FileListRSP rsp) {
        FundFinancingMaterialsEnum fundFinancingMaterialsEnum = FundFinancingMaterialsEnum.getByName(rsp.getMaterialsType());
        FundDirectFinancingMaterialsEnum fundDirectFinancingMaterialsEnum = FundDirectFinancingMaterialsEnum.getByName(rsp.getMaterialsType());
        if(Objects.isNull(fundFinancingMaterialsEnum) && Objects.isNull(fundDirectFinancingMaterialsEnum)){
            return Integer.MAX_VALUE;
        }
        if(Objects.nonNull(fundFinancingMaterialsEnum)){
            return fundFinancingMaterialsEnum.getSort();
        }
        return fundDirectFinancingMaterialsEnum.getSort();
    }

}
