package cn.zswltech.mithras.factory.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.rating.RatingAmountApi;
import cn.zswltech.mithras.dto.rating.*;
import cn.zswltech.mithras.dto.rating.ratingamount.*;
import cn.zswltech.mithras.factory.model.RatingAmount;
import cn.zswltech.mithras.factory.service.RatingAmountService;
import com.mysql.cj.x.protobuf.Mysqlx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class RatingAmountController implements RatingAmountApi {

    @Resource
    private RatingAmountService ratingAmountService;

    @Override
    public R<PageR<RatingAmountPageRSP>> ratingAmountPage(RatingAmountPageREQ req) {
        return R.ok(ratingAmountService.ratingAmountPage(req));
    }

    @Override
    public R<RatingAmountDetailRSP> ratingAmountDetail(RatingAmountDetailREQ req) {
        return R.ok(ratingAmountService.ratingAmountDetail(req));
    }

    @Override
    public R<RatingAmountInfoRSP> ratingAmountInfo(RatingAmountInfoREQ req) {
        return R.ok(ratingAmountService.ratingAmountInfo(req));
    }

    @Override
    public R<RatingAmountModelQueryRSP> ratingAmountModelQuery(RatingAmountModelQueryREQ req) {
        return R.ok(ratingAmountService.ratingAmountModelQuery(req));
    }

    @Override
    public R<RatingAmountAddRSP> ratingAmountAdd(RatingAmountAddREQ req) {
        return R.ok(ratingAmountService.ratingAmountAdd(req));
    }

    @Override
    public R<RatingAmountAccessCheckRSP> ratingAmountAccessCheck(RatingAmountAccessCheckREQ req) {
        return R.ok(ratingAmountService.ratingAmountAccessCheck(req));
    }

    @Override
    public R<RatingAmountUpdateRSP> ratingAmountUpdate(RatingAmountUpdateREQ req) {
        return R.ok(ratingAmountService.ratingAmountUpdate(req));
    }

    @Override
    public R<Void> ratingAmountDelete(RatingAmountDeleteREQ req) {
        ratingAmountService.ratingAmountDelete(req);
        return R.ok();
    }

    @Override
    public R<RatingAmountProjInfoRSP> ratingAmountProjInfo(RatingAmountProjInfoREQ req) {
        return R.ok(ratingAmountService.ratingAmountProjInfo(req));
    }

    @Override
    public R<RatingParamInfoDuoApprovalRSP> paramInfo(RatingParamInfoREQ req) {
        return R.ok(ratingAmountService.paramInfo(req));
    }

    @Override
    public R<RatingExecuteRSP> execute(RatingExecuteREQ req) {
        return R.ok(ratingAmountService.execute(req));
    }

    @Override
    public R<Void> amountFinish(RatingAmountFinishREQ req) {
        ratingAmountService.amountFinish(req);
        return R.ok();
    }

    @Override
    public R<Void> ratingAmountEffect(RatingAmountEffectREQ req) {
        ratingAmountService.ratingAmountEffect(req);
        return R.ok();
    }

    @Override
    public R<Void> ratingAmountIndexApproval(RatingReportApprovalRSP req) {
        ratingAmountService.ratingAmountIndexApproval(req);
        return R.ok();
    }

    @Override
    public R<RatingAmountReportRSP> amountReport(RatingAmountReportREQ req) {
        return R.ok(ratingAmountService.amountReport(req));
    }

//    @Override
//    public R<Void> xxlJobTest() {
//        ratingAmountService.ratingAbandon();
//        return R.ok();
//    }

}
