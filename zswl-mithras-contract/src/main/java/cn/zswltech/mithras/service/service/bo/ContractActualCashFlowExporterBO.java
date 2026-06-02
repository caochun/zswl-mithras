package cn.zswltech.mithras.service.service.bo;

import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.service.mapper.model.contract.ContractReceiptLib;
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
