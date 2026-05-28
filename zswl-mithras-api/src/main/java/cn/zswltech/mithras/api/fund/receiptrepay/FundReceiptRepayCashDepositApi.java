package cn.zswltech.mithras.api.fund.receiptrepay;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashDepositListREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashDepositListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashDepositModifyREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description 保证金明细
 * @date 2023-02-20
 */
@Api(tags = "保证金明细-接口")
public interface FundReceiptRepayCashDepositApi {

    @ApiOperation("修改保证金明细")
    @PostMapping("/fund/receipt/repay/cash/deposit/modify")
    R<Void> modify(@RequestBody @Valid List<FundReceiptRepayCashDepositModifyREQ> req);

    @ApiOperation("保证金明细列表")
    @PostMapping("/fund/receipt/repay/cash/deposit/list")
    R<PageR<FundReceiptRepayCashDepositListRSP>> list(@RequestBody @Valid FundReceiptRepayCashDepositListREQ req);

}