package cn.zswltech.mithras.application.orchestration.document.gendoc.render.afterlease;

import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseCheckReportTemplateVersionEnum;
import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckPlanClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.OutputStream;

/**
 * @author dingqi
 * @date 2022/11/19
 * @description
 */
@Slf4j
@Component
public class AfterLeaseCheckReportPublicV1Render extends AbstractAfterLeaseCheckReportPublicRender {
    @Override
    protected AfterLeaseCheckReportTemplateVersionEnum getTemplateVersion() {
        return AfterLeaseCheckReportTemplateVersionEnum.PUBLIC_V1;
    }

    @Override
    public String render(OutputStream outputStream, NewAfterLeaseCheckPlanClient checkPlanClient) {
        ExcelWriter excelWriter = this.getExcelWriter();
        AfterLeaseCheckReportBO checkReportBO = this.getAfterLeaseCheckReportBO(checkPlanClient);
        // 主报告
        this.generateMainReport(excelWriter, checkPlanClient, checkReportBO);
        // 财务报告
        this.doFinanceReport(excelWriter, checkPlanClient);
        // 写入到输出流中
        excelWriter.flush(outputStream, true);
        return this.getRenderFileName();
    }
}
