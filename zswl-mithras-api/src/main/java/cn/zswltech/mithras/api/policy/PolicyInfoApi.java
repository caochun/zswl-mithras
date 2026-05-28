package cn.zswltech.mithras.api.policy;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.payment.dto.PaymentPoliceImportREQ;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.policy.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @create: 2023-06-15
 **/
@Api(tags = "保单管理接口")
public interface PolicyInfoApi {

    @ApiOperation("新增项目列表")
    @GetMapping("/policy/add/proj/list")
    R<List<SelectRSP>> projList();

    @ApiOperation("合同列表")
    @GetMapping("/policy/add/contract/list")
    R<List<SelectRSP>> contractList(PolicyAddContractREQ req);

    @ApiOperation("新增保单信息")
    @PostMapping("/policy/info/add")
    R<Long> add(@Valid PolicyInfoAddREQ req);

    @ApiOperation("提交保单信息")
    @PostMapping("/policy/info/submit")
    R<Void> submit(@Valid @RequestBody PolicyInfoSubmitREQ req);

    @ApiOperation("修改保单信息")
    @PostMapping("/policy/info/modify")
    R<Void> modify(@Valid PolicyInfoModifyREQ req);

    @ApiOperation("保单详情-已废弃")
    //@PostMapping("/policy/info/detail")
    R<PolicyInfoDetailRSP> detail(@RequestBody @Valid PolicyInfoDetailREQ req);

    @ApiOperation("保单信息列表")
    @PostMapping("/policy/info/list")
    R<PageR<PolicyInfoListRSP>> list(@RequestBody @Valid PolicyInfoListREQ req);

    @ApiOperation("删除保单信息")
    @PostMapping("/policy/info/remove")
    R<Void> remove(@RequestBody @Valid PolicyInfoRemoveREQ req);

    @ApiOperation("导入保单信息")
    @PostMapping("/policy/import")
    R<String> importExcel(@Valid PaymentPoliceImportREQ paymentPoliceImportREQ);


}
