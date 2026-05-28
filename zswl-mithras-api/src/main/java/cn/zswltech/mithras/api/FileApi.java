package cn.zswltech.mithras.api;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.file.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @ClassName FileApi
 * 文件上传下载相关
 * @Author jackerhe
 * @Date 2022/11/19 10:44 上午
 * @Version 1.0
 **/
@Api(tags = "文本相关接口")
public interface FileApi {

    @ApiOperation("上传文件")
    @PostMapping("/file/upload")
    R<FileUploadRSP> upload(@Valid FileUploadREQ fileUploadREQ);

    @ApiOperation("获取文件预上传地址")
    @PostMapping("/file/upload/presigned")
    R<FileUploadPresignedRSP> presignedPut(@RequestBody @Valid FileUploadPresignedREQ req);

    @ApiOperation("获取文件后信息记录")
    @PostMapping("/file/upload/record")
    R<FileUploadPresignedRSP> uploadRecord(@RequestBody @Valid FileUploadRecordREQ req);


    @ApiOperation("获取文件信息")
    @PostMapping("/file/list")
    R<PageR<FileListRSP>> list(@RequestBody @Valid FileListREQ req);

    @ApiOperation("获取文件分组信息")
    @PostMapping("/file/list/group")
    R<List<Pair<String, List<FileListRSP>>>> listGroup(@RequestBody @Valid FileListREQ req);

    @ApiOperation("删除文件")
    @PostMapping("/file/remove")
    R<Void> remove(@RequestBody @Valid FileRemoveREQ req);

    @ApiOperation("下载报告")
    @GetMapping("/file/download")
    R<FileDownLoadRSP> download(@Valid FileDownLoadREQ req);

    @ApiOperation("下载模板")
    @GetMapping("/file/download/template")
    R<String> downloadTemplate(@Valid FileDownLoadTemplateREQ req);

    @ApiOperation("批量删除文件")
    @PostMapping("/file/batch/remove")
    R<Void> batchRemove(@RequestBody @Valid FileBatchRemoveREQ req);

    @ApiOperation("批量下载报告")
    @GetMapping("/file/batch/download")
    void batchDownload(@Valid FileBatchDownLoadREQ req);

    @ApiOperation("统一文件导出接口")
    @PostMapping("/file/export")
    void export(@RequestBody @Valid FileExportREQ req);


    @ApiOperation("重命名文件")
    @PostMapping("/file/rename")
    R<Void> rename(@RequestBody @Valid FileRenameREQ req);

}
