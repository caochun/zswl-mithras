package cn.zswltech.mithras.ftp.newftp.application.port;

import cn.zswltech.mithras.dto.newftp.FtpAssessInfo;
import cn.zswltech.mithras.dto.newftp.NewFtpInterestChangeApplySaveREQ;

import java.util.List;

public interface NewFtpInterestChangeAssessmentPort {

    List<FtpAssessInfo> listByApplyId(Long applyId);

    void replaceByApplyId(Long applyId, List<NewFtpInterestChangeApplySaveREQ.FtpAssessInfo> assessmentInfoList);

    boolean existsByApplyId(Long applyId);
}
