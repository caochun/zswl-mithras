package cn.zswltech.mithras.others.数据订正;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.afterlease.model.*;
import cn.zswltech.mithras.afterlease.application.*;
import cn.zswltech.mithras.afterlease.application.lib.AfterLeaseCheckPlanClientLibService;
import cn.zswltech.mithras.afterlease.application.lib.AfterLeaseCheckReportContentLibService;
import cn.zswltech.mithras.afterlease.application.lib.AfterLeaseCheckReportSummaryLibService;
import cn.zswltech.mithras.afterlease.application.lib.NewAfterLeaseCheckReportDetailLibService;
import cn.zswltech.mithras.web.MithrasApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/11/15
 * @description
 */
@RunWith(SpringRunner.class)
@Slf4j
@ActiveProfiles("dev")
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class 租后检查报告数据订正 {
    @Test
    public void fix() {
        List<NewAfterLeaseCheckReportFieldConfig> fieldRemarkList = SpringUtil.getBean(NewAfterLeaseCheckReportFieldConfigService.class).list();
        Map<String, String> fieldRemark = fieldRemarkList.stream().collect(Collectors.toMap(NewAfterLeaseCheckReportFieldConfig::getFieldName, NewAfterLeaseCheckReportFieldConfig::getFieldRemark));
        List<NewAfterLeaseCheckPlanClient> all = SpringUtil.getBean(AfterLeaseCheckPlanClientService.class).list();
        if (CollectionUtil.isEmpty(all)) {
            return;
        }
        List<NewAfterLeaseCheckReportDetail> detailList = new LinkedList<>();
        for (NewAfterLeaseCheckPlanClient checkPlanClient : all) {
            NewAfterLeaseCheckReportDetail detail = new NewAfterLeaseCheckReportDetail();
            detail.setCheckPlanClientId(checkPlanClient.getId());
            List<NewAfterLeaseCheckReportContent> contentList = SpringUtil.getBean(AfterLeaseCheckReportContentService.class).listByCheckPlanClientId(checkPlanClient.getId());
            List<NewAfterLeaseCheckReportSummary> summaryList = SpringUtil.getBean(AfterLeaseCheckReportSummaryService.class).listByCheckPlanClientId(checkPlanClient.getId());
            if (CollectionUtil.isNotEmpty(contentList)) {
                List<NewAfterLeaseCheckReportDetail.FieldData> contentFieldList = new LinkedList<>();
                for (NewAfterLeaseCheckReportContent checkReportContent : contentList) {
                    if (StrUtil.isBlank(checkReportContent.getContent())) {
                        continue;
                    }
                    NewAfterLeaseCheckReportDetail.FieldData fieldData = new NewAfterLeaseCheckReportDetail.FieldData();
                    fieldData.setModuleIndex(0);
                    fieldData.setFieldName(checkReportContent.getTemplateCode());
                    fieldData.setFieldValue(checkReportContent.getContent());
                    fieldData.setFieldRemark(fieldRemark.get(checkReportContent.getTemplateCode()));
                    fieldData.setFieldType(checkReportContent.getTemplateContentInputType());
                    if (StrUtil.isNotBlank(checkReportContent.getTemplateContentInputOption())) {
                        fieldData.setFieldOption(JSONUtil.toList(checkReportContent.getTemplateContentInputOption(), OptionData.class));
                    }
                    contentFieldList.add(fieldData);
                }
                if (CollectionUtil.isNotEmpty(contentFieldList)) {
                    detail.setReportContent(JSONUtil.toJsonStr(contentFieldList));
                }
            }
            if (CollectionUtil.isNotEmpty(summaryList)) {
                List<NewAfterLeaseCheckReportDetail.FieldData> summaryFieldList = new LinkedList<>();
                for (NewAfterLeaseCheckReportSummary checkReportSummary : summaryList) {
                    if (StrUtil.isBlank(checkReportSummary.getContent())) {
                        continue;
                    }
                    NewAfterLeaseCheckReportDetail.FieldData fieldData = new NewAfterLeaseCheckReportDetail.FieldData();
                    fieldData.setModuleIndex(0);
                    fieldData.setFieldName(checkReportSummary.getTemplateCode());
                    fieldData.setFieldValue(checkReportSummary.getContent());
                    fieldData.setFieldRemark(fieldRemark.get(checkReportSummary.getTemplateCode()));
                    fieldData.setFieldType(checkReportSummary.getTemplateContentInputType());
                    if (StrUtil.isNotBlank(checkReportSummary.getTemplateContentInputOption())) {
                        fieldData.setFieldOption(JSONUtil.toList(checkReportSummary.getTemplateContentInputOption(), OptionData.class));
                    }
                    summaryFieldList.add(fieldData);
                }
                if (CollectionUtil.isNotEmpty(summaryFieldList)) {
                    detail.setReportSummary(JSONUtil.toJsonStr(summaryFieldList));
                }
            }
            detailList.add(detail);
        }
        if (CollectionUtil.isNotEmpty(detailList)) {
            SpringUtil.getBean(NewAfterLeaseCheckReportDetailService.class).saveBatch(detailList);
        }
    }

    @Test
    public void fixLib() {
        List<NewAfterLeaseCheckReportFieldConfig> fieldRemarkList = SpringUtil.getBean(NewAfterLeaseCheckReportFieldConfigService.class).list();
        Map<String, String> fieldRemark = fieldRemarkList.stream().collect(Collectors.toMap(NewAfterLeaseCheckReportFieldConfig::getFieldName, NewAfterLeaseCheckReportFieldConfig::getFieldRemark));
        List<NewAfterLeaseCheckPlanClientLib> all = SpringUtil.getBean(AfterLeaseCheckPlanClientLibService.class).list();
        if (CollectionUtil.isEmpty(all)) {
            return;
        }
        List<NewAfterLeaseCheckReportDetail> detailList = SpringUtil.getBean(NewAfterLeaseCheckReportDetailService.class).list();
        Map<Long, Long> checkClientId2DetailIdMap = detailList.stream().collect(Collectors.toMap(NewAfterLeaseCheckReportDetail::getCheckPlanClientId, NewAfterLeaseCheckReportDetail::getId));
        List<NewAfterLeaseCheckReportDetailLib> detailLibList = new LinkedList<>();
        for (NewAfterLeaseCheckPlanClientLib checkPlanClientLib : all) {
            NewAfterLeaseCheckReportDetailLib detailLib = new NewAfterLeaseCheckReportDetailLib();
            detailLib.setCheckPlanClientId(checkPlanClientLib.getOriginId());
            detailLib.setOriginId(checkClientId2DetailIdMap.get(checkPlanClientLib.getOriginId()));
            detailLib.setVersion(checkPlanClientLib.getVersion());
            detailLib.setVersionType(checkPlanClientLib.getVersionType());
            List<NewAfterLeaseCheckReportContentLib> contentLibList = SpringUtil.getBean(AfterLeaseCheckReportContentLibService.class).listByCheckClientIdAndVersion(checkPlanClientLib.getOriginId(), checkPlanClientLib.getVersion());
            List<NewAfterLeaseCheckReportSummaryLib> summaryLibList = SpringUtil.getBean(AfterLeaseCheckReportSummaryLibService.class).listByCheckClientId(checkPlanClientLib.getOriginId(), checkPlanClientLib.getVersion());
            if (CollectionUtil.isNotEmpty(contentLibList)) {
                List<NewAfterLeaseCheckReportDetailLib.FieldData> contentFieldList = new LinkedList<>();
                for (NewAfterLeaseCheckReportContentLib checkReportContentLib : contentLibList) {
                    if (StrUtil.isBlank(checkReportContentLib.getContent())) {
                        continue;
                    }
                    NewAfterLeaseCheckReportDetailLib.FieldData fieldData = new NewAfterLeaseCheckReportDetailLib.FieldData();
                    fieldData.setModuleIndex(0);
                    fieldData.setFieldName(checkReportContentLib.getTemplateCode());
                    fieldData.setFieldValue(checkReportContentLib.getContent());
                    fieldData.setFieldRemark(fieldRemark.get(checkReportContentLib.getTemplateCode()));
                    fieldData.setFieldType(checkReportContentLib.getTemplateContentInputType());
                    if (StrUtil.isNotBlank(checkReportContentLib.getTemplateContentInputOption())) {
                        fieldData.setFieldOption(JSONUtil.toList(checkReportContentLib.getTemplateContentInputOption(), OptionData.class));
                    }
                    contentFieldList.add(fieldData);
                }
                if (CollectionUtil.isNotEmpty(contentFieldList)) {
                    detailLib.setReportContent(JSONUtil.toJsonStr(contentFieldList));
                }
            }
            if (CollectionUtil.isNotEmpty(summaryLibList)) {
                List<NewAfterLeaseCheckReportDetailLib.FieldData> summaryFieldList = new LinkedList<>();
                for (NewAfterLeaseCheckReportSummaryLib checkReportSummaryLib : summaryLibList) {
                    if (StrUtil.isBlank(checkReportSummaryLib.getContent())) {
                        continue;
                    }
                    NewAfterLeaseCheckReportDetailLib.FieldData fieldData = new NewAfterLeaseCheckReportDetailLib.FieldData();
                    fieldData.setModuleIndex(0);
                    fieldData.setFieldName(checkReportSummaryLib.getTemplateCode());
                    fieldData.setFieldValue(checkReportSummaryLib.getContent());
                    fieldData.setFieldRemark(fieldRemark.get(checkReportSummaryLib.getTemplateCode()));
                    fieldData.setFieldType(checkReportSummaryLib.getTemplateContentInputType());
                    if (StrUtil.isNotBlank(checkReportSummaryLib.getTemplateContentInputOption())) {
                        fieldData.setFieldOption(JSONUtil.toList(checkReportSummaryLib.getTemplateContentInputOption(), OptionData.class));
                    }
                    summaryFieldList.add(fieldData);
                }
                if (CollectionUtil.isNotEmpty(summaryFieldList)) {
                    detailLib.setReportSummary(JSONUtil.toJsonStr(summaryFieldList));
                }
            }
            detailLibList.add(detailLib);
        }
        if (CollectionUtil.isNotEmpty(detailLibList)) {
            SpringUtil.getBean(NewAfterLeaseCheckReportDetailLibService.class).saveBatch(detailLibList);
        }
    }
}
