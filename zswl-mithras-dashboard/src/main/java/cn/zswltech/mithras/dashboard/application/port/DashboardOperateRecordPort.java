package cn.zswltech.mithras.dashboard.application.port;

import java.util.Collection;
import java.util.List;

public interface DashboardOperateRecordPort {

    List<DashboardOperateRecordSnapshot> listOperateRecords(Collection<String> processInstanceIds);

    List<DashboardNodeBackRecordSnapshot> listNodeBackRecords(Collection<String> processInstanceIds);
}
