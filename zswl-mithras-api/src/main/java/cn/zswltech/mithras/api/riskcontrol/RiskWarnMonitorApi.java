package cn.zswltech.mithras.api.riskcontrol;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.riskcontrol.opinion.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author vico
 * @description 监控预警
 */
@Api(tags = "监控预警-接口")
public interface RiskWarnMonitorApi {

    /**
     * 舆情列表
     */
    @ApiOperation("监控预警-舆情列表")
    @PostMapping("/risk/warn/monitor/opinion/list")
    R<PageR<RiskWarnMonitorOpinionListRSP>> opinionList(@RequestBody @Valid RiskWarnMonitorOpinionListREQ req);

    @ApiOperation("监控预警-预警列表")
    @PostMapping("/risk/warn/monitor/warn/list")
    R<PageR<RiskWarnMonitorWarnListRSP>> warnList(@RequestBody @Valid RiskWarnMonitorWarnListREQ req);

    @ApiOperation("监控预警-预警详情")
    @PostMapping("/risk/warn/monitor/warn/detail")
    R<RiskWarnMonitorWarnDetailRSP> warnDetail(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("监控预警-统计")
    @PostMapping("/risk/warn/monitor/statistics")
    R<RiskWarnMonitorStatisticsRSP> statistics();

    @ApiOperation("监控预警-风险数量变化")
    @PostMapping("/risk/warn/monitor/quantity/change")
    R<List<RiskWarnMonitorQuantityChangeRSP>> quantityChange();

    @ApiOperation("监控预警-风险类型占比")
    @PostMapping("/risk/warn/monitor/type/change")
    R<List<RiskWarnMonitorTypeChangeRSP>> typeChange();

    @ApiOperation("预警更新信息")
    @PostMapping("/risk/warn/monitor/modify")
    R<Void> warnModify(@RequestBody @Valid RiskControlWarnModifyREQ req);

}