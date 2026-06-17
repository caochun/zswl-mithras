package cn.zswltech.mithras.ftp.oldftp.application.port;

import cn.zswltech.mithras.ftp.oldftp.application.port.model.FtpGuidanceProcessInfo;

public interface FtpGuidanceWorkflowPort {

    FtpGuidanceProcessInfo findMonthlyGuidanceProcess(Long guidanceId);

    FtpGuidanceProcessInfo findQuarterlyGuidanceProcess(Long guidanceId);

    void startMonthlyGuidanceCreateFlow(Long guidanceId, Integer year, Integer month);

    void startMonthlyGuidanceModifyFlow(Long guidanceId, Integer year, Integer month);

    void startQuarterlyGuidanceCreateFlow(Long guidanceId, Integer year, Integer quarter);

    void startQuarterlyGuidanceModifyFlow(Long guidanceId, Integer year, Integer quarter);
}
