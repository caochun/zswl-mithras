package cn.zswltech.mithras.service.controller.fund.receiptrepay;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.receiptrepay.FundReceiptRepayCashFlowApi;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashFlowListREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashFlowListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashFlowModifyREQ;
import cn.zswltech.mithras.service.auth.checker.fund.FundReceiptRepayModifySubAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.fund.receiptrepay.FundReceiptRepayCashFlowMapper;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayCashFlowService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 本金利息一览表
 * @date 2023-02-20
 */
@RestController
public class FundReceiptRepayCashFlowController implements FundReceiptRepayCashFlowApi {

    @Resource
    private FundReceiptRepayCashFlowService fundReceiptRepayCashFlowService;
    @Resource
    private FundReceiptRepayModifySubAuthChecker fundReceiptRepayModifySubAuthChecker;

    @Override
    public R<Void> modify(List<FundReceiptRepayCashFlowModifyREQ> req) {
        fundReceiptRepayModifySubAuthChecker.checkBatch(BusinessModuleEnum.FUND_RECEIPT_REPAY, FundReceiptRepayCashFlowMapper.class, req.stream().map(FundReceiptRepayCashFlowModifyREQ::getId).collect(Collectors.toList()), null);
        fundReceiptRepayCashFlowService.modifyBatch(req);
        return R.ok();
    }

    @Override
    public R<PageR<FundReceiptRepayCashFlowListRSP>> list(FundReceiptRepayCashFlowListREQ req) {
        return R.ok(fundReceiptRepayCashFlowService.list(req));
    }

}