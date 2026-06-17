package cn.zswltech.mithras.ftp.newftp.application.port;

import cn.zswltech.mithras.ftp.oldftp.bo.CashFtpInfluenceBO;

import java.time.LocalDate;

public interface FtpPricingContextPort {

    CashFtpInfluenceBO assembleCashFtpInfluence(Long contractId, LocalDate targetDate);
}
