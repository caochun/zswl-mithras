package cn.zswltech.mithras.ftp.oldftp.application.port;

import java.time.LocalDate;

public interface FtpInterestJobPort {

    void calculateFtpInterest(Long targetFtpInterestId, LocalDate startDate, LocalDate endDate);
}
