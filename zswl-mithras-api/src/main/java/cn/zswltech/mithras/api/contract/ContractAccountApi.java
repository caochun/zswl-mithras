package cn.zswltech.mithras.api.contract;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.account.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;


/**
* @description 合同-收款账户表
* @author vico
* @date 2022-08-12
*/
@Api(tags = "合同-账户表-接口")
public interface ContractAccountApi {

    @ApiOperation("新增合同-账户表")
    @PostMapping("/contract/account/add")
    R<Void> add(@RequestBody @Valid ContractAccountAddREQ req);

    @ApiOperation("修改合同-账户表")
    @PostMapping("/contract/account/modify")
    R<Void> modify(@RequestBody @Valid List<ContractAccountModifyREQ> req);

    @ApiOperation("合同-账户表列表")
    @PostMapping("/contract/account/list")
    R<List<ContractAccountListRSP>> list(@RequestBody @Valid ContractAccountListREQ req);

    @ApiOperation("删除合同-收款账户表")
    @PostMapping("/contract/account/remove")
    R<Void> remove(@RequestBody @Valid ContractAccountRemoveREQ req);

}