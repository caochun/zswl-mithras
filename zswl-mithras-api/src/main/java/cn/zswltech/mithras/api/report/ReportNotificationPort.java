package cn.zswltech.mithras.api.report;

import java.util.List;

public interface ReportNotificationPort {

    void sendCreditReportDataChange(List<Long> receivers, String processInstanceId);
}
