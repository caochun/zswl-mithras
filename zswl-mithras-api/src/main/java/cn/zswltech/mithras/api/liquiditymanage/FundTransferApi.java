package cn.zswltech.mithras.api.liquiditymanage;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.liquiditymanage.fundtransfer.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * @author zhouning
 * @date 2024/12/24 16:27
 * @className FundTransferApi
 * @description
 */
@Api(tags = "监管户待转资金Api")
@RequestMapping(path = "/fundTransfer")
public interface FundTransferApi {

    @PostMapping(path = "/account/list")
    @ApiOperation(value = "监管户待转资金列表展示")
    R<FundTransferListRSP> list(@RequestBody @Valid FundTransferListREQ req);


    @PostMapping(path = "/graph/list")
    @ApiOperation(value = "监管户待转资金图表展示")
    R<FundTransferGraphRSP> graphList(@RequestBody @Valid FundTransferGraphREQ req);


    @PostMapping(path = "/detail/list")
    @ApiOperation(value = "监管户待转资金账户详情")
    R<FundTransferDetailListRSP> detail(@RequestBody @Valid FundTransferDetailListREQ req);


    @PostMapping(path = "/account/daily")
    @ApiOperation(value = "监管户待转资金列表每日情况展示")
    R<FundTransferListDailyRSP> listDaily(@RequestBody @Valid FundTransferListDailyREQ req);


    @PostMapping(path = "/graph/daily")
    @ApiOperation(value = "监管户待转资金图表每日情况展示")
    R<FundTransferGraphDailyRSP> graphDaily(@RequestBody @Valid FundTransferGraphDailyREQ req);


    @PostMapping(path = "/current/daily")
    @ApiOperation(value = "监管户待转资金图表当日情况展示")
    R<FundTransferCurrentDailyRSP> currentDaily(@RequestBody @Valid FundTransferCurrentDailyREQ req);


    @ApiOperation("账户列表")
    @PostMapping("/bankAccount/list")
    R<List<FundTransferBankAccountListRSP>> accountList(@RequestBody @Valid FundTransferBankAccountListREQ req);

    @PostMapping("/test")
    void test();

    @PostMapping(path = "/account/current/daily")
    @ApiOperation(value = "监管户待转资金当日账户情况展示")
    R<PageR<AccountDepositedAmountDetail>> accountCurrentDaily(@RequestBody @Valid FundTransferAccountCurrentDailyREQ req);
}
