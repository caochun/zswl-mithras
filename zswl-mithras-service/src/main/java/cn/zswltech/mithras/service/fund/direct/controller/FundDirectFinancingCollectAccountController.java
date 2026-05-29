package cn.zswltech.mithras.service.fund.direct.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.directfinancing.FundDirectFinancingCollectAccountApi;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.fund.financing.FundDirectFinancingMainModifyAuthChecker;
import cn.zswltech.mithras.service.auth.checker.fund.financing.FundDirectFinancingSubModifyAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.fund.direct.mapper.FundDirectFinancingCollectAccountMapper;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingCollectAccountService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
* @description 直接融资-对方收款账户
* @author zhaozhengkang
* @date 2023-06-17
*/
@RestController
public class FundDirectFinancingCollectAccountController implements FundDirectFinancingCollectAccountApi {

    @Resource
    private FundDirectFinancingCollectAccountService fundDirectFinancingCollectAccountService;

    @Override
    @DataAuthCheck(keyFieldName = "financingId", checkerClass = FundDirectFinancingMainModifyAuthChecker.class, businessModule = BusinessModuleEnum.FUND_DIRECT_FINANCING)
    public R<Void> add(FundDirectFinancingCollectAccountAddREQ req) {
        fundDirectFinancingCollectAccountService.add(req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = FundDirectFinancingSubModifyAuthChecker.class, businessModule = BusinessModuleEnum.FUND_DIRECT_FINANCING, mapperClass = FundDirectFinancingCollectAccountMapper.class)
    public R<Void> modify(FundDirectFinancingCollectAccountModifyREQ req){
        fundDirectFinancingCollectAccountService.modify(req);
        return R.ok();
    }

    @Override
    public R<List<FundDirectFinancingCollectAccountListRSP>> list(FundDirectFinancingCollectAccountListREQ req) {
        return R.ok(fundDirectFinancingCollectAccountService.list(req));
    }

    @Override
    public R<Void> remove(FundDirectFinancingSingleIdREQ req) {
        fundDirectFinancingCollectAccountService.remove(req.getId());
        return R.ok();
    }


}