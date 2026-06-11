package cn.zswltech.mithras.application.orchestration.document.gendoc.render.afterlease;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseCheckReportTemplateVersionEnum;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckReportDetail;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.policy.DynamicTableRenderPolicy;
import com.deepoove.poi.render.RenderContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/11/16
 * @description
 */
@Slf4j
@Component
public class AfterLeaseCheckReportNonPublicV3Render extends AbstractAfterLeaseCheckReportNonPublicRender {
    @Override
    protected AfterLeaseCheckReportTemplateVersionEnum getTemplateVersion() {
        return AfterLeaseCheckReportTemplateVersionEnum.NON_PUBLIC_V3;
    }

    @Override
    public String render(OutputStream outputStream, NewAfterLeaseCheckPlanClient checkPlanClient) throws Exception {
        AfterLeaseCheckReportBO afterLeaseCheckReportBO = this.getAfterLeaseCheckReportBO(checkPlanClient);
        Map<String, Object> renderMap = this.getCommonRenderMap(checkPlanClient, afterLeaseCheckReportBO);
        // 承租人重点财务情况
        this.fillLeaseFinanceMetricTable(afterLeaseCheckReportBO.getFieldDataList(), renderMap);
        // 担保人模块数据填充
        this.fillGuarantorInfo(afterLeaseCheckReportBO.getFieldDataList(), renderMap);
        // 使用模板渲染文档
        Configure configure = Configure.createDefault();
        // FIXME 需要删除未使用的担保人单元格
//        configure.customPolicy("after_lease_check_report_table", new MyTablePolicy(Integer.parseInt(renderMap.get("corpCount").toString()), Integer.parseInt(renderMap.get("normalCount").toString())));
        XWPFTemplate template = XWPFTemplate.compile(fileTemplateService.getTemplate(this.getTemplateVersion().getTemplateType(), this.getTemplateVersion().getTemplateFileName()), configure).render(renderMap);
        template.writeAndClose(outputStream);
        return this.getRenderFileName();
    }

    private void fillLeaseFinanceMetricTable(List<NewAfterLeaseCheckReportDetail.FieldData> fieldDataList, Map<String, Object> renderMap) {
        Optional<NewAfterLeaseCheckReportDetail.FieldData> optionalLeaseFinanceTable = fieldDataList.stream().filter(e -> Objects.equals(e.getFieldName(), "NP_C_2_03_03")).findFirst();
        if (!optionalLeaseFinanceTable.isPresent()) {
            return;
        }
        NewAfterLeaseCheckReportDetail.FieldData fieldData = optionalLeaseFinanceTable.get();
        if (StrUtil.isBlank(fieldData.getFieldValue())) {
            return;
        }
        FinanceTable financeTable = JSONUtil.toBean(fieldData.getFieldValue(), FinanceTable.class);
        if (CollectionUtil.isEmpty(financeTable.getFieldValue())) {
            return;
        }
        for (int i = 0; i < financeTable.getFieldValue().size(); i++) {
            FinanceTable.FieldValue fieldValue = financeTable.getFieldValue().get(i);
            renderMap.put(("leaseFinance" + (i + 1) + "2"), Optional.ofNullable(fieldValue.getCurrentPeriod()).map(e -> BigDecimal.valueOf(e).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP).toPlainString()).orElse(""));
            renderMap.put(("leaseFinance" + (i + 1) + "3"), Optional.ofNullable(fieldValue.getSamePeriodLastYear()).map(e -> BigDecimal.valueOf(e).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP).toPlainString()).orElse(""));
            renderMap.put(("leaseFinance" + (i + 1) + "4"), Optional.ofNullable(fieldValue.getGrowthRate()).map(e -> BigDecimal.valueOf(e).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP).toPlainString()).orElse(""));
            renderMap.put(("leaseFinance" + (i + 1) + "5"), Optional.ofNullable(fieldValue.getRemark()).orElse(""));
        }
    }

    private void fillGuarantorInfo(List<NewAfterLeaseCheckReportDetail.FieldData> fieldDataList, Map<String, Object> renderMap) {
        List<NewAfterLeaseCheckReportDetail.FieldData> filterDataList = fieldDataList.stream().filter(e -> !Objects.equals(e.getModuleIndex(), 0)).collect(Collectors.toList());
        int corpCount = 0;
        int normalCount = 0;
        for (NewAfterLeaseCheckReportDetail.FieldData fieldData : filterDataList) {
            if (Objects.equals("NP_C_3_04", fieldData.getFieldName())) {
                corpCount++;
            }
            if (Objects.equals("NP_C_3_07", fieldData.getFieldName())) {
                normalCount++;
            }
            if (Objects.equals(fieldData.getFieldName(), "NP_C_3_05_03")) {
                // 渲染表格
                FinanceTable financeTable = JSONUtil.toBean(fieldData.getFieldValue(), FinanceTable.class);
                if (CollectionUtil.isNotEmpty(financeTable.getFieldValue())) {
                    for (int i = 0; i < financeTable.getFieldValue().size(); i++) {
                        FinanceTable.FieldValue fieldValue = financeTable.getFieldValue().get(i);
                        renderMap.put((fieldData.getFieldName() + "_module_" + fieldData.getModuleIndex() + "_" + (i + 1) + "1"), Optional.ofNullable(fieldValue.getCurrentPeriod()).map(e -> BigDecimal.valueOf(e).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP).toPlainString()).orElse(""));
                        renderMap.put((fieldData.getFieldName() + "_module_" + fieldData.getModuleIndex() + "_" + (i + 1) + "2"), Optional.ofNullable(fieldValue.getSamePeriodLastYear()).map(e -> BigDecimal.valueOf(e).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP).toPlainString()).orElse(""));
                        renderMap.put((fieldData.getFieldName() + "_module_" + fieldData.getModuleIndex() + "_" + (i + 1) + "3"), Optional.ofNullable(fieldValue.getGrowthRate()).map(e -> BigDecimal.valueOf(e).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP).toPlainString()).orElse(""));
                        renderMap.put((fieldData.getFieldName() + "_module_" + fieldData.getModuleIndex() + "_" + (i + 1) + "4"), Optional.ofNullable(fieldValue.getRemark()).orElse(""));
                    }
                }
            } else {
                renderMap.put((fieldData.getFieldName() + "_module_" + fieldData.getModuleIndex()), this.parseContent(fieldData.getFieldType(), fieldData.getFieldOption(), fieldData.getFieldValue()));
            }
        }
        renderMap.put("corpCount", corpCount);
        renderMap.put("normalCount", normalCount);
    }

    public static class MyTablePolicy extends DynamicTableRenderPolicy {
        private final int corpCount;
        private final int normalCount;

        public MyTablePolicy(int corpCount, int normalCount) {
            this.corpCount = corpCount;
            this.normalCount = normalCount;
        }

        @Override
        public void render(XWPFTable table, Object data) throws Exception {
            int corpTemplateTotalRows = 6;
            int corpTemplateStartRowIndex = 17;
            int normalTemplateTotalRows = 10;
            int normalTemplateStartRowIndex = 24;
            int corpNeedRemoveCount = corpTemplateTotalRows - corpCount;
            int normalNeedRemoveCount = normalTemplateTotalRows - normalCount;
            if (corpNeedRemoveCount > 0) {
                for (int i = corpTemplateStartRowIndex + corpCount; i < corpTemplateStartRowIndex + corpTemplateTotalRows ; i++) {
                    table.removeRow(i);
                }
            }
            if (normalNeedRemoveCount > 0) {
                for (int i = normalTemplateStartRowIndex + normalCount; i < normalTemplateStartRowIndex + normalTemplateTotalRows; i++) {
                    table.removeRow(i);
                }
            }
        }

        @Override
        protected void beforeRender(RenderContext<Object> context) {
            clearPlaceholder(context, false);
        }

        @Override
        protected void afterRender(RenderContext<Object> context) {
            clearPlaceholder(context, true);
        }
    }
}
