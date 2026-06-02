package cn.zswltech.mithras.service.service.contract.operationprepare;

import cn.zswltech.mithras.contract.enums.contract.ContractOperationEnum;

/**
 * @author dingqi
 * @date 2023/4/3
 * @description
 */
public interface ContractOperationPrepare {
    void prepare(Long contractId);

    ContractOperationEnum operationScene();
}
