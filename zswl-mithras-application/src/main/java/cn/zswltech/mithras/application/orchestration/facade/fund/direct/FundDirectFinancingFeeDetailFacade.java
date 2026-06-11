package cn.zswltech.mithras.application.orchestration.facade.fund.direct;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.fund.directfinancing.application.FundDirectFinancingFeeDetailApplicationService;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.fund.directfinancing.application.auth.FundDirectFinancingMainModifyAuthChecker;
import cn.zswltech.mithras.fund.directfinancing.application.auth.FundDirectFinancingSubModifyAuthChecker;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.fund.directfinancing.mapper.FundDirectFinancingFeeDetailMapper;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingFeeDetailService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zhaozhengkang
 * @description 直接融资-费用明细
 * @date 2023-06-17
 */

@Service
public class FundDirectFinancingFeeDetailFacade implements FundDirectFinancingFeeDetailApplicationService {

    @Resource
    private FundDirectFinancingFeeDetailService fundDirectFinancingFeeDetailService;

    @Override
    @DataAuthCheck(keyFieldName = "financingId", checkerClass = FundDirectFinancingMainModifyAuthChecker.class, businessModule = "FUND_DIRECT_FINANCING")
    public R<Void> modifyProgramme(FundDirectFinancingProgrammeModifyREQ req) {
        fundDirectFinancingFeeDetailService.modifyProgramme(req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "financingId", checkerClass = FundDirectFinancingMainModifyAuthChecker.class, businessModule = "FUND_DIRECT_FINANCING")
    public R<Void> add(FundDirectFinancingFeeDetailAddREQ req) {
        fundDirectFinancingFeeDetailService.add(req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = FundDirectFinancingSubModifyAuthChecker.class, businessModule = "FUND_DIRECT_FINANCING", mapperClass = FundDirectFinancingFeeDetailMapper.class)
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