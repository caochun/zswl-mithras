package cn.zswltech.mithras.ftp.oldftp.application.port;

import java.time.LocalDate;

public interface FtpIncomeJobPort {

    void ftpIncomeMaintenance(LocalDate targetDate);
}
