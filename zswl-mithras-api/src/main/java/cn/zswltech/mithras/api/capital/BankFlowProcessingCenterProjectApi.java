package cn.zswltech.mithras.api.capital;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.MultiplePkREQ;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.capital.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * @author yangxiong
 * @date 2024/5/18/16:32
 * @description 银行流水中心
 */
@Api(tags = "银行流水中心")
@RequestMapping(path = "/bank/center")
public interface BankFlowProcessingCenterProjectApi {

    @ApiOperation(value = "银行流水中心-各个tab列表")
    @PostMapping(path = "/list")
    R<PageR<BankFlowProcessingCenterListRSP>> selectBankCenterByTab(@RequestBody @Valid BankFlowProcessingCenterListREQ req);

    @ApiOperation(value = "根据合同ID和现金流项目查询现金流编号")
    @PostMapping(path = "/project/code/list")
    R<List<String>> projectCashFlowCodeList(@RequestBody @Valid CashFlowCodeListREQ req);

    @ApiOperation(value = "根据计划收款ID和期项和现金流项目查询金额")
    @PostMapping(path = "/project/amount/detail")
    R<BankFlowProcessingCenterProjDetailRSP> projAmountDetail(@RequestBody @Valid ProjAmountDetailREQ req);

    @ApiOperation(value = "批量核销")
    @PostMapping(path = "/batch/write/off")
    R<Void> batchWriteOff(@RequestBody @Valid BankFlowProcessingCenterProjWriteOffREQ req);

    @ApiOperation(value = "手动拉取资金流水")
    @PostMapping(path = "/manual/pull/flow")
    R<List<String>> manualPullFlow(@RequestBody BankFlowProcessingCenterManualPullFlowREQ req);

    @ApiOperation(value = "无需处理")
    @PostMapping(path = "/no/processing/require")
    R<Void> noProcessingRequire(@RequestBody @Valid NoProcessingRequireREQ req);

    @ApiOperation(value = "删除")
    @PostMapping(path = "/delete")
    R<Void> delete(@RequestBody @Valid FinanceFlowDeleteREQ req);

    @ApiOperation(value = "根据现金流ID获取对应子列表")
    @PostMapping(path = "/project/sub/list")
    R<List<BankCenterSubTableProjectListRSP>> subListByCashFlowId(@RequestBody @Valid BankCenterSubTableProjectListREQ req);

    @ApiOperation(value = "轧差退款")
    @PostMapping(path = "/netting/refund")
    R<Void> nettingRefund(@RequestBody @Valid BankFlowNettingRefundREQ req);


    @ApiOperation(value = "确认收入")
    @PostMapping(path = "/confirm/income")
    R<Void> confirmIncome(@RequestBody @Valid BankFlowConfirmIncomeREQ req);

    @ApiOperation(value = "合同借据编号")
    @PostMapping(path = "/receiptCode")
    R<List<BankFlowContractReceiptListRSP>> getReceiptCode(@RequestBody @Valid BankFlowContractReceiptREQ req);

    @ApiOperation("银行流水还原至处理中心")
    @PostMapping("/restore")
    R<Void> restoreBankFlow(@RequestBody @Valid MultiplePkREQ req);
}
