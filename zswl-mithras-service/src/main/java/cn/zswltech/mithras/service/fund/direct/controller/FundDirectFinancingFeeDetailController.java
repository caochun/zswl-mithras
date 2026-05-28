package cn.zswltech.mithras.service.fund.direct.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.directfinancing.FundDirectFinancingFeeDetailApi;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.fund.financing.FundDirectFinancingMainModifyAuthChecker;
import cn.zswltech.mithras.service.auth.checker.fund.financing.FundDirectFinancingSubModifyAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.fund.direct.mapper.FundDirectFinancingFeeDetailMapper;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingFeeDetailService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author zhaozhengkang
 * @description 直接融资-费用明细
 * @date 2023-06-17
 */
@RestController
public class FundDirectFinancingFeeDetailController implements FundDirectFinancingFeeDetailApi {

    @Resource
    private FundDirectFinancingFeeDetailService fundDirectFinancingFeeDetailService;

    @Override
    @DataAuthCheck(keyFieldName = "financingId", checkerClass = FundDirectFinancingMainModifyAuthChecker.class, businessModule = BusinessModuleEnum.FUND_DIRECT_FINANCING)
    public R<Void> modifyProgramme(FundDirectFinancingProgrammeModifyREQ req) {
        fundDirectFinancingFeeDetailService.modifyProgramme(req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "financingId", checkerClass = FundDirectFinancingMainModifyAuthChecker.class, businessModule = BusinessModuleEnum.FUND_DIRECT_FINANCING)
    public R<Void> add(FundDirectFinancingFeeDetailAddREQ req) {
        fundDirectFinancingFeeDetailService.add(req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = FundDirectFinancingSubModifyAuthChecker.class, businessModule = BusinessModuleEnum.FUND_DIRECT_FINANCING, mapperClass = FundDirectFinancingFeeDetailMapper.class)
    public R<Void> modifyFee(FundDirectFinancingFeeDetailModifyREQ req) {
        fundDirectFinancingFeeDetailService.modify(req);
        return R.ok();
    }

    @Override
    public R<FundDirectFinancingFeeDetailRSP> detail(FundDirectFinancingSingleIdREQ req) {
        return R.ok(fundDirectFinancingFeeDetailService.detail(req.getId()));
    }

    @Override
    public R<FundDirectFinancingFeeDetailListRSP> list(FundDirectFinancingFeeDetailListREQ req) {
        return R.ok(fundDirectFinancingFeeDetailService.list(req));
    }

    @Override
    public R<Void> remove(FundDirectFinancingSingleIdREQ req) {
        fundDirectFinancingFeeDetailService.remove(req.getId());
        return R.ok();
    }

}