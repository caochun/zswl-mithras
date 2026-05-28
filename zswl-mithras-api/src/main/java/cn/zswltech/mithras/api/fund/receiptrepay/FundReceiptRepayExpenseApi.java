package cn.zswltech.mithras.api.fund.receiptrepay;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayExpenseListREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayExpenseListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayExpenseModifyREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description 费用一览表
 * @date 2023-02-20
 */
@Api(tags = "费用一览表-接口")
public interface FundReceiptRepayExpenseApi {

    @ApiOperation("修改费用一览表")
    @PostMapping("/fund/receipt/repay/expense/modify")
    R<Void> modify(@RequestBody @Valid List<FundReceiptRepayExpenseModifyREQ> req);

    @ApiOperation("费用一览表列表")
    @PostMapping("/fund/receipt/repay/expense/list")
    R<PageR<FundReceiptRepayExpenseListRSP>> list(@RequestBody @Valid FundReceiptRepayExpenseListREQ req);

}