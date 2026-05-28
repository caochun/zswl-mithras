package cn.zswltech.mithras.api.filingmaterials;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.filingmaterials.FilingBaseREQ;
import cn.zswltech.mithras.dto.filingmaterials.FilingBasicRemoveREQ;
import cn.zswltech.mithras.dto.filingmaterials.FundFilingMaterialsBatchDownloadREQ;
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
@Api(tags = "租后检查资料归档-接口")
@RequestMapping("/after/filingMaterial")
public interface AfterFilingMaterialsApi {


    @ApiOperation("批量下载")
    @PostMapping("/batchDownload")
    void batchDownload(@RequestBody @Valid FundFilingMaterialsBatchDownloadREQ req) throws IOException;

    @ApiOperation("获取展示一级目录")
    @PostMapping(path = "/getOperationsDirDict")
    R<Map<String, List<SelectRSP>>> getOperationsDirDict(@RequestBody @Valid FilingBaseREQ req) ;

    @ApiOperation("删除资料")
    @PostMapping("/file/remove")
    R<Void> remove(@RequestBody @Valid FilingBasicRemoveREQ filingBasicRemoveREQ);

    @ApiOperation("测试test")
    @GetMapping("/test")
    void test() ;

}
