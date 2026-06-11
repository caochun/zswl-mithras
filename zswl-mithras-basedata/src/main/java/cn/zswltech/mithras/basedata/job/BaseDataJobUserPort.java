package cn.zswltech.mithras.basedata.job;

import java.util.List;
import java.util.Set;

public interface BaseDataJobUserPort {

    List<Long> jobUsers(Set<String> jobCodes);

    List<Long> queryJobUserIds(String jobCode);
}
