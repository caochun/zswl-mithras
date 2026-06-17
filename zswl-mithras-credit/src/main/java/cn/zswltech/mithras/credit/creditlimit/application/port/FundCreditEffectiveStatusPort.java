package cn.zswltech.mithras.credit.creditlimit.application.port;

import java.time.LocalDate;

public interface FundCreditEffectiveStatusPort {

    void invalidExpiredFundCredit(LocalDate now);
}
