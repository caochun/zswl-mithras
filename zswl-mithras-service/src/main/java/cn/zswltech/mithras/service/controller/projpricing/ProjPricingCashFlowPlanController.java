package cn.zswltech.mithras.service.controller.projpricing;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.projpricing.ProjPricingCashFlowPlanApi;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.projpricing.cashflowplan.ProjPricingCashFlowPlanExportREQ;
import cn.zswltech.mithras.dto.projpricing.cashflowplan.ProjPricingCashFlowPlanListCompareRSP;
import cn.zswltech.mithras.dto.projpricing.cashflowplan.ProjPricingCashFlowPlanListREQ;
import cn.zswltech.mithras.dto.projpricing.cashflowplan.ProjPricingCashFlowPlanListRSP;
import cn.zswltech.mithras.dto.projreview.cashflowplan.IRRCalculateResultRSP;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonAddSubAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonModifyMainAuthCheckerNew;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingCashFlowPlan;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingLeasePrice;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewCashFlowPlan;
import cn.zswltech.mithras.service.others.LackDataException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.projpricing.ProjPricingBaseInfoService;
import cn.zswltech.mithras.service.service.projpricing.ProjPricingCashFlowPlanService;
import cn.zswltech.mithras.service.service.projpricing.ProjPricingLeasePriceService;
import cn.zswltech.mithras.service.service.projpricing.ProjPricingPriceService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewCashFlowPlanService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewMeetMinuteBaseInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@RestController
public class ProjPricingCashFlowPlanController implements ProjPricingCashFlowPlanApi {

