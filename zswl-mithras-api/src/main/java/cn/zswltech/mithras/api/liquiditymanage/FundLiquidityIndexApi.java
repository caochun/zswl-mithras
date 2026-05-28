package cn.zswltech.mithras.api.liquiditymanage;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityIndex.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author chenyifei
 */
@Api(tags = "流动性管理-流动性管理-接口")
public interface FundLiquidityIndexApi {

    @PostMapping(path = "/liquidity/manage/index")
    @ApiOperation(value = "流动性指标")
    R<LiquidityIndexDetailRSP> manageIndex(@RequestBody @Valid LiquidityIndexDetailREQ req);

    @PostMapping(path = "/liquidity/manage/board")
    @ApiOperation(value = "流动性看板")
    R<LiquidityBoardDetailSumRSP> manageBoard(@RequestBody @Valid LiquidityBoardDetailREQ req);

    @PostMapping(path = "/liquidity/manage/mismatch")
    @ApiOperation(value = "错配明细")
    R<List<LiquidityMismatchDetailRSP>> manageMismatch(@RequestBody @Valid LiquidityMismatchDetailREQ req);

    @PostMapping(path = "/liquidity/manage/rent/income")
    @ApiOperation(value = "租金流入")
    R<List<LiquidityBoardRentIncomeRSP>> manageRentIncome(@RequestBody @Valid LiquidityBoardRentIncomeREQ req);

    @PostMapping(path = "/liquidity/manage/repay")
    @ApiOperation(value = "还本付息")
    R<List<LiquidityBoardRepayPrincipalInterestRSP>> manageRepay(@RequestBody @Valid LiquidityBoardRepayPrincipalInterestREQ req);

}
