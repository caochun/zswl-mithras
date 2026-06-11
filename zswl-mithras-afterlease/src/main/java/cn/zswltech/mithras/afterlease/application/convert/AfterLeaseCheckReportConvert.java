package cn.zswltech.mithras.afterlease.application.convert;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.dto.afterlease.*;
import cn.zswltech.mithras.afterlease.mapper.model.*;

import java.util.*;

/**
 * @author dingqi
 * @date 2022/11/16
 * @description
 */
public class AfterLeaseCheckReportConvert {
    public static AfterLeaseCheckReportBaseRSP toAfterLeaseCheckReportBaseRSP(NewAfterLeaseCheckReportBase reportBase) {
        AfterLeaseCheckReportBaseRSP rsp = new AfterLeaseCheckReportBaseRSP();
        if (Objects.isNull(reportBase)) {
            return rsp;
        }
        rsp.setId(reportBase.getId());
        rsp.setCheckPlanClientId(reportBase.getCheckPlanClientId());
        rsp.setCheckWay(reportBase.getCheckWay());
        rsp.setIndustry(reportBase.getIndustry());
        rsp.setClientId(reportBase.getClientId());
        rsp.setClientName(reportBase.getClientName());
        rsp.setRiskManagerId(reportBase.getRiskManagerId());
        rsp.setRiskManagerName(reportBase.getRiskManagerName());
        rsp.setNextRepayAmount(reportBase.getNextRepayAmount());
        if (Objects.nonNull(reportBase.getNextRepayDate())) {
            rsp.setNextRepayDate(LocalDateTimeUtil.format(reportBase.getNextRepayDate(), DatePattern.NORM_DATE_PATTERN));
        }
        rsp.setBizDeptName(reportBase.getBizDeptName());
//        if (Objects.nonNull(reportBase.getCheckDate())) {
//            rsp.setCheckDate(LocalDateTimeUtil.format(reportBase.getCheckDate(), DatePattern.NORM_DATE_PATTERN));
//        }
        if (Objects.nonNull(reportBase.getCheckPeriodEnd())) {
            rsp.setCheckPeriodEnd(LocalDateTimeUtil.format(reportBase.getCheckPeriodEnd(), DatePattern.NORM_DATE_PATTERN));
        }
        if (Objects.nonNull(reportBase.getCheckPeriodStart())) {
            rsp.setCheckPeriodStart(LocalDateTimeUtil.format(reportBase.getCheckPeriodStart(), DatePattern.NORM_DATE_PATTERN));
        }
        rsp.setCheckPlanClientId(reportBase.getCheckPlanClientId());
        rsp.setContractAmount(reportBase.getContractAmount());
        if (Objects.nonNull(reportBase.getDeadline())) {
            rsp.setDeadline(LocalDateTimeUtil.format(reportBase.getDeadline(), DatePattern.NORM_DATE_PATTERN));
        }
        rsp.setMainPerson(reportBase.getMainPerson());
        rsp.setMainPersonContactWay(reportBase.getContactWay());
        rsp.setMainPersonJob(reportBase.getJob());
        rsp.setSponsorId(reportBase.getSponsorUserId());
        rsp.setSponsorName(reportBase.getSponsorUserName());
        // 不再取数据库，实时查
        //rsp.setRiskExposure(reportBase.getRiskExposure());
        rsp.setBizDeptId(reportBase.getBizDeptId());
        rsp.setBizDeptName(reportBase.getBizDeptName());
        return rsp;
    }

    public static AfterLeaseCheckReportNonPublicExtraRSP toAfterLeaseCheckReportNonPublicExtraRSP(NewAfterLeaseCheckReportExtra reportExtra) {
        AfterLeaseCheckReportNonPublicExtraRSP rsp = new AfterLeaseCheckReportNonPublicExtraRSP();
        rsp.setId(reportExtra.getId());
        rsp.setTemplateId(reportExtra.getTemplateId());
        rsp.setTemplateTitle(reportExtra.getTemplateTitle());
        rsp.setCheckResult(reportExtra.getCheckResult());
        rsp.setRemark(reportExtra.getRemark());
        return rsp;
    }

    public static NewAfterLeaseCheckReportExtra toAfterLeaseCheckReportExtra(Long checkPlanClientId, AfterLeaseCheckReportNonPublicExtraREQ.Data data) {
        NewAfterLeaseCheckReportExtra extra = new NewAfterLeaseCheckReportExtra();
        extra.setCheckPlanClientId(checkPlanClientId);
        extra.setId(data.getId());
        extra.setTemplateId(data.getTemplateId());
        extra.setCheckResult(data.getCheckResult());
        extra.setRemark(data.getRemark());
        return extra;
    }

