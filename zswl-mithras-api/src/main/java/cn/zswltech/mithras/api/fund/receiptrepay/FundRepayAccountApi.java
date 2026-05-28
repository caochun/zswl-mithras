package cn.zswltech.mithras.api.fund.receiptrepay;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundRepayAccountAddREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundRepayAccountModifyREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundRepayAccountListREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundRepayAccountListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundRepayAccountRemoveREQ;

/**
* @description 资金管理-融资管理-我方付款账户
* @author zhaozhengkang
* @date 2023-02-22
*/
@Api(tags = "资金管理-融资管理-我方付款账户-接口")
public interface FundRepayAccountApi {

    @ApiOperation("新增资金管理-融资管理-我方付款账户")
    @PostMapping("/fund/repay/account/add")
    R<Void> add(@RequestBody @Valid FundRepayAccountAddREQ req);

    @ApiOperation("修改资金管理-融资管理-我方付款账户")
    @PostMapping("/fund/repay/account/modify")
    R<Void> modify(@RequestBody @Valid FundRepayAccountModifyREQ req);

    @ApiOperation("资金管理-融资管理-我方付款账户列表")
    @PostMapping("/fund/repay/account/list")
    R<PageR<FundRepayAccountListRSP>> list(@RequestBody @Valid FundRepayAccountListREQ req);

    @ApiOperation("删除资金管理-融资管理-我方付款账户")
    @PostMapping("/fund/repay/account/remove")
    R<Void> remove(@RequestBody @Valid FundRepayAccountRemoveREQ req);

}