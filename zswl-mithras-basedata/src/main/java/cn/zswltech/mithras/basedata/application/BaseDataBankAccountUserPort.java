package cn.zswltech.mithras.basedata.application;

import java.util.Map;
import java.util.Set;

public interface BaseDataBankAccountUserPort {

    Map<Long, String> sysUserId2Name(Set<Long> userIds);
}
