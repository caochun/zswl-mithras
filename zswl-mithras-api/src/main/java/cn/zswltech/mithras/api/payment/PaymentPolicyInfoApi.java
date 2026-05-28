package cn.zswltech.mithras.api.payment;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.payment.dto.PaymentPoliceImportREQ;
import cn.zswltech.mithras.dto.payment.lib.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

/**
* @description 保单信息接口
* @author zhaozhengkang
* @date 2022-09-13
*/
@Api(tags = "付款申请-保单信息接口")
@RestController
public interface PaymentPolicyInfoApi {

    @ApiOperation("新增保单信息")
    @PostMapping("/payment/policy/info/add")
    R<Void> add(@RequestParam("files") MultipartFile[] files, Long paymentId, PaymentPolicyInfoAddREQ req);

    @ApiOperation("修改保单信息")
    @PostMapping("/payment/policy/info/modify")
    R<Void> modify(@RequestParam("files") MultipartFile[] files, Long id,  PaymentPolicyInfoModifyREQ req);


    @ApiOperation("修改保单信息勾选框")
    @PostMapping("/payment/policy/info/modify/flag")
    R<Void> modifyFlag(@RequestBody @Valid PaymentPolicyInfoModifyFlagREQ req);

    @ApiOperation("保单信息列表")
    @PostMapping("/payment/policy/info/list")
    R<PageR<PaymentPolicyInfoListRSP>> list(@RequestBody @Valid PaymentPolicyInfoListREQ req);

    @ApiOperation("下载保单信息")
    @GetMapping("/payment/policy/info/export")
    R<Void> export(@Valid PaymentPolicyInfoExportREQ req);

    @ApiOperation("删除保单信息")
    @PostMapping("/payment/policy/info/remove")
    R<Void> remove(@RequestBody @Valid PaymentPolicyInfoRemoveREQ req);

    @ApiOperation("批量删除保单信息")
    @PostMapping("/payment/policy/info/removeBatch")
    R<Void> removeBatch(@RequestBody @Valid PaymentPolicyInfoRemoveBatchREQ req);

    @ApiOperation("导入保单信息")
    @PostMapping("/payment/policy/import")
    R<String> importExcel(@Valid PaymentPoliceImportREQ paymentPoliceImportREQ);

    @ApiOperation("下载保单模板")
    @GetMapping("/payment/policy/template/download")
    R<String> downloadTemplate();

}