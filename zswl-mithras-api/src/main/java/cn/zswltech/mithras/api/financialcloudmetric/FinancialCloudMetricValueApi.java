package cn.zswltech.mithras.api.financialcloudmetric;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.financialcloudmetric.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @description 金融云指标
* @author zhaozhengkang
* @date 2023-04-12
*/
@Api(tags = "金融云指标-接口")
public interface FinancialCloudMetricValueApi {

    @ApiOperation("金融云指标-系统计算")
    @PostMapping("/financial/cloud/metric/value/calc")
    R<Void> cacl(@RequestBody @Valid FinancialCloudMetricValueCalcREQ req);

    @ApiOperation("修改金融云指标")
    @PostMapping("/financial/cloud/metric/value/modify")
    R<Void> modify(@RequestBody @Valid List<FinancialCloudMetricValueModifyREQ> req);

    @ApiOperation("金融云指标-列表")
    @PostMapping("/financial/cloud/metric/value/list")
    R<FinancialCloudMetricValueRSP> list(@RequestBody @Valid FinancialCloudMetricValueListREQ req);

    @ApiOperation("删除金融云-批量报送")
    @PostMapping("/financial/cloud/metric/value/report")
    R<Void> report(@RequestBody @Valid FinancialCloudMetricValueCalcREQ req);

    @ApiOperation("创建金融云指标")
    @PostMapping("/financial/cloud/metric/value/create")
    R<Void> create(@RequestBody @Valid FinancialCloudMetricValueCalcREQ req);

    @ApiOperation("金融云指标-下拉列表")
    @PostMapping("/financial/cloud/metric/pulldownList")
    R<Map<String, Set<String>>> pulldownList();

    @ApiOperation("金融云指标详情")
    @PostMapping("/financial/cloud/metric/detail")
    R<FinancialCloudMetricDetailRsp> detail(@RequestBody @Valid SinglePkREQ req);

}