package cn.zswltech.mithras.api.financeprojectdistribution;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.financeprojectdistribution.FinanceProjectDistributionDeptWeightSaveREQ;
import cn.zswltech.mithras.dto.financeprojectdistribution.FinanceProjectDistributionWeightREQ;
import cn.zswltech.mithras.dto.financeprojectdistribution.FinanceProjectDistributionWeightRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author lllin
 * @date 2025-12-199
 * @description
 */
@Api(tags = "绩效考核-项目分配-部门分配-比重")
@RequestMapping(path = "/finance/project/distribution/dept/weight")
public interface FinanceProjectDistributionDeptWeightApi {

    @ApiOperation(value = "绩效考核-项目分配-部门分配比重-保存")
    @PostMapping(path = "/save")
    R<Void> saveDept(@RequestBody @Valid FinanceProjectDistributionDeptWeightSaveREQ req);

    @ApiOperation("绩效考核-项目分配-分配比重-详情")
    @PostMapping(path = "/detail")
    R<FinanceProjectDistributionWeightRSP> detail(@RequestBody @Valid FinanceProjectDistributionWeightREQ req);

}
