package cn.zswltech.mithras.afterlease.application.convert;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckPlanDetailRSP;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckPlanListRSP;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckSummaryReportRSP;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckPlanBase;

/**
 * @author dingqi
 * @date 2022/11/9
 * @description
 */
public class AfterLeaseCheckPlanConvert {
    public static AfterLeaseCheckPlanListRSP toAfterLeaseCheckPlanListRSP(NewAfterLeaseCheckPlanBase newAfterLeaseCheckPlanBase) {
        AfterLeaseCheckPlanListRSP rsp = new AfterLeaseCheckPlanListRSP();
        rsp.setId(newAfterLeaseCheckPlanBase.getId());
        rsp.setPlanName(newAfterLeaseCheckPlanBase.getPlanName());
        rsp.setPlanType(newAfterLeaseCheckPlanBase.getPlanType());
        rsp.setYear(newAfterLeaseCheckPlanBase.getYear());
        rsp.setQuarter(newAfterLeaseCheckPlanBase.getQuarter());
        rsp.setMonth(newAfterLeaseCheckPlanBase.getMonth());
        rsp.setCheckStartDate(LocalDateTimeUtil.format(newAfterLeaseCheckPlanBase.getStartDate(), DatePattern.NORM_DATE_PATTERN));
        rsp.setCheckEndDate(LocalDateTimeUtil.format(newAfterLeaseCheckPlanBase.getEndDate(), DatePattern.NORM_DATE_PATTERN));
        rsp.setPlanStatus(newAfterLeaseCheckPlanBase.getPlanStatus());
        rsp.setApprovalStatus(newAfterLeaseCheckPlanBase.getApprovalStatus());
        rsp.setCreateTime(LocalDateTimeUtil.format(newAfterLeaseCheckPlanBase.getCreateTime(), DatePattern.NORM_DATETIME_MINUTE_PATTERN));
        rsp.setUpdateTime(LocalDateTimeUtil.format(newAfterLeaseCheckPlanBase.getUpdateTime(), DatePattern.NORM_DATETIME_MINUTE_PATTERN));
        rsp.setCheckWay(newAfterLeaseCheckPlanBase.getCheckWay());
        rsp.setLastCheckWay(newAfterLeaseCheckPlanBase.getLastCheckWay());
        return rsp;
    }

    public static AfterLeaseCheckPlanDetailRSP toAfterLeaseCheckPlanDetailRSP(NewAfterLeaseCheckPlanBase newAfterLeaseCheckPlanBase) {
        AfterLeaseCheckPlanDetailRSP rsp = new AfterLeaseCheckPlanDetailRSP();
        rsp.setId(newAfterLeaseCheckPlanBase.getId());
        rsp.setPlanName(newAfterLeaseCheckPlanBase.getPlanName());
        rsp.setPlanType(newAfterLeaseCheckPlanBase.getPlanType());
        rsp.setYear(newAfterLeaseCheckPlanBase.getYear());
        rsp.setQuarter(newAfterLeaseCheckPlanBase.getQuarter());
        rsp.setMonth(newAfterLeaseCheckPlanBase.getMonth());
        rsp.setCheckStartDate(LocalDateTimeUtil.format(newAfterLeaseCheckPlanBase.getStartDate(), DatePattern.NORM_DATE_PATTERN));
        rsp.setCheckEndDate(LocalDateTimeUtil.format(newAfterLeaseCheckPlanBase.getEndDate(), DatePattern.NORM_DATE_PATTERN));
        rsp.setPlanStatus(newAfterLeaseCheckPlanBase.getPlanStatus());
        return rsp;
    }

    public static AfterLeaseCheckSummaryReportRSP toAfterLeaseCheckSummaryReportRSP(MaterialsList materialsList) {
        AfterLeaseCheckSummaryReportRSP rsp = new AfterLeaseCheckSummaryReportRSP();
        rsp.setFileId(materialsList.getId());
        rsp.setFileName(materialsList.getFilename());
        rsp.setCreateTime(LocalDateTimeUtil.format(materialsList.getCreateTime(), DatePattern.NORM_DATE_PATTERN));
        return rsp;
    }
}
