package cn.zswltech.mithras.api.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.normal.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author junke
 */
@Api(tags = "自然人银行账户-接口")
public interface NormalBankAccountApi {

    @ApiOperation("自然人银行账户-新增")
    @PostMapping("/normal/bank/account/add")
    R<Void> add(@RequestBody @Valid NormalBankAccountAddREQ req);

    @ApiOperation("自然人银行账户-修改")
    @PostMapping("/normal/bank/account/modify")
    R<Void> modify(@RequestBody @Valid NormalBankAccountModifyREQ req);

    @ApiOperation("自然人银行账户-删除")
    @PostMapping("/normal/bank/account/remove")
    R<Void> remove(@RequestBody @Valid NormalBankAccountRemoveREQ req);

    @ApiOperation("自然人银行账户-列表")
    @PostMapping("/normal/bank/account/list")
    R<PageR<NormalBankAccountListRSP>> list(@RequestBody @Valid NormalBankAccountListREQ req);
}
