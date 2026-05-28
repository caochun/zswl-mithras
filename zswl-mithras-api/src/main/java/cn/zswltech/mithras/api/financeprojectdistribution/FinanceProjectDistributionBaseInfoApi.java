package cn.zswltech.mithras.api.financeprojectdistribution;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.financeprojectdistribution.FinanceProjectDistributionBaseInfoRSP;
import cn.zswltech.mithras.dto.financeprojectdistribution.FinanceProjectDistributionBaseREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author lllin
 * @date 2025-12-19
 * @description
 */
@Api(tags = "绩效考核-项目分配-基本信息")
@RequestMapping(path = "/finance/projectdistribution/baseinfo")
public interface FinanceProjectDistributionBaseInfoApi {
    @ApiOperation("绩效考核-项目分配-基本信息-详情")
    @PostMapping("/detail")
    R<FinanceProjectDistributionBaseInfoRSP> detail(@RequestBody @Valid FinanceProjectDistributionBaseREQ req);

}
