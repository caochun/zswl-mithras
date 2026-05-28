package cn.zswltech.mithras.api.contract;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.ContractRelationREQ;
import cn.zswltech.mithras.dto.contract.ContractRelationRSP;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageAddREQ;
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageListRSP;
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageModifyREQ;
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageRemoveREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;


/**
* @description 合同-抵押措施
* @author vico
* @date 2022-08-12
*/
@Api(tags = "合同-抵押措施-接口")
public interface ContractMortgageApi {
    @ApiOperation("合同-抵押措施-生成抵押合同编号")
    @PostMapping("/contract/mortgage/contractcode/generate")
    R<Void> generateMortgageContractCode(@RequestBody @Valid ContractSingleIdREQ contractSingleIdREQ);

    @ApiOperation("新增合同-抵押措施")
    @PostMapping("/contract/mortgage/add")
    R<Void> add(@Valid ContractMortgageAddREQ req);

    @ApiOperation("修改抵押措施")
    @PostMapping("/contract/mortgage/modify")
    R<Void> modify(@Valid ContractMortgageModifyREQ req);

    @ApiOperation("合同编号-抵押人id查询合同编号")
    @PostMapping("/contract/mortgage/relation/contract")
    R<List<ContractRelationRSP>> relation(@RequestBody @Valid ContractRelationREQ req);

    @ApiOperation("合同-抵押措施列表")
    @PostMapping("/contract/mortgage/list")
    R<List<ContractMortgageListRSP>> list(@RequestBody @Valid ContractIdListREQ req);

    @ApiOperation("删除合同-抵押措施")
    @PostMapping("/contract/mortgage/remove")
    R<Void> remove(@RequestBody @Valid ContractMortgageRemoveREQ req);

    @ApiOperation("下载导入模板")
    @GetMapping("/contract/mortgage/template/download")
    R<String> downloadTemplate();

}