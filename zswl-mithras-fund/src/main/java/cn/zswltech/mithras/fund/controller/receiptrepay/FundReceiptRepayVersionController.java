package cn.zswltech.mithras.fund.controller.receiptrepay;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.receiptrepay.FundReceiptRepayVersionApi;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.version.BatchDetailRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.version.BatchReceiptDownloadREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.version.BatchReceiptListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.version.BatchSubmitREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.version.CreateBatchREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import javax.annotation.Resource;
import java.util.List;
import cn.zswltech.mithras.fund.application.receiptrepay.api.FundReceiptRepayVersionApplicationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FundReceiptRepayVersionController implements FundReceiptRepayVersionApi {
    @Resource
    private FundReceiptRepayVersionApplicationService fundReceiptRepayVersionApplicationService;

    @Override
    public R<Void> submit(SinglePkREQ req) {
        return fundReceiptRepayVersionApplicationService.submit(req);
    }

    @Override
    public R<Long> createBatch(CreateBatchREQ req) {
        return fundReceiptRepayVersionApplicationService.createBatch(req);
    }

    @Override
    public R<Void> batchSubmit(BatchSubmitREQ req) {
        return fundReceiptRepayVersionApplicationService.batchSubmit(req);
    }

    @Override
    public R<BatchDetailRSP> batchDetail(SinglePkREQ req) {
        return fundReceiptRepayVersionApplicationService.batchDetail(req);
    }

    @Override
    public R<List<BatchReceiptListRSP>> batchReceiptList(SinglePkREQ req) {
        return fundReceiptRepayVersionApplicationService.batchReceiptList(req);
    }

    @Override
    public void batchReceiptDownload(BatchReceiptDownloadREQ req) {
        fundReceiptRepayVersionApplicationService.batchReceiptDownload(req);
    }

    @Override
    public R<PageR<CommonVersionListRSP>> list(CommonVersionListREQ req) {
        return fundReceiptRepayVersionApplicationService.list(req);
    }

    @Override
    public R<CommonVersionDiffRSP> comparePreVersion(SinglePkREQ req) {
        return fundReceiptRepayVersionApplicationService.comparePreVersion(req);
    }
}
