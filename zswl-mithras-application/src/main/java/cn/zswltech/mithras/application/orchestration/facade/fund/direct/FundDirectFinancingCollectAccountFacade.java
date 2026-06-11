package cn.zswltech.mithras.application.orchestration.facade.fund.direct;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.fund.direct.application.directfinancing.FundDirectFinancingCollectAccountApplicationService;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.fund.direct.application.auth.FundDirectFinancingMainModifyAuthChecker;
import cn.zswltech.mithras.fund.direct.application.auth.FundDirectFinancingSubModifyAuthChecker;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.fund.direct.mapper.FundDirectFinancingCollectAccountMapper;
import cn.zswltech.mithras.fund.direct.application.directfinancing.FundDirectFinancingCollectAccountService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @description 直接融资-对方收款账户
* @author zhaozhengkang
* @date 2023-06-17
*/


@Service
public class FundDirectFinancingCollectAccountFacade implements FundDirectFinancingCollectAccountApplicationService {

    @Resource
    private FundDirectFinancingCollectAccountService fundDirectFinancingCollectAccountService;

    @Override
    @DataAuthCheck(keyFieldName = "financingId", checkerClass = FundDirectFinancingMainModifyAuthChecker.class, businessModule = "FUND_DIRECT_FINANCING")
    public R<Void> add(FundDirectFinancingCollectAccountAddREQ req) {
        fundDirectFinancingCollectAccountService.add(req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = FundDirectFinancingSubModifyAuthChecker.class, businessModule = "FUND_DIRECT_FINANCING", mapperClass = FundDirectFinancingCollectAccountMapper.class)
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