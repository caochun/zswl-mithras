package cn.zswltech.mithras.afterlease.application;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface AfterLeaseCorpCommercePort {
    Optional<Map<Long, String>> selectIndustryTypeBatchByIds(Collection<Long> clientIds);
}