    public static NewAfterLeaseCheckReportBase toAfterLeaseCheckReportBase(AfterLeaseCheckReportBaseREQ req) {
        NewAfterLeaseCheckReportBase reportBase = new NewAfterLeaseCheckReportBase();
        reportBase.setId(req.getId());
        reportBase.setCheckPlanClientId(req.getCheckPlanClientId());
        reportBase.setClientName(req.getClientName());
        reportBase.setIndustry(req.getIndustry());
        if (Objects.nonNull(req.getCheckPeriodStart())) {
            reportBase.setCheckPeriodStart(LocalDateTimeUtil.parseDate(req.getCheckPeriodStart(), DatePattern.NORM_DATE_PATTERN));
        }
        if (Objects.nonNull(req.getCheckPeriodEnd())) {
            reportBase.setCheckPeriodEnd(LocalDateTimeUtil.parseDate(req.getCheckPeriodEnd(), DatePattern.NORM_DATE_PATTERN));
        }
        reportBase.setMainPerson(req.getMainPerson());
        reportBase.setJob(req.getMainPersonJob());
        reportBase.setContactWay(req.getMainPersonContactWay());
        reportBase.setContractAmount(req.getContractAmount());
        reportBase.setRiskExposure(req.getRiskExposure());
        reportBase.setDeadline(LocalDateTimeUtil.parseDate(req.getDeadline(), DatePattern.NORM_DATE_PATTERN));
        reportBase.setClientName(req.getClientName());
        reportBase.setNextRepayAmount(req.getNextRepayAmount());
        if (Objects.nonNull(req.getNextRepayDate())) {
            reportBase.setNextRepayDate(LocalDateTimeUtil.parseDate(req.getNextRepayDate(), DatePattern.NORM_DATE_PATTERN));
        }
        return reportBase;
    }

    public static NewAfterLeaseCheckReportContent toAfterLeaseCheckReportContent(Long checkPlanProjectId, NewAfterLeaseCheckReportTemplate template) {
        NewAfterLeaseCheckReportContent rc = new NewAfterLeaseCheckReportContent();
        rc.setCheckPlanClientId(checkPlanProjectId);
        rc.setTemplateId(template.getId());
        rc.setTemplateCode(template.getCode());
        rc.setTemplateGroupName(template.getGroupName());
        rc.setTemplateTitle(template.getTitle());
        rc.setTemplateContentInputLabel(template.getContentInputLabel());
        rc.setTemplateContentInputType(template.getContentInputType());
        rc.setTemplateContentInputOption(template.getContentInputOption());
        rc.setTemplateOrderNum(template.getOrderNum());
        return rc;
    }

    public static NewAfterLeaseCheckReportSummary toAfterLeaseCheckReportSummary(Long checkPlanClientId, NewAfterLeaseCheckReportTemplate template) {
        NewAfterLeaseCheckReportSummary rs = new NewAfterLeaseCheckReportSummary();
        rs.setCheckPlanClientId(checkPlanClientId);
        rs.setTemplateId(template.getId());
        rs.setTemplateCode(template.getCode());
        rs.setTemplateGroupName(template.getGroupName());
        rs.setTemplateTitle(template.getTitle());
        rs.setTemplateContentInputLabel(template.getContentInputLabel());
        rs.setTemplateContentInputType(template.getContentInputType());
        rs.setTemplateContentInputOption(template.getContentInputOption());
        rs.setTemplateOrderNum(template.getOrderNum());
        return rs;
    }

    public static NewAfterLeaseCheckReportExtra toAfterLeaseCheckReportExtra(Long checkPlanClientId, NewAfterLeaseCheckReportTemplate template) {
        NewAfterLeaseCheckReportExtra re = new NewAfterLeaseCheckReportExtra();
        re.setCheckPlanClientId(checkPlanClientId);
        re.setTemplateId(template.getId());
        re.setTemplateCode(template.getCode());
        re.setTemplateTitle(template.getTitle());
        re.setTemplateOrderNum(template.getOrderNum());
        return re;
    }

