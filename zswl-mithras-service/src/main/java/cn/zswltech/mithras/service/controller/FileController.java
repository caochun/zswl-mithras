package cn.zswltech.mithras.service.controller;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.api.FileApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.file.*;
import cn.zswltech.mithras.service.auth.aop.TokenParamAuth;
import cn.zswltech.mithras.service.service.materialsfile.FileExportService;
import cn.zswltech.mithras.service.service.materialsfile.FileService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @ClassName FileController
 * @Author jackerhe
 * @Date 2022/11/20 9:56 上午
 * @Version 1.0
 **/
@RestController
public class FileController implements FileApi {

    @Resource
    private FileService fileService;
    @Resource
    private FileExportService fileExportService;

    @Override
    public R<FileUploadRSP> upload(@Valid FileUploadREQ fileUploadREQ) {
        return R.ok(fileService.upload(fileUploadREQ));
    }

    @Override
    public R<FileUploadPresignedRSP> presignedPut(@Valid FileUploadPresignedREQ req) {
        return R.ok(fileService.presignedPut(req));
    }

    @Override
    public R<FileUploadPresignedRSP> uploadRecord(@Valid FileUploadRecordREQ req) {
        fileService.uploadRecord(req);
        return R.ok();
    }

    @Override
    public R<PageR<FileListRSP>> list(@Valid FileListREQ req) {
        return R.ok(fileService.list(req));
    }

    @Override
    public R<List<Pair<String, List<FileListRSP>>>> listGroup(@Valid FileListREQ req) {
        return R.ok(fileService.listGroup(req));
    }

    @Override
    public R<Void> remove(@Valid FileRemoveREQ req) {
        fileService.remove(req);
        return R.ok();
    }

    @Override
    public R<FileDownLoadRSP> download(@Valid FileDownLoadREQ req) {
        return R.ok(fileService.download(req));
    }

    @Override
    public R<String> downloadTemplate(FileDownLoadTemplateREQ req) {
        return R.ok(fileService.downloadTemplate(req));
    }

    @Override
    public R<Void> batchRemove(@Valid FileBatchRemoveREQ req) {
        fileService.batchRemove(req);
        return R.ok();
    }

    @Override
    @TokenParamAuth
    public void batchDownload(@Valid FileBatchDownLoadREQ req) {
        fileService.batchDownload(req);
    }

    @Override
    public void export(FileExportREQ req) {
        fileExportService.export(req);
    }

    @Override
    public R<Void> rename(FileRenameREQ req) {
        fileService.rename(req);
        return R.ok();
    }


}
