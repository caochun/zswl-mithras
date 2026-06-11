package cn.zswltech.mithras.liquidity.service;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.liquiditymanage.fundtransfer.AccountDepositedAmountDetail;
import cn.zswltech.mithras.dto.liquiditymanage.fundtransfer.FundTransferAccountCurrentDailyREQ;
import cn.zswltech.mithras.dto.liquiditymanage.fundtransfer.FundTransferBankAccountListREQ;
import cn.zswltech.mithras.dto.liquiditymanage.fundtransfer.FundTransferBankAccountListRSP;
import cn.zswltech.mithras.dto.liquiditymanage.fundtransfer.FundTransferCurrentDailyREQ;
import cn.zswltech.mithras.dto.liquiditymanage.fundtransfer.FundTransferCurrentDailyRSP;
import cn.zswltech.mithras.dto.liquiditymanage.fundtransfer.FundTransferDetailListREQ;
import cn.zswltech.mithras.dto.liquiditymanage.fundtransfer.FundTransferDetailListRSP;
import cn.zswltech.mithras.dto.liquiditymanage.fundtransfer.FundTransferGraphDailyREQ;
import cn.zswltech.mithras.dto.liquiditymanage.fundtransfer.FundTransferGraphDailyRSP;
import cn.zswltech.mithras.dto.liquiditymanage.fundtransfer.FundTransferGraphREQ;
import cn.zswltech.mithras.dto.liquiditymanage.fundtransfer.FundTransferGraphRSP;
import cn.zswltech.mithras.dto.liquiditymanage.fundtransfer.FundTransferListDailyREQ;
import cn.zswltech.mithras.dto.liquiditymanage.fundtransfer.FundTransferListDailyRSP;
import cn.zswltech.mithras.dto.liquiditymanage.fundtransfer.FundTransferListREQ;
import cn.zswltech.mithras.dto.liquiditymanage.fundtransfer.FundTransferListRSP;

import java.util.List;

public interface FundTransferApplicationService {

    FundTransferListRSP list(FundTransferListREQ req);

    FundTransferGraphRSP graphList(FundTransferGraphREQ req);

    FundTransferDetailListRSP detailList(FundTransferDetailListREQ req);

    FundTransferListDailyRSP listDaily(FundTransferListDailyREQ req);

    FundTransferGraphDailyRSP graphDaily(FundTransferGraphDailyREQ req);

    FundTransferCurrentDailyRSP currentDaily(FundTransferCurrentDailyREQ req);

    List<FundTransferBankAccountListRSP> list(FundTransferBankAccountListREQ req);

    void financingRepayInfoInAdvance();

    PageR<AccountDepositedAmountDetail> accountCurrentDaily(FundTransferAccountCurrentDailyREQ req);
}
