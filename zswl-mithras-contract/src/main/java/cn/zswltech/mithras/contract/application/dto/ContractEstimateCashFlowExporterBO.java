package cn.zswltech.mithras.contract.application.dto;

import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentEstimate;
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
