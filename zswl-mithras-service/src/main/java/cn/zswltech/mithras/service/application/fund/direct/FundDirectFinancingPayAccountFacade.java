package cn.zswltech.mithras.service.application.fund.direct;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.funddirect.application.directfinancing.FundDirectFinancingPayAccountApplicationService;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.fund.financing.FundDirectFinancingMainModifyAuthChecker;
import cn.zswltech.mithras.service.auth.checker.fund.financing.FundDirectFinancingSubModifyAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.fund.direct.mapper.FundDirectFinancingPayAccountMapper;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingPayAccountService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @description 直接融资-还款账户
* @author zhaozhengkang
* @date 2023-06-17
*/


@Service
public class FundDirectFinancingPayAccountFacade implements FundDirectFinancingPayAccountApplicationService {

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