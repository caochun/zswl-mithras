package cn.zswltech.mithras.api.dashboard;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author yangxiong
 * @date 2024/6/24/15:20
 * @description
 */
@Api(tags = "工作台-绩效信息")
@RequestMapping(path = "/dashboard/performance")
public interface DashboardPerformanceApi {

    @ApiOperation("我的业绩-部门业绩")
    @PostMapping(path = "/dept")
    R<DeptPerformanceRSP> deptPerformance();

    @ApiOperation("我的业绩-个人业绩")
    @PostMapping(path = "/personal")
    R<List<PersonalPerformanceRSP>> personalPerformance();

    @ApiOperation("业绩排名-部门间排名")
    @PostMapping(path = "/dept/ship/sort")
    R<KpiDeptShipSortPerformanceRSP> deptShipSortPerformance();

    @ApiOperation("业绩排名-部门内排名")
    @PostMapping(path = "/dept/in/sort")
    R<List<DeptInSortPerformanceRSP>> deptInSortPerformance();
}
