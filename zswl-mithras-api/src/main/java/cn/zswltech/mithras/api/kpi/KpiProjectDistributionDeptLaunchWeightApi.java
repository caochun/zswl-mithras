package cn.zswltech.mithras.api.kpi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionDeptLaunchWeightAddREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionDeptLaunchWeightModifyREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionDeptLaunchWeightListREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionDeptLaunchWeightListRSP;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionDeptLaunchWeightRemoveREQ;

/**
* @description 绩效考核-部门-项目投放分配比重表
* @author hspcadmin
* @date 2025-09-29
*/
@Api(tags = "绩效考核-部门-项目投放分配比重表-接口")
public interface KpiProjectDistributionDeptLaunchWeightApi {

    @ApiOperation("新增绩效考核-部门-项目投放分配比重表")
    @PostMapping("/kpi/project/distribution/dept/launch/weight/add")
    R<Void> add(@RequestBody @Valid KpiProjectDistributionDeptLaunchWeightAddREQ req);

    @ApiOperation("修改绩效考核-部门-项目投放分配比重表")
    @PostMapping("/kpi/project/distribution/dept/launch/weight/modify")
    R<Void> modify(@RequestBody @Valid KpiProjectDistributionDeptLaunchWeightModifyREQ req);

    @ApiOperation("绩效考核-部门-项目投放分配比重表列表")
    @PostMapping("/kpi/project/distribution/dept/launch/weight/list")
    R<PageR<KpiProjectDistributionDeptLaunchWeightListRSP>> list(@RequestBody @Valid KpiProjectDistributionDeptLaunchWeightListREQ req);

    @ApiOperation("删除绩效考核-部门-项目投放分配比重表")
    @PostMapping("/kpi/project/distribution/dept/launch/weight/remove")
    R<Void> remove(@RequestBody @Valid KpiProjectDistributionDeptLaunchWeightRemoveREQ req);

}