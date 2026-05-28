package cn.zswltech.mithras.api.workbench;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.workbench.ProjectMetricReq;
import cn.zswltech.mithras.dto.workbench.ProjectVO;
import cn.zswltech.mithras.dto.workbench.WorkbenchMetricReq;
import cn.zswltech.mithras.dto.workbench.chart.PieChartValueVO;
import cn.zswltech.mithras.dto.workbench.chart.RadarChartValueVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/10 11:05
 */
@Api(tags = "工作台-小可视化指标-接口")
public interface WorkbenchLittleChartApi {
    @ApiOperation("工作台-当前角色table列表")
    @PostMapping("/workbench/little/chart/tabs")
    R<List<String>> tabs(@RequestBody @Valid WorkbenchMetricReq req);

    @ApiOperation("工作台-雷达图指标")
    @PostMapping("/workbench/little/chart/radar")
    R<RadarChartValueVO> radarChart(@RequestBody @Valid WorkbenchMetricReq req);

    @ApiOperation("工作台-五级分类饼图")
    @PostMapping("/workbench/little/chart/pie/fivelevel")
    R<PieChartValueVO> fiveLevelPieChart(@RequestBody @Valid WorkbenchMetricReq req);

    @ApiOperation("工作台-融资成本饼图")
    @PostMapping("/workbench/little/chart/pie/financecost")
    R<PieChartValueVO> financeCostPieChart(@RequestBody @Valid WorkbenchMetricReq req);

    @ApiOperation("工作台-项目立项列表")
    @PostMapping("/workbench/little/chart/list/projestablish")
    R<List<ProjectVO>> projEstablishList(@RequestBody @Valid ProjectMetricReq req);

    @ApiOperation("工作台-项目评审列表")
    @PostMapping("/workbench/little/chart/list/projreview")
    R<List<ProjectVO>> projReviewList(@RequestBody @Valid ProjectMetricReq req);

}
