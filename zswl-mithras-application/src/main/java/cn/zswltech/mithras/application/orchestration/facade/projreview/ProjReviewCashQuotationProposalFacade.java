package cn.zswltech.mithras.application.orchestration.facade.projreview;

import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewCashQuotationProposalApplicationService;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowMeetMinutePlanExportREQ;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowMeetMinutePlanListREQ;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowPlanListRSP;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewCashFlowPlan;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewMeetMinuteBaseInfo;
import cn.zswltech.mithras.foundation.exception.LackDataException;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewCashFlowPlanService;
import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewLeasePriceService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewMeetMinuteBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
* @description 项目评审会议纪要表
* @author vico
* @date 2025-03-18
*/
@Service
@Slf4j
public class ProjReviewCashQuotationProposalFacade implements ProjReviewCashQuotationProposalApplicationService {

    @Resource
    private ProjReviewCashFlowPlanService projReviewCashFlowPlanService;
    @Resource
    private HttpServletResponse httpServletResponse;
    @Resource
    private ProjReviewLeasePriceService projReviewLeasePriceService;
    @Resource
    private ProjReviewMeetMinuteBaseInfoService projReviewMeetMinuteBaseInfoService;


    @Override
    public R<Void> upload(MultipartFile file, Long meetMinuteId) {
        if (Objects.isNull(meetMinuteId)) {
            throw new LackDataException("项目评审会议纪要id不能为空");
        }
        try {
            ProjReviewMeetMinuteBaseInfo meetMinuteBaseInfo = projReviewMeetMinuteBaseInfoService.getById(meetMinuteId);
            if (ObjectUtil.isEmpty(meetMinuteBaseInfo)) {
                throw new LackDataException("项目评审会议纪要为空");
            }
            projReviewCashFlowPlanService.importExcel(file.getInputStream(), meetMinuteBaseInfo.getProjReviewId(), meetMinuteId);
            return R.ok();
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导入现金流计划表发生异常", e);
            throw new MithrasException("执行数据导入发生异常");
        }
    }

    @Override
    public R<List<ProjReviewCashFlowPlanListRSP>> list(@Valid ProjReviewCashFlowMeetMinutePlanListREQ projReviewCashFlowPlanListREQ) {
        List<ProjReviewCashFlowPlan> list = projReviewCashFlowPlanService.listByProjReviewId(projReviewCashFlowPlanListREQ.getProjReviewId(), projReviewCashFlowPlanListREQ.getVersion());
        if (CollectionUtils.isEmpty(list)) {
            return R.ok(Collections.emptyList());
        }
        List<ProjReviewCashFlowPlan> projReviewCashFlowPlanList = new ArrayList<>();
        ProjReviewLeasePrice projReviewLeasePrice = projReviewLeasePriceService.lambdaQuery().eq(ProjReviewLeasePrice::getProjectId, projReviewCashFlowPlanListREQ.getProjReviewId()).one();
        if (!ObjectUtils.isEmpty(list) && !ObjectUtils.isEmpty(projReviewLeasePrice) && !ObjectUtils.isEmpty(projReviewLeasePrice.getFirstInstallmentInterest())
                && projReviewLeasePrice.getFirstInstallmentInterest() > 0) {
            //  增加零期现金流
            ProjReviewCashFlowPlan projReviewCashFlowPlan = new ProjReviewCashFlowPlan();
            projReviewCashFlowPlan.setProjectId(projReviewCashFlowPlanListREQ.getProjReviewId());
            projReviewCashFlowPlan.setCashFlowDate(projReviewLeasePrice.getPlannedStartingDate());
            projReviewCashFlowPlan.setCashFlowPhase(0);
            projReviewCashFlowPlan.setRent(projReviewLeasePrice.getFirstInstallmentInterest());
            projReviewCashFlowPlan.setPrincipal(0L);
            projReviewCashFlowPlan.setInterest(projReviewLeasePrice.getFirstInstallmentInterest());
            projReviewCashFlowPlan.setRemainingPrincipal(0L);
            projReviewCashFlowPlanList.add(projReviewCashFlowPlan);
            projReviewCashFlowPlanList.addAll(list);
            list = projReviewCashFlowPlanList;
        }
        List<ProjReviewCashFlowPlanListRSP> data = new ArrayList<>(list.size());
        for (ProjReviewCashFlowPlan projReviewCashFlowPlan : list) {
            ProjReviewCashFlowPlanListRSP rsp = new ProjReviewCashFlowPlanListRSP();
            rsp.setId(projReviewCashFlowPlan.getId());
            rsp.setDate(LocalDateTimeUtil.format(projReviewCashFlowPlan.getCashFlowDate(), DatePattern.NORM_DATE_PATTERN));
            rsp.setPhase(projReviewCashFlowPlan.getCashFlowPhase());
            rsp.setCashFlowAmount(projReviewCashFlowPlan.getCashFlowAmount());
            rsp.setRent(projReviewCashFlowPlan.getRent());
            rsp.setPrincipal(projReviewCashFlowPlan.getPrincipal());
            rsp.setInterest(projReviewCashFlowPlan.getInterest());
            rsp.setRemainingPrincipal(projReviewCashFlowPlan.getRemainingPrincipal());
            data.add(rsp);
        }
        return R.ok(data);
    }

    @Override
    public void exportRent(@Valid ProjReviewCashFlowMeetMinutePlanExportREQ projReviewCashFlowPlanExportREQ) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("租金表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            projReviewCashFlowPlanService.exportRent(projReviewCashFlowPlanExportREQ, httpServletResponse.getOutputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出租金表/支付表发生未知异常[req: {}]", JSONUtil.toJsonStr(projReviewCashFlowPlanExportREQ), e);
            throw new MithrasException("导出租金表/支付表发生未知异常");
        }
    }

    @Override
    public void exportCashFlow(@Valid ProjReviewCashFlowMeetMinutePlanExportREQ projReviewCashFlowPlanExportREQ) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("现金流表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            projReviewCashFlowPlanService.exportCashFlow(projReviewCashFlowPlanExportREQ, httpServletResponse.getOutputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出现金流表发生未知异常[req: {}]", JSONUtil.toJsonStr(projReviewCashFlowPlanExportREQ), e);
            throw new MithrasException("导出现金流表发生未知异常");
        }
    }


}