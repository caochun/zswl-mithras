package cn.zswltech.mithras.application.orchestration.adapter.afterlease;

import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckReportRenderPort;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.afterlease.AfterLeaseCheckReportRenderFactory;
import org.springframework.stereotype.Component;

import java.io.OutputStream;

@Component
public class AfterLeaseCheckReportRenderPortAdapter implements AfterLeaseCheckReportRenderPort {
    @Override
    public void render(OutputStream outputStream, NewAfterLeaseCheckPlanClient checkPlanClient, String reportType, String templateVersion) throws Exception {
        AfterLeaseCheckReportRenderFactory.getInstance(reportType, templateVersion).render(outputStream, checkPlanClient);
    }
}
