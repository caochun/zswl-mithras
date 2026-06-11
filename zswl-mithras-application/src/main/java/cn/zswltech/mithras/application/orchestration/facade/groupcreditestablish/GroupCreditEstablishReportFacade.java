package cn.zswltech.mithras.application.orchestration.facade.groupcreditestablish;

import cn.zswltech.mithras.credit.application.groupcredit.establish.GroupCreditEstablishReportApplicationService;
import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.groupcreditestablish.report.*;
import cn.zswltech.mithras.application.orchestration.groupcredit.establish.GroupCreditEstablishReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 立项报告文件
 *
 * @author wangchuanhao
 * @date 2022/7/22 10:49 AM
 */
@Service
public class GroupCreditEstablishReportFacade implements GroupCreditEstablishReportApplicationService {

    @Autowired
    private HttpServletResponse response;

    @Resource
    private GroupCreditEstablishReportService groupCreditEstablishReportService;

    @Override
    public R<Void> upload(MultipartFile file, GroupCreditEstablishReportUploadREQ req) {
        groupCreditEstablishReportService.upload(file, req);
        return R.ok();
    }

    @Override
    public R<List<Pair<String, List<GroupCreditEstablishReportListRSP>>>> list(GroupCreditEstablishReportListREQ req) {
        return R.ok(groupCreditEstablishReportService.list(req));
    }

    @Override
    public R<Void> remove(GroupCreditEstablishReportRemoveREQ req) {
        groupCreditEstablishReportService.remove(req);
        return R.ok();
    }

    @Override
    public R<FileListRSP> download(GroupCreditEstablishReportDownloadREQ req) {
        return R.ok(groupCreditEstablishReportService.download(req.getId()));
    }

}
