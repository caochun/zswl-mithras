package cn.zswltech.mithras.contract.convert.contract;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.contract.tenantry.ContractTenantryModifyREQ;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;

/**
 * @author dingqi
 * @date 2022/10/19
 * @description
 */
public class ContractTenantryConvert {
    public static ContractTenantry merge(ContractTenantry oldDbData, ContractTenantryModifyREQ modifyREQ) {
        ContractTenantry newData = new ContractTenantry();
        BeanUtil.copyProperties(oldDbData, newData);
        newData.setContractId(modifyREQ.getContractId());
        newData.setLesseeType(modifyREQ.getLesseeType());
        newData.setStockRiskExposure(modifyREQ.getStockRiskExposure());
        newData.setContactId(modifyREQ.getContactId());
        newData.setIsReport(modifyREQ.getIsReport());
        newData.setLeaseItemFileType(modifyREQ.getLeaseItemFileType());
        newData.setResolutionType(modifyREQ.getResolutionType());
        newData.setRentConcatAccountId(modifyREQ.getRentConcatAccountId());
        newData.setRentConcatAccountName(modifyREQ.getRentConcatAccountName());
        return newData;
    }

    public static ContractTenantry toUnfinishedContractTenantry(ClientInfo clientInfo) {
        ContractTenantry contractTenantry = new ContractTenantry();
        contractTenantry.setLesseeId(clientInfo.getClientId());
        contractTenantry.setLesseeName(clientInfo.getClientName());
        return contractTenantry;
    }
}
