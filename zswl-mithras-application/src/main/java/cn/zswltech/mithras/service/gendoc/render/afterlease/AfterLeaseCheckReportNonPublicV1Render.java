package cn.zswltech.mithras.service.gendoc.render.afterlease;

import cn.zswltech.mithras.afterlease.domain.enums.AfterLeaseCheckReportTemplateVersionEnum;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckPlanClient;
import com.deepoove.poi.XWPFTemplate;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.util.Map;

/**
 * @author dingqi
 * @date 2022/11/20
 * @description
 */
@Component
public class AfterLeaseCheckReportNonPublicV1Render extends AbstractAfterLeaseCheckReportNonPublicRender {
    @Override
    protected AfterLeaseCheckReportTemplateVersionEnum getTemplateVersion() {
        return AfterLeaseCheckReportTemplateVersionEnum.NON_PUBLIC_V1;
    }

    @Override
    public String render(OutputStream outputStream, NewAfterLeaseCheckPlanClient newAfterLeaseCheckPlanClient) throws Exception {
        AfterLeaseCheckReportBO afterLeaseCheckReportBO = this.getAfterLeaseCheckReportBO(newAfterLeaseCheckPlanClient);
        Map<String, Object> renderMap = this.getCommonRenderMap(newAfterLeaseCheckPlanClient, afterLeaseCheckReportBO);
        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(fileTemplateService.getTemplate(this.getTemplateVersion().getTemplateType(), this.getTemplateVersion().getTemplateFileName())).render(renderMap);
        template.writeAndClose(outputStream);
        return this.getRenderFileName();
    }
}
