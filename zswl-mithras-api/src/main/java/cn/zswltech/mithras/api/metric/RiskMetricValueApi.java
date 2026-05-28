package cn.zswltech.mithras.api.metric;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.metric.value.RiskMetricValueListReq;
import cn.zswltech.mithras.dto.metric.value.RiskMetricValueListRsp;
import cn.zswltech.mithras.dto.metric.value.RiskMetricValueModifyReq;
import cn.zswltech.mithras.dto.metric.value.RiskMetricValueReportReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author yibin
 */
@Api("风险指标模板管理")
public interface RiskMetricValueApi {

    @ApiOperation("更新风险指标值")
    @PostMapping("/risk/metric/value/modify")
    R<Void> modifyValue(@RequestBody @Valid List<RiskMetricValueModifyReq> req);

    @ApiOperation("风险指标指列表")
    @PostMapping("/risk/metric/value/list")
    R<RiskMetricValueListRsp> list(@RequestBody @Valid RiskMetricValueListReq req);


    @ApiOperation("风险指标报送")
    @PostMapping("/risk/metric/value/report")
    R<Void> report(@RequestBody @Valid RiskMetricValueReportReq req);

    @ApiOperation("风险指标计算")
    @PostMapping("/risk/metric/value/calc")
    R<Void> calc(@RequestBody @Valid RiskMetricValueReportReq req);


}
