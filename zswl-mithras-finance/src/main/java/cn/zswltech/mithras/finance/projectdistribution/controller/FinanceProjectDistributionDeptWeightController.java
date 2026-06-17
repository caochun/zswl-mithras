package cn.zswltech.mithras.finance.projectdistribution.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.financeprojectdistribution.FinanceProjectDistributionDeptWeightApi;
import cn.zswltech.mithras.dto.financeprojectdistribution.*;
import cn.zswltech.mithras.finance.projectdistribution.service.FinanceProjectDistributionDeptWeightApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author lllin
 * @date2025/12/19
 * @description
 */
@Slf4j
@RestController
public class FinanceProjectDistributionDeptWeightController implements FinanceProjectDistributionDeptWeightApi {

    @Resource
    private FinanceProjectDistributionDeptWeightApplicationService financeProjectDistributionDeptWeightService;

    @Override
    public R<Void> saveDept(FinanceProjectDistributionDeptWeightSaveREQ req) {
        financeProjectDistributionDeptWeightService.saveDept(req);
        return R.ok();
    }

    @Override
    public R<FinanceProjectDistributionWeightRSP> detail(@Valid FinanceProjectDistributionWeightREQ req) {
        return R.ok(financeProjectDistributionDeptWeightService.detail(req));
    }


}