    public static List<AfterLeaseCheckReportCSRSP> toAfterLeaseCheckReportCSRSPListFromContentData(List<NewAfterLeaseCheckReportContent> reportContentList) {
//        LinkedHashMap<String, List<NewAfterLeaseCheckReportContent>> map = new LinkedHashMap<>();
//        for (NewAfterLeaseCheckReportContent reportContent : reportContentList) {
//            List<NewAfterLeaseCheckReportContent> list = map.get(reportContent.getTemplateGroupName());
//            if (Objects.isNull(list)) {
//                list = new LinkedList<>();
//                map.put(reportContent.getTemplateGroupName(), list);
//            }
//            list.add(reportContent);
//        }
//        List<AfterLeaseCheckReportCSRSP> result = new ArrayList<>(map.size());
//        for (Map.Entry<String, List<NewAfterLeaseCheckReportContent>> entry : map.entrySet()) {
//            String groupName = entry.getKey();
//            List<NewAfterLeaseCheckReportContent> contentList = entry.getValue();
//            AfterLeaseCheckReportCSRSP rsp = new AfterLeaseCheckReportCSRSP();
//            rsp.setGroupName(groupName);
//            List<AfterLeaseCheckReportCSRSP.Content> contentRSPList = new ArrayList<>(contentList.size());
//            for (NewAfterLeaseCheckReportContent checkProjectReportContent : contentList) {
//                AfterLeaseCheckReportCSRSP.Content content = new AfterLeaseCheckReportCSRSP.Content();
//                content.setId(checkProjectReportContent.getId());
//                content.setTemplateId(checkProjectReportContent.getTemplateId());
//                content.setTemplateCode(checkProjectReportContent.getTemplateCode());
//                content.setTemplateTitle(checkProjectReportContent.getTemplateTitle());
//                content.setTemplateContentInputLabel(checkProjectReportContent.getTemplateContentInputLabel());
//                content.setTemplateContentInputType(checkProjectReportContent.getTemplateContentInputType());
//                content.setTemplateOptionList(JSONUtil.toBean(checkProjectReportContent.getTemplateContentInputOption(), new TypeReference<List<OptionRSP>>() {
//                }, false));
//                content.setContent(checkProjectReportContent.getContent());
//                contentRSPList.add(content);
//            }
//            rsp.setContentList(contentRSPList);
//            result.add(rsp);
//        }
//        return result;
        return null;
    }

    public static List<AfterLeaseCheckReportCSRSP> toAfterLeaseCheckReportCSRSPListFromSummaryData(List<NewAfterLeaseCheckReportSummary> reportSummaryList) {
//        LinkedHashMap<String, List<NewAfterLeaseCheckReportSummary>> map = new LinkedHashMap<>();
//        for (NewAfterLeaseCheckReportSummary reportSummary : reportSummaryList) {
//            List<NewAfterLeaseCheckReportSummary> list = map.get(reportSummary.getTemplateGroupName());
//            if (Objects.isNull(list)) {
//                list = new LinkedList<>();
//                map.put(reportSummary.getTemplateGroupName(), list);
//            }
//            list.add(reportSummary);
//        }
//        List<AfterLeaseCheckReportCSRSP> result = new ArrayList<>(map.size());
//        for (Map.Entry<String, List<NewAfterLeaseCheckReportSummary>> entry : map.entrySet()) {
//            String groupName = entry.getKey();
//            List<NewAfterLeaseCheckReportSummary> contentList = entry.getValue();
//            AfterLeaseCheckReportCSRSP rsp = new AfterLeaseCheckReportCSRSP();
//            rsp.setGroupName(groupName);
//            List<AfterLeaseCheckReportCSRSP.Content> contentRSPList = new ArrayList<>(contentList.size());
//            for (NewAfterLeaseCheckReportSummary checkProjectReportSummary : contentList) {
//                AfterLeaseCheckReportCSRSP.Content content = new AfterLeaseCheckReportCSRSP.Content();
//                content.setId(checkProjectReportSummary.getId());
//                content.setTemplateId(checkProjectReportSummary.getTemplateId());
//                content.setTemplateCode(checkProjectReportSummary.getTemplateCode());
//                content.setTemplateTitle(checkProjectReportSummary.getTemplateTitle());
//                content.setTemplateContentInputLabel(checkProjectReportSummary.getTemplateContentInputLabel());
//                content.setTemplateContentInputType(checkProjectReportSummary.getTemplateContentInputType());
//                content.setTemplateOptionList(JSONUtil.toBean(checkProjectReportSummary.getTemplateContentInputOption(), new TypeReference<List<OptionRSP>>() {
//                }, false));
//                content.setContent(checkProjectReportSummary.getContent());
//                contentRSPList.add(content);
//            }
//            rsp.setContentList(contentRSPList);
//            result.add(rsp);
//        }
//        return result;
        return null;
    }
}
