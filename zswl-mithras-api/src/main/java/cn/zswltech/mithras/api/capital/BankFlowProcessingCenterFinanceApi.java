package cn.zswltech.mithras.api.capital;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.capital.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * @author yangxiong
 * @date 2024/5/18/16:32
 * @description 银行流水中心-资金端
 */
@Api(tags = "银行流水中心-资金端")
@RequestMapping(path = "/bank/center")
public interface BankFlowProcessingCenterFinanceApi {
    @ApiOperation("机构/产品列表")
    @PostMapping(path = "/finance/org/list")
    R<List<BankFlowProcessingCenterFinanceOrgRSP>> listOrg(@RequestBody @Valid BankFlowProcessingCenterFinanceOrgREQ req);

    @ApiOperation("融资列表")
    @PostMapping(path = "/finance/info/list")
    R<List<BankFlowProcessingCenterFinanceInfoRSP>> listFinanceInfo(@RequestBody @Valid BankFlowProcessingCenterFinanceInfoREQ req);

    @ApiOperation("收款现金流项目信息")
    @PostMapping(path = "/finance/cashflow/list")
    R<List<BankFlowProcessingCenterFinanceCashFlowRSP>> listCashFlow(@RequestBody @Valid BankFlowProcessingCenterFinanceCashFlowREQ req);

    @ApiOperation("付款现金流项目信息")
    @PostMapping(path = "/finance/payment/cashflow/list")
    R<BankFlowProcessingCenterFinancePaymentCashFlowSumRSP> listPaymentCashFlow(@RequestBody @Valid BankFlowProcessingCenterFinancePaymentCashFlowREQ req);

    @ApiOperation("获取子列表信息")
    @PostMapping(path = "/finance/sub/list")
    R<List<BankCenterSubTableFinanceListRSP>> subList(@RequestBody @Valid BankCenterSubTableFinanceListREQ req);


    @ApiOperation(value = "资金端的付款核销")
    @PostMapping(path = "/finance/payment/writeoff")
    R<Void> financingPaymentWriteOff(@RequestBody @Valid FinancePaymentWriteOffREQ req);

    @ApiOperation(value = "根据id查询流水")
    @PostMapping(path = "/finance/flow/list")
    R<List<BankFlowProcessingCenterListRSP>> listFlow(@RequestBody @Valid BankFlowQueryREQ req);

    @ApiOperation("获取还款计划拆分明细")
    @PostMapping(path = "/finance/repay/split/list")
    R<List<FinanceRepaySplitRSP>> listRepaySplit(@RequestBody @Valid FinanceRepaySplitREQ req);

    @ApiOperation("获取还款计划拆分核销明细")
    @PostMapping(path = "/finance/repay/split/writeoff/list")
    R<List<FinanceRepaySplitRecordRSP>> listRepaySplitWriteOffRecord(@RequestBody @Valid FinanceRepaySplitRecordREQ req);
}
