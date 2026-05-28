package cn.zswltech.mithras.api.groupcreditreview;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.groupcreditreview.GroupCreditReviewRatingCheckRSP;
import cn.zswltech.mithras.dto.groupcreditreview.version.GroupCreditReviewEffectREQ;
import cn.zswltech.mithras.dto.groupcreditreview.version.GroupCreditReviewVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @description 集团授信评审基本信息表
 * @author wangchuanhao
 * @date 2022-11-11
 */
@Api(tags = "集团授信评审管理-版本管理接口")
public interface GroupCreditReviewVersionApi {

    /**
     * 集团授信评审信息生效（或提交审批）
     * @param req
     * @return
     */
    @ApiOperation("集团授信评审信息生效（或提交审批）")
    @PostMapping("/group/credit/review/effect")
    R<Void> effect(@RequestBody @Valid GroupCreditReviewEffectREQ req);

    @ApiOperation("集团授信评审管理版本列表")
    @PostMapping("/group/credit/review/version/list")
    R<PageR<CommonVersionListRSP>> list(@RequestBody @Valid CommonVersionListREQ req);

    @ApiOperation("集团授信评审管理版本比较详情（与上一版本比较）")
    @PostMapping("/group/credit/review/compare/preVersion")
    R<CommonVersionDiffRSP> comparePreVersion(@RequestBody @Valid GroupCreditReviewVersionDiffREQ req);

    @ApiOperation("检查评审相关评级信息")
    @PostMapping("/group/credit/review/rating/check")
    R<GroupCreditReviewRatingCheckRSP> checkRatingInfo(@RequestBody @Valid SinglePkREQ req);

}