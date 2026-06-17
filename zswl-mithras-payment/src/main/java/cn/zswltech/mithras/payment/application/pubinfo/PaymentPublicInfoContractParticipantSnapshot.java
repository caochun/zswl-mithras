package cn.zswltech.mithras.payment.application.pubinfo;

import cn.hutool.core.collection.CollUtil;
import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class PaymentPublicInfoContractParticipantSnapshot {

    private List<Long> tenantryIds;

    private List<Long> guarantorIds;

    private List<Long> mortgageIds;

    private List<Long> pledgeIds;

    public List<Long> getAllClientIds() {
        return CollUtil.unionAll(
                        safeList(tenantryIds),
                        safeList(guarantorIds),
                        safeList(mortgageIds),
                        safeList(pledgeIds))
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private static List<Long> safeList(List<Long> values) {
        return values == null ? Collections.emptyList() : values;
    }
}
