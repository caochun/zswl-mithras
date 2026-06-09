package cn.zswltech.mithras.service.factory.file.impl;

import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.fund.domain.enums.receiptrepay.BatchFundReceiptRepayMaterialsEnum;
import cn.zswltech.mithras.service.factory.file.AbstractFileListProvider;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 资金收付款
 *
 * @author wangchuanhao
 * @date 2023/2/6 2:02 PM
 */
@Component
public class BatchFundReceiptRepayFileListProvider extends AbstractFileListProvider {

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.BATCH_FUND_RECEIPT_REPAY;
    }

    @Override
    protected int getGroupFileSort(FileListRSP rsp) {
        return Optional.ofNullable(BatchFundReceiptRepayMaterialsEnum.getByName(rsp.getMaterialsType())).map(BatchFundReceiptRepayMaterialsEnum::getSort).orElse(Integer.MAX_VALUE);
    }

}
