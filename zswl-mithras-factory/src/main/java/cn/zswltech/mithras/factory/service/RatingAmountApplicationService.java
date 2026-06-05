package cn.zswltech.mithras.factory.service;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.rating.RatingExecuteREQ;
import cn.zswltech.mithras.dto.rating.RatingExecuteRSP;
import cn.zswltech.mithras.dto.rating.RatingParamInfoDuoApprovalRSP;
import cn.zswltech.mithras.dto.rating.RatingParamInfoREQ;
import cn.zswltech.mithras.dto.rating.RatingReportApprovalRSP;
import cn.zswltech.mithras.dto.rating.ratingamount.RatingAmountAccessCheckREQ;
import cn.zswltech.mithras.dto.rating.ratingamount.RatingAmountAccessCheckRSP;
import cn.zswltech.mithras.dto.rating.ratingamount.RatingAmountAddREQ;
import cn.zswltech.mithras.dto.rating.ratingamount.RatingAmountAddRSP;
import cn.zswltech.mithras.dto.rating.ratingamount.RatingAmountDeleteREQ;
import cn.zswltech.mithras.dto.rating.ratingamount.RatingAmountDetailREQ;
import cn.zswltech.mithras.dto.rating.ratingamount.RatingAmountDetailRSP;
import cn.zswltech.mithras.dto.rating.ratingamount.RatingAmountEffectREQ;
import cn.zswltech.mithras.dto.rating.ratingamount.RatingAmountFinishREQ;
import cn.zswltech.mithras.dto.rating.ratingamount.RatingAmountInfoREQ;
import cn.zswltech.mithras.dto.rating.ratingamount.RatingAmountInfoRSP;
import cn.zswltech.mithras.dto.rating.ratingamount.RatingAmountModelQueryREQ;
import cn.zswltech.mithras.dto.rating.ratingamount.RatingAmountModelQueryRSP;
import cn.zswltech.mithras.dto.rating.ratingamount.RatingAmountPageREQ;
import cn.zswltech.mithras.dto.rating.ratingamount.RatingAmountPageRSP;
import cn.zswltech.mithras.dto.rating.ratingamount.RatingAmountProjInfoREQ;
import cn.zswltech.mithras.dto.rating.ratingamount.RatingAmountProjInfoRSP;
import cn.zswltech.mithras.dto.rating.ratingamount.RatingAmountReportREQ;
import cn.zswltech.mithras.dto.rating.ratingamount.RatingAmountReportRSP;
import cn.zswltech.mithras.dto.rating.ratingamount.RatingAmountUpdateREQ;
import cn.zswltech.mithras.dto.rating.ratingamount.RatingAmountUpdateRSP;

public interface RatingAmountApplicationService {

    PageR<RatingAmountPageRSP> ratingAmountPage(RatingAmountPageREQ req);

    RatingAmountDetailRSP ratingAmountDetail(RatingAmountDetailREQ req);

    RatingAmountInfoRSP ratingAmountInfo(RatingAmountInfoREQ req);

    RatingAmountModelQueryRSP ratingAmountModelQuery(RatingAmountModelQueryREQ req);

    RatingAmountAddRSP ratingAmountAdd(RatingAmountAddREQ req);

    RatingAmountAccessCheckRSP ratingAmountAccessCheck(RatingAmountAccessCheckREQ req);

    RatingAmountUpdateRSP ratingAmountUpdate(RatingAmountUpdateREQ req);

    void ratingAmountDelete(RatingAmountDeleteREQ req);

    RatingAmountProjInfoRSP ratingAmountProjInfo(RatingAmountProjInfoREQ req);

    RatingParamInfoDuoApprovalRSP paramInfo(RatingParamInfoREQ req);

    RatingExecuteRSP execute(RatingExecuteREQ req);

    void amountFinish(RatingAmountFinishREQ req);

    void ratingAmountEffect(RatingAmountEffectREQ req);

    void ratingAmountIndexApproval(RatingReportApprovalRSP req);

    RatingAmountReportRSP amountReport(RatingAmountReportREQ req);
}
