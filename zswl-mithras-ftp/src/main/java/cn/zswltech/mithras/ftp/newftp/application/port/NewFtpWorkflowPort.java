package cn.zswltech.mithras.ftp.newftp.application.port;

import cn.zswltech.mithras.ftp.newftp.application.port.model.NewFtpProcessInfo;

import java.time.LocalDate;

public interface NewFtpWorkflowPort {

    NewFtpProcessInfo findGuidanceProcess(Long mainId);

    void startGuidanceCreateFlow(Long mainId, LocalDate month);

    void startGuidanceModifyFlow(Long mainId, LocalDate month);

    String startInterestChangeApplyFlow(Long applyId);
}
