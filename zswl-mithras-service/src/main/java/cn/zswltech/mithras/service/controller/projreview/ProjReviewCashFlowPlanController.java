package cn.zswltech.mithras.service.controller.projreview;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.json.JSONUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.projreview.ProjReviewCashFlowPlanApi;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.projreview.cashflowplan.IRRCalculateResultRSP;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowPlanExportREQ;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowPlanListREQ;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowPlanListRSP;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonAddSubAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonModifyMainAuthCheckerNew;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewCashFlowQuotationProposal;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.service.others.LackDataException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.projreview.ProjReviewCashFlowQuotationProposalService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewLeasePriceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RestController;
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
 * @author dingqi
 * @date 2022/8/2
 * @description
 */
@Slf4j
@RestController
public class ProjReviewCashFlowPlanController implements ProjReviewCashFlowPlanApi {

    @Resource
    private ProjReviewCashFlowQuotationProposalService projReviewCashFlowQuotationProposalService;
    @Resource
    private HttpServletResponse httpServletResponse;
    @Resource
    private OssClient ossClient;
    @Resource
    private ProjReviewLeasePriceService projReviewLeasePriceService;

    @Override
    public R<String> download() {
        try {
            /*httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(GlobalConstants.TEMPLATE_OSS_NAME_PROJ_REVIEW_CASH_FLOW, StandardCharsets.UTF_8.name()));
            ossClient.downLoad(httpServletResponse.getOutputStream(), GlobalConstants.TEMPLATE_OSS_NAME_PROJ_REVIEW_CASH_FLOW);*/
            return R.ok(ossClient.getPreviewUrl(GlobalConstants.TEMPLATE_OSS_NAME_PROJ_REVIEW_CASH_FLOW, GlobalConstants.FILE_TEMPLATE_EXPIRY));
        } catch (Exception e) {
            log.error("下载现金流计划表模板发生异常", e);
            throw new MithrasException("下载模板发生异常");
        }
    }

    @DataAuthCheck(
            checkerClass = CommonModifyMainAuthCheckerNew.class,
            paramType = DataAuthCheck.ParamType.DIRECT,
            paramIndex = 1,
            businessModule = BusinessModuleEnum.PROJ_REVIEW
    )
    @Override
    public R<Void> upload(MultipartFile file, Long projReviewId) {
        if (Objects.isNull(projReviewId)) {
            throw new LackDataException("项目评审记录id不能为空");
        }
        try {
            projReviewCashFlowQuotationProposalService.importExcel(file.getInputStream(), projReviewId);
            return R.ok();
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导入现金流计划表发生异常", e);
            throw new MithrasException("执行数据导入发生异常");
        }
    }

    @Override
    public R<List<ProjReviewCashFlowPlanListRSP>> list(ProjReviewCashFlowPlanListREQ projReviewCashFlowPlanListREQ) {
//        if (StringUtils.isBlank(projReviewCashFlowPlanListREQ.getVersion())) {
        List<ProjReviewCashFlowQuotationProposal> list = projReviewCashFlowQuotationProposalService.listByProjReviewId(projReviewCashFlowPlanListREQ.getId(), projReviewCashFlowPlanListREQ.getVersion());
        if (CollectionUtils.isEmpty(list)) {
            return R.ok(Collections.emptyList());
        }
        List<ProjReviewCashFlowQuotationProposal> projReviewCashFlowPlanList = new ArrayList<>();
        ProjReviewLeasePrice projReviewLeasePrice = projReviewLeasePriceService.lambdaQuery().eq(ProjReviewLeasePrice::getProjectId, projReviewCashFlowPlanListREQ.getId()).one();
        if (!ObjectUtils.isEmpty(list) && !ObjectUtils.isEmpty(projReviewLeasePrice) && !ObjectUtils.isEmpty(projReviewLeasePrice.getFirstInstallmentInterest())
                && projReviewLeasePrice.getFirstInstallmentInterest() > 0) {
            //  增加零期现金流
            ProjReviewCashFlowQuotationProposal projReviewCashFlowPlan = new ProjReviewCashFlowQuotationProposal();
            projReviewCashFlowPlan.setProjectId(projReviewCashFlowPlanListREQ.getId());
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
        for (ProjReviewCashFlowQuotationProposal projReviewCashFlowPlan : list) {
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
//        } else {
//            return R.ok(projReviewCashFlowPlanLibService.list(projReviewCashFlowPlanListREQ));
//        }
    }

    @Override
    public void exportRent(@Valid ProjReviewCashFlowPlanExportREQ projReviewCashFlowPlanExportREQ) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("租金表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            projReviewCashFlowQuotationProposalService.exportRent(projReviewCashFlowPlanExportREQ, httpServletResponse.getOutputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出租金表/支付表发生未知异常[req: {}]", JSONUtil.toJsonStr(projReviewCashFlowPlanExportREQ), e);
            throw new MithrasException("导出租金表/支付表发生未知异常");
        }
    }

    @Override
    public void exportCashFlow(@Valid ProjReviewCashFlowPlanExportREQ projReviewCashFlowPlanExportREQ) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("现金流表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            projReviewCashFlowQuotationProposalService.exportCashFlow(projReviewCashFlowPlanExportREQ, httpServletResponse.getOutputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出现金流表发生未知异常[req: {}]", JSONUtil.toJsonStr(projReviewCashFlowPlanExportREQ), e);
            throw new MithrasException("导出现金流表发生未知异常");
        }
    }

    @DataAuthCheck(keyFieldName = "id", checkerClass = CommonAddSubAuthCheckerNew.class, businessModule = BusinessModuleEnum.PROJ_REVIEW)
    @Override
    public R<Void> generate(@Valid SinglePkREQ singlePkREQ) {
        projReviewCashFlowQuotationProposalService.generate(singlePkREQ.getId());
        return R.ok();
    }

    //    @DataAuthCheck(keyFieldName = "id", checkerClass = CommonAddSubAuthCheckerNew.class, businessModule = BusinessModuleEnum.PROJ_REVIEW)
    @Override
    public R<IRRCalculateResultRSP> calculateIRR(@Valid SinglePkREQ singlePkREQ) {
        IRRCalculateResultRSP rsp = projReviewCashFlowQuotationProposalService.calculateIRR(singlePkREQ.getId());
        return R.ok(rsp);
    }
}
