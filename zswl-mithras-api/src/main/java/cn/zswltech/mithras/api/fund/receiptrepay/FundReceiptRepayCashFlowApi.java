package cn.zswltech.mithras.api.fund.receiptrepay;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashFlowListREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashFlowListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashFlowModifyREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description 本金利息一览表
 * @date 2023-02-20
 */
@Api(tags = "本金利息一览表-接口")
public interface FundReceiptRepayCashFlowApi {

    @ApiOperation("修改本金利息一览表")
    @PostMapping("/fund/receipt/repay/cash/flow/modify")
    R<Void> modify(@RequestBody @Valid List<FundReceiptRepayCashFlowModifyREQ> req);

    @ApiOperation("本金利息一览表列表")
    @PostMapping("/fund/receipt/repay/cash/flow/list")
    R<PageR<FundReceiptRepayCashFlowListRSP>> list(@RequestBody @Valid FundReceiptRepayCashFlowListREQ req);

}