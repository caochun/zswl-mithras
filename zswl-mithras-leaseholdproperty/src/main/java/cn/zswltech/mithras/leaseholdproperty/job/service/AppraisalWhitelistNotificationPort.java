package cn.zswltech.mithras.leaseholdproperty.job.service;

import java.util.List;

public interface AppraisalWhitelistNotificationPort {

    void sendExpireRemind(Long whitelistId, List<Long> toIds, String relation);
}
