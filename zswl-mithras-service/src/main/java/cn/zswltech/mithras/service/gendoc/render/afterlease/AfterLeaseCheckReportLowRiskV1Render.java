package cn.zswltech.mithras.service.gendoc.render.afterlease;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.common.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.afterlease.AfterLeaseCheckReportTemplateVersionEnum;
import cn.zswltech.mithras.service.enums.afterlease.AfterLeaseCheckWayEnum;
import cn.zswltech.mithras.service.mapper.model.afterlease.*;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckReportMetaService;
import com.deepoove.poi.XWPFTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/11/20 11:19
 */
@Component
public class AfterLeaseCheckReportLowRiskV1Render extends AbstractAfterLeaseCheckReportRender {
    @Resource
    private AfterLeaseCheckReportMetaService afterLeaseCheckReportMetaService;

    @Override
    protected AfterLeaseCheckReportTemplateVersionEnum getTemplateVersion() {
        return AfterLeaseCheckReportTemplateVersionEnum.LOW_RISK_V1;
    }

    @Override
    public String render(OutputStream outputStream, NewAfterLeaseCheckPlanClient o) throws Exception {
        Map<String, Object> renderMap = new HashMap<>(32);
        AfterLeaseCheckReportBO reportBO = this.getAfterLeaseCheckReportBO(o);
        // 填充基本信息
        renderMap.put("deptName", reportBO.getBizDeptName());
        renderMap.put("checkPerson", reportBO.getUserNameMap().get(o.getBelongSponsorId()));
        renderMap.put("checkDate", o.getCheckTime());
        renderMap.put("clientName", reportBO.getReportBase().getClientName());
        renderMap.put("industry", reportBO.getReportBase().getIndustry());
        AfterLeaseCheckWayEnum checkWayEnum = AfterLeaseCheckWayEnum.find(o.getCheckWay());
        renderMap.put("checkWay", Optional.ofNullable(checkWayEnum).map(AfterLeaseCheckWayEnum::display).orElse(""));
        renderMap.put("contractAmount", Optional.ofNullable(reportBO.getReportBase().getContractAmount()).map(e -> this.toWan(e) + "万元").orElse(null));
        renderMap.put("deadline", Optional.ofNullable(reportBO.getReportBase().getDeadline()).map(e -> LocalDateTimeUtil.format(e, DatePattern.NORM_DATE_PATTERN)).orElse(""));
        renderMap.put("riskExposure", Optional.ofNullable(reportBO.getReportBase().getRiskExposure()).map(e -> this.toWan(e) + "万元").orElse(null));
        StringBuilder stringBuilder = new StringBuilder();
        if (Objects.nonNull(reportBO.getReportBase().getNextRepayDate())) {
            stringBuilder.append(LocalDateTimeUtil.format(reportBO.getReportBase().getNextRepayDate(), DatePattern.NORM_DATE_PATTERN));
            stringBuilder.append("还款");
        }
        if (Objects.nonNull(reportBO.getReportBase().getNextRepayAmount())) {
            stringBuilder.append(this.toWan(reportBO.getReportBase().getNextRepayAmount()));
            stringBuilder.append("万元");
        }
        renderMap.put("nextRepay", stringBuilder.toString());
        // 报告内容/总结
        Map<String, NewAfterLeaseCheckReportDetail.FieldData> fieldDataMap = reportBO.getFieldDataList().stream().filter(e -> Objects.equals(e.getModuleIndex(), 0)).collect(Collectors.toMap(NewAfterLeaseCheckReportDetail.FieldData::getFieldName, e -> e));
        for (Map.Entry<String, NewAfterLeaseCheckReportDetail.FieldData> entry : fieldDataMap.entrySet()) {
            renderMap.put(entry.getKey(), this.parseContent(entry.getValue().getFieldType(), entry.getValue().getFieldOption(), entry.getValue().getFieldValue()));
        }
        // 渲染文档
        NewAfterLeaseCheckReportMeta reportMeta = afterLeaseCheckReportMetaService.getByCheckPlanClientId(o.getId());
        AfterLeaseCheckReportTemplateVersionEnum templateVersion = AfterLeaseCheckReportTemplateVersionEnum.findByTypeVersion(reportMeta.getReportType(), reportMeta.getReportTemplateVersion());
        if (Objects.isNull(templateVersion)) {
            throw new MithrasException("没有找到对应版本的报告模板");
        }
        InputStream inputStream = fileTemplateService.getTemplate(templateVersion.getTemplateType(), templateVersion.getTemplateFileName());
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return "租后检查报告_低风险业务" + GlobalConstants.OFFICE_WORD_SUFFIX;
    }
}
