package cn.zswltech.mithras.leaseholdproperty.application.port;

import java.util.List;

public interface AppraisalWhitelistNotificationPort {

    void sendExpireRemind(Long whitelistId, List<Long> toIds, String relation);
}
