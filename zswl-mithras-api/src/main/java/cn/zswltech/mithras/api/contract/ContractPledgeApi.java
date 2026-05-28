package cn.zswltech.mithras.api.contract;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.ContractRelationREQ;
import cn.zswltech.mithras.dto.contract.ContractRelationRSP;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeAddREQ;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeListRSP;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeModifyREQ;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeRemoveREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;


/**
* @description 合同-质押措施
* @author vico
* @date 2022-08-12
*/
@Api(tags = "合同-质押措施-接口")
public interface ContractPledgeApi {

    @ApiOperation("新增合同-质押措施")
    @PostMapping("/contract/pledge/add")
    R<Boolean> add(@Valid ContractPledgeAddREQ req);

    @ApiOperation("修改合同-质押措施")
    @PostMapping("/contract/pledge/modify")
    R<Void> modify(@Valid ContractPledgeModifyREQ req);

    @ApiOperation("合同-质押措施列表")
    @PostMapping("/contract/pledge/list")
    R<List<ContractPledgeListRSP>> list(@RequestBody @Valid ContractIdListREQ req);

    @ApiOperation("删除合同-质押措施")
    @PostMapping("/contract/pledge/remove")
    R<Void> remove(@RequestBody @Valid ContractPledgeRemoveREQ req);

    @ApiOperation("合同-质押措施-生成质押合同编号")
    @PostMapping("/contract/pledge/contractcode/generate")
    R<Void> generatePledgeContractCode(@RequestBody @Valid ContractSingleIdREQ contractSingleIdREQ);

    @ApiOperation("下载导入模板")
    @GetMapping("/contract/pledge/template/download")
    R<String> downloadPledgeItemTemplate();

    @ApiOperation("合同编号-质押人id查询合同编号")
    @PostMapping("/contract/pledge/relation/contract")
    R<List<ContractRelationRSP>> relation(@RequestBody @Valid ContractRelationREQ req);
}