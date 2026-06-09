package cn.zswltech.mithras.policy.application;

import java.util.Collection;
import java.util.Map;

public interface PolicyOperatorNamePort {

    Map<Long, String> sysUserId2Name(Collection<Long> userIds);
}
