package cn.zswltech.mithras.api.fund.receiptrepay;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.fund.financing.pledge.FundFinancingPledgeListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.*;
import cn.zswltech.mithras.dto.version.DiffValue;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author zhaozhengkang
 * @description 收付款
 * @date 2023-02-20
 */
@Api(tags = "收付款-接口")
public interface FundReceiptRepayBaseInfoApi {
    @ApiOperation("修改收付款")
    @PostMapping("/fund/receipt/repay/base/info/modify")
    R<Void> modify(@RequestBody @Valid FundReceiptRepayBaseInfoModifyREQ req);

    @ApiOperation("收付款列表")
    @PostMapping("/fund/receipt/repay/base/info/list")
    R<FundReceiptRepayBaseInfoListSumRSP> list(@RequestBody @Valid FundReceiptRepayBaseInfoListREQ req);

    @ApiOperation("直接融资收付款详情")
    @PostMapping("/fund/receipt/repay/base/info/directDetail")
    R<FundDirectFinancingBaseInfoDetailRSP> directDetail(@RequestBody @Valid FundReceiptRepayBaseInfoDetailREQ req);

    @ApiOperation("收付款详情")
    @PostMapping("/fund/receipt/repay/base/info/detail")
    R<FundReceiptRepayBaseInfoDetailRSP> detail(@RequestBody @Valid FundReceiptRepayBaseInfoDetailREQ req);

    @ApiOperation("质押明细接口")
    @PostMapping("/fund/receipt/repay/base/info/pledge/detail")
    R<List<FundFinancingPledgeListRSP>> pledgeDetail(@RequestBody @Valid FundReceiptRepayBaseInfoDetailREQ req);

    @ApiOperation("直接融资收付款-基本信息比对")
    @PostMapping("/fund/receipt/repay/base/info/directCompare")
    R<Map<String, DiffValue>> directCompare(@RequestBody @Valid FundReceiptRepayBaseInfoDetailREQ req);

    @ApiOperation("收付款-获取融资类型")
    @PostMapping("/fund/receipt/repay/base/info/financingType")
    R<FundReceiptRepayBaseInfoListRSP> financingType(@RequestBody @Valid FundReceiptRepayBaseInfoDetailREQ req);

}