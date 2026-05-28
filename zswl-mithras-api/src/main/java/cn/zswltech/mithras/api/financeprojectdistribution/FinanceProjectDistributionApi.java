package cn.zswltech.mithras.api.financeprojectdistribution;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.financeprojectdistribution.FinanceProjectDistributionSubmitREQ;
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
@Api(tags = "财务-项目分配")
@RequestMapping(path = "/finance/projectdistribution")
public interface FinanceProjectDistributionApi {
    @ApiOperation("财务-项目分配-提交审批")
    @PostMapping(path = "/submit")
    R<Void> submit(@RequestBody @Valid FinanceProjectDistributionSubmitREQ req);
}
