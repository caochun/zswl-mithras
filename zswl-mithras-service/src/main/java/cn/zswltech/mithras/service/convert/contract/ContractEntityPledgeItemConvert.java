package cn.zswltech.mithras.service.convert.contract;

import cn.zswltech.mithras.common.constant.GlobalConstants;
import cn.zswltech.mithras.service.excel.model.ContractEntityPledgeItemExcelModel;
import cn.zswltech.mithras.service.mapper.model.contract.ContractPledgeItem;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/8/16
 * @description
 */
public class ContractEntityPledgeItemConvert {
    public static ContractPledgeItem toContractPledgeItem(ContractEntityPledgeItemExcelModel excelModel) {
        ContractPledgeItem contractPledgeItem = new ContractPledgeItem();
        contractPledgeItem.setSequence(excelModel.getSequence());
        contractPledgeItem.setCategory(excelModel.getCategory());
        if (Objects.nonNull(excelModel.getAssessedValue())) {
            contractPledgeItem.setAssessedValue(excelModel.getAssessedValue().multiply(new BigDecimal(Integer.parseInt(GlobalConstants.MONEY_MULTIPLE))).longValue());
        }
        return contractPledgeItem;
    }
}
