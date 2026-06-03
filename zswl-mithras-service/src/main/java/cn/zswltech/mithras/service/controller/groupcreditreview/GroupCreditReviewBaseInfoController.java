package cn.zswltech.mithras.service.controller.groupcreditreview;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.groupcreditreview.GroupCreditReviewBaseInfoApi;
import cn.zswltech.mithras.dto.groupcreditestablish.GroupCreditEstablishVagueListREQ;
import cn.zswltech.mithras.dto.groupcreditestablish.GroupCreditEstablishVagueListRSP;
import cn.zswltech.mithras.dto.groupcreditreview.GroupCreditReviewListREQ;
import cn.zswltech.mithras.dto.groupcreditreview.GroupCreditReviewListRSP;
import cn.zswltech.mithras.dto.groupcreditreview.baseinfo.*;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.model.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.service.service.groupcreditestablish.GroupCreditEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.groupcreditreview.GroupCreditReviewBaseInfoService;
import cn.zswltech.mithras.service.service.groupcreditreview.GroupCreditReviewService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @description 集团授信评审基本信息表
* @author wangchuanhao
* @date 2022-11-11
*/
@RestController
public class GroupCreditReviewBaseInfoController implements GroupCreditReviewBaseInfoApi {

    @Resource
    private GroupCreditReviewBaseInfoService groupCreditReviewBaseInfoService;
    @Resource
    private GroupCreditEstablishBaseInfoService groupCreditEstablishBaseInfoService;
    @Resource
    private GroupCreditReviewService groupCreditReviewService;

    @Override
    public R<GroupCreditReviewBaseInfoAddRSP> add(GroupCreditReviewBaseInfoAddREQ req) {
        return R.ok(groupCreditReviewBaseInfoService.add(req));
    }

    @Override
    public R<Void> modify(GroupCreditReviewBaseInfoModifyREQ req){
        groupCreditReviewBaseInfoService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<GroupCreditReviewListRSP>> list(GroupCreditReviewListREQ req){
        PageR<GroupCreditReviewListRSP> data = groupCreditReviewBaseInfoService.list(req);
        return R.ok(data);
    }

    @Override
    public R<GroupCreditReviewBaseInfoDetailRSP> detail(GroupCreditReviewBaseInfoDetailREQ req) {
        return R.ok(groupCreditReviewBaseInfoService.detail(req.getGroupCreditReviewId(), req.getVersion()));
    }

    @Override
    public R<Long> getClientStockRiskExposure(GroupCreditReviewBaseInfoDetailREQ req) {
        return R.ok(groupCreditReviewService.getRemainCreditAmount(req.getGroupCreditReviewId()));
    }

    @Override
    public R<GroupCreditReviewInfoUpdateRatingRSP> updateRating(GroupCreditReviewBaseInfoUpdateRatingREQ req) {
        return R.ok(groupCreditReviewBaseInfoService.updateRating(req));
    }

    @Override
    public R<List<GroupCreditEstablishVagueListRSP>> vague(GroupCreditEstablishVagueListREQ req) {
        List<GroupCreditEstablishVagueListRSP> groupCreditEstablishList = groupCreditEstablishBaseInfoService.vagueQuery(req);
        if (CollectionUtils.isEmpty(groupCreditEstablishList)) {
            return R.ok(new ArrayList<>());
        }
        // 查询出已有授信评审的项目并过滤掉
        Set<Long> existReviewEstablishIdSet = groupCreditReviewBaseInfoService.getBaseMapper().selectList(Wrappers.<GroupCreditReviewBaseInfo>lambdaQuery()
                        .in(GroupCreditReviewBaseInfo::getGroupCreditEstablishId, groupCreditEstablishList.stream().map(GroupCreditEstablishVagueListRSP::getId).collect(Collectors.toList())))
                .stream().map(GroupCreditReviewBaseInfo::getGroupCreditEstablishId)
                .collect(Collectors.toSet());
        groupCreditEstablishList.removeIf(e -> existReviewEstablishIdSet.contains(e.getId()));
        return R.ok(groupCreditEstablishList);
    }

}