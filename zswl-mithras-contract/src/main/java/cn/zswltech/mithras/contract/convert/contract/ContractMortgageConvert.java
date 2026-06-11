package cn.zswltech.mithras.contract.convert.contract;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageModifyREQ;
import cn.zswltech.mithras.contract.model.contract.ContractMortgage;

/**
 * @author dingqi
 * @date 2022/10/19
 * @description
 */
public class ContractMortgageConvert {
    public static ContractMortgage merge(ContractMortgage oldDbData, ContractMortgageModifyREQ modifyREQ) {
        ContractMortgage newData = new ContractMortgage();
        BeanUtil.copyProperties(oldDbData, newData);
        ContractTypeConversionWorker typeConversionWorker = SpringUtil.getBean(ContractTypeConversionWorker.class);
        newData.setRelatContracts(typeConversionWorker.toJsonString(modifyREQ.getRelatContracts()));
        newData.setMortgageType(modifyREQ.getMortgageType());
        newData.setMortgageIds(typeConversionWorker.toJsonString(modifyREQ.getMortgageIds()));
        newData.setMortgageDescribe(modifyREQ.getMortgageDescribe());
        newData.setAssess(modifyREQ.getAssess());
        newData.setAssessDate(typeConversionWorker.toLocalDateForYYYYMMDD(modifyREQ.getAssessDate()));
        newData.setAppraisalCompany(modifyREQ.getAppraisalCompany());
        newData.setAppraisalCode(modifyREQ.getAppraisalCode());
        newData.setHighest(modifyREQ.getHighest());
        newData.setMortgageContractCode(modifyREQ.getMortgageContractCode());
        newData.setMortgageItemType(modifyREQ.getMortgageItemType());
        newData.setContractMortgageType(modifyREQ.getContractMortgageType());
        return newData;
    }
}
