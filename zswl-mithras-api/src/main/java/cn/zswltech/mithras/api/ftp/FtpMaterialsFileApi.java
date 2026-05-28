package cn.zswltech.mithras.api.ftp;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.ftp.FtpBatchIdsReq;
import cn.zswltech.mithras.dto.ftp.FtpMaterialListRSP;
import cn.zswltech.mithras.dto.ftp.FtpMaterialListReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/10 09:59
 */
@Api(tags = "ftp资料相关接口")
@RequestMapping("/ftp/materials")
public interface FtpMaterialsFileApi {
    @ApiOperation("文件列表")
    @PostMapping("/file/list")
    R<List<FtpMaterialListRSP>> fileList(@RequestBody @Valid FtpMaterialListReq req);

    @ApiOperation("文件上传")
    @PostMapping("/file/upload")
    R<Void> fileUpload(@RequestParam MultipartFile file,
                       @RequestParam @ApiParam("FTP_QUARTERLY_GUIDANCE(季度)、FTP_MONTHLY_GUIDANCE(月度)") String bizType,
                       @RequestParam @ApiParam("所属的指导id")Long belongId);

    @ApiOperation("文件删除")
    @PostMapping("/file/remove")
    R<Void> fileRemove(@RequestBody @Valid FtpBatchIdsReq req);

    @ApiOperation("文件下载")
    @GetMapping("/file/download")
    R<FileListRSP> fileDownload(@Valid FtpBatchIdsReq req);
}
