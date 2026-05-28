package cn.zswltech.mithras.api.projestablish;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.projestablish.report.ProjEstablishReportDownloadREQ;
import cn.zswltech.mithras.dto.projestablish.report.ProjEstablishReportGenerateREQ;
import cn.zswltech.mithras.dto.projestablish.report.ProjEstablishReportListREQ;
import cn.zswltech.mithras.dto.projestablish.report.ProjEstablishReportListRSP;
import cn.zswltech.mithras.dto.projestablish.report.ProjEstablishReportRemoveREQ;
import cn.zswltech.mithras.dto.projestablish.report.ProjEstablishReportUploadREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * 立项报告文件
 *
 * @author wangchuanhao
 * @date 2022/7/22 10:36 AM
 */
@Api(tags = "立项报告文件-接口")
public interface ProjEstablishReportApi {

    @ApiOperation("立项报告文件-上传")
    @PostMapping("/proj/establish/report/upload")
    R<Void> upload(@RequestParam("file") MultipartFile file, @Valid ProjEstablishReportUploadREQ req);

    @ApiOperation("立项报告文件列表")
    @PostMapping("/proj/establish/report/list")
    R<List<Pair<String, List<ProjEstablishReportListRSP>>>> list(@RequestBody @Valid ProjEstablishReportListREQ req);

    @ApiOperation("立项报告文件-删除")
    @PostMapping("/proj/establish/report/remove")
    R<Void> remove(@RequestBody @Valid ProjEstablishReportRemoveREQ req);

    @ApiOperation("立项报告文件-下载")
    @GetMapping("/proj/establish/report/download")
    R<FileListRSP> download(@Valid ProjEstablishReportDownloadREQ req) throws IOException;

    @ApiOperation("立项报告文件-生成")
    @PostMapping("/proj/establish/report/generate")
    void generate(@RequestBody @Valid ProjEstablishReportGenerateREQ req) throws IOException;

    @ApiOperation("立项报告文件-生成-在线编辑")
    @PostMapping("/proj/establish/report/generateOnline")
    R<Long> generateOnline(@RequestBody @Valid ProjEstablishReportGenerateREQ req) throws IOException;

}
