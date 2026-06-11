package cn.zswltech.mithras.application.orchestration.document.gendoc.render.afterlease;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseCheckReportTemplateVersionEnum;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckReportDetail;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/11/16
 * @description
 */
@Component
public class AfterLeaseCheckReportPublicV3Render extends AbstractAfterLeaseCheckReportPublicRender {
    @Override
    protected AfterLeaseCheckReportTemplateVersionEnum getTemplateVersion() {
        return AfterLeaseCheckReportTemplateVersionEnum.PUBLIC_V3;
    }

    @Override
    public String render(OutputStream outputStream, NewAfterLeaseCheckPlanClient checkPlanClient) throws Exception {
        ExcelWriter excelWriter = this.getExcelWriter();
        AfterLeaseCheckReportBO checkReportBO = this.getAfterLeaseCheckReportBO(checkPlanClient);
        // 主报告
        this.generateMainReport(excelWriter, checkPlanClient, checkReportBO);
        Map<String, NewAfterLeaseCheckReportDetail.FieldData> fieldDataMap = checkReportBO.getFieldDataList().stream().filter(e -> Objects.equals(e.getModuleIndex(), 0)).collect(Collectors.toMap(NewAfterLeaseCheckReportDetail.FieldData::getFieldName, e -> e, (a, b) -> a));
        // 补充V2版本的数据
        excelWriter.writeCellValue("B51", Optional.ofNullable(fieldDataMap.get("P_S_1_04")).map(NewAfterLeaseCheckReportDetail.FieldData::getFieldValue).orElse(null));
        NewAfterLeaseCheckReportDetail.FieldData leaseFinanceTableData = fieldDataMap.get("P_C_2_07");
        if (Objects.nonNull(leaseFinanceTableData) && StrUtil.isNotBlank(leaseFinanceTableData.getFieldValue())) {
            FinanceTable financeTable = JSONUtil.toBean(leaseFinanceTableData.getFieldValue(), FinanceTable.class);
            this.fillFinanceTable(excelWriter, financeTable, 34);
        }
        NewAfterLeaseCheckReportDetail.FieldData guarantorFinanceTableData = fieldDataMap.get("P_C_3_07");
        if (Objects.nonNull(guarantorFinanceTableData) && StrUtil.isNotBlank(guarantorFinanceTableData.getFieldValue())) {
            FinanceTable financeTable = JSONUtil.toBean(guarantorFinanceTableData.getFieldValue(), FinanceTable.class);
            this.fillFinanceTable(excelWriter, financeTable, 43);
        }
        // 财务报告
        this.doFinanceReport(excelWriter, checkPlanClient);
        // 写入到输出流中
        excelWriter.flush(outputStream, true);
        return this.getRenderFileName();
    }

    private void fillFinanceTable(ExcelWriter excelWriter, FinanceTable financeTable, int rowIndexOffset) {
        for (int i = 0; i < 7; i++) {
            int rowIndex = i + rowIndexOffset;
            FinanceTable.FieldValue fieldValue = financeTable.getFieldValue().get(i);
            excelWriter.writeCellValue(1, rowIndex, Optional.ofNullable(fieldValue.getCurrentPeriod()).map(e -> BigDecimal.valueOf(e).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP).toPlainString()).orElse(""));
            excelWriter.writeCellValue(2, rowIndex, Optional.ofNullable(fieldValue.getSamePeriodLastYear()).map(e -> BigDecimal.valueOf(e).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP).toPlainString()).orElse(""));
            excelWriter.writeCellValue(3, rowIndex, Optional.ofNullable(fieldValue.getGrowthRate()).map(e -> BigDecimal.valueOf(e).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP).toPlainString()).orElse(""));
            excelWriter.writeCellValue(4, rowIndex, Optional.ofNullable(fieldValue.getRemark()).orElse(""));
        }
    }
}
