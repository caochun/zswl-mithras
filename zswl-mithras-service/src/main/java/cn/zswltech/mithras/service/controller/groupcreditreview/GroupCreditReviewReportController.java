package cn.zswltech.mithras.service.controller.groupcreditreview;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.groupcreditreview.GroupCreditReviewReportApi;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.groupcreditreview.report.*;
import cn.zswltech.mithras.service.service.groupcreditreview.GroupCreditReviewReportService;
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
    private GroupCreditReviewReportService groupCreditReviewReportService;

    @Override
    public R<Void> upload(MultipartFile file, GroupCreditReviewReportUploadREQ req) {
        groupCreditReviewReportService.upload(file, req);
        return R.ok();
    }

    @Override
    public R<List<Pair<String, List<GroupCreditReviewReportListRSP>>>> list(GroupCreditReviewReportListREQ req) {
        return R.ok(groupCreditReviewReportService.list(req));
    }

    @Override
    public R<Void> remove(GroupCreditReviewReportRemoveREQ req) {
        groupCreditReviewReportService.remove(req);
        return R.ok();
    }

    @Override
    public R<FileListRSP> download(GroupCreditReviewReportDownloadREQ req) {
        return R.ok(groupCreditReviewReportService.download(req.getId()));
    }

}
