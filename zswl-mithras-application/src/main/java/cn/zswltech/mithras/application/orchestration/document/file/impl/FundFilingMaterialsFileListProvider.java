package cn.zswltech.mithras.application.orchestration.document.file.impl;

import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.fund.enums.financing.FundDirectFinancingMaterialsEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingMaterialsEnum;
import cn.zswltech.mithras.application.orchestration.document.file.AbstractFileListProvider;
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
