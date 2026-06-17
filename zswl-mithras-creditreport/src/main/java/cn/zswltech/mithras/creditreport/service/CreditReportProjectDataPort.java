package cn.zswltech.mithras.creditreport.service;

import java.util.List;

public interface CreditReportProjectDataPort {

    CreditReportProjectSnapshot getProjectSnapshot(String bizType, Long projectId);

    List<Long> listAvailableProjReviewIds(String projIdDataType, Long projId);

    List<Long> listClientIds(String bizType, Long projectId);

    List<CreditReportProjectSnapshot> listProjectSnapshotsByClientId(Long clientId);
}
