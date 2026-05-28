package cn.zswltech.mithras.service.controller.financeprojectdistribution;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.financeprojectdistribution.FinanceProjectDistributionBaseInfoApi;
import cn.zswltech.mithras.dto.financeprojectdistribution.FinanceProjectDistributionBaseInfoRSP;
import cn.zswltech.mithras.dto.financeprojectdistribution.FinanceProjectDistributionBaseREQ;
import cn.zswltech.mithras.service.service.financeprofitdistribution.FinanceProjectDistributionBaseInfoService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author lllin
 * @date2025/12/19
 * @description
 */
@RestController
public class FinanceProjectDistributionBaseInfoController implements FinanceProjectDistributionBaseInfoApi {
    @Resource
    private FinanceProjectDistributionBaseInfoService financeProjectDistributionBaseInfoService;

    @Override
    public R<FinanceProjectDistributionBaseInfoRSP> detail(@Valid FinanceProjectDistributionBaseREQ req) {
        return R.ok(financeProjectDistributionBaseInfoService.detail(req));
    }


}
