package cn.zswltech.mithras.api.contract;
import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.tenantry.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.R;
import java.util.List;


/**
* @description 合同-承租人表
* @author vico
* @date 2022-08-12
*/
@Api(tags = "合同-承租人/债权人/债务人表-接口")
public interface ContractTenantryApi {


    @ApiOperation("修改合同-承租人/债权人/债务人表")
    @PostMapping("/contract/tenantry/modify")
    R<Void> modify(@Valid ContractTenantryModifyREQ req);

    @ApiOperation("合同-承租人/债权人/债务人表列表")
    @PostMapping("/contract/tenantry/list")
    R<List<ContractTenantryListRSP>> list(@RequestBody @Valid ContractIdListREQ req);

    @ApiOperation("删除合同-承租人/债权人/债务人表")
    @PostMapping("/contract/tenantry/remove")
    R<Void> remove(@RequestBody @Valid ContractTenantryRemoveREQ req);
}