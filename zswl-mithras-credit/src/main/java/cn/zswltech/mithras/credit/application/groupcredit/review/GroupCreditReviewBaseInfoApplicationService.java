package cn.zswltech.mithras.credit.application.groupcredit.review;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.groupcreditestablish.GroupCreditEstablishVagueListREQ;
import cn.zswltech.mithras.dto.groupcreditestablish.GroupCreditEstablishVagueListRSP;
import cn.zswltech.mithras.dto.groupcreditreview.GroupCreditReviewListREQ;
import cn.zswltech.mithras.dto.groupcreditreview.GroupCreditReviewListRSP;
import cn.zswltech.mithras.dto.groupcreditreview.baseinfo.GroupCreditReviewBaseInfoAddREQ;
import cn.zswltech.mithras.dto.groupcreditreview.baseinfo.GroupCreditReviewBaseInfoAddRSP;
import cn.zswltech.mithras.dto.groupcreditreview.baseinfo.GroupCreditReviewBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.groupcreditreview.baseinfo.GroupCreditReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.groupcreditreview.baseinfo.GroupCreditReviewBaseInfoModifyREQ;
import cn.zswltech.mithras.dto.groupcreditreview.baseinfo.GroupCreditReviewBaseInfoUpdateRatingREQ;
import cn.zswltech.mithras.dto.groupcreditreview.baseinfo.GroupCreditReviewInfoUpdateRatingRSP;

import java.util.List;

public interface GroupCreditReviewBaseInfoApplicationService {

    GroupCreditReviewBaseInfoAddRSP add(GroupCreditReviewBaseInfoAddREQ req);

    void modify(GroupCreditReviewBaseInfoModifyREQ req);

    PageR<GroupCreditReviewListRSP> list(GroupCreditReviewListREQ req);

    GroupCreditReviewBaseInfoDetailRSP detail(GroupCreditReviewBaseInfoDetailREQ req);

    Long getClientStockRiskExposure(GroupCreditReviewBaseInfoDetailREQ req);

    GroupCreditReviewInfoUpdateRatingRSP updateRating(GroupCreditReviewBaseInfoUpdateRatingREQ req);

    List<GroupCreditEstablishVagueListRSP> vague(GroupCreditEstablishVagueListREQ req);
}
