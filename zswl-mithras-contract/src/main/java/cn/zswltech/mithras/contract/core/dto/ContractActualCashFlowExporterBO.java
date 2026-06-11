package cn.zswltech.mithras.contract.core.dto;

import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.contract.model.contract.ContractReceipt;
import cn.zswltech.mithras.contract.model.contract.ContractReceiptLib;
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
