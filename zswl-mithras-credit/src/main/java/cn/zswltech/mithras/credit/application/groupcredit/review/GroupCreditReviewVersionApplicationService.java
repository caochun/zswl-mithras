package cn.zswltech.mithras.credit.application.groupcredit.review;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.groupcreditreview.GroupCreditReviewRatingCheckRSP;
import cn.zswltech.mithras.dto.groupcreditreview.version.GroupCreditReviewEffectREQ;
import cn.zswltech.mithras.dto.groupcreditreview.version.GroupCreditReviewVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;

public interface GroupCreditReviewVersionApplicationService {

    void effect(GroupCreditReviewEffectREQ req);

    PageR<CommonVersionListRSP> list(CommonVersionListREQ req);

    CommonVersionDiffRSP comparePreVersion(GroupCreditReviewVersionDiffREQ req);

    GroupCreditReviewRatingCheckRSP checkRatingInfo(SinglePkREQ req);
}
