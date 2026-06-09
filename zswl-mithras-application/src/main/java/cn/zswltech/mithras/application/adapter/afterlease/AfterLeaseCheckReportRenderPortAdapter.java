package cn.zswltech.mithras.application.adapter.afterlease;

import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckReportRenderPort;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.service.gendoc.render.afterlease.AfterLeaseCheckReportRenderFactory;
import org.springframework.stereotype.Component;

import java.io.OutputStream;

@Component
public class AfterLeaseCheckReportRenderPortAdapter implements AfterLeaseCheckReportRenderPort {
    @Override
    public void render(OutputStream outputStream, NewAfterLeaseCheckPlanClient checkPlanClient, String reportType, String templateVersion) throws Exception {
        AfterLeaseCheckReportRenderFactory.getInstance(reportType, templateVersion).render(outputStream, checkPlanClient);
    }
}
