package cn.zswltech.mithras.api.fund.directfinancing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @description 直接融资-还款账户
 * @author zhaozhengkang
* @date 2023-06-17
*/
@Api(tags = "直接融资-还款账户-接口")
public interface FundDirectFinancingPayAccountApi {

    @ApiOperation("新增直接融资-还款账户")
    @PostMapping("/fund/direct/financing/pay/account/add")
    R<Void> add(@RequestBody @Valid FundDirectFinancingPayAccountAddREQ req);

    @ApiOperation("修改直接融资-还款账户")
    @PostMapping("/fund/direct/financing/pay/account/modify")
    R<Void> modify(@RequestBody @Valid FundDirectFinancingPayAccountModifyREQ req);

    @ApiOperation("直接融资-还款账户列表")
    @PostMapping("/fund/direct/financing/pay/account/list")
    R<List<FundDirectFinancingPayAccountListRSP>> list(@RequestBody @Valid FundDirectFinancingPayAccountListREQ req);

    @ApiOperation("删除直接融资-还款账户")
    @PostMapping("/fund/direct/financing/pay/account/remove")
    R<Void> remove(@RequestBody @Valid FundDirectFinancingSingleIdREQ req);

}