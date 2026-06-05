package cn.zswltech.mithras.liquiditymanage.service;

import cn.zswltech.mithras.dto.liquiditymanage.liquidityIndex.LiquidityBoardDetailREQ;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityIndex.LiquidityBoardDetailSumRSP;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityIndex.LiquidityBoardRentIncomeREQ;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityIndex.LiquidityBoardRentIncomeRSP;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityIndex.LiquidityBoardRepayPrincipalInterestREQ;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityIndex.LiquidityBoardRepayPrincipalInterestRSP;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityIndex.LiquidityIndexDetailREQ;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityIndex.LiquidityIndexDetailRSP;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityIndex.LiquidityMismatchDetailREQ;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityIndex.LiquidityMismatchDetailRSP;

import java.util.List;

public interface FundLiquidityIndexApplicationService {

    LiquidityIndexDetailRSP manageIndex(LiquidityIndexDetailREQ req);

    LiquidityBoardDetailSumRSP manageBoard(LiquidityBoardDetailREQ req);

    List<LiquidityMismatchDetailRSP> manageMismatch(LiquidityMismatchDetailREQ req);

    List<LiquidityBoardRentIncomeRSP> manageRentIncome(LiquidityBoardRentIncomeREQ req);

    List<LiquidityBoardRepayPrincipalInterestRSP> manageRepay(LiquidityBoardRepayPrincipalInterestREQ req);
}
