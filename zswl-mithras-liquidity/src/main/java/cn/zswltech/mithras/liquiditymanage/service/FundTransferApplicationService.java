package cn.zswltech.mithras.liquiditymanage.service;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.liquiditymanage.fundTransfer.AccountDepositedAmountDetail;
import cn.zswltech.mithras.dto.liquiditymanage.fundTransfer.FundTransferAccountCurrentDailyREQ;
import cn.zswltech.mithras.dto.liquiditymanage.fundTransfer.FundTransferBankAccountListREQ;
import cn.zswltech.mithras.dto.liquiditymanage.fundTransfer.FundTransferBankAccountListRSP;
import cn.zswltech.mithras.dto.liquiditymanage.fundTransfer.FundTransferCurrentDailyREQ;
import cn.zswltech.mithras.dto.liquiditymanage.fundTransfer.FundTransferCurrentDailyRSP;
import cn.zswltech.mithras.dto.liquiditymanage.fundTransfer.FundTransferDetailListREQ;
import cn.zswltech.mithras.dto.liquiditymanage.fundTransfer.FundTransferDetailListRSP;
import cn.zswltech.mithras.dto.liquiditymanage.fundTransfer.FundTransferGraphDailyREQ;
import cn.zswltech.mithras.dto.liquiditymanage.fundTransfer.FundTransferGraphDailyRSP;
import cn.zswltech.mithras.dto.liquiditymanage.fundTransfer.FundTransferGraphREQ;
import cn.zswltech.mithras.dto.liquiditymanage.fundTransfer.FundTransferGraphRSP;
import cn.zswltech.mithras.dto.liquiditymanage.fundTransfer.FundTransferListDailyREQ;
import cn.zswltech.mithras.dto.liquiditymanage.fundTransfer.FundTransferListDailyRSP;
import cn.zswltech.mithras.dto.liquiditymanage.fundTransfer.FundTransferListREQ;
import cn.zswltech.mithras.dto.liquiditymanage.fundTransfer.FundTransferListRSP;

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
