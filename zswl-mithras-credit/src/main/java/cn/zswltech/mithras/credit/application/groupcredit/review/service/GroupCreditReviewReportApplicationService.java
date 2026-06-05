package cn.zswltech.mithras.credit.application.groupcredit.review.service;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.groupcreditreview.report.GroupCreditReviewReportListREQ;
import cn.zswltech.mithras.dto.groupcreditreview.report.GroupCreditReviewReportListRSP;
import cn.zswltech.mithras.dto.groupcreditreview.report.GroupCreditReviewReportRemoveREQ;
import cn.zswltech.mithras.dto.groupcreditreview.report.GroupCreditReviewReportUploadREQ;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GroupCreditReviewReportApplicationService {

    void upload(MultipartFile file, GroupCreditReviewReportUploadREQ req);

    List<Pair<String, List<GroupCreditReviewReportListRSP>>> list(GroupCreditReviewReportListREQ req);

    void remove(GroupCreditReviewReportRemoveREQ req);

    FileListRSP download(Long id);
}
