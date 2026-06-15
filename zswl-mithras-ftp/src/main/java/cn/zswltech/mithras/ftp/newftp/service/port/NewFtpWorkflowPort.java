package cn.zswltech.mithras.ftp.newftp.service.port;

import java.time.LocalDate;

public interface NewFtpWorkflowPort {

    NewFtpProcessInfo findGuidanceProcess(Long mainId);

    void startGuidanceCreateFlow(Long mainId, LocalDate month);

    void startGuidanceModifyFlow(Long mainId, LocalDate month);

    String startInterestChangeApplyFlow(Long applyId);
}
