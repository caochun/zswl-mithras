package cn.zswltech.mithras.api.contract;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.prepayment.ContractPrepaymentAddREQ;
import cn.zswltech.mithras.dto.contract.prepayment.ContractPrepaymentDetailRSP;
import cn.zswltech.mithras.dto.contract.prepayment.ContractPrepaymentModifyREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @description 合同-提前还款表
 * @author vico
 * @date 2022-08-12
 */
@Api(tags = "合同-提前还款表表-接口")
public interface ContractPrepaymentApi {

    @ApiOperation("新增合同-提前还款表")
    @PostMapping("/contract/prepayment/add")
    R<Void> add(@RequestBody @Valid ContractPrepaymentAddREQ req);

    @ApiOperation("修改合同-提前还款表")
    @PostMapping("/contract/prepayment/modify")
    R<Void> modify(@RequestBody @Valid ContractPrepaymentModifyREQ req);

    @ApiOperation("合同-提前还款表列表")
    @PostMapping("/contract/prepayment/list")
    R<ContractPrepaymentDetailRSP> list(@RequestBody @Valid ContractIdListREQ req);

    @ApiOperation("提前还款表-计算")
    @PostMapping("/contract/prepayment/calculation")
    R<ContractPrepaymentAddREQ> calculation(@RequestBody ContractPrepaymentAddREQ req);

}
