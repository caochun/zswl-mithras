package cn.zswltech.mithras.funddirect.interfaces.directfinancing;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.directfinancing.FundDirectFinancingBaseInfoApi;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import cn.zswltech.mithras.funddirect.application.directfinancing.FundDirectFinancingBaseInfoApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class FundDirectFinancingBaseInfoController implements FundDirectFinancingBaseInfoApi {

    @Resource
    private FundDirectFinancingBaseInfoApplicationService fundDirectFinancingBaseInfoApplicationService;

    @Override
    public R<Long> add(FundDirectFinancingBaseInfoAddREQ req) {
        return fundDirectFinancingBaseInfoApplicationService.add(req);
    }

    @Override
    public R<Void> modify(FundDirectFinancingBaseInfoModifyREQ req) {
        return fundDirectFinancingBaseInfoApplicationService.modify(req);
    }

    @Override
    public R<PageR<FundDirectFinancingBaseInfoListRSP>> list(FundDirectFinancingBaseInfoListREQ req) {
        return fundDirectFinancingBaseInfoApplicationService.list(req);
    }

    @Override
    public R<FundDirectFinancingBaseInfoListRSP> sum(FundDirectFinancingBaseInfoListREQ req) {
        return fundDirectFinancingBaseInfoApplicationService.sum(req);
    }

    @Override
    public R<FundDirectFinancingBaseInfoDetailRSP> detail(FundDirectFinancingSingleIdREQ req) {
        return fundDirectFinancingBaseInfoApplicationService.detail(req);
    }

    @Override
    public R<Void> obsolete(FundDirectFinancingSingleIdREQ req) {
        return fundDirectFinancingBaseInfoApplicationService.obsolete(req);
    }

    @Override
    public R<Void> sync(FundDirectFinancingSingleIdREQ req) {
        return fundDirectFinancingBaseInfoApplicationService.sync(req);
    }

    @Override
    public R<Void> delete(FundDirectFinancingSingleIdREQ req) {
        return fundDirectFinancingBaseInfoApplicationService.delete(req);
    }

    @Override
    public R<Void> settle(FundDirectFinancingSingleIdREQ req) {
        return fundDirectFinancingBaseInfoApplicationService.settle(req);
    }

    @Override
    public void batchDownload(FundFinancingBatchDownloadREQ req) {
        fundDirectFinancingBaseInfoApplicationService.batchDownload(req);
    }

    @Override
    public void download(FileDownloadREQ fileDownloadREQ) {
        fundDirectFinancingBaseInfoApplicationService.download(fileDownloadREQ);
    }
}
