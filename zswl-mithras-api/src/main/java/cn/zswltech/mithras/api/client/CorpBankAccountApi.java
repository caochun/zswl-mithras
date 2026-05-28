package cn.zswltech.mithras.api.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.bankaccount.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author luyi
 */
@Api(tags = "法人银行账户-接口")
public interface CorpBankAccountApi {

    @ApiOperation("银行账号新增")
    @PostMapping("/corp/bank/account/add")
    R<Void> add(@RequestBody @Valid CorpBankAccountAddREQ req);

    @ApiOperation("银行账号变更")
    @PostMapping("/corp/bank/account/modify")
    R<Void> modify(@RequestBody @Valid CorpBankAccountModifyREQ req);

    @ApiOperation("银行账号删除")
    @PostMapping("/corp/bank/account/remove")
    R<Void> remove(@RequestBody @Valid CorpBankAccountRemoveREQ req);

    @ApiOperation("银行账号列表")
    @PostMapping("/corp/bank/account/list")
    R<PageR<CorpBankAccountListRSP>> list(@RequestBody @Valid CorpBankAccountListREQ req);

    @ApiOperation("版本银行账号列表")
    @PostMapping("/corp/versioned/bank/account/list")
    R<PageR<CorpBankAccountListRSP>> versionedList(@RequestBody @Valid CorpVersionedBankAccountListREQ req);


}
