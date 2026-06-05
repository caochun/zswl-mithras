package cn.zswltech.mithras.workbench.interfaces.metric;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.workbench.WorkbenchLittleChartApi;
import cn.zswltech.mithras.dto.workbench.ProjectMetricReq;
import cn.zswltech.mithras.dto.workbench.ProjectVO;
import cn.zswltech.mithras.dto.workbench.WorkbenchMetricReq;
import cn.zswltech.mithras.dto.workbench.chart.PieChartValueVO;
import cn.zswltech.mithras.dto.workbench.chart.RadarChartValueVO;
import cn.zswltech.mithras.workbench.application.WorkbenchLittleChartApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class WorkbenchLittleChartController implements WorkbenchLittleChartApi {

    @Resource
    private WorkbenchLittleChartApplicationService workbenchLittleChartApplicationService;

    @Override
    public R<List<String>> tabs(WorkbenchMetricReq req) {
        return workbenchLittleChartApplicationService.tabs(req);
    }

    @Override
    public R<RadarChartValueVO> radarChart(WorkbenchMetricReq req) {
        return workbenchLittleChartApplicationService.radarChart(req);
    }

    @Override
    public R<PieChartValueVO> fiveLevelPieChart(WorkbenchMetricReq req) {
        return workbenchLittleChartApplicationService.fiveLevelPieChart(req);
    }

    @Override
    public R<PieChartValueVO> financeCostPieChart(WorkbenchMetricReq req) {
        return workbenchLittleChartApplicationService.financeCostPieChart(req);
    }

    @Override
    public R<List<ProjectVO>> projEstablishList(ProjectMetricReq req) {
        return workbenchLittleChartApplicationService.projEstablishList(req);
    }

    @Override
    public R<List<ProjectVO>> projReviewList(ProjectMetricReq req) {
        return workbenchLittleChartApplicationService.projReviewList(req);
    }
}
