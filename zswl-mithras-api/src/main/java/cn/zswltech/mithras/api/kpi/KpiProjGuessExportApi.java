package cn.zswltech.mithras.api.kpi;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.kpi.KpiProjGuessDetailREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjGuessIndexREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

import javax.validation.Valid;

/**
 * 绩效-项目测算表-导出接口
 * @Author jackerhe
 * @Date 2023/7/3 1:54 下午
 * @Version 1.0
 **/
@Api(tags = "绩效-项目测算表-导出接口")
public interface KpiProjGuessExportApi {

    /**
     * 绩效-项目测算表-合同列表
     **/
    @ApiOperation("绩效-项目测算表-合同列表")
    @GetMapping("/kpi/proj/guess/export/contract/list")
    R<Void> contractList(@Valid KpiProjGuessIndexREQ req);

    /**
     * 绩效-项目测算表-时间列表
     **/
    @ApiOperation("绩效-项目测算表-时间列表")
    @GetMapping("/kpi/proj/guess/export/time/list")
    R<Void> timeList(@Valid KpiProjGuessIndexREQ req);
    /**
     * 绩效-项目测算表-部门列表
     **/
    @ApiOperation("绩效-项目测算表-部门列表")
    @GetMapping("/kpi/proj/guess/export/dept/list")
    R<Void> deptList(@Valid KpiProjGuessIndexREQ req);

    /**
     * 绩效-项目测算表-人员列表
     **/
    @ApiOperation("绩效-项目测算表-人员列表")
    @GetMapping("/kpi/proj/guess/export/people/list")
    R<Void> peopleList(@Valid KpiProjGuessIndexREQ req);

    /**
     * 绩效-项目测算表-合同-时间-部门详情
     **/
    @ApiOperation("绩效-项目测算表-合同详情")
    @GetMapping("/kpi/proj/guess/export/contract/detail")
    R<Void> contractDetail(@Valid KpiProjGuessDetailREQ req);

    /**
     * 绩效-项目测算表-人员详情
     **/
    @ApiOperation("绩效-项目测算表-人员详情")
    @GetMapping("/kpi/proj/guess/export/people/detail")
    R<Void> peopleDetail(@Valid KpiProjGuessDetailREQ req);

}
