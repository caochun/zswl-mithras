package cn.zswltech.mithras.service.controller.liquiditymanage;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.liquiditymanage.FundLiquidityIndexApi;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityIndex.*;
import cn.zswltech.mithras.service.service.liquiditymanage.FundLiquidityIndexService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * FundLiquidityIndexController
 *
 * @author chenyifei
 * @since 2024/12/16
 */
@RestController
public class FundLiquidityIndexController implements FundLiquidityIndexApi {

    @Resource
    private FundLiquidityIndexService liquidityIndexService;

    @Override
    public R<LiquidityIndexDetailRSP> manageIndex(LiquidityIndexDetailREQ req) {
        return R.ok(liquidityIndexService.manageIndex(req));
    }

    @Override
    public R<LiquidityBoardDetailSumRSP> manageBoard(LiquidityBoardDetailREQ req) {
        return R.ok(liquidityIndexService.manageBoard(req));
    }

    @Override
    public R<List<LiquidityMismatchDetailRSP>> manageMismatch(LiquidityMismatchDetailREQ req) {
        return R.ok(liquidityIndexService.manageMismatch(req));
    }

    @Override
    public R<List<LiquidityBoardRentIncomeRSP>> manageRentIncome(LiquidityBoardRentIncomeREQ req) {
        return R.ok(liquidityIndexService.manageRentIncome(req));
    }

    @Override
    public R<List<LiquidityBoardRepayPrincipalInterestRSP>> manageRepay(LiquidityBoardRepayPrincipalInterestREQ req) {
        return R.ok(liquidityIndexService.manageRepay(req));
    }
}
