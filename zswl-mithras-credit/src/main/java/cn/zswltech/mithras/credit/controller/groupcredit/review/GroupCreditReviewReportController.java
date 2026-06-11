package cn.zswltech.mithras.credit.controller.groupcredit.review;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.groupcreditreview.GroupCreditReviewReportApi;
import cn.zswltech.mithras.credit.application.groupcredit.review.GroupCreditReviewReportApplicationService;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.groupcreditreview.report.GroupCreditReviewReportDownloadREQ;
import cn.zswltech.mithras.dto.groupcreditreview.report.GroupCreditReviewReportListREQ;
import cn.zswltech.mithras.dto.groupcreditreview.report.GroupCreditReviewReportListRSP;
import cn.zswltech.mithras.dto.groupcreditreview.report.GroupCreditReviewReportRemoveREQ;
import cn.zswltech.mithras.dto.groupcreditreview.report.GroupCreditReviewReportUploadREQ;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * 集团授信评审文件
 *
 * @author wangchuanhao
 * @date 2022/7/22 10:49 AM
 */
@RestController
public class GroupCreditReviewReportController implements GroupCreditReviewReportApi {

    @Resource
    private GroupCreditReviewReportApplicationService groupCreditReviewReportApplicationService;

    @Override
    public R<Void> upload(MultipartFile file, GroupCreditReviewReportUploadREQ req) {
        groupCreditReviewReportApplicationService.upload(file, req);
        return R.ok();
    }

    @Override
    public R<List<Pair<String, List<GroupCreditReviewReportListRSP>>>> list(GroupCreditReviewReportListREQ req) {
        return R.ok(groupCreditReviewReportApplicationService.list(req));
    }

    @Override
    public R<Void> remove(GroupCreditReviewReportRemoveREQ req) {
        groupCreditReviewReportApplicationService.remove(req);
        return R.ok();
    }

    @Override
    public R<FileListRSP> download(GroupCreditReviewReportDownloadREQ req) {
        return R.ok(groupCreditReviewReportApplicationService.download(req.getId()));
    }
}
