package cn.zswltech.mithras.credit.application.groupcredit;

import cn.zswltech.mithras.dto.groupcreditestablish.baseinfo.GroupCreditEstablishBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.groupcreditreview.baseinfo.GroupCreditReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.credit.groupcredit.establish.mapper.model.GroupCreditEstablishBaseInfoLib;
import cn.zswltech.mithras.credit.groupcredit.review.mapper.model.GroupCreditReviewBaseInfoLib;

public interface GroupCreditBaseInfoAssembler {

    GroupCreditEstablishBaseInfoDetailRSP establishLib2Rsp(GroupCreditEstablishBaseInfoLib lib);

    GroupCreditReviewBaseInfoDetailRSP reviewLib2Rsp(GroupCreditReviewBaseInfoLib lib);
}
