package cn.zswltech.mithras.api.filingmaterials;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.filingmaterials.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author lllin
 * @create: 2025-12-03
 **/
@Api(tags = "其他资料归档-接口")
@RequestMapping("/other/filingMaterial")
public interface OtherFilingMaterialsApi {

    @ApiOperation("获取系统内评审项目")
    @PostMapping("/list")
    R<PageR<OtherPageListRSP>> list(@RequestBody @Valid OtherPageListREQ req);

    @ApiOperation("获取资料类型")
    @PostMapping("/getMaterialsDesc")
    R<String> getMaterialsDesc(@RequestBody @Valid FilingBaseREQ req);

    @ApiOperation("保存资料类型")
    @PostMapping("/saveMaterialsDesc")
    R<Void> saveMaterialsDesc(@RequestBody @Valid OtherSaveMaterialsDescREQ req);


    @ApiOperation("检查资料类型")
    @PostMapping("/checkMaterialsDesc")
    R<Boolean> checkMaterialsDesc(@RequestBody @Valid FilingBaseREQ req);

    @ApiOperation("获取系统内评审项目")
    @PostMapping("/getReviewProject")
    R<List<OtherProjectListRESP>> getReviewProject();

    @ApiOperation("项目确认")
    @GetMapping("/select/confirm")
    R<String> selectConfirm(@RequestParam("projReviewId") String projReviewId);

    @ApiOperation("取消操作")
    @GetMapping("/cancel")
    R<Void> cancel(@RequestParam("id") String id);

    @ApiOperation("提交审批")
    @PostMapping("/commit")
    R<Void> commit(@RequestBody @Valid FilingBaseREQ req);

    @ApiOperation("批量下载")
    @PostMapping("/batchDownload")
    void batchDownload(@RequestBody @Valid FundFilingMaterialsBatchDownloadREQ req) throws IOException;

    @ApiOperation("获取展示一级目录")
    @PostMapping(path = "/getOperationsDirDict")
    R<Map<String, List<SelectRSP>>> getOperationsDirDict(@RequestBody @Valid FilingBaseREQ req) ;

    @ApiOperation("删除资料")
    @PostMapping("/file/remove")
    R<Void> remove(@RequestBody @Valid FilingBasicRemoveREQ filingBasicRemoveREQ);

    @ApiOperation("台账列表导出")
    @PostMapping("/export")
    void export(@RequestBody OtherPageListREQ req);

    @ApiOperation("测试test")
    @GetMapping("/test")
    void test() ;

}
