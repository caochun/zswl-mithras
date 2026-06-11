package cn.zswltech.mithras.foundation.port;

import java.util.Collection;
import java.util.Map;

public interface ContractNameResolver {

    Map<Long, String> contractId2Name(Collection<Long> ids);

    Map<Long, String> receiptId2Name(Collection<Long> ids);
}
