package cn.zswltech.mithras.api.policy;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.payment.dto.PaymentPoliceTmpImportREQ;
import cn.zswltech.mithras.dto.policy.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
* @description 保单暂存表c
* @author vico
* @date 2023-10-23
*/
@Api(tags = "保单暂存表c-接口")
public interface PolicyInfoTmpApi {

    @ApiOperation("新增保单暂存表c")
    @PostMapping("/policy/info/tmp/add")
    R<Long> add(@RequestBody @Valid PolicyInfoTmpAddREQ req);

    @ApiOperation("修改保单暂存表c")
    @PostMapping("/policy/info/tmp/modify")
    R<Void> modify(@RequestBody @Valid PolicyInfoTmpModifyREQ req);

    @ApiOperation("保单暂存表c列表")
    @PostMapping("/policy/info/tmp/list")
    R<PageR<PolicyInfoTmpListRSP>> list(@RequestBody @Valid PolicyInfoTmpListREQ req);

//    @ApiOperation("保单暂存表c回显-工作台暂存保单处使用")
//    @PostMapping("/policy/info/tmp/get")
//    R<PolicyInfoTmpListRSP> get(@RequestBody @Valid PolicyInfoTmpDetailREQ req);

    @ApiOperation("删除保单暂存表c")
    @PostMapping("/policy/info/tmp/remove")
    R<Void> remove(@RequestBody @Valid PolicyInfoTmpRemoveREQ req);

    @ApiOperation("导入保单信息")
    @PostMapping("/policy/info/tmp/import")
    R<String> importExcel(@Valid PaymentPoliceTmpImportREQ paymentPoliceImportREQ);

    @ApiOperation("保单暂存表c导出")
    @PostMapping("/policy/info/tmp/export")
    R<Void> policyTmpExport(@RequestBody @Valid PolicyTmpExportREQ req);


}