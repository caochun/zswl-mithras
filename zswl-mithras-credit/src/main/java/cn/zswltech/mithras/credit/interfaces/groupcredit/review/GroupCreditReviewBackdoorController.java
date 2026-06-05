package cn.zswltech.mithras.credit.interfaces.groupcredit.review;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.groupcreditreview.GroupCreditReviewBackdoorApi;
import cn.zswltech.mithras.credit.application.groupcredit.review.service.GroupCreditReviewBackdoorApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 后门接口 方便查看一个项目评审对应的授信评审，和一个授信评审对应多少项目评审
 *
 * @author wangchuanhao
 * @date 2022/11/28 10:06 AM
 */
@RestController
public class GroupCreditReviewBackdoorController implements GroupCreditReviewBackdoorApi {

    @Resource
    private GroupCreditReviewBackdoorApplicationService groupCreditReviewBackdoorApplicationService;

    @Override
    public R<Map<String, Object>> queryProjReview(Long projReviewId) {
        return R.ok(groupCreditReviewBackdoorApplicationService.queryProjReview(projReviewId));
    }

    @Override
    public R<List<Map<String, Object>>> queryRelationship(Long groupCreditReviewId) {
        return R.ok(groupCreditReviewBackdoorApplicationService.queryRelationship(groupCreditReviewId));
    }
}
