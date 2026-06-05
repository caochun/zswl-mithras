package cn.zswltech.mithras.credit.interfaces.groupcredit.review;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.groupcreditreview.GroupCreditReviewBaseInfoApi;
import cn.zswltech.mithras.credit.application.groupcredit.review.service.GroupCreditReviewBaseInfoApplicationService;
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
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description 集团授信评审基本信息表
 * @author wangchuanhao
 * @date 2022-11-11
 */
@RestController
public class GroupCreditReviewBaseInfoController implements GroupCreditReviewBaseInfoApi {

    @Resource
    private GroupCreditReviewBaseInfoApplicationService groupCreditReviewBaseInfoApplicationService;

    @Override
    public R<GroupCreditReviewBaseInfoAddRSP> add(GroupCreditReviewBaseInfoAddREQ req) {
        return R.ok(groupCreditReviewBaseInfoApplicationService.add(req));
    }

    @Override
    public R<Void> modify(GroupCreditReviewBaseInfoModifyREQ req) {
        groupCreditReviewBaseInfoApplicationService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<GroupCreditReviewListRSP>> list(GroupCreditReviewListREQ req) {
        return R.ok(groupCreditReviewBaseInfoApplicationService.list(req));
    }

    @Override
    public R<GroupCreditReviewBaseInfoDetailRSP> detail(GroupCreditReviewBaseInfoDetailREQ req) {
        return R.ok(groupCreditReviewBaseInfoApplicationService.detail(req));
    }

    @Override
    public R<Long> getClientStockRiskExposure(GroupCreditReviewBaseInfoDetailREQ req) {
        return R.ok(groupCreditReviewBaseInfoApplicationService.getClientStockRiskExposure(req));
    }

    @Override
    public R<GroupCreditReviewInfoUpdateRatingRSP> updateRating(GroupCreditReviewBaseInfoUpdateRatingREQ req) {
        return R.ok(groupCreditReviewBaseInfoApplicationService.updateRating(req));
    }

    @Override
    public R<List<GroupCreditEstablishVagueListRSP>> vague(GroupCreditEstablishVagueListREQ req) {
        return R.ok(groupCreditReviewBaseInfoApplicationService.vague(req));
    }
}
