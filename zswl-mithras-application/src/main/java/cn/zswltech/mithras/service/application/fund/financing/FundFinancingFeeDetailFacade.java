package cn.zswltech.mithras.service.application.fund.financing;

import cn.hutool.db.Page;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.fund.application.financing.api.FundFinancingFeeDetailApplicationService;
import cn.zswltech.mithras.dto.fund.financing.fee.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.fund.financing.FundFinancingMainModifyAuthChecker;
import cn.zswltech.mithras.service.auth.checker.fund.financing.FundFinancingSubModifyAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.financing.FundFinancingFeeDetailMapper;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingFeeDetailService;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 * @author zhaozhengkang
 * @description 间融-费用明细
 * @date 2023-06-17
 */
@Service
public class FundFinancingFeeDetailFacade implements FundFinancingFeeDetailApplicationService {

    @Resource
    private FundFinancingFeeDetailService fundFinancingFeeDetailService;


    @Override
    @DataAuthCheck(keyFieldName = "financingId", checkerClass = FundFinancingMainModifyAuthChecker.class, businessModule = "FUND_FINANCING")
    public R<Void> add(FundFinancingFeeDetailAddREQ req) {
        fundFinancingFeeDetailService.add(req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = FundFinancingSubModifyAuthChecker.class, businessModule = "FUND_FINANCING", mapperClass = FundFinancingFeeDetailMapper.class)
    public R<Void> modifyFee(FundFinancingFeeDetailModifyREQ req) {
        fundFinancingFeeDetailService.modify(req);
        return R.ok();
    }


    @Override
    public R<PageR<FundFinancingFeeDetailRSP>> list(FundFinancingFeeDetailListREQ req) {
        return R.ok(fundFinancingFeeDetailService.list(req));
    }

    @Override
    public R<Void> remove(FundFinancingFeeSingleIdREQ req) {
        fundFinancingFeeDetailService.remove(req.getId());
        return R.ok();
    }

}