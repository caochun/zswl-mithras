/*
package cn.zswltech.mithras.api.metric;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.metric.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

*/
/**
 * @author yibin
 *//*

@Api("风险指标模板管理")
public interface RiskMetricApi {

    @ApiOperation("添加风险指标模板")
    @PostMapping("/risk/metric/add")
    R<Long> add(@RequestBody @Valid RiskMetricAddReq req);

    @ApiOperation("删除风险指标模板")
    @PostMapping("/risk/metric/remove")
    R<Void> remove(@RequestBody @Valid RiskMetricIdReq req);

    @ApiOperation("风险指标模板详情")
    @PostMapping("/risk/metric/detail")
    R<RiskMetricDetailRsp> detail(@RequestBody @Valid RiskMetricIdReq req);

    @ApiOperation("更新风险指标模板")
    @PostMapping("/risk/metric/modify")
    R<Void> modify(@RequestBody @Valid RiskMetricModifyReq req);

    @ApiOperation("风险指标模板列表")
    @PostMapping("/risk/metric/list")
    R<PageR<RiskMetricListRsp>> list(@RequestBody @Valid RiskMetricListReq req);


}
*/
