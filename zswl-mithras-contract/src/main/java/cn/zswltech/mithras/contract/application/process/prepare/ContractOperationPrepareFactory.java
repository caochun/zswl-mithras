package cn.zswltech.mithras.contract.application.process.prepare;

import cn.zswltech.mithras.contract.core.operationprepare.ContractOperationPrepare;
import cn.zswltech.mithras.contract.enums.contract.ContractOperationEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dingqi
 * @date 2023/4/3
 * @description
 */
public class ContractOperationPrepareFactory {
    private static final Map<String, ContractOperationPrepare> map = new HashMap<>();

    public static void register(ContractOperationPrepare contractOperationPrepare) {
        map.put(contractOperationPrepare.operationScene().name(), contractOperationPrepare);
    }

    public static ContractOperationPrepare getInstance(ContractOperationEnum contractOperationEnum) {
        return map.get(contractOperationEnum.name());
    }
}
