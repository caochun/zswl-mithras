package cn.zswltech.mithras.api.kpi;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionDeptLaunchWeightInfo;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionDeptWeightInfo;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionDeptWeightSaveREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionPrevREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * @author bigbear
 * @date 2025/4/9 14:59
 * @description
 */
@Api(tags = "绩效考核-项目分配-部门分配-比重")
@RequestMapping(path = "/kpi/project/distribution/dept/weight")
public interface KpiProjectDistributionDeptWeightApi {

    @ApiOperation(value = "绩效考核-项目分配-部门分配比重-保存")
    @PostMapping(path = "/save")
    R<Void> saveDept(@RequestBody @Valid KpiProjectDistributionDeptWeightSaveREQ req);

    @PostMapping(path = "/prev")
    @ApiOperation(value = "绩效考核-项目分配-部门分配比重-查看上个版本")
    R<List<KpiProjectDistributionDeptWeightInfo>> prev(@RequestBody @Valid KpiProjectDistributionPrevREQ req);

    @PostMapping(path = "/launchPrev")
    @ApiOperation(value = "绩效考核-项目投放分配比-查看上个版本")
    R<List<KpiProjectDistributionDeptLaunchWeightInfo>> launchPrev(@RequestBody @Valid KpiProjectDistributionPrevREQ req);

}
