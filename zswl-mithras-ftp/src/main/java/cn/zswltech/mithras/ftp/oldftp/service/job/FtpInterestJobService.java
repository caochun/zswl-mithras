package cn.zswltech.mithras.ftp.oldftp.service.job;

import java.time.LocalDate;

public interface FtpInterestJobService {

    void calculateFtpInterest(Long targetFtpInterestId, LocalDate startDate, LocalDate endDate);
}