    @Resource
    private ProjPricingCashFlowPlanService projPricingCashFlowPlanService;
    @Resource
    private ProjPricingLeasePriceService projPricingLeasePriceService;
    @Resource
    private ProjPricingPriceService projPricingPriceService;
    @Resource
    private HttpServletResponse httpServletResponse;
    @Resource
    private OssClient ossClient;
    @Resource
    private ProjReviewMeetMinuteBaseInfoService projReviewMeetMinuteBaseInfoService;
    @Resource
    private ProjPricingBaseInfoService baseInfoService;
    @Resource
    private ProjReviewCashFlowPlanService projReviewCashFlowPlanService;

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
            businessModule = BusinessModuleEnum.PROJ_PRICING
    )
    @Override
    public R<Void> upload(MultipartFile file, Long projPricingId) {
        if (Objects.isNull(projPricingId)) {
            throw new LackDataException("项目定价记录id不能为空");
        }
        try {
            projPricingCashFlowPlanService.importExcel(file.getInputStream(), projPricingId);
            return R.ok();
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导入现金流计划表发生异常", e);
            throw new MithrasException("执行数据导入发生异常");
        }
    }

    @Override
    public R<List<ProjPricingCashFlowPlanListRSP>> list(ProjPricingCashFlowPlanListREQ projPricingCashFlowPlanListREQ) {
//        if (StringUtils.isBlank(projReviewCashFlowPlanListREQ.getVersion())) {
        List<ProjPricingCashFlowPlan> list = projPricingCashFlowPlanService.listByProjPricingId(projPricingCashFlowPlanListREQ.getId(), projPricingCashFlowPlanListREQ.getVersion());
        if (CollectionUtils.isEmpty(list)) {
            return R.ok(Collections.emptyList());
        }
        List<ProjPricingCashFlowPlan> projReviewCashFlowPlanList = new ArrayList<>();
        ProjPricingLeasePrice projPricingLeasePrice = projPricingLeasePriceService.lambdaQuery().eq(ProjPricingLeasePrice::getProjectId, projPricingCashFlowPlanListREQ.getId()).one();
        if (!ObjectUtils.isEmpty(list) && !ObjectUtils.isEmpty(projPricingLeasePrice) && !ObjectUtils.isEmpty(projPricingLeasePrice.getFirstInstallmentInterest())
                && projPricingLeasePrice.getFirstInstallmentInterest() > 0) {
            //  增加零期现金流
            ProjPricingCashFlowPlan projPricingCashFlowPlan = new ProjPricingCashFlowPlan();
            projPricingCashFlowPlan.setProjectId(projPricingCashFlowPlanListREQ.getId());
            projPricingCashFlowPlan.setCashFlowDate(projPricingLeasePrice.getPlannedStartingDate());
            projPricingCashFlowPlan.setCashFlowPhase(0);
            projPricingCashFlowPlan.setRent(projPricingLeasePrice.getFirstInstallmentInterest());
            projPricingCashFlowPlan.setPrincipal(0L);
            projPricingCashFlowPlan.setInterest(projPricingLeasePrice.getFirstInstallmentInterest());
            projPricingCashFlowPlan.setRemainingPrincipal(0L);
            projReviewCashFlowPlanList.add(projPricingCashFlowPlan);
            projReviewCashFlowPlanList.addAll(list);
            list = projReviewCashFlowPlanList;
        }
        List<ProjPricingCashFlowPlanListRSP> data = new ArrayList<>(list.size());
        for (ProjPricingCashFlowPlan projPricingCashFlowPlan : list) {
            ProjPricingCashFlowPlanListRSP rsp = new ProjPricingCashFlowPlanListRSP();
            rsp.setId(projPricingCashFlowPlan.getId());
            rsp.setDate(LocalDateTimeUtil.format(projPricingCashFlowPlan.getCashFlowDate(), DatePattern.NORM_DATE_PATTERN));
            rsp.setPhase(projPricingCashFlowPlan.getCashFlowPhase());
            rsp.setCashFlowAmount(projPricingCashFlowPlan.getCashFlowAmount());
            rsp.setRent(projPricingCashFlowPlan.getRent());
            rsp.setPrincipal(projPricingCashFlowPlan.getPrincipal());
            rsp.setInterest(projPricingCashFlowPlan.getInterest());
            rsp.setRemainingPrincipal(projPricingCashFlowPlan.getRemainingPrincipal());
            data.add(rsp);
        }
        return R.ok(data);
//        } else {
//            return R.ok(projReviewCashFlowPlanLibService.list(projReviewCashFlowPlanListREQ));
//        }
    }

    @Override
    public void exportRent(ProjPricingCashFlowPlanExportREQ ProjPricingCashFlowPlanExportREQ) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("租金表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            projPricingCashFlowPlanService.exportRent(ProjPricingCashFlowPlanExportREQ, httpServletResponse.getOutputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出租金表/支付表发生未知异常[req: {}]", JSONUtil.toJsonStr(ProjPricingCashFlowPlanExportREQ), e);
            throw new MithrasException("导出租金表/支付表发生未知异常");
        }
    }

    @Override
    public void exportCashFlow(ProjPricingCashFlowPlanExportREQ ProjPricingCashFlowPlanExportREQ) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("现金流表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            projPricingCashFlowPlanService.exportCashFlow(ProjPricingCashFlowPlanExportREQ, httpServletResponse.getOutputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出现金流表发生未知异常[req: {}]", JSONUtil.toJsonStr(ProjPricingCashFlowPlanExportREQ), e);
            throw new MithrasException("导出现金流表发生未知异常");
        }
    }

    @DataAuthCheck(keyFieldName = "id", checkerClass = CommonAddSubAuthCheckerNew.class, businessModule = BusinessModuleEnum.PROJ_PRICING)
    @Override
    public R<Void> generate(SinglePkREQ singlePkREQ) {
        projPricingCashFlowPlanService.generate(singlePkREQ.getId());
        return R.ok();
    }

    @Override
    public R<IRRCalculateResultRSP> calculateIRR(SinglePkREQ singlePkREQ) {
        IRRCalculateResultRSP rsp = projPricingCashFlowPlanService.calculateIRR(singlePkREQ.getId());
        return R.ok(rsp);
    }

    @Override
    public R<List<ProjPricingCashFlowPlanListCompareRSP>> compare(@Valid ProjPricingCashFlowPlanListREQ projPricingCashFlowPlanListREQ) {
        ProjPricingBaseInfo pricingBaseInfo = baseInfoService.getById(projPricingCashFlowPlanListREQ.getId());
        if (ObjectUtil.isEmpty(pricingBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        ProjReviewBaseInfo reviewBaseInfo = SpringContextHolder.getBean(ProjReviewBaseInfoService.class).getOne(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .eq(ProjReviewBaseInfo::getProjCode, pricingBaseInfo.getProjCode())
                .notIn(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.CLOSED.name(), RecordStatus.EXPIRE.name()));
        if (ObjectUtil.isEmpty(reviewBaseInfo)) {
            return R.ok();
        }
        List<ProjPricingCashFlowPlanListCompareRSP> rsps = new ArrayList<>();
        //评审
        List<ProjReviewCashFlowPlan> projReviewCashFlowPlans = projReviewCashFlowPlanService.listByProjReviewId(reviewBaseInfo.getId(), null);
        if (ObjectUtil.isEmpty(projReviewCashFlowPlans)) {
            return R.ok();
        }
        //评审数据
        Map<Integer, ProjReviewCashFlowPlan> projReviewCashFlowPlanMap = projReviewCashFlowPlans.stream().filter(e -> ObjectUtil.isNotEmpty(e.getCashFlowPhase())).collect(Collectors.toMap(ProjReviewCashFlowPlan::getCashFlowPhase, e -> e, (a, b) -> a));
        //定价数据
        List<ProjPricingCashFlowPlan> projPricingCashFlowPlans = projPricingCashFlowPlanService.listByProjPricingId(projPricingCashFlowPlanListREQ.getId(), projPricingCashFlowPlanListREQ.getVersion());
        if (ObjectUtil.isNotEmpty(projPricingCashFlowPlans)) {
            projPricingCashFlowPlans.forEach(e -> {
                ProjPricingCashFlowPlanListCompareRSP rsp = new ProjPricingCashFlowPlanListCompareRSP();
                ProjPricingCashFlowPlanListRSP pricingRSP = BeanUtil.copyProperties(e, ProjPricingCashFlowPlanListRSP.class);
                pricingRSP.setPhase(e.getCashFlowPhase());
                pricingRSP.setDate(e.getCashFlowDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
                pricingRSP.setCashFlowAmount(e.getCashFlowAmount());
                ProjReviewCashFlowPlan projReviewCashFlowPlan = projReviewCashFlowPlanMap.get(e.getCashFlowPhase());
                rsp.setPricingDate(pricingRSP);
                rsp.setMeetMinuteDate(buildRsp(projReviewCashFlowPlan));
                compareResult(rsp);
                rsps.add(rsp);
            });

        }
        return R.ok(rsps);
    }

    private ProjPricingCashFlowPlanListRSP buildRsp(ProjReviewCashFlowPlan projReviewCashFlowPlan) {
        if (ObjectUtil.isEmpty(projReviewCashFlowPlan)) {
            return null;
        }
        ProjPricingCashFlowPlanListRSP rsp = new ProjPricingCashFlowPlanListRSP();
        rsp.setId(projReviewCashFlowPlan.getId());
        rsp.setDate(projReviewCashFlowPlan.getCashFlowDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        rsp.setPhase(projReviewCashFlowPlan.getCashFlowPhase());
        rsp.setRent(projReviewCashFlowPlan.getCashFlowAmount());
        rsp.setCashFlowAmount(projReviewCashFlowPlan.getCashFlowAmount());
        rsp.setInterest(projReviewCashFlowPlan.getInterest());
        rsp.setPrincipal(projReviewCashFlowPlan.getPrincipal());
        rsp.setRemainingPrincipal(projReviewCashFlowPlan.getRemainingPrincipal());
        return rsp;
    }

    private void compareResult(ProjPricingCashFlowPlanListCompareRSP rsp) {
        rsp.setCompareResult(Boolean.FALSE);
        if (ObjectUtil.isEmpty(rsp.getMeetMinuteDate())) {
            rsp.setCompareResult(Boolean.TRUE);
        } else if (!ObjectUtil.equals(rsp.getPricingDate().getPrincipal(), rsp.getMeetMinuteDate().getPrincipal())) {
            rsp.setCompareResult(Boolean.TRUE);
        }
    }

}
