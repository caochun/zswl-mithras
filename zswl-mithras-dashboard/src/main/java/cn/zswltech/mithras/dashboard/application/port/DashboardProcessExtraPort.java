package cn.zswltech.mithras.dashboard.application.port;

import java.util.Collection;
import java.util.Map;

public interface DashboardProcessExtraPort {

    Map<String, DashboardProcessExtraSnapshot> listByProcessInstanceIds(Collection<String> processInstanceIds);
}
