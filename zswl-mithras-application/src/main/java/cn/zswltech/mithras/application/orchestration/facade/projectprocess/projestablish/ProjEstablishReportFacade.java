package cn.zswltech.mithras.application.orchestration.facade.projectprocess.projestablish;

import cn.zswltech.mithras.projectprocess.application.projestablish.ProjEstablishReportApplicationService;
import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.projestablish.report.*;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.application.orchestration.auth.checker.common.CommonModifyMainAuthCheckerNew;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * 立项报告文件
 *
 * @author wangchuanhao
 * @date 2022/7/22 10:49 AM
 */
@Service
public class ProjEstablishReportFacade implements ProjEstablishReportApplicationService {

    @Autowired
    private HttpServletResponse response;

    @Resource
    private ProjEstablishReportService projEstablishReportService;

    @Override
    public R<Void> upload(MultipartFile file, ProjEstablishReportUploadREQ req) {
        projEstablishReportService.upload(file, req);
        return R.ok();
    }

    @Override
    public R<List<Pair<String, List<ProjEstablishReportListRSP>>>> list(ProjEstablishReportListREQ req) {
        return R.ok(projEstablishReportService.list(req));
    }

    @Override
    public R<Void> remove(ProjEstablishReportRemoveREQ req) {
        projEstablishReportService.remove(req);
        return R.ok();
    }

    @Override
    public R<FileListRSP> download(ProjEstablishReportDownloadREQ req) throws IOException {
        return R.ok(projEstablishReportService.download(req.getRecordId()));
    }

    @Override
    @DataAuthCheck(
            keyFieldName = "projEstablishId",
            checkerClass = CommonModifyMainAuthCheckerNew.class,
            businessModule = "PROJ_ESTABLISH"
    )
    public void generate(ProjEstablishReportGenerateREQ req) throws IOException {
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("立项报告.docx"));
        projEstablishReportService.generate(response.getOutputStream(), req);
    }

    @Override
    @DataAuthCheck(
            keyFieldName = "projEstablishId",
            checkerClass = CommonModifyMainAuthCheckerNew.class,
            businessModule = "PROJ_ESTABLISH"
    )
    public R<Long> generateOnline(ProjEstablishReportGenerateREQ req) throws IOException {
        return R.ok(projEstablishReportService.generateOnline(req));
    }

}
