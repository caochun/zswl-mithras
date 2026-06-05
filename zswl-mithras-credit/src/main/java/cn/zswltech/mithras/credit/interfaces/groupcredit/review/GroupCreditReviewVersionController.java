package cn.zswltech.mithras.credit.interfaces.groupcredit.review;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.groupcreditreview.GroupCreditReviewVersionApi;
import cn.zswltech.mithras.credit.application.groupcredit.review.service.GroupCreditReviewVersionApplicationService;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.groupcreditreview.GroupCreditReviewRatingCheckRSP;
import cn.zswltech.mithras.dto.groupcreditreview.version.GroupCreditReviewEffectREQ;
import cn.zswltech.mithras.dto.groupcreditreview.version.GroupCreditReviewVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author wangchuanhao
 * @description 集团授信评审基本信息表
 * @date 2022-11-11
 */
@RestController
public class GroupCreditReviewVersionController implements GroupCreditReviewVersionApi {

    @Resource
    private GroupCreditReviewVersionApplicationService groupCreditReviewVersionApplicationService;

    @Override
    public R<Void> effect(GroupCreditReviewEffectREQ req) {
        groupCreditReviewVersionApplicationService.effect(req);
        return R.ok();
    }

    @Override
    public R<PageR<CommonVersionListRSP>> list(CommonVersionListREQ req) {
        return R.ok(groupCreditReviewVersionApplicationService.list(req));
    }

    @Override
    public R<CommonVersionDiffRSP> comparePreVersion(GroupCreditReviewVersionDiffREQ req) {
        return R.ok(groupCreditReviewVersionApplicationService.comparePreVersion(req));
    }

    @Override
    public R<GroupCreditReviewRatingCheckRSP> checkRatingInfo(SinglePkREQ req) {
        return R.ok(groupCreditReviewVersionApplicationService.checkRatingInfo(req));
    }
}
