package cn.zswltech.mithras.fund.application;

import java.util.Collection;
import java.util.Map;

public interface FundOrganizationNamePort {

    Map<Long, String> sysUserId2Name(Collection<Long> userIds);
}
