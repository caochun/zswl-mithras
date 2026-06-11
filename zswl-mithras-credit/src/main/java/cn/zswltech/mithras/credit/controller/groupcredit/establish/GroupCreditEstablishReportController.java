package cn.zswltech.mithras.credit.controller.groupcredit.establish;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.groupcreditestablish.report.GroupCreditEstablishReportDownloadREQ;
import cn.zswltech.mithras.dto.groupcreditestablish.report.GroupCreditEstablishReportListREQ;
import cn.zswltech.mithras.dto.groupcreditestablish.report.GroupCreditEstablishReportListRSP;
import cn.zswltech.mithras.dto.groupcreditestablish.report.GroupCreditEstablishReportRemoveREQ;
import cn.zswltech.mithras.dto.groupcreditestablish.report.GroupCreditEstablishReportUploadREQ;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import cn.zswltech.mithras.api.groupcreditestablish.GroupCreditEstablishReportApi;
import cn.zswltech.mithras.credit.application.groupcredit.establish.GroupCreditEstablishReportApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class GroupCreditEstablishReportController implements GroupCreditEstablishReportApi {
    @Resource
    private GroupCreditEstablishReportApplicationService groupCreditEstablishReportApplicationService;

    @Override
    public R<Void> upload(MultipartFile file, GroupCreditEstablishReportUploadREQ req) {
        return groupCreditEstablishReportApplicationService.upload(file, req);
    }

    @Override
    public R<List<Pair<String, List<GroupCreditEstablishReportListRSP>>>> list(GroupCreditEstablishReportListREQ req) {
        return groupCreditEstablishReportApplicationService.list(req);
    }

    @Override
    public R<Void> remove(GroupCreditEstablishReportRemoveREQ req) {
        return groupCreditEstablishReportApplicationService.remove(req);
    }

    @Override
    public R<FileListRSP> download(GroupCreditEstablishReportDownloadREQ req) {
        return groupCreditEstablishReportApplicationService.download(req);
    }
}
