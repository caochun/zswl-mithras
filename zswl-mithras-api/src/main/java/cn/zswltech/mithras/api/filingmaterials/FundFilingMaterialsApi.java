package cn.zswltech.mithras.api.filingmaterials;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.file.FileDownLoadRSP;
import cn.zswltech.mithras.dto.filingmaterials.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author lllin
 * @create: 2025-12-03
 **/
@Api(tags = "资金资料归档-接口")
@RequestMapping("/fund/filingMaterial")
public interface FundFilingMaterialsApi {


    @ApiOperation("批量下载")
    @PostMapping("/batchDownload")
    void batchDownload(@RequestBody @Valid FundFilingMaterialsBatchDownloadREQ req) throws IOException;

    @ApiOperation("获取运营终审列表展示一级目录")
    @PostMapping(path = "/getOperationsDirDict")
    R<Map<String, List<SelectRSP>>> getOperationsDirDict(@RequestBody @Valid FilingBaseREQ req) ;

    @ApiOperation("下载文件")
    @GetMapping("/download")
    void download(@Valid FilingFileDownloadREQ filingFileDownloadREQ) throws IOException;

    @ApiOperation("校验文件上传")
    @PostMapping("/checkFile")
    R<Boolean> checkFile(@RequestBody @Valid FilingBaseREQ req);

    @ApiOperation("测试test")
    @GetMapping("/test")
    void test() ;

}
