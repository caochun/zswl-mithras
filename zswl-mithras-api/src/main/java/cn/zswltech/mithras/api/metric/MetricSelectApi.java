package cn.zswltech.mithras.api.metric;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SelectRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

/**
 * @author yibin
 */
@Api("风险指标所有下拉枚举接口")
public interface MetricSelectApi {

    @ApiOperation("所有下拉")
    @GetMapping("/risk/metric/select")
    R<Map<String, List<SelectRSP>>> allSelect();
}
