package cn.zswltech.mithras.api.kpi;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionWeightREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionWeightRSP;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionWeightSaveREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author dingqi
 * @date 2023/6/15
 * @description
 */
@Api(tags = "绩效考核-项目分配-分配比重")
@RequestMapping(path = "/kpi/projectdistribution/weight")
public interface KpiProjectDistributionWeightApi {
    @ApiOperation("绩效考核-项目分配-分配比重-详情")
    @PostMapping(path = "/detail")
    R<KpiProjectDistributionWeightRSP> detail(@RequestBody @Valid KpiProjectDistributionWeightREQ req);

    @ApiOperation("绩效考核-项目分配-分配比重-保存")
    @PostMapping(path = "/save")
    R<Void> save(@RequestBody @Valid KpiProjectDistributionWeightSaveREQ req);

    @ApiOperation("定时任务测试")
    @ApiImplicitParam(value = "任务标识", name = "uuid")
    @GetMapping("/process/test")
    R<Void> test();
}
