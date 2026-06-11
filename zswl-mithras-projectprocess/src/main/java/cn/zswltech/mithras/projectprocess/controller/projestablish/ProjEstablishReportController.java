package cn.zswltech.mithras.projectprocess.controller.projestablish;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.projestablish.report.ProjEstablishReportDownloadREQ;
import cn.zswltech.mithras.dto.projestablish.report.ProjEstablishReportGenerateREQ;
import cn.zswltech.mithras.dto.projestablish.report.ProjEstablishReportListREQ;
import cn.zswltech.mithras.dto.projestablish.report.ProjEstablishReportListRSP;
import cn.zswltech.mithras.dto.projestablish.report.ProjEstablishReportRemoveREQ;
import cn.zswltech.mithras.dto.projestablish.report.ProjEstablishReportUploadREQ;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import cn.zswltech.mithras.api.projestablish.ProjEstablishReportApi;
import cn.zswltech.mithras.projectprocess.application.projestablish.ProjEstablishReportApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class ProjEstablishReportController implements ProjEstablishReportApi {
    @Resource
    private ProjEstablishReportApplicationService projEstablishReportApplicationService;

    @Override
    public R<Void> upload(MultipartFile file, ProjEstablishReportUploadREQ req) {
        return projEstablishReportApplicationService.upload(file, req);
    }

    @Override
    public R<List<Pair<String, List<ProjEstablishReportListRSP>>>> list(ProjEstablishReportListREQ req) {
        return projEstablishReportApplicationService.list(req);
    }

    @Override
    public R<Void> remove(ProjEstablishReportRemoveREQ req) {
        return projEstablishReportApplicationService.remove(req);
    }

    @Override
    public R<FileListRSP> download(ProjEstablishReportDownloadREQ req) throws IOException {
        return projEstablishReportApplicationService.download(req);
    }

    @Override
    public void generate(ProjEstablishReportGenerateREQ req) throws IOException {
        projEstablishReportApplicationService.generate(req);
    }

    @Override
    public R<Long> generateOnline(ProjEstablishReportGenerateREQ req) throws IOException {
        return projEstablishReportApplicationService.generateOnline(req);
    }
}
