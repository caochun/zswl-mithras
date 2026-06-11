package cn.zswltech.mithras.contract.application.file;

import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import org.junit.jupiter.api.function.ThrowingConsumer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SharedResources {
    public static Map<String,Map<String, ThrowingConsumer<ContractBaseInfo>>> sharedMap = new ConcurrentHashMap<>();
}
