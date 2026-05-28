package cn.zswltech.mithras.api.contract;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.receipt.*;
import cn.zswltech.mithras.dto.contract.rent.ContractReceiptComputeActualTaxRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/8/20
 * @description
 */
@Api(tags = "合同管理-借据管理")
public interface ContractReceiptApi {
    @ApiOperation("删除借据")
    @PostMapping("/contract/receipt/remove")
    R<Void> remove(@RequestBody @Valid ContractReceiptRemoveREQ contractReceiptRemoveREQ);

    @ApiOperation("更新实际IRR")
    @PostMapping("/contract/receipt/updateActualIRR")
    R<Void> updateActualIRR(@RequestBody @Valid ContractReceiptUpdateIrrREQ req);

    @ApiOperation("更新税额和不含税额")
    @PostMapping("/contract/receipt/updateActualTax")
    R<Void> updateActualTax(@RequestBody @Valid ContractReceiptUpdateActualTaxREQ req);

    @ApiOperation("计算财务成本")
    @PostMapping("/contract/receipt/queryActualTax")
    R<List<ContractReceiptComputeActualTaxRSP>> computeFinancialCosts(@RequestBody @Valid ContractReceiptQueryActualTaxREQ req);

    @ApiOperation("自动生成 实际租金表.docx")
    @PostMapping("/contract/receipt/generateActualRentFile")
    R<Void> generate(@RequestBody @Valid ContractReceiptQueryActualTaxREQ req) throws Exception;

    @ApiOperation("编辑区-更改借据的实际起租日期")
    @PostMapping("/contract/receipt/edit/updateActualStartDate")
    R<Void> updateActualStartDate(@RequestBody @Valid ContractReceiptUpdateStartDateREQ req);

    @ApiOperation("流程中-更改借据的实际起租日")
    @PostMapping("/contract/receipt/process/updateActualStartDate")
    R<Void> updateActualLeaseDate(@RequestBody @Valid ContractReceiptUpdateStartDateREQ req);

    @ApiOperation("通过合同查询借据")
    @PostMapping(path = "/receipt/list/bycontract")
    R<List<ContractReceiptInfoRSP>> listByContractId(@RequestBody @Valid ContractSingleIdREQ req);
}