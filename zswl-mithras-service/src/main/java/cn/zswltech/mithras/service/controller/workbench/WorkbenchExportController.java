package cn.zswltech.mithras.service.controller.workbench;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.workbench.WorkbenchExportApi;
import cn.zswltech.mithras.dto.workbench.*;
import cn.zswltech.mithras.common.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.common.enums.ProjectBizType;
import cn.zswltech.mithras.service.enums.contract.ContractStatus;
import cn.zswltech.mithras.service.enums.projlifecycle.ProjStageEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author dingqi
 * @date 2024/4/2
 * @description
 */
@RestController
public class WorkbenchExportController implements WorkbenchExportApi {
    @Resource
    private WorkbenchChartMetricController workbenchChartMetricController;
    @Resource
    private HttpServletResponse httpServletResponse;

    @Override
    public void clientListExport(@Valid CardListExportReq req) throws Exception {
        if (Objects.equals(req.getIsAll(), YesOrNoNumberEnum.YES.getCode())) {
            req.setPage(1);
            req.setPageSize(10000);
        }
        R<PageR<ClientProjectListRSP>> result = workbenchChartMetricController.clientList(req);
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
                    Util.toYuan(rsp.getApplyCreditAmount(), true),
                    Util.toYuan(rsp.getContractAmount(), true),
                    Optional.ofNullable(ProjStageEnum.find(rsp.getProjStage())).map(ProjStageEnum::display).orElse(""),
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
        R<PageR<NewProjectListRSP>> result = workbenchChartMetricController.projList(req);
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
                    Util.toYuan(rsp.getStockRiskExposure(), true),
                    Util.toYuan(rsp.getApplyCreditAmount(), true),
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
        R<PageR<NewReviewListRSP>> result = workbenchChartMetricController.reviewList(req);
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
                    Util.toYuan(rsp.getStockRiskExposure(), true),
                    Util.toYuan(rsp.getApplyCreditAmount(), true),
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
        R<PageR<NewPaymentListRSP>> result = workbenchChartMetricController.paymentList(req);
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
                    Util.toYuan(rsp.getContractAmount(), true),
                    Optional.ofNullable(ContractStatus.of(rsp.getContractStatus())).map(ContractStatus::display).orElse(""),
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
        R<PageR<ProjInfoListRSP>> result = workbenchChartMetricController.overdueList(req);
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
                    Util.toYuan(rsp.getStockRiskExposure(), true),
                    Util.toYuan(rsp.getContractAmount(), true),
                    Util.toYuan(rsp.getLastPrincipal(), true),
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
        R<PageR<ProjInfoListRSP>> result = workbenchChartMetricController.undesirableList(req);
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
                    Util.toYuan(rsp.getStockRiskExposure(), true),
                    Util.toYuan(rsp.getContractAmount(), true),
                    Util.toYuan(rsp.getLastPrincipal(), true),
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
        R<List<ProjectVO>> result = workbenchChartMetricController.reviewList(req);
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
        R<PageR<PaymentDetailRsp>> result = workbenchChartMetricController.launchList(req);
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
                    Util.toYuan(rsp.getPaidAmount(), true),
                    Util.toYuan(rsp.getRemainAmount(), true),
                    Optional.ofNullable(rsp.getPlanPayDate()).map(e -> LocalDateTimeUtil.format(rsp.getPlanPayDate(), DatePattern.NORM_DATE_PATTERN)).orElse(""),
                    Optional.ofNullable(rsp.getPaidInDate()).map(e -> LocalDateTimeUtil.format(rsp.getPaidInDate(), DatePattern.NORM_DATE_PATTERN)).orElse(""))
            );
        }
        httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("经营快报-新增投放" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
        excelWriter.flush(httpServletResponse.getOutputStream(), true);
    }
}
