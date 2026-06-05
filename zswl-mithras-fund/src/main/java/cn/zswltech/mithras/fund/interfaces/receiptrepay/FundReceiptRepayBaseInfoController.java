package cn.zswltech.mithras.fund.interfaces.receiptrepay;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.receiptrepay.FundReceiptRepayBaseInfoApi;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.fund.financing.pledge.FundFinancingPledgeListREQ;
import cn.zswltech.mithras.dto.fund.financing.pledge.FundFinancingPledgeListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.*;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.fund.application.lib.receiptrepay.service.FundReceiptRepayBaseInfoLibService;
import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import cn.zswltech.mithras.fund.application.receiptrepay.api.FundReceiptRepayBaseInfoApplicationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FundReceiptRepayBaseInfoController implements FundReceiptRepayBaseInfoApi {
    @Resource
    private FundReceiptRepayBaseInfoApplicationService fundReceiptRepayBaseInfoApplicationService;

    @Override
    public R<Void> modify(FundReceiptRepayBaseInfoModifyREQ req) {
        return fundReceiptRepayBaseInfoApplicationService.modify(req);
    }

    @Override
    public R<FundReceiptRepayBaseInfoListSumRSP> list(FundReceiptRepayBaseInfoListREQ req) {
        return fundReceiptRepayBaseInfoApplicationService.list(req);
    }

    @Override
    public R<FundDirectFinancingBaseInfoDetailRSP> directDetail(FundReceiptRepayBaseInfoDetailREQ req) {
        return fundReceiptRepayBaseInfoApplicationService.directDetail(req);
    }

    @Override
    public R<FundReceiptRepayBaseInfoDetailRSP> detail(FundReceiptRepayBaseInfoDetailREQ req) {
        return fundReceiptRepayBaseInfoApplicationService.detail(req);
    }

    @Override
    public R<List<FundFinancingPledgeListRSP>> pledgeDetail(FundReceiptRepayBaseInfoDetailREQ req) {
        return fundReceiptRepayBaseInfoApplicationService.pledgeDetail(req);
    }

    @Override
    public R<Map<String, DiffValue>> directCompare(FundReceiptRepayBaseInfoDetailREQ req) {
        return fundReceiptRepayBaseInfoApplicationService.directCompare(req);
    }

    @Override
    public R<FundReceiptRepayBaseInfoListRSP> financingType(FundReceiptRepayBaseInfoDetailREQ req) {
        return fundReceiptRepayBaseInfoApplicationService.financingType(req);
    }
}
