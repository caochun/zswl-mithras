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
@Api(tags = "业务资料归档-接口")
@RequestMapping("/filingMaterial")
public interface FilingMaterialsApi {


    @ApiOperation("批量下载")
    @PostMapping("/batchDownload")
    void batchDownload(@RequestBody @Valid FilingMaterialsREQ req) throws IOException;

    @ApiOperation("项目资料归档-获取tab")
    @PostMapping("/getTab")
    R<List<FilingMaterialsTabRSP>> filingTab(@RequestBody @Valid FilingBaseREQ req);

    @ApiOperation("项目资料归档-基本信息资料清单")
    @PostMapping("/getCustomerReferenceMaterials")
    R<FilingProjMaterialsListListRSP> getCustomerReferenceMaterials(@RequestBody @Valid FilingMaterialsQueryREQ req);

    @ApiOperation("项目资料归档-资料同步是否展示")
    @PostMapping("/synchronizationButtonFlag")
    R<Boolean> synchronizationButtonFlag(@RequestBody @Valid FilingMaterialsSynchronizationREQ req);

    @ApiOperation("项目资料归档-资料同步")
    @PostMapping("/materialsSynchronization")
    R<Void> materialsSynchronization(@RequestBody @Valid FilingMaterialsSynchronizationREQ req);

    @ApiOperation("项目资料归档-资料引入")
    @PostMapping("/materialsImport")
    R<Void> materialsImport(@RequestBody @Valid FilingMaterialsImportREQ req);

    @ApiOperation("项目资料归档-提交审批")
    @PostMapping("/effect")
    R<String> effect(@RequestBody @Valid FilingBaseREQ req);


    @ApiOperation("项目资料归档-非基础信息的参考资料")
    @PostMapping("/getNonCustomerReferenceMaterials")
    R<FilingMaterialsQueryRSP> getNonCustomerReferenceMaterials(@RequestBody @Valid FilingMaterialsQueryREQ req);

    @ApiOperation("获取运营终审列表展示一级目录")
    @PostMapping(path = "/getOperationsDirDict")
    R<Map<String, List<SelectRSP>>> getOperationsDirDict(@RequestBody @Valid FilingBaseREQ req) ;

    @ApiOperation("下载文件")
    @GetMapping("/download")
    R<FileDownLoadRSP> download(@Valid FilingFileDownloadREQ filingFileDownloadREQ) throws IOException;

    @ApiOperation("下载资料清单模板")
    @GetMapping("/template/download")
    void downloadTemplate(@Valid FilingTemplateDownloasREQ filingTemplateDownloasREQ);

    @ApiOperation("删除资料")
    @PostMapping("/file/remove")
    R<Void> remove(@RequestBody @Valid FilingBasicRemoveREQ filingBasicRemoveREQ);

    @ApiOperation("获取当前审批流程的最新节点")
    @PostMapping("/getCurTaskDefKey")
    R<String> getCurTaskDefKey(@RequestBody @Valid FilingProcessREQ filingProcessREQ);

}
