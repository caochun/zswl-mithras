package cn.zswltech.mithras.api.workbench;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.workbench.CardMetricChooseDto;
import cn.zswltech.mithras.dto.workbench.WorkBenchRoleListRsp;
import cn.zswltech.mithras.dto.workbench.WorkbenchCardMetricListRsp;
import cn.zswltech.mithras.dto.workbench.WorkbenchMetricReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description 工作台-卡片指标
 * @date 2023-05-09
 */
@Api(tags = "工作台-卡片指标-接口")
public interface WorkbenchCardMetricApi {
    @ApiOperation("工作台-当前用户角色列表")
    @GetMapping("/workbench/metric/listrole")
    R<WorkBenchRoleListRsp> listRole();

    @ApiOperation("工作台-卡片指标列表")
    @PostMapping("/workbench/card/metric/list")
    R<List<WorkbenchCardMetricListRsp>> listCardMetrics(@RequestBody @Valid WorkbenchMetricReq req);

    @ApiOperation("工作台-卡片指标配置")
    @GetMapping("/workbench/card/metric/config")
    R<List<CardMetricChooseDto>> currentUserChooseVo();

    @ApiOperation("工作台-卡片指标配置")
    @PostMapping("/workbench/card/metric/config/modify")
    R<Void> modifyChoosedCard(@RequestBody @Valid List<CardMetricChooseDto> req);
}