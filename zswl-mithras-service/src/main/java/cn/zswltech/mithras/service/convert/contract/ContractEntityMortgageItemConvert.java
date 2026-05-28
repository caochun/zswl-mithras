package cn.zswltech.mithras.service.convert.contract;

import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.excel.model.ContractEntityMortgageItemExcelModel;
import cn.zswltech.mithras.service.mapper.model.contract.ContractMortgageItem;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/8/16
 * @description
 */
public class ContractEntityMortgageItemConvert {
    public static ContractMortgageItem toContractMortgageItem(ContractEntityMortgageItemExcelModel excelModel) {
        ContractMortgageItem contractMortgageItem = new ContractMortgageItem();
        contractMortgageItem.setSequence(excelModel.getSequence());
        contractMortgageItem.setCategory(excelModel.getCategory());
        contractMortgageItem.setUniqueIdentifyCodeType(excelModel.getUniqueIdentifyCodeType());
        contractMortgageItem.setUniqueIdentifyCode(excelModel.getUniqueIdentifyCode());
        contractMortgageItem.setName(excelModel.getName());
        contractMortgageItem.setSupplier(excelModel.getSupplier());
        if (Objects.nonNull(excelModel.getOriginalBookValue())) {
            contractMortgageItem.setOriginalBookValue(excelModel.getOriginalBookValue().multiply(new BigDecimal(Integer.parseInt(GlobalConstants.MONEY_MULTIPLE))).longValue());
        }
        if (Objects.nonNull(excelModel.getOriginalBookNetValue())) {
            contractMortgageItem.setOriginalBookNetValue(excelModel.getOriginalBookNetValue().multiply(new BigDecimal(Integer.parseInt(GlobalConstants.MONEY_MULTIPLE))).longValue());
        }
        if (Objects.nonNull(excelModel.getAssessedValue())) {
            contractMortgageItem.setAssessedValue(excelModel.getAssessedValue().multiply(new BigDecimal(Integer.parseInt(GlobalConstants.MONEY_MULTIPLE))).longValue());
        }
        if (Objects.nonNull(excelModel.getAssessedNetValue())) {
            contractMortgageItem.setAssessedNetValue(excelModel.getAssessedNetValue().multiply(new BigDecimal(Integer.parseInt(GlobalConstants.MONEY_MULTIPLE))).longValue());
        }
        contractMortgageItem.setQuantity(excelModel.getQuantity());
        contractMortgageItem.setUnit(excelModel.getUnit());
        contractMortgageItem.setPurchaseDate(excelModel.getPurchaseDate());
        contractMortgageItem.setInvoiceCode(excelModel.getInvoiceCode());
        contractMortgageItem.setStoragePlace(excelModel.getStoragePlace());
        return contractMortgageItem;
    }
}
