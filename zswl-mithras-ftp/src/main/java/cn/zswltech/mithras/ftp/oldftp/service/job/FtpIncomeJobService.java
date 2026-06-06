package cn.zswltech.mithras.ftp.oldftp.service.job;

import java.time.LocalDate;

public interface FtpIncomeJobService {

    void ftpIncomeMaintenance(LocalDate targetDate);
}
