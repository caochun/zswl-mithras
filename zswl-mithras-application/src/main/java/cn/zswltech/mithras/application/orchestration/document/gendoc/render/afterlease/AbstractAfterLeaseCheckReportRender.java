package cn.zswltech.mithras.application.orchestration.document.gendoc.render.afterlease;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseCheckReportTemplateVersionEnum;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseCheckWayEnum;
import cn.zswltech.mithras.contract.gendoc.AbstractBasicRender;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckReportBase;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckReportDetail;
import cn.zswltech.mithras.afterlease.mapper.model.OptionData;
import cn.zswltech.mithras.customer.mapper.model.client.Client;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckReportBaseService;
import cn.zswltech.mithras.afterlease.application.NewAfterLeaseCheckReportDetailService;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.document.file.template.FileTemplateService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewBaseInfoService;
import lombok.Data;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/11/20
 * @description
 */
public abstract class AbstractAfterLeaseCheckReportRender extends AbstractBasicRender<NewAfterLeaseCheckPlanClient> {
    @Resource
    protected Id2NameService id2NameService;
    @Resource
    protected ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    protected AfterLeaseCheckReportBaseService afterLeaseCheckReportBaseService;
    @Resource
    protected ClientService clientService;
    @Resource
    protected FileTemplateService fileTemplateService;
    @Resource
    protected NewAfterLeaseCheckReportDetailService afterLeaseCheckReportDetailService;

    protected abstract AfterLeaseCheckReportTemplateVersionEnum getTemplateVersion();

    protected AfterLeaseCheckReportBO getAfterLeaseCheckReportBO(NewAfterLeaseCheckPlanClient checkPlanClient) {
        Client client = clientService.getById(checkPlanClient.getClientId());
        Assert.notNull(client, () -> MithrasException.newException("客户不存在"));
        Map<Long, String> bizDeptNameMap = id2NameService.deptId2Name(Collections.singletonList(checkPlanClient.getBelongDeptId()));
        List<Long> userIdList = new LinkedList<>();
        userIdList.add(checkPlanClient.getBelongSponsorId());
        Map<Long, String> userNameMap = id2NameService.sysUserId2Name(userIdList);
        NewAfterLeaseCheckReportBase reportBase = afterLeaseCheckReportBaseService.getReportBase(checkPlanClient.getId());
        Assert.notNull(reportBase, () -> MithrasException.newException(checkPlanClient.getClientName() + "的检查报告基本信息不存在"));
        AfterLeaseCheckReportBO afterLeaseCheckReportBO = new AfterLeaseCheckReportBO();
        afterLeaseCheckReportBO.setClientName(client.getClientName());
        afterLeaseCheckReportBO.setReportBase(reportBase);
        afterLeaseCheckReportBO.setUserNameMap(userNameMap);
        afterLeaseCheckReportBO.setBizDeptName(bizDeptNameMap.get(checkPlanClient.getBelongDeptId()));
        NewAfterLeaseCheckReportDetail detail = afterLeaseCheckReportDetailService.getOneByCheckPlanClientId(checkPlanClient.getId());
        List<NewAfterLeaseCheckReportDetail.FieldData> fieldDataList = new LinkedList<>();
        if (Objects.nonNull(detail)) {
            if (StrUtil.isNotBlank(detail.getReportContent())) {
                List<NewAfterLeaseCheckReportDetail.FieldData> list = JSONUtil.toList(detail.getReportContent(), NewAfterLeaseCheckReportDetail.FieldData.class);
                fieldDataList.addAll(list);
            }
            if (StrUtil.isNotBlank(detail.getReportSummary())) {
                List<NewAfterLeaseCheckReportDetail.FieldData> list = JSONUtil.toList(detail.getReportSummary(), NewAfterLeaseCheckReportDetail.FieldData.class);
                fieldDataList.addAll(list);
            }
        }
        afterLeaseCheckReportBO.setFieldDataList(fieldDataList);
        return afterLeaseCheckReportBO;
    }

