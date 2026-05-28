package cn.zswltech.mithras.api.financeprojectdistribution;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.financeprojectdistribution.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
* @description 财务-部门-项目投放分配比重表
* @author hspcadmin
* @date 2025-09-29
*/
@Api(tags = "财务-部门-项目投放分配比重表-接口")
public interface FinanceProjectDistributionDeptLaunchWeightApi {

    @ApiOperation("新增-部门-项目投放分配比重表")
    @PostMapping("/finance/project/distribution/dept/launch/weight/add")
    R<Void> add(@RequestBody @Valid FinanceProjectDistributionDeptLaunchWeightAddREQ req);

    @ApiOperation("修改-部门-项目投放分配比重表")
    @PostMapping("/finance/project/distribution/dept/launch/weight/modify")
    R<Void> modify(@RequestBody @Valid FinanceProjectDistributionDeptLaunchWeightModifyREQ req);

    @ApiOperation("财务-部门-项目投放分配比重表列表")
    @PostMapping("/finance/project/distribution/dept/launch/weight/list")
    R<PageR<FinanceProjectDistributionDeptLaunchWeightListRSP>> list(@RequestBody @Valid FinanceProjectDistributionDeptLaunchWeightListREQ req);

    @ApiOperation("删除-部门-项目投放分配比重表")
    @PostMapping("/finance/project/distribution/dept/launch/weight/remove")
    R<Void> remove(@RequestBody @Valid FinanceProjectDistributionDeptLaunchWeightRemoveREQ req);

}