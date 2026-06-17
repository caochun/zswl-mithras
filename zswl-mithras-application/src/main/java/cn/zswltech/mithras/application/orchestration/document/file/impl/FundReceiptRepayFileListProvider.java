package cn.zswltech.mithras.application.orchestration.document.file.impl;

import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.fund.enums.receiptrepay.FundReceiptRepayMaterialsEnum;
import cn.zswltech.mithras.application.orchestration.document.file.AbstractFileListProvider;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 资金收付款
 *
 * @author wangchuanhao
 * @date 2023/2/6 2:02 PM
 */
@Component
public class FundReceiptRepayFileListProvider extends AbstractFileListProvider {

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.FUND_RECEIPT_REPAY;
    }

    @Override
    protected int getGroupFileSort(FileListRSP rsp) {
        return Optional.ofNullable(FundReceiptRepayMaterialsEnum.getByName(rsp.getMaterialsType())).map(FundReceiptRepayMaterialsEnum::getSort).orElse(Integer.MAX_VALUE);
    }

}
