package cn.zswltech.mithras.liquidity.service;

import cn.zswltech.mithras.dto.liquiditymanage.liquidityindex.LiquidityBoardDetailREQ;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityindex.LiquidityBoardDetailSumRSP;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityindex.LiquidityBoardRentIncomeREQ;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityindex.LiquidityBoardRentIncomeRSP;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityindex.LiquidityBoardRepayPrincipalInterestREQ;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityindex.LiquidityBoardRepayPrincipalInterestRSP;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityindex.LiquidityIndexDetailREQ;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityindex.LiquidityIndexDetailRSP;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityindex.LiquidityMismatchDetailREQ;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityindex.LiquidityMismatchDetailRSP;

import java.util.List;

public interface FundLiquidityIndexApplicationService {

    LiquidityIndexDetailRSP manageIndex(LiquidityIndexDetailREQ req);

    LiquidityBoardDetailSumRSP manageBoard(LiquidityBoardDetailREQ req);

    List<LiquidityMismatchDetailRSP> manageMismatch(LiquidityMismatchDetailREQ req);

    List<LiquidityBoardRentIncomeRSP> manageRentIncome(LiquidityBoardRentIncomeREQ req);

    List<LiquidityBoardRepayPrincipalInterestRSP> manageRepay(LiquidityBoardRepayPrincipalInterestREQ req);
}
