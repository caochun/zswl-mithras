package cn.zswltech.mithras.service.gendoc.render.afterlease;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.common.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.afterlease.AfterLeaseCheckReportTemplateVersionEnum;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckReportDetail;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckReportMeta;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckReportMetaService;
import com.deepoove.poi.XWPFTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author bigbear
 * @version 1.0
 * @description 国有资产类
 * @since 2025/9/18 10:22
 **/
@Slf4j
@Component
public class AfterLeaseCheckReportStateOwnedAssetV3Render extends AbstractAfterLeaseCheckReportRender {

    protected String getRenderFileName() {
        return "租后检查报告_国有资产类" + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

    @Resource
    protected AfterLeaseCheckReportMetaService afterLeaseCheckReportMetaService;

    @Override
    protected AfterLeaseCheckReportTemplateVersionEnum getTemplateVersion() {
        return AfterLeaseCheckReportTemplateVersionEnum.STATE_OWNED_ASSET_V3;
    }

    @Override
    public String render(OutputStream outputStream, NewAfterLeaseCheckPlanClient newAfterLeaseCheckPlanClient) throws Exception {
        AfterLeaseCheckReportBO reportBO = this.getAfterLeaseCheckReportBO(newAfterLeaseCheckPlanClient);
        // 填充基本信息
        Map<String, Object> renderMap = super.getCommonRenderMap(newAfterLeaseCheckPlanClient, reportBO);
        // 报告内容/总结
        Map<String, NewAfterLeaseCheckReportDetail.FieldData> fieldDataMap = reportBO.getFieldDataList()
                .stream().filter(e -> Objects.equals(e.getModuleIndex(), 0))
                .collect(Collectors.toMap(NewAfterLeaseCheckReportDetail.FieldData::getFieldName, e -> e, (a, b) -> a));
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
        List<NewAfterLeaseCheckReportDetail.FieldData> filterDataList = fieldDataList.stream().filter(e -> !Objects.equals(e.getModuleIndex(), 0)).collect(Collectors.toList());
        int corpCount = 0;
        int normalCount = 0;
        for (NewAfterLeaseCheckReportDetail.FieldData fieldData : filterDataList) {
            if (Objects.equals("SOA_C_5_02", fieldData.getFieldName())) {
                corpCount++;
            }
            if (Objects.equals("SOA_C_8_02", fieldData.getFieldName())) {
                normalCount++;
            }
            renderMap.put((fieldData.getFieldName() + "_module_" + fieldData.getModuleIndex()), this.parseContent(fieldData.getFieldType(), fieldData.getFieldOption(), fieldData.getFieldValue()));
        }
        renderMap.put("corpCount", corpCount);
        renderMap.put("normalCount", normalCount);
    }

    private void fillLeaseFinanceMetricTable(List<NewAfterLeaseCheckReportDetail.FieldData> fieldDataList, Map<String, Object> renderMap) {
        Optional<NewAfterLeaseCheckReportDetail.FieldData> optionalLeaseFinanceTable = fieldDataList.stream().filter(e -> Objects.equals(e.getFieldName(), "SOA_C_2_01")).findFirst();
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
