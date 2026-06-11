package cn.zswltech.mithras.rating.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.rating.RatingClientApi;
import cn.zswltech.mithras.dto.rating.*;
import cn.zswltech.mithras.dto.rating.ratingclient.*;
import cn.zswltech.mithras.rating.application.RatingClientApplicationService;
import cn.zswltech.mithras.rating.service.RatingClientAreaIndicatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@RestController
@Slf4j
public class RatingClientController implements RatingClientApi {

    @Resource
    private RatingClientApplicationService ratingClientService;
    @Resource
    private RatingClientAreaIndicatorService ratingClientAreaIndicatorService;

    @Override
    public R<Void> modifyAreaIndicator(RatingClientAreaIndicatorModifyREQ req) {
        ratingClientAreaIndicatorService.modify(req.getRatingClientId(), Collections.singletonList(req));
        return R.ok();
    }

    @Override
    public R<Void> batchModifyAreaIndicator(RatingClientAreaIndicatorBatchModifyREQ req) {
        ratingClientAreaIndicatorService.modify(req.getRatingClientId(), req.getIndicatorValueList());
        return R.ok();
    }

    @Override
    public R<RatingClientAddRSP> ratingClientAdd(RatingClientAddREQ req) {
        return R.ok(ratingClientService.ratingClientAdd(req));
    }

    @Override
    public R<RatingClientAccessCheckRSP> ratingClientAccessCheck(RatingClientAccessCheckREQ req) {
        return R.ok(ratingClientService.ratingClientAccessCheck(req));
    }

    @Override
    public R<RatingClientUpdateRSP> ratingClientUpdate(RatingClientUpdateREQ req) {
        return R.ok(ratingClientService.ratingClientUpdate(req));
    }

    @Override
    public R<Void> ratingClientDelete(RatingClientDeleteREQ req) {
        ratingClientService.ratingClientDelete(req);
        return R.ok();
    }

//    @Override
//    public R<RatingClientRestoreRSP> ratingClientRestore(RatingClientRestoreREQ req) {
//        return R.ok(ratingClientService.ratingClientRestore(req));
//    }

    @Override
    public R<RatingClientInfoRSP> ratingClientInfo(RatingClientInfoREQ req) {
        return R.ok(ratingClientService.ratingClientInfo(req));
    }

    @Override
    public R<List<RatingModelQueryRSP>> modelQuery(RatingModelQueryREQ req) {
        return R.ok(ratingClientService.modelQuery(req));
    }

    @Override
    public R<RatingParamInfoDuoApprovalRSP> paramInfo(RatingParamInfoREQ req) {
        return R.ok(ratingClientService.paramInfo(req));
    }

    @Override
    public R<RatingExecuteRSP> execute(RatingExecuteREQ req) {
        return R.ok(ratingClientService.execute(req));
    }

    @Override
    public R<Void> clientFinish(RatingClientFinishREQ req) {
        ratingClientService.clientFinish(req);
        return R.ok();
    }

    @Override
    public R<PageR<RatingClientPageRSP>> ratingClientPage(RatingClientPageREQ req) {
        return R.ok(ratingClientService.ratingClientPage(req));
    }

    @Override
    public R<RatingClientDetailRSP> ratingClientDetail(RatingClientDetailREQ req) {
        return R.ok(ratingClientService.ratingClientDetail(req));
    }

    @Override
    public R<Void> ratingClientEffect(RatingClientEffectREQ req) {
        ratingClientService.ratingClientEffect(req);
        return R.ok();
    }

    @Override
    public R<Void> ratingClientIndexApproval(RatingReportApprovalRSP req) {
        ratingClientService.ratingClientIndexApproval(req);
        return R.ok();
    }

    @Override
    public R<RatingClientReportRSP> clientReport(RatingClientReportREQ req) {
        return R.ok(ratingClientService.clientReport(req));
    }

    @Override
    public R<Void> overturn(RatingClientOverturnREQ req) {
        ratingClientService.overturn(req);
        return R.ok();
    }

    @Override
    public R<Void> adjust(RatingClientOverturnREQ req) {
        ratingClientService.adjust(req);
        return R.ok();
    }

//    @Override
//    public R<Void> overturnApproval(RatingOverturnApprovalREQ req) {
//        ratingClientService.overturnApproval(req);
//        return R.ok();
//    }

    @Override
    public R<RatingClientAbstractRSP> clientAbstract(RatingClientAbstractREQ req) {
        return R.ok(ratingClientService.clientAbstract(req));
    }

    @Override
    public R<List<RatingClientOverturnRecordRSP>> clientOverturnRecord(RatingClientOverturnRecordREQ req) {
        return R.ok(ratingClientService.clientOverturnRecord(req));
    }

    @Override
    public R<Void> clientIndexCheck(RatingClientIndexCheckREQ req) {
        ratingClientService.clientIndexCheck(req);
        return R.ok();
    }
}
