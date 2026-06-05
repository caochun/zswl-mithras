package cn.zswltech.mithras.factory.service;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.rating.RatingExecuteREQ;
import cn.zswltech.mithras.dto.rating.RatingExecuteRSP;
import cn.zswltech.mithras.dto.rating.RatingModelQueryREQ;
import cn.zswltech.mithras.dto.rating.RatingModelQueryRSP;
import cn.zswltech.mithras.dto.rating.RatingParamInfoDuoApprovalRSP;
import cn.zswltech.mithras.dto.rating.RatingParamInfoREQ;
import cn.zswltech.mithras.dto.rating.RatingReportApprovalRSP;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientAbstractREQ;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientAbstractRSP;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientAccessCheckREQ;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientAccessCheckRSP;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientAddREQ;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientAddRSP;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientDeleteREQ;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientDetailREQ;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientDetailRSP;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientEffectREQ;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientFinishREQ;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientIndexCheckREQ;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientInfoREQ;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientInfoRSP;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientOverturnREQ;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientOverturnRecordREQ;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientOverturnRecordRSP;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientPageREQ;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientPageRSP;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientReportREQ;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientReportRSP;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientUpdateREQ;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientUpdateRSP;

import java.util.List;

public interface RatingClientApplicationService {

    RatingClientAddRSP ratingClientAdd(RatingClientAddREQ req);

    RatingClientAccessCheckRSP ratingClientAccessCheck(RatingClientAccessCheckREQ req);

    RatingClientUpdateRSP ratingClientUpdate(RatingClientUpdateREQ req);

    void ratingClientDelete(RatingClientDeleteREQ req);

    RatingClientInfoRSP ratingClientInfo(RatingClientInfoREQ req);

    List<RatingModelQueryRSP> modelQuery(RatingModelQueryREQ req);

    RatingParamInfoDuoApprovalRSP paramInfo(RatingParamInfoREQ req);

    RatingExecuteRSP execute(RatingExecuteREQ req);

    void clientFinish(RatingClientFinishREQ req);

    PageR<RatingClientPageRSP> ratingClientPage(RatingClientPageREQ req);

    RatingClientDetailRSP ratingClientDetail(RatingClientDetailREQ req);

    void ratingClientEffect(RatingClientEffectREQ req);

    void ratingClientIndexApproval(RatingReportApprovalRSP req);

    RatingClientReportRSP clientReport(RatingClientReportREQ req);

    void overturn(RatingClientOverturnREQ req);

    void adjust(RatingClientOverturnREQ req);

    RatingClientAbstractRSP clientAbstract(RatingClientAbstractREQ req);

    List<RatingClientOverturnRecordRSP> clientOverturnRecord(RatingClientOverturnRecordREQ req);

    void clientIndexCheck(RatingClientIndexCheckREQ req);
}
