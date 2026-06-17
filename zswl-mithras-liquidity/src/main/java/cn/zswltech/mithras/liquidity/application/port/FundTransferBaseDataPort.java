package cn.zswltech.mithras.liquidity.application.port;

import cn.zswltech.mithras.dto.liquiditymanage.fundtransfer.FundTransferBankAccountListREQ;
import cn.zswltech.mithras.dto.liquiditymanage.fundtransfer.FundTransferBankAccountListRSP;

import java.time.LocalDate;
import java.util.List;

public interface FundTransferBaseDataPort {

    long calculateWorkDays(LocalDate from, LocalDate to);

    List<FundTransferBankAccountListRSP> listSupervisionAccounts(FundTransferBankAccountListREQ req);
}
