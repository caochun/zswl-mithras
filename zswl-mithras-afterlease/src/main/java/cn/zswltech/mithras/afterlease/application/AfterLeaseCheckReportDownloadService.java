package cn.zswltech.mithras.afterlease.application;

import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckPlanClient;

import java.io.OutputStream;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/11/20
 * @description
 */
public interface AfterLeaseCheckReportDownloadService {
    void downloadSingleClientReport(OutputStream outputStream, NewAfterLeaseCheckPlanClient checkPlanClient);

    void downloadBatchClientReport(OutputStream outputStream, List<Long> checkClientId);

    void downloadBizDeptClientReport(OutputStream outputStream, Long planId, Long bizDeptId);
}
