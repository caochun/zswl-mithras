package cn.zswltech.mithras.dashboard.application.port;

import java.util.List;

public interface DashboardAfterLeasePreparePort {

    List<DashboardAfterLeasePrepareSnapshot> listUnsubmitted(String startUserId, String businessId);
}
