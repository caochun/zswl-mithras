package cn.zswltech.mithras.financeprojectdistribution.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.financeprojectdistribution.FinanceProjectDistributionApi;
import cn.zswltech.mithras.dto.financeprojectdistribution.FinanceProjectDistributionSubmitREQ;
import cn.zswltech.mithras.financeprojectdistribution.service.FinanceProjectDistributionApplicationService;
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
public class FinanceProjectDistributionController implements FinanceProjectDistributionApi {
    @Resource
    private FinanceProjectDistributionApplicationService financeProjectDistributionService;


    @Override
    public R<Void> submit(@Valid FinanceProjectDistributionSubmitREQ req) {
        financeProjectDistributionService.submit(req.getProjectDistributionId(),true);
        return R.ok();
    }

}
