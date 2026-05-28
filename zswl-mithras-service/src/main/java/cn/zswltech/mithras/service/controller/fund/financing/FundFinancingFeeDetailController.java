package cn.zswltech.mithras.service.controller.fund.financing;

import cn.hutool.db.Page;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.financing.FundFinancingFeeDetailApi;
import cn.zswltech.mithras.dto.fund.financing.fee.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.fund.financing.FundFinancingMainModifyAuthChecker;
import cn.zswltech.mithras.service.auth.checker.fund.financing.FundFinancingSubModifyAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.fund.financing.FundFinancingFeeDetailMapper;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingFeeDetailService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author zhaozhengkang
 * @description 间融-费用明细
 * @date 2023-06-17
 */
@RestController
public class FundFinancingFeeDetailController implements FundFinancingFeeDetailApi {

    @Resource
    private FundFinancingFeeDetailService fundFinancingFeeDetailService;


    @Override
    @DataAuthCheck(keyFieldName = "financingId", checkerClass = FundFinancingMainModifyAuthChecker.class, businessModule = BusinessModuleEnum.FUND_FINANCING)
    public R<Void> add(FundFinancingFeeDetailAddREQ req) {
        fundFinancingFeeDetailService.add(req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = FundFinancingSubModifyAuthChecker.class, businessModule = BusinessModuleEnum.FUND_FINANCING, mapperClass = FundFinancingFeeDetailMapper.class)
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