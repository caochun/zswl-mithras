package cn.zswltech.mithras.fund.controller.financing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.financing.FundFinancingBaseInfoApi;
import cn.zswltech.mithras.dto.fund.directfinancing.FileDownloadREQ;
import cn.zswltech.mithras.dto.fund.directfinancing.FundFinancingBatchDownloadREQ;
import cn.zswltech.mithras.dto.fund.financing.baseinfo.*;
import cn.zswltech.mithras.dto.fund.financing.SingleFinancingIdREQ;
import javax.annotation.Resource;
import javax.validation.Valid;
import cn.zswltech.mithras.fund.application.financing.api.FundFinancingBaseInfoApplicationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FundFinancingBaseInfoController implements FundFinancingBaseInfoApi {
    @Resource
    private FundFinancingBaseInfoApplicationService fundFinancingBaseInfoApplicationService;

    @Override
    public R<Void> modify(@Valid FundFinancingBaseInfoModifyREQ req) {
        return fundFinancingBaseInfoApplicationService.modify(req);
    }

    @Override
    public R<FundFinancingBaseInfoDetailRSP> detail(@Valid SingleFinancingIdREQ req) {
        return fundFinancingBaseInfoApplicationService.detail(req);
    }

    @Override
    public R<Void> modifyPlanLoanDate(@Valid FundFinancingPlanLoanDateModifyREQ req) {
        return fundFinancingBaseInfoApplicationService.modifyPlanLoanDate(req);
    }

    @Override
    public R<Void> modifyActualLoanDate(@Valid FundFinancingActualLoanDateModifyREQ req) {
        return fundFinancingBaseInfoApplicationService.modifyActualLoanDate(req);
    }

    @Override
    public R<Long> calcRemainingGuaranteeAmount(@Valid FundFinancingCalcRemainingGuaranteeAmountREQ req) {
        return fundFinancingBaseInfoApplicationService.calcRemainingGuaranteeAmount(req);
    }

    @Override
    public R<FundFinancingCarryInterestInfoRSP> getCarryInterestInfo(@Valid SingleFinancingIdREQ req) {
        return fundFinancingBaseInfoApplicationService.getCarryInterestInfo(req);
    }

    @Override
    public R<Void> http() {
        return fundFinancingBaseInfoApplicationService.http();
    }

    @Override
    public void batchDownload(@Valid FundFinancingBatchDownloadREQ req) {
        fundFinancingBaseInfoApplicationService.batchDownload(req);
    }

    @Override
    public void download(@Valid FileDownloadREQ req) {
        fundFinancingBaseInfoApplicationService.download(req);
    }
}
