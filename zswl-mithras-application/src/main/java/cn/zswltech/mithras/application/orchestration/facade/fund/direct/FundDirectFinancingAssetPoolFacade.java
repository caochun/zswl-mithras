package cn.zswltech.mithras.application.orchestration.facade.fund.direct;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.fund.directfinancing.application.FundDirectFinancingAssetPoolApplicationService;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingAssetPoolDetailREQ;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingAssetPoolDetailRSP;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingAssetPoolModifyREQ;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.fund.directfinancing.application.auth.FundDirectFinancingMainModifyAuthChecker;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.fund.directfinancing.application.FundDirectFinancingAssetPoolService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @description 直接融资-资产池信息
* @author zhaozhengkang
* @date 2023-06-17
*/


@Service
public class FundDirectFinancingAssetPoolFacade implements FundDirectFinancingAssetPoolApplicationService {

    @Resource
    private FundDirectFinancingAssetPoolService fundDirectFinancingAssetPoolService;


    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = FundDirectFinancingMainModifyAuthChecker.class, businessModule = "FUND_DIRECT_FINANCING")
    public R<Void> modify(FundDirectFinancingAssetPoolModifyREQ req){
        fundDirectFinancingAssetPoolService.modify(req);
        return R.ok();
    }

    @Override
    public R<FundDirectFinancingAssetPoolDetailRSP> detail(FundDirectFinancingAssetPoolDetailREQ req) {
        return R.ok(fundDirectFinancingAssetPoolService.detail(req.getFinancingId()));
    }

}