package cn.zswltech.mithras.application.orchestration.document.gendoc.render.afterlease;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseCheckReportTemplateVersionEnum;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckReportDetail;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckReportMeta;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckReportMetaService;
import com.deepoove.poi.XWPFTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author bigbear
 * @version 1.0
 * @description 公交车类
 * @since 2025/9/18 10:22
 **/
@Slf4j
@Component
public class AfterLeaseCheckReportBusV3Render extends AbstractAfterLeaseCheckReportRender {

    @Resource
    protected AfterLeaseCheckReportMetaService afterLeaseCheckReportMetaService;

    protected String getRenderFileName() {
        return "租后检查报告_公交车类" + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

    @Override
    protected AfterLeaseCheckReportTemplateVersionEnum getTemplateVersion() {
        return AfterLeaseCheckReportTemplateVersionEnum.BUS_V3;
    }

    @Override
    public String render(OutputStream outputStream, NewAfterLeaseCheckPlanClient newAfterLeaseCheckPlanClient) throws Exception {
        AfterLeaseCheckReportBO reportBO = this.getAfterLeaseCheckReportBO(newAfterLeaseCheckPlanClient);
        // 填充基本信息
        Map<String, Object> renderMap = super.getCommonRenderMap(newAfterLeaseCheckPlanClient, reportBO);
        // 报告内容/总结
        Map<String, NewAfterLeaseCheckReportDetail.FieldData> fieldDataMap = reportBO.getFieldDataList()
                .stream().filter(e -> Objects.equals(e.getModuleIndex(), 0))
                .collect(Collectors.toMap(NewAfterLeaseCheckReportDetail.FieldData::getFieldName, Function.identity(), (a, b) -> a));
        for (Map.Entry<String, NewAfterLeaseCheckReportDetail.FieldData> entry : fieldDataMap.entrySet()) {
            renderMap.put(entry.getKey(), this.parseContent(entry.getValue().getFieldType(), entry.getValue().getFieldOption(), entry.getValue().getFieldValue()));
        }
        // 担保人
        fillGuarantorInfo(reportBO.getFieldDataList(), renderMap);
        // 财务指标
        fillLeaseFinanceMetricTable(reportBO.getFieldDataList(), renderMap);
        // 渲染文档
        NewAfterLeaseCheckReportMeta reportMeta = afterLeaseCheckReportMetaService.getByCheckPlanClientId(newAfterLeaseCheckPlanClient.getId());
        AfterLeaseCheckReportTemplateVersionEnum templateVersion = AfterLeaseCheckReportTemplateVersionEnum.findByTypeVersion(reportMeta.getReportType(), reportMeta.getReportTemplateVersion());
        if (Objects.isNull(templateVersion)) {
            throw new MithrasException("没有找到对应版本的报告模板");
        }
        InputStream inputStream = fileTemplateService.getTemplate(templateVersion.getTemplateType(), templateVersion.getTemplateFileName());
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return getRenderFileName();
    }

    private void fillGuarantorInfo(List<NewAfterLeaseCheckReportDetail.FieldData> fieldDataList, Map<String, Object> renderMap) {
        List<NewAfterLeaseCheckReportDetail.FieldData> filterDataList = fieldDataList.stream()
                .filter(e -> !Objects.equals(e.getModuleIndex(), 0)).collect(Collectors.toList());
        int corpCount = 0;
        int normalCount = 0;
        for (NewAfterLeaseCheckReportDetail.FieldData fieldData : filterDataList) {
            if (Objects.equals("B_C_5_02", fieldData.getFieldName())) {
                corpCount++;
            }
            if (Objects.equals("B_C_8_02", fieldData.getFieldName())) {
                normalCount++;
            }
            renderMap.put((fieldData.getFieldName() + "_module_" + fieldData.getModuleIndex()), this.parseContent(fieldData.getFieldType(), fieldData.getFieldOption(), fieldData.getFieldValue()));
        }
        renderMap.put("corpCount", corpCount);
        renderMap.put("normalCount", normalCount);
    }

    private void fillLeaseFinanceMetricTable(List<NewAfterLeaseCheckReportDetail.FieldData> fieldDataList, Map<String, Object> renderMap) {
        Optional<NewAfterLeaseCheckReportDetail.FieldData> optionalLeaseFinanceTable = fieldDataList.stream().filter(e -> Objects.equals(e.getFieldName(), "B_C_2_01")).findFirst();
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
}
