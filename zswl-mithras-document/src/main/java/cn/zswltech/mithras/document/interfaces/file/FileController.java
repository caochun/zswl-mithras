package cn.zswltech.mithras.document.interfaces.file;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.api.FileApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.document.application.file.api.FileApplicationService;
import cn.zswltech.mithras.dto.file.FileBatchDownLoadREQ;
import cn.zswltech.mithras.dto.file.FileBatchRemoveREQ;
import cn.zswltech.mithras.dto.file.FileDownLoadREQ;
import cn.zswltech.mithras.dto.file.FileDownLoadRSP;
import cn.zswltech.mithras.dto.file.FileDownLoadTemplateREQ;
import cn.zswltech.mithras.dto.file.FileExportREQ;
import cn.zswltech.mithras.dto.file.FileListREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.file.FileRemoveREQ;
import cn.zswltech.mithras.dto.file.FileRenameREQ;
import cn.zswltech.mithras.dto.file.FileUploadPresignedREQ;
import cn.zswltech.mithras.dto.file.FileUploadPresignedRSP;
import cn.zswltech.mithras.dto.file.FileUploadREQ;
import cn.zswltech.mithras.dto.file.FileUploadRSP;
import cn.zswltech.mithras.dto.file.FileUploadRecordREQ;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
public class FileController implements FileApi {

    @Resource
    private FileApplicationService fileApplicationService;

    @Override
    public R<FileUploadRSP> upload(@Valid FileUploadREQ fileUploadREQ) {
        return fileApplicationService.upload(fileUploadREQ);
    }

    @Override
    public R<FileUploadPresignedRSP> presignedPut(@Valid FileUploadPresignedREQ req) {
        return fileApplicationService.presignedPut(req);
    }

    @Override
    public R<FileUploadPresignedRSP> uploadRecord(@Valid FileUploadRecordREQ req) {
        return fileApplicationService.uploadRecord(req);
    }

    @Override
    public R<PageR<FileListRSP>> list(@Valid FileListREQ req) {
        return fileApplicationService.list(req);
    }

    @Override
    public R<List<Pair<String, List<FileListRSP>>>> listGroup(@Valid FileListREQ req) {
        return fileApplicationService.listGroup(req);
    }

    @Override
    public R<Void> remove(@Valid FileRemoveREQ req) {
        return fileApplicationService.remove(req);
    }

    @Override
    public R<FileDownLoadRSP> download(@Valid FileDownLoadREQ req) {
        return fileApplicationService.download(req);
    }

    @Override
    public R<String> downloadTemplate(@Valid FileDownLoadTemplateREQ req) {
        return fileApplicationService.downloadTemplate(req);
    }

    @Override
    public R<Void> batchRemove(@Valid FileBatchRemoveREQ req) {
        return fileApplicationService.batchRemove(req);
    }

    @Override
    public void batchDownload(@Valid FileBatchDownLoadREQ req) {
        fileApplicationService.batchDownload(req);
    }

    @Override
    public void export(@Valid FileExportREQ req) {
        fileApplicationService.export(req);
    }

    @Override
    public R<Void> rename(@Valid FileRenameREQ req) {
        return fileApplicationService.rename(req);
    }
}
