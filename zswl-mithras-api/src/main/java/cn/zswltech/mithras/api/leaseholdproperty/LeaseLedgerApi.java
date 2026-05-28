package cn.zswltech.mithras.api.leaseholdproperty;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.MultiplePkREQ;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.file.FileUploadRSP;
import cn.zswltech.mithras.dto.file.template.FileTemplateListREQ;
import cn.zswltech.mithras.dto.file.template.FileTemplateListRSP;
import cn.zswltech.mithras.dto.leaseholdproperty.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * @author yangxiong
 * @description 租赁物台账接口
 * @since 2023-09-19
 */
@Api(tags = "租赁物审核-台账接口")
@RequestMapping("/ledger")
public interface LeaseLedgerApi {

    @ApiOperation("台账分页列表")
    @PostMapping("/page")
    R<PageR<LeaseLedgerMainRSP>> getPage(@RequestBody LeaseLedgerMainREQ param);

    @ApiOperation("台账列表导出")
    @PostMapping("/download")
    void download(@RequestBody LeaseLedgerMainREQ param);

    @ApiOperation("详情-合同详情信息")
    @PostMapping("/detail/contract")
    R<LedgerContractDetailRSP> getContractInfoById(@RequestBody @Valid LeaseLedgerDetailREQ param);

    @ApiOperation("租赁物查重-中登网-查询")
    @PostMapping("/detail/check-repeat/select")
    R<LeaseCheckRepeatRSP> getLeaseCheckRepeatById(@RequestBody @Valid LeaseLedgerDetailREQ param);

    @ApiOperation("租赁物查重-中登网-保存")
    @PostMapping("/detail/check-repeat/save")
    R<Boolean> checkRepeatSave(@RequestBody @Valid LeaseCheckRepeatREQ param);

    @ApiOperation("租赁物总金额-保存")
    @PostMapping("/detail/leaseitem/totalamount/save")
    R<Void> saveLeaseItemTotalAmount(@RequestBody @Valid LeaseItemAmountREQ req);

    @ApiOperation("租赁物元数据保存")
    @PostMapping("/detail/leaseitem/metadata/save")
    R<Void> saveLeaseItemMetadata(@RequestBody @Valid LeaseItemMetadataREQ req);

    @ApiOperation("租赁物中文类型初始化")
    @PostMapping(path = "/detail/init")
    R<Void> init();

    @ApiOperation("租赁物元数据信息")
    @PostMapping("/detail/leaseitem/metadata/get")
    R<LeaseItemMetadataRSP> getLeaseItemMetadata(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("租赁物清单-模板下载")
    @PostMapping("/detail/leaseitem/template/download")
    void downloadLeaseItemTemplate(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("租赁物清单-导入")
    @PostMapping("/detail/leaseitem/import")
    R<Void> importLeaseItemList(@Valid LeaseItemImportREQ req);

    @ApiOperation("租赁物清单-列表")
    @PostMapping("/detail/leaseitem/pagelist")
    R<LeaseItemListRSP> listItemWithPage(@RequestBody @Valid LeaseItemListREQ req);

    @ApiOperation("租赁物清单-导出")
    @PostMapping("/detail/leaseitem/export")
    void exportLeaseItemList(@RequestBody @Valid LeaseItemListExportREQ req);

    @ApiOperation("租赁物清单-批量删除")
    @PostMapping("/detail/leaseitem/remove")
    R<Void> removeLeaseItemList(@RequestBody @Valid MultiplePkREQ req);

    @ApiOperation("租赁物文件加水印上传-流程中使用")
    @PostMapping("/flow/upload")
    R<List<FileUploadRSP>> flowUpdate(@Valid LeaseFlowUploadREQ param);

    @ApiOperation("租赁物-中登网查重-模板下载")
    @PostMapping("/detail/check-repeat/template/download")
    R<PageR<FileTemplateListRSP>> downloadCheckRepeatTemplate(@RequestBody @Valid FileTemplateListREQ req);

    @ApiOperation("租赁物内部查重")
    @PostMapping("/detail/leaseitem/dedup")
    R<LeaseItemRedupRSP> dedup(@RequestBody @Valid LeaseItemListREQ req);
}
