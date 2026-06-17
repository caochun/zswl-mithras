package cn.zswltech.mithras.rating.application.port;

import cn.zswltech.mithras.rating.application.port.model.RatingAmountClientSnapshot;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface RatingAmountClientFactPort {

    Map<Long, RatingAmountClientSnapshot> clientSnapshotMap(Collection<Long> clientIds);

    RatingAmountClientSnapshot clientSnapshot(Long clientId);

    Long latestOperatingIncome(Long clientId);

    Long groupClientId(Long clientId);

    List<Long> groupMemberClientIds(Long clientId);

    List<Long> unsettledClientIds(List<Long> clientIds);
}
