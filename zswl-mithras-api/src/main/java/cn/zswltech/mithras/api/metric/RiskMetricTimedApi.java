package cn.zswltech.mithras.api.metric;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.metric.timed.RiskMetricTimedListReq;
import cn.zswltech.mithras.dto.metric.timed.RiskMetricTimedListRsp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author yibin
 */
@Api("风险指标定时存数-api")
public interface RiskMetricTimedApi {

    @PostMapping("/risk/metric/timed/list")
    @ApiOperation("风险指标定时存数-列表")
    R<PageR<RiskMetricTimedListRsp>> list(@RequestBody @Valid RiskMetricTimedListReq req);

}
