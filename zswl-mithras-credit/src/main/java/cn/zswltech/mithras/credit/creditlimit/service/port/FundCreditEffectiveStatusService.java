package cn.zswltech.mithras.credit.creditlimit.service.port;

import java.time.LocalDate;

public interface FundCreditEffectiveStatusService {

    void invalidExpiredFundCredit(LocalDate now);
}
