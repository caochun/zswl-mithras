package cn.zswltech.mithras.api.contract;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.leaseitem.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author dingqi
 * @date 2022/8/15
 * @description
 */
@Api(tags = "合同管理-租赁物清单相关接口")
public interface ContractLeaseItemApi {
    @ApiOperation("导入租赁物清单")
    @PostMapping("/contract/leaseitem/import")
    R<Void> importExcel(@Valid ContractLeaseItemImportREQ contractLeaseItemImportREQ);

    @ApiOperation("租赁物清单列表")
    @PostMapping("/contract/leaseitem/list")
    R<ContractLeaseItemListRSP> listLeaseItem(@RequestBody @Valid ContractLeaseItemListREQ req);

    @ApiOperation("下载导入模板")
    @GetMapping("/contract/leaseitem/template/download")
    R<String> downloadTemplate();

    @ApiOperation("租赁物清单预选择")
    @PostMapping("/contract/leaseitem/prechoose")
    R<ContractPreChooseLeaseItemRSP> getPreChooseLeaseItem(@RequestBody @Valid ContractPreChooseLeaseItemREQ req);

    @ApiOperation("租赁物清单选择")
    @PostMapping("/contract/leaseitem/choose")
    R<Void> chooseLeaseItem(@RequestBody @Valid ContractChooseLeaseItemREQ req);

    @ApiOperation("导出租赁物清单")
    @PostMapping("/contract/leaseitem/export")
    void exportExcel(@RequestBody @Valid ContractLeaseItemExportREQ req);

    @ApiOperation("保存租赁物总额")
    @PostMapping("/contract/leaseitem/totalamount/save")
    R<Void> saveLeaseItemTotalAmount(@RequestBody @Valid ContractLeaseItemTotalAmountREQ req);

    @ApiOperation("检查是否存在审批中的租赁物")
    @PostMapping("/contract/leaseitem/inprocess/check")
    R<Void> checkLeaseItemInProcess(@RequestBody @Valid ContractSingleIdREQ req);
}
