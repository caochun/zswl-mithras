package cn.zswltech.mithras.contract.core.dto;

import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractRentEstimate;
import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2022/12/20
 * @description
 */
@Data
public class ContractEstimateCashFlowExporterBO {
    private ContractBaseInfo contractBaseInfo;
    private List<ContractRentEstimate> contractRentEstimateList;
    private String version;
}
