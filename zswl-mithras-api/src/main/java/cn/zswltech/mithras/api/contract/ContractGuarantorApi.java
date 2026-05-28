package cn.zswltech.mithras.api.contract;
import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.guarantor.*;
import cn.zswltech.mithras.dto.contract.ContractRelationREQ;
import cn.zswltech.mithras.dto.contract.ContractRelationRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

import cn.zswltech.mithras.api.common.R;

import java.util.List;


/**
* @description 合同-担保措施
* @author vico
* @date 2022-08-12
*/
@Api(tags = "合同-担保措施-接口")
public interface ContractGuarantorApi {
    @ApiOperation("合同-担保措施-生成保证合同编号")
    @PostMapping("/contract/guarantor/contractcode/generate")
    R<Void> generateGuarantorContractCode(@RequestBody @Valid ContractSingleIdREQ contractSingleIdREQ);

    @ApiOperation("新增合同-担保措施")
    @PostMapping("/contract/guarantor/add")
    R<Boolean> add(@Valid ContractGuarantorAddREQ req);

    @ApiOperation("修改合同-担保措施")
    @PostMapping("/contract/guarantor/modify")
    R<Void> modify(@Valid ContractGuarantorModifyREQ req);

    @ApiOperation("合同编号-担保人id查询合同编号")
    @PostMapping("/contract/guarantor/relation/contract")
    R<List<ContractRelationRSP>> relation(@RequestBody @Valid ContractRelationREQ req);

    @ApiOperation("合同-担保措施列表")
    @PostMapping("/contract/guarantor/list")
    R<List<ContractGuarantorListRSP>> list(@RequestBody @Valid ContractIdListREQ req);

    @ApiOperation("删除合同-担保措施")
    @PostMapping("/contract/guarantor/remove")
    R<Void> remove(@RequestBody @Valid ContractGuarantorRemoveREQ req);

}