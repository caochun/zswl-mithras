package cn.zswltech.mithras.contract.application.file;

import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SharedResources {
    public static Map<String, Map<String, ContractGenerateAction<ContractBaseInfo>>> sharedMap = new ConcurrentHashMap<>();
}
