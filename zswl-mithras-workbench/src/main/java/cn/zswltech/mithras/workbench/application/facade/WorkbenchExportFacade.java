package cn.zswltech.mithras.workbench.application.facade;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.workbench.*;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.workbench.application.WorkbenchChartMetricApplicationService;
import cn.zswltech.mithras.workbench.application.WorkbenchExportApplicationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author dingqi
 * @date 2024/4/2
 * @description
 */
@Service
public class WorkbenchExportFacade implements WorkbenchExportApplicationService {
    private static final Map<String, String> PROJECT_STAGE_DISPLAY = new HashMap<>();
    private static final Map<String, String> CONTRACT_STATUS_DISPLAY = new HashMap<>();

    static {
        PROJECT_STAGE_DISPLAY.put("PROJESTABLISH_STAGE", "立项阶段");
        PROJECT_STAGE_DISPLAY.put("PROJREVIEW_STAGE", "评审阶段");
        PROJECT_STAGE_DISPLAY.put("CONTRACT_STAGE", "合同与投放阶段");
        PROJECT_STAGE_DISPLAY.put("CONTRACTSETTLE_STAGE", "结清阶段");

        CONTRACT_STATUS_DISPLAY.put("NEW", "新建");
        CONTRACT_STATUS_DISPLAY.put("CLOSED", "已关闭");
        CONTRACT_STATUS_DISPLAY.put("INVALID", "作废");
        CONTRACT_STATUS_DISPLAY.put("SETTLE", "结清");
        CONTRACT_STATUS_DISPLAY.put("START_RENT", "起租");
        CONTRACT_STATUS_DISPLAY.put("TAKE_EFFECT", "生效");
    }

    @Resource
    private WorkbenchChartMetricApplicationService workbenchChartMetricApplicationService;
    @Resource
    private HttpServletResponse httpServletResponse;

    @Override
    public void clientListExport(@Valid CardListExportReq req) throws Exception {
        if (Objects.equals(req.getIsAll(), YesOrNoNumberEnum.YES.getCode())) {
            req.setPage(1);
            req.setPageSize(10000);
        }
        R<PageR<ClientProjectListRSP>> result = workbenchChartMetricApplicationService.clientList(req);
        if (Objects.isNull(result) || Objects.isNull(result.getData()) || CollectionUtil.isEmpty(result.getData().getList())) {
            throw new MithrasException("暂无数据");
        }
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        excelWriter.writeHeadRow(ListUtil.of(
                "客户名称",
                "项目名称",
                "申报授信金额（元）",
                "合同金额（元）",
                "业务阶段",
                "业务类型",
                "项目部门",
                "项目主办")
        );
        for (ClientProjectListRSP rsp : result.getData().getList()) {
            excelWriter.writeRow(ListUtil.of(
                    rsp.getClientName(),
                    rsp.getProjectName(),
                    toYuan(rsp.getApplyCreditAmount()),
                    toYuan(rsp.getContractAmount()),
                    projectStageDisplay(rsp.getProjStage()),
                    rsp.getBizType(),
                    rsp.getBizDeptName(),
                    rsp.getProjSponsorUserName())
            );
        }
        httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("本年新增客户" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
        excelWriter.flush(httpServletResponse.getOutputStream(), true);
    }

    @Override
    public void projListExport(@Valid CardListExportReq req) throws Exception {
        if (Objects.equals(req.getIsAll(), YesOrNoNumberEnum.YES.getCode())) {
            req.setPage(1);
            req.setPageSize(10000);
        }
        R<PageR<NewProjectListRSP>> result = workbenchChartMetricApplicationService.projList(req);
        if (Objects.isNull(result) || Objects.isNull(result.getData()) || CollectionUtil.isEmpty(result.getData().getList())) {
            throw new MithrasException("暂无数据");
        }
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        excelWriter.writeHeadRow(ListUtil.of(
                "项目名称",
                "客户名称",
                "存量风险敞口（元）",
                "申报授信金额（元）",
                "业务类型",
                "业务部门",
                "项目主办")
        );
        for (NewProjectListRSP rsp : result.getData().getList()) {
            excelWriter.writeRow(ListUtil.of(
                    rsp.getProjectName(),
                    rsp.getClientName(),
                    toYuan(rsp.getStockRiskExposure()),
                    toYuan(rsp.getApplyCreditAmount()),
                    Optional.ofNullable(ProjectBizType.of(rsp.getBizType())).map(ProjectBizType::display).orElse(""),
                    rsp.getBizDeptName(),
                    rsp.getProjSponsorUserName())
            );
        }
        httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        if (Objects.nonNull(req.getIsYear()) && req.getIsYear()) {
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("本年新增立项数" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
        } else {
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("本月新增立项数" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
        }
        excelWriter.flush(httpServletResponse.getOutputStream(), true);
    }

    @Override
    public void reviewList(@Valid CardListExportReq req) throws Exception {
        if (Objects.equals(req.getIsAll(), YesOrNoNumberEnum.YES.getCode())) {
            req.setPage(1);
            req.setPageSize(10000);
        }
        R<PageR<NewReviewListRSP>> result = workbenchChartMetricApplicationService.reviewList(req);
        if (Objects.isNull(result) || Objects.isNull(result.getData()) || CollectionUtil.isEmpty(result.getData().getList())) {
            throw new MithrasException("暂无数据");
        }
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        excelWriter.writeHeadRow(ListUtil.of(
                "项目名称",
                "客户名称",
                "存量风险敞口（元）",
                "申报授信金额（元）",
                "业务类型",
                "业务部门",
                "项目主办")
        );
        for (NewReviewListRSP rsp : result.getData().getList()) {
            excelWriter.writeRow(ListUtil.of(
                    rsp.getProjectName(),
                    rsp.getClientName(),
                    toYuan(rsp.getStockRiskExposure()),
                    toYuan(rsp.getApplyCreditAmount()),
                    Optional.ofNullable(ProjectBizType.of(rsp.getBizType())).map(ProjectBizType::display).orElse(""),
                    rsp.getBizDeptName(),
                    rsp.getProjSponsorUserName())
            );
        }
        httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("本年新增评审数" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
        excelWriter.flush(httpServletResponse.getOutputStream(), true);
    }

    @Override
    public void paymentList(@Valid CardListExportReq req) throws Exception {
        if (Objects.equals(req.getIsAll(), YesOrNoNumberEnum.YES.getCode())) {
            req.setPage(1);
            req.setPageSize(10000);
        }
        R<PageR<NewPaymentListRSP>> result = workbenchChartMetricApplicationService.paymentList(req);
        if (Objects.isNull(result) || Objects.isNull(result.getData()) || CollectionUtil.isEmpty(result.getData().getList())) {
            throw new MithrasException("暂无数据");
        }
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        excelWriter.writeHeadRow(ListUtil.of(
                "合同编号",
                "项目名称",
                "客户名称",
                "合同金额（元）",
                "合同状态",
                "起租日期",
                "业务类型",
                "业务部门",
                "项目主办")
        );
        for (NewPaymentListRSP rsp : result.getData().getList()) {
            excelWriter.writeRow(ListUtil.of(
                    rsp.getContractCode(),
                    rsp.getProjectName(),
                    rsp.getClientName(),
                    toYuan(rsp.getContractAmount()),
                    contractStatusDisplay(rsp.getContractStatus()),
                    Optional.ofNullable(rsp.getActualLeaseDate()).map(e -> LocalDateTimeUtil.format(e, DatePattern.NORM_DATE_PATTERN)).orElse(""),
                    Optional.ofNullable(ProjectBizType.of(rsp.getBizType())).map(ProjectBizType::display).orElse(""),
                    rsp.getBizDeptName(),
                    rsp.getProjSponsorUserName())
            );
        }
        httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        if (Objects.nonNull(req.getIsYear()) && req.getIsYear()) {
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("本年累计投放金额" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
        } else {
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("本月累计投放金额" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
        }
        excelWriter.flush(httpServletResponse.getOutputStream(), true);
    }

    @Override
    public void overdueList(@Valid CardListExportReq req) throws Exception {
        if (Objects.equals(req.getIsAll(), YesOrNoNumberEnum.YES.getCode())) {
            req.setPage(1);
            req.setPageSize(10000);
        }
        R<PageR<ProjInfoListRSP>> result = workbenchChartMetricApplicationService.overdueList(req);
        if (Objects.isNull(result) || Objects.isNull(result.getData()) || CollectionUtil.isEmpty(result.getData().getList())) {
            throw new MithrasException("暂无数据");
        }
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        excelWriter.writeHeadRow(ListUtil.of(
                "项目名称",
                "客户名称",
                "存量风险敞口（元）",
                "合同金额（元）",
                "剩余本金（元）",
                "业务类型",
                "业务部门",
                "项目主办")
        );
        for (ProjInfoListRSP rsp : result.getData().getList()) {
            excelWriter.writeRow(ListUtil.of(
                    rsp.getProjectName(),
                    rsp.getClientName(),
                    toYuan(rsp.getStockRiskExposure()),
                    toYuan(rsp.getContractAmount()),
                    toYuan(rsp.getLastPrincipal()),
                    Optional.ofNullable(ProjectBizType.of(rsp.getBizType())).map(ProjectBizType::display).orElse(""),
                    rsp.getBizDeptName(),
                    rsp.getProjSponsorUserName())
            );
        }
        httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("逾期项目数" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
        excelWriter.flush(httpServletResponse.getOutputStream(), true);
    }

    @Override
    public void undesirableList(@Valid CardListExportReq req) throws Exception {
        if (Objects.equals(req.getIsAll(), YesOrNoNumberEnum.YES.getCode())) {
            req.setPage(1);
            req.setPageSize(10000);
        }
        R<PageR<ProjInfoListRSP>> result = workbenchChartMetricApplicationService.undesirableList(req);
        if (Objects.isNull(result) || Objects.isNull(result.getData()) || CollectionUtil.isEmpty(result.getData().getList())) {
            throw new MithrasException("暂无数据");
        }
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        excelWriter.writeHeadRow(ListUtil.of(
                "项目名称",
                "客户名称",
                "存量风险敞口（元）",
                "合同金额（元）",
                "剩余本金（元）",
                "业务类型",
                "业务部门",
                "项目主办")
        );
        for (ProjInfoListRSP rsp : result.getData().getList()) {
            excelWriter.writeRow(ListUtil.of(
                    rsp.getProjectName(),
                    rsp.getClientName(),
                    toYuan(rsp.getStockRiskExposure()),
                    toYuan(rsp.getContractAmount()),
                    toYuan(rsp.getLastPrincipal()),
                    Optional.ofNullable(ProjectBizType.of(rsp.getBizType())).map(ProjectBizType::display).orElse(""),
                    rsp.getBizDeptName(),
                    rsp.getProjSponsorUserName())
            );
        }
        httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("不良项目数" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
        excelWriter.flush(httpServletResponse.getOutputStream(), true);
    }

//    public static void main(String[] args) {
//        int size = 1;
//        int page = 1;
//        int pageSize = 5;
//        int startIndex = Math.min((page - 1) * pageSize, size - 1);
//        int endIndex = Math.min((page * pageSize - 1), size - 1);
//        System.out.println(startIndex + " " + endIndex);
//    }

    @Override
    public void reviewList(@Valid ProjectMetricExportReq req) throws Exception {
        R<List<ProjectVO>> result = workbenchChartMetricApplicationService.reviewList(req);
        if (Objects.isNull(result) || Objects.isNull(result.getData()) || CollectionUtil.isEmpty(result.getData())) {
            throw new MithrasException("暂无数据");
        }
        List<ProjectVO> list = result.getData();
        if (Objects.equals(req.getIsAll(), YesOrNoNumberEnum.NO.getCode())) {
            int startIndex = Math.min((req.getPage() - 1) * req.getPageSize(), list.size() - 1);
            int endIndex = Math.min((req.getPage() * req.getPageSize() - 1), list.size() - 1);
            List<ProjectVO> newList = new LinkedList<>();
            for (int i = startIndex; i <= endIndex; i++) {
                newList.add(list.get(i));
            }
            list = newList;
        }
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        excelWriter.writeHeadRow(ListUtil.of(
                "审批通过时间",
                "项目名称",
                "主办",
                "协办",
                "部门",
                "业务类型",
                "授信金额（万元）")
        );
        for (ProjectVO rsp : list) {
            excelWriter.writeRow(ListUtil.of(
                    Optional.ofNullable(rsp.getApproveTime()).map(e -> LocalDateTimeUtil.format(e, DatePattern.NORM_DATETIME_PATTERN)),
                    rsp.getProjectName(),
                    rsp.getProjectOrganizer(),
                    rsp.getProjectCoOrganizer(),
                    rsp.getDeptName(),
                    rsp.getBizType(),
                    rsp.getApplyCreditAmount())
            );
        }
        httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("经营快报-新增项目评审" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
        excelWriter.flush(httpServletResponse.getOutputStream(), true);
    }

    @Override
    public void launchList(@Valid ProjectMetricExportReq req) throws Exception {
        if (Objects.equals(req.getIsAll(), YesOrNoNumberEnum.YES.getCode())) {
            req.setPage(1);
            req.setPageSize(10000);
        }
        R<PageR<PaymentDetailRsp>> result = workbenchChartMetricApplicationService.launchList(req);
        if (Objects.isNull(result) || Objects.isNull(result.getData()) || CollectionUtil.isEmpty(result.getData().getList())) {
            throw new MithrasException("暂无数据");
        }
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        excelWriter.writeHeadRow(ListUtil.of(
                "合同编号",
                "项目名称",
                "类别",
                "业务部门",
                "投放金额（元）",
                "剩余未付金额（元）",
                "应付日期",
                "实付日期")
        );
        for (PaymentDetailRsp rsp : result.getData().getList()) {
            excelWriter.writeRow(ListUtil.of(
                    rsp.getContractCode(),
                    rsp.getProjectName(),
                    rsp.getBizType(),
                    rsp.getBizDeptName(),
                    toYuan(rsp.getPaidAmount()),
                    toYuan(rsp.getRemainAmount()),
                    Optional.ofNullable(rsp.getPlanPayDate()).map(e -> LocalDateTimeUtil.format(rsp.getPlanPayDate(), DatePattern.NORM_DATE_PATTERN)).orElse(""),
                    Optional.ofNullable(rsp.getPaidInDate()).map(e -> LocalDateTimeUtil.format(rsp.getPaidInDate(), DatePattern.NORM_DATE_PATTERN)).orElse(""))
            );
        }
        httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("经营快报-新增投放" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
        excelWriter.flush(httpServletResponse.getOutputStream(), true);
    }

    private String toYuan(Long amount) {
        if (Objects.isNull(amount)) {
            return null;
        }
        BigDecimal yuan = NumberUtil.div(amount.toString(), String.valueOf(Long.parseLong(GlobalConstants.MONEY_MULTIPLE)));
        return NumberUtil.decimalFormat(",##0.00##", yuan);
    }

    private String projectStageDisplay(String code) {
        return Optional.ofNullable(PROJECT_STAGE_DISPLAY.get(code)).orElse("");
    }

    private String contractStatusDisplay(String code) {
        return Optional.ofNullable(CONTRACT_STATUS_DISPLAY.get(code)).orElse("");
    }
}
