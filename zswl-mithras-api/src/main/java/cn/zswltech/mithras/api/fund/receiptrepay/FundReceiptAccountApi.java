package cn.zswltech.mithras.api.fund.receiptrepay;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptAccountAddREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptAccountModifyREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptAccountListREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptAccountListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptAccountRemoveREQ;

/**
* @description 资金管理-融资管理-对方收款账户
* @author zhaozhengkang
* @date 2023-02-22
*/
@Api(tags = "资金管理-融资管理-对方收款账户-接口")
public interface FundReceiptAccountApi {

    @ApiOperation("新增资金管理-融资管理-对方收款账户")
    @PostMapping("/fund/receipt/account/add")
    R<Void> add(@RequestBody @Valid FundReceiptAccountAddREQ req);

    @ApiOperation("修改资金管理-融资管理-对方收款账户")
    @PostMapping("/fund/receipt/account/modify")
    R<Void> modify(@RequestBody @Valid FundReceiptAccountModifyREQ req);

    @ApiOperation("资金管理-融资管理-对方收款账户列表")
    @PostMapping("/fund/receipt/account/list")
    R<PageR<FundReceiptAccountListRSP>> list(@RequestBody @Valid FundReceiptAccountListREQ req);

    @ApiOperation("删除资金管理-融资管理-对方收款账户")
    @PostMapping("/fund/receipt/account/remove")
    R<Void> remove(@RequestBody @Valid FundReceiptAccountRemoveREQ req);

}