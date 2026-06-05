package cn.zswltech.mithras.workbench.interfaces.metric;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.workbench.WorkbenchChartMetricApi;
import cn.zswltech.mithras.dto.workbench.*;
import cn.zswltech.mithras.dto.workbench.chart.LineBarChartValueVO;
import cn.zswltech.mithras.workbench.application.WorkbenchChartMetricApplicationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class WorkbenchChartMetricController implements WorkbenchChartMetricApi {

    @Resource
    private WorkbenchChartMetricApplicationService workbenchChartMetricApplicationService;

    @Override
    public R<List<String>> tabs(WorkbenchMetricReq req) {
        return workbenchChartMetricApplicationService.tabs(req);
    }

    @Override
    public R<LineBarChartValueVO> barChart(WorkbenchBarMetricReq req) {
        return workbenchChartMetricApplicationService.barChart(req);
    }

    @Override
    public R<LineBarChartValueVO> fundsLiquidityChart(WorkbenchMetricReq req) {
        return workbenchChartMetricApplicationService.fundsLiquidityChart(req);
    }

    @Override
    public R<LineBarChartValueVO> deptReturnRateChart(WorkbenchMetricReq req) {
        return workbenchChartMetricApplicationService.deptReturnRateChart(req);
    }

    @Override
    public R<LineBarChartValueVO> projectTypeReturnRateChart(WorkbenchMetricReq req) {
        return workbenchChartMetricApplicationService.projectTypeReturnRateChart(req);
    }

    @Override
    public R<LineBarChartValueVO> stockPrincipalChart(WorkbenchMetricReq req) {
        return workbenchChartMetricApplicationService.stockPrincipalChart(req);
    }

    @Override
    public R<LineBarChartValueVO> releaseCollectionChart(WorkbenchMetricReq req) {
        return workbenchChartMetricApplicationService.releaseCollectionChart(req);
    }

    @Override
    public R<List<ProjectVO>> reviewList(ProjectMetricReq req) {
        return workbenchChartMetricApplicationService.reviewList(req);
    }

    @Override
    public R<PageR<PaymentDetailRsp>> launchList(ProjectMetricReq req) {
        return workbenchChartMetricApplicationService.launchList(req);
    }

    @Override
    public R<PageR<ClientProjectListRSP>> clientList(CardListReq req) {
        return workbenchChartMetricApplicationService.clientList(req);
    }

    @Override
    public R<PageR<NewProjectListRSP>> projList(CardListReq req) {
        return workbenchChartMetricApplicationService.projList(req);
    }

    @Override
    public R<PageR<NewReviewListRSP>> reviewList(CardListReq req) {
        return workbenchChartMetricApplicationService.reviewList(req);
    }

    @Override
    public R<PageR<NewPaymentListRSP>> paymentList(CardListReq req) {
        return workbenchChartMetricApplicationService.paymentList(req);
    }

    @Override
    public R<PageR<ProjInfoListRSP>> overdueList(CardListReq req) {
        return workbenchChartMetricApplicationService.overdueList(req);
    }

    @Override
    public R<PageR<ProjInfoListRSP>> undesirableList(CardListReq req) {
        return workbenchChartMetricApplicationService.undesirableList(req);
    }

    @GetMapping("/cal/test")
    public void testCalculate() {
        workbenchChartMetricApplicationService.testCalculate();
    }
}
