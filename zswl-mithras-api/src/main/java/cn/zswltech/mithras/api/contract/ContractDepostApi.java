package cn.zswltech.mithras.api.contract;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.depost.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * @author dingqi
 * @date 2022/8/12
 * @description
 */
@Api(tags = "合同管理-保证金退抵")
public interface ContractDepostApi {
    @ApiOperation("保证金合同抵扣租金校验")
    @PostMapping("/contract/depost/check")
    R check(@RequestBody @Valid ContractDepostREQ req);

    @ApiOperation("保证金合同抵扣租金信息查看")
    @PostMapping("/contract/depost/depostInfo")
    R depostInfo(@RequestParam("id") Long id);

    @ApiOperation("保证金合同抵扣租金信息列表")
    @PostMapping("/contract/depost/rentList")
    R rentList(@RequestBody @Valid ContractRetreatREQ req);

    @ApiOperation("保证金合同抵扣租金信息新增")
    @PostMapping("/contract/depost/rentAdd")
    R rentAdd(@RequestBody @Valid ContractDeductRentInfoREQ req);

//    @ApiOperation("保证金合同抵扣租金信息更新")
//    @PostMapping("/contract/depost/rentUpdate")
//    R rentUpdate(@RequestBody @Valid ContractDeductRentInfoREQ req);

    @ApiOperation("保证金合同抵扣租金信息删除")
    @PostMapping("/contract/depost/rentDel")
    R rentDel(@RequestBody @Valid ContractDeductRentREQ req);

    @ApiOperation("保证金合同抵扣租金信息关联付款信息")
    @PostMapping("/contract/depost/getByContractId")
    R getByContractId(@RequestBody @Valid ContractDepostREQ req);

    @ApiOperation("保证金合同抵扣租金信息关联现金流信息")
    @PostMapping("/contract/depost/getByCollectionId")
    R getByCollectionId(@RequestParam("collectionId") String collectionId);

    @ApiOperation("保证金合同抵扣租金信息模板下载")
    @GetMapping("/contract/depost/downloadtemplete")
    R downloadtemplete();

    @ApiOperation("保证金合同抵扣租金信息保存")
    @PostMapping("/contract/depost/depostSave")
    R depostSave(@RequestBody @Valid ContractRetreatSubmitREQ req);

    @ApiOperation("保证金合同抵扣租金信息提交审批")
    @PostMapping("/contract/depost/submit")
    R submit(@RequestBody @Valid ContractRetreatSubmitREQ req);

    @ApiOperation("保证金退回通知确认")
    @PostMapping("/contract/depost/noticeCommit")
    R noticeCommit(@RequestParam("prepareId") String prepareId);

}
