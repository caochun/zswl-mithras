package cn.zswltech.mithras.service.fund.direct.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.directfinancing.FundDirectFinancingPayAccountApi;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.fund.financing.FundDirectFinancingMainModifyAuthChecker;
import cn.zswltech.mithras.service.auth.checker.fund.financing.FundDirectFinancingSubModifyAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.fund.direct.mapper.FundDirectFinancingPayAccountMapper;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingPayAccountService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
* @description 直接融资-还款账户
* @author zhaozhengkang
* @date 2023-06-17
*/
@RestController
public class FundDirectFinancingPayAccountController implements FundDirectFinancingPayAccountApi {

    @Resource
    private FundDirectFinancingPayAccountService fundDirectFinancingPayAccountService;

    @Override
    @DataAuthCheck(keyFieldName = "financingId", checkerClass = FundDirectFinancingMainModifyAuthChecker.class, businessModule = BusinessModuleEnum.FUND_DIRECT_FINANCING)
    public R<Void> add(FundDirectFinancingPayAccountAddREQ req) {
        fundDirectFinancingPayAccountService.add(req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = FundDirectFinancingSubModifyAuthChecker.class, businessModule = BusinessModuleEnum.FUND_DIRECT_FINANCING, mapperClass = FundDirectFinancingPayAccountMapper.class)
    public R<Void> modify(FundDirectFinancingPayAccountModifyREQ req){
        fundDirectFinancingPayAccountService.modify(req);
        return R.ok();
    }

    @Override
    public R<List<FundDirectFinancingPayAccountListRSP>> list(FundDirectFinancingPayAccountListREQ req) {
        return R.ok(fundDirectFinancingPayAccountService.list(req));
    }

    @Override
    public R<Void> remove(FundDirectFinancingSingleIdREQ req) {
        fundDirectFinancingPayAccountService.remove(req.getId());
        return R.ok();
    }


}