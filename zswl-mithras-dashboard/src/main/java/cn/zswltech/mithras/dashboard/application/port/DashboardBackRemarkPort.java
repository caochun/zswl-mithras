package cn.zswltech.mithras.dashboard.application.port;

import java.util.Collection;
import java.util.Map;

public interface DashboardBackRemarkPort {

    Map<String, String> listBackRemarkByProcessInstanceIdsAndHandlerIds(Collection<String> processInstanceIds, Collection<String> handlerIds);
}
