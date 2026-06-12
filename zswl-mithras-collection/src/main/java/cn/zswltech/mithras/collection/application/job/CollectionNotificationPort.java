package cn.zswltech.mithras.collection.application.job;

import java.util.List;

public interface CollectionNotificationPort {

    void sendRentDueRemind(List<Long> toIds, Long collectionId, String code, String relation);

    void sendRentRepayOverdueRemind(Long toId, String relation);
}
