package cn.zswltech.mithras.credit.application.groupcredit.review.service;

import java.util.List;
import java.util.Map;

public interface GroupCreditReviewBackdoorApplicationService {

    Map<String, Object> queryProjReview(Long projReviewId);

    List<Map<String, Object>> queryRelationship(Long groupCreditReviewId);
}
