package cn.zswltech.mithras.service.service.bo;

import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractRentEstimate;
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