    protected String parseContent(String inputType, List<OptionData> optionDataList, String content) {
        if (StrUtil.isBlank(content)) {
            return content;
        }
        if (Objects.equals("textArea", inputType) || Objects.equals("text", inputType)) {
            return content;
        } else if (Objects.equals("radio", inputType)) {
            for (OptionData optionData : optionDataList) {
                if (Objects.equals(content, optionData.getValue())) {
                    return optionData.getLabel();
                }
            }
        } else if (Objects.equals("checkbox", inputType)) {
            Map<String, String> optionMap = new HashMap<>();
            for (OptionData optionData : optionDataList) {
                optionMap.put(optionData.getValue(), optionData.getLabel());
            }
            List<String> list = new LinkedList<>();
            String[] array = content.split(",");
            for (String s : array) {
                String label = optionMap.get(s);
                if (StrUtil.isNotBlank(label)) {
                    list.add(label);
                }
            }
            return CollectionUtil.join(list, "，");
        }
        return "";
    }

    @Data
    public static class AfterLeaseCheckReportBO {
        private NewAfterLeaseCheckReportBase reportBase;
        private Map<Long, String> userNameMap;
        private String bizDeptName;
        private String clientName;
        private Long remainingPrincipal;
        private List<NewAfterLeaseCheckReportDetail.FieldData> fieldDataList;
    }

    @Data
    public static class FinanceTable {
        private List<Column> columns;
        private List<FieldValue> fieldValue;

        @Data
        public static class Column {
            /**
             * 表头
             */
            private String title;
        }

        @Data
        public static class FieldValue {
            /**
             * 科目
             */
            private String subject;
            /**
             * 本期
             */
            private Long currentPeriod;
            /**
             * 上年同期
             */
            private Long samePeriodLastYear;
            /**
             * 同比/增减率
             */
            private Long growthRate;
            /**
             * 说明
             */
            private String remark;
        }
    }

    protected Map<String, Object> getCommonRenderMap(NewAfterLeaseCheckPlanClient newAfterLeaseCheckPlanClient, AfterLeaseCheckReportBO reportBO) {
        Map<String, Object> renderMap = new HashMap<>(32);
        // 填充基本信息
        renderMap.put("deptName", reportBO.getBizDeptName());
        if (CharSequenceUtil.isNotBlank(newAfterLeaseCheckPlanClient.getGuaranteeNames())) {
            renderMap.put("guaranteeNames", CharSequenceUtil.join(", ", JSONUtil.toList(newAfterLeaseCheckPlanClient.getGuaranteeNames(), String.class)));
        }
        if (Objects.nonNull(newAfterLeaseCheckPlanClient.getRemainingPrincipal())) {
            renderMap.put("remainingPrincipal", this.toWan(newAfterLeaseCheckPlanClient.getRemainingPrincipal()));
        } else {
            Map<Long, Long> principalMap = clientService.getClientRemainingPrincipalMap(Collections.singletonList(newAfterLeaseCheckPlanClient.getClientId()));
            if (CollUtil.isNotEmpty(principalMap)) {
                renderMap.put("remainingPrincipal", this.toWan(principalMap.get(newAfterLeaseCheckPlanClient.getClientId())));
            }
        }
        renderMap.put("job", reportBO.getReportBase().getJob());
        renderMap.put("mainPerson", reportBO.getReportBase().getMainPerson());
        renderMap.put("checkDate", newAfterLeaseCheckPlanClient.getCheckTime());
        renderMap.put("checkPerson", reportBO.getUserNameMap().get(newAfterLeaseCheckPlanClient.getBelongSponsorId()));
        renderMap.put("clientName", reportBO.getReportBase().getClientName());
        renderMap.put("industry", reportBO.getReportBase().getIndustry());
        AfterLeaseCheckWayEnum checkWayEnum = AfterLeaseCheckWayEnum.find(newAfterLeaseCheckPlanClient.getCheckWay());
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
        return renderMap;
    }
}
