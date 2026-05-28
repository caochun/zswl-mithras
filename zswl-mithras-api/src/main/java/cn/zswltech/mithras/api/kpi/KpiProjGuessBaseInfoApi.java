package cn.zswltech.mithras.api.kpi;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.kpi.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
* 绩效-项目测算表
* @author jackerhe
* @date 2023-06-15
*/
@Api(tags = "绩效-项目测算表-接口")
public interface KpiProjGuessBaseInfoApi {

    /**
     * 绩效-项目测算表-合同列表
     **/
    @ApiOperation("绩效-项目测算表-合同列表")
    @PostMapping("/kpi/proj/guess/contract/list")
    R<PageR<KpiProjGuessContractIndexRSP>> contractList(@RequestBody @Valid KpiProjGuessIndexREQ req);

    /**
     * 绩效-项目测算表-时间列表
     **/
    @ApiOperation("绩效-项目测算表-时间列表")
    @PostMapping("/kpi/proj/guess/time/list")
    R<List<KpiProjGuessContractIndexRSP>> timeList(@RequestBody @Valid KpiProjGuessIndexREQ req);
    /**
     * 绩效-项目测算表-部门列表
     **/
    @ApiOperation("绩效-项目测算表-部门列表")
    @PostMapping("/kpi/proj/guess/dept/list")
    R<List<KpiProjGuessContractIndexRSP>> deptList(@RequestBody @Valid KpiProjGuessIndexREQ req);

    /**
     * 绩效-项目测算表-人员列表
     **/
    @ApiOperation("绩效-项目测算表-人员列表")
    @PostMapping("/kpi/proj/guess/people/list")
    R<List<KpiProjGuessPeopleIndexRSP>> peopleList(@RequestBody @Valid KpiProjGuessIndexREQ req);

    /**
     * 绩效-项目测算表-合同-时间-部门详情
     **/
    @ApiOperation("绩效-项目测算表-合同详情")
    @PostMapping("/kpi/proj/guess/contract/detail")
    R<PageR<KpiProjGuessDetailRSP>> contractDetail(@RequestBody @Valid KpiProjGuessDetailREQ req);

    /**
     * 绩效-项目测算表-人员详情
     **/
    @ApiOperation("绩效-项目测算表-人员详情")
    @PostMapping("/kpi/proj/guess/people/detail")
    R<PageR<KpiProjGuessPeopleDetailRSP>> peopleDetail(@RequestBody @Valid KpiProjGuessDetailREQ req);

    /**
     * 绩效-项目测算表-绩效测算
     **/
    @ApiOperation("绩效-项目测算表-测算")
    @PostMapping("/kpi/proj/guess/calculate")
    R<Void> calculate(@RequestBody @Valid KpiProjGuessCalculateREQ req);

    ///###新增维度

    /**
     * 绩效-项目测算表-项目经理列表
     **/
    @ApiOperation("绩效-项目测算表-项目经理列表")
    @PostMapping("/kpi/proj/guess/proj/manager/list")
    R<List<KpiProjGuessProjManageIndexRSP>> projManager(@RequestBody @Valid KpiProjGuessProjManagerREQ req);

    /**
     * 绩效-项目测算表-项目经理列表
     **/
    @ApiOperation("绩效-项目测算表-项目经理详情")
    @PostMapping("/kpi/proj/guess/proj/manager/detail")
    R<List<KpiProjGuessProjManageDetailRSP>> projManagerDetail(@RequestBody @Valid KpiProjGuessProjManagerDetailREQ req);

    /**
     * 绩效-项目测算表-项目经理利润完成率列表
     **/
    @ApiOperation("绩效-项目测算表-项目经理利润完成率列表")
    @PostMapping("/kpi/proj/guess/proj/manager/completion/list")
    R<List<KpiProjGuessProjManageCompletionIndexRSP>> projManagerCompletion(@RequestBody @Valid KpiProjGuessProjManagerREQ req);

    /**
     * 绩效-项目测算表-项目经理利润完成率详情
     **/
    @ApiOperation("绩效-项目测算表-项目经理利润完成率详情")
    @PostMapping("/kpi/proj/guess/proj/manager/completion/detail")
    R<List<KpiProjGuessProjManagerCompletionDetailRSP>> projManagerCompletionDetail(@RequestBody @Valid KpiProjGuessProjManagerDetailREQ req);


    /**
     * 绩效-项目测算表-部门池列表
     **/
    @ApiOperation("绩效-项目测算表-部门池列表")
    @PostMapping("/kpi/proj/guess/dept/pool/list")
    R<List<KpiProjGuessDeptPoolIndexRSP>> deptPool(@RequestBody @Valid KpiProjGuessDeptPoolREQ req);

    /**
     * 绩效-项目测算表-部门池详情
     **/
    @ApiOperation("绩效-项目测算表-部门池详情")
    @PostMapping("/kpi/proj/guess/dept/pool/detail")
    R<List<KpiProjGuessDeptPooleDetailRSP>> deptPoolDetail(@RequestBody @Valid KpiProjGuessDeptPoolREQ req);


}