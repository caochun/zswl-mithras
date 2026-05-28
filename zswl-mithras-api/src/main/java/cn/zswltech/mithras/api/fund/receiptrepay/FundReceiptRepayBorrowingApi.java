package cn.zswltech.mithras.api.fund.receiptrepay;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBorrowingListREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBorrowingListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBorrowingModifyREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description 借款流入
 * @date 2023-02-20
 */
@Api(tags = "借款流入-接口")
public interface FundReceiptRepayBorrowingApi {

    @ApiOperation("修改借款流入")
    @PostMapping("/fund/receipt/repay/borrowing/modify")
    R<Void> modify(@RequestBody @Valid List<FundReceiptRepayBorrowingModifyREQ> req);

    @ApiOperation("借款流入列表")
    @PostMapping("/fund/receipt/repay/borrowing/list")
    R<PageR<FundReceiptRepayBorrowingListRSP>> list(@RequestBody @Valid FundReceiptRepayBorrowingListREQ req);

}