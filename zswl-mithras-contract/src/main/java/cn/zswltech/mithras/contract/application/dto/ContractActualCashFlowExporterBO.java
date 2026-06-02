package cn.zswltech.mithras.contract.application.dto;

import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceiptLib;
import lombok.Data;

/**
 * @author dingqi
 * @date 2022/10/21
 * @description
 */
@Data
public class ContractActualCashFlowExporterBO {
    private ContractBaseInfo contractBaseInfo;
    private ContractBaseInfoLib contractBaseInfoLib;
    private ContractReceipt contractReceipt;
    private ContractReceiptLib contractReceiptLib;
    private boolean history;
    private String version;
}
