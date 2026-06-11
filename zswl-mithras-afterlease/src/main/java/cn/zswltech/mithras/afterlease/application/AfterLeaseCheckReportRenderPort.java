package cn.zswltech.mithras.afterlease.application;

import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckPlanClient;

import java.io.OutputStream;

public interface AfterLeaseCheckReportRenderPort {
    void render(OutputStream outputStream, NewAfterLeaseCheckPlanClient checkPlanClient, String reportType, String templateVersion) throws Exception;
}
