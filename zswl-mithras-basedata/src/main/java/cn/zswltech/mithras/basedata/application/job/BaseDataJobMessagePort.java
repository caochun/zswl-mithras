package cn.zswltech.mithras.basedata.application.job;

import java.util.List;

public interface BaseDataJobMessagePort {

    void sendLprRemind(List<Long> userIds, String flowId, int month);
}
