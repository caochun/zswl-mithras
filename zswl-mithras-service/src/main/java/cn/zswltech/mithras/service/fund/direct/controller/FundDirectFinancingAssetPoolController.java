package cn.zswltech.mithras.service.fund.direct.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.directfinancing.FundDirectFinancingAssetPoolApi;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingAssetPoolDetailREQ;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingAssetPoolDetailRSP;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingAssetPoolModifyREQ;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.fund.financing.FundDirectFinancingMainModifyAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingAssetPoolService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
* @description 直接融资-资产池信息
* @author zhaozhengkang
* @date 2023-06-17
*/
@RestController
public class FundDirectFinancingAssetPoolController implements FundDirectFinancingAssetPoolApi {

    @Resource
    private FundDirectFinancingAssetPoolService fundDirectFinancingAssetPoolService;


    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = FundDirectFinancingMainModifyAuthChecker.class, businessModule = BusinessModuleEnum.FUND_DIRECT_FINANCING)
    public R<Void> modify(FundDirectFinancingAssetPoolModifyREQ req){
        fundDirectFinancingAssetPoolService.modify(req);
        return R.ok();
    }

    @Override
    public R<FundDirectFinancingAssetPoolDetailRSP> detail(FundDirectFinancingAssetPoolDetailREQ req) {
        return R.ok(fundDirectFinancingAssetPoolService.detail(req.getFinancingId()));
    }

}