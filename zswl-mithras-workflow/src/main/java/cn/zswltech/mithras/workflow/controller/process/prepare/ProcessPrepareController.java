package cn.zswltech.mithras.workflow.controller.process.prepare;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.process.prepare.ProcessPrepareApi;
import cn.zswltech.mithras.dto.IdPageREQ;
import cn.zswltech.mithras.dto.IdREQ;
import cn.zswltech.mithras.dto.process.prepare.FinancingRepayActualProcessDetailListRSP;
import cn.zswltech.mithras.dto.process.prepare.ProcessPrepareDetailRSP;
import cn.zswltech.mithras.dto.process.prepare.ProcessPrepareListREQ;
import cn.zswltech.mithras.dto.process.prepare.ProcessPrepareListRSP;
import cn.zswltech.mithras.dto.process.prepare.RentCollectionMonthDetailListRSP;
import cn.zswltech.mithras.dto.process.prepare.RentCollectionMonthKeyListRSP;
import cn.zswltech.mithras.dto.process.prepare.RentCollectionMonthModifyAccountREQ;
import cn.zswltech.mithras.workflow.process.prepare.ProcessPrepareApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
public class ProcessPrepareController implements ProcessPrepareApi {

    @Resource
    private ProcessPrepareApplicationService processPrepareApplicationService;

    @Override
    public R<PageR<ProcessPrepareListRSP>> list(@Valid ProcessPrepareListREQ req) {
        return processPrepareApplicationService.list(req);
    }

    @Override
    public R<ProcessPrepareDetailRSP> detail(@Valid IdREQ req) {
        return processPrepareApplicationService.detail(req);
    }

    @Override
    public R<Void> commitPrepare(@Valid IdREQ req) {
        return processPrepareApplicationService.commitPrepare(req);
    }

    @Override
    public R<Void> discardPrepare(@Valid IdREQ req) {
        return processPrepareApplicationService.discardPrepare(req);
    }

    @Override
    public R<PageR<RentCollectionMonthDetailListRSP>> detailList(@Valid IdPageREQ req) {
        return processPrepareApplicationService.detailList(req);
    }

    @Override
    public R<RentCollectionMonthKeyListRSP> keyList(@Valid IdPageREQ req) {
        return processPrepareApplicationService.keyList(req);
    }

    @Override
    public void downloadList(@Valid IdREQ req) {
        processPrepareApplicationService.downloadList(req);
    }

    @Override
    public byte[] previewNotify(@Valid IdREQ req) {
        return processPrepareApplicationService.previewNotify(req);
    }

    @Override
    public void downloadPreview(@Valid IdREQ req) {
        processPrepareApplicationService.downloadPreview(req);
    }

    @Override
    public void downloadBatch(@Valid IdREQ req) {
        processPrepareApplicationService.downloadBatch(req);
    }

    @Override
    public R<Void> refreshRentInfo(@Valid IdREQ req) {
        return processPrepareApplicationService.refreshRentInfo(req);
    }

    @Override
    public R<Void> generateNextMonthRentNotify() {
        return processPrepareApplicationService.generateNextMonthRentNotify();
    }

    @Override
    public R<Void> modifyBankAccountInfo(@Valid RentCollectionMonthModifyAccountREQ req) {
        return processPrepareApplicationService.modifyBankAccountInfo(req);
    }

    @Override
    public R<PageR<FinancingRepayActualProcessDetailListRSP>> repayDetailList(@Valid IdPageREQ req) {
        return processPrepareApplicationService.repayDetailList(req);
    }
}
