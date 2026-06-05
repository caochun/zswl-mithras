package cn.zswltech.mithras.service.adapter.groupcredit.review;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.credit.application.groupcredit.review.service.GroupCreditReviewBaseInfoApplicationService;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.model.GroupCreditReviewBaseInfo;
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
import cn.zswltech.mithras.service.service.groupcreditestablish.GroupCreditEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.groupcreditreview.GroupCreditReviewBaseInfoService;
import cn.zswltech.mithras.service.service.groupcreditreview.GroupCreditReviewService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupCreditReviewBaseInfoApplicationAdapter implements GroupCreditReviewBaseInfoApplicationService {

    @Resource
    private GroupCreditReviewBaseInfoService groupCreditReviewBaseInfoService;
    @Resource
    private GroupCreditEstablishBaseInfoService groupCreditEstablishBaseInfoService;
    @Resource
    private GroupCreditReviewService groupCreditReviewService;

    @Override
    public GroupCreditReviewBaseInfoAddRSP add(GroupCreditReviewBaseInfoAddREQ req) {
        return groupCreditReviewBaseInfoService.add(req);
    }

    @Override
    public void modify(GroupCreditReviewBaseInfoModifyREQ req) {
        groupCreditReviewBaseInfoService.modify(req);
    }

    @Override
    public PageR<GroupCreditReviewListRSP> list(GroupCreditReviewListREQ req) {
        return groupCreditReviewBaseInfoService.list(req);
    }

    @Override
    public GroupCreditReviewBaseInfoDetailRSP detail(GroupCreditReviewBaseInfoDetailREQ req) {
        return groupCreditReviewBaseInfoService.detail(req.getGroupCreditReviewId(), req.getVersion());
    }

    @Override
    public Long getClientStockRiskExposure(GroupCreditReviewBaseInfoDetailREQ req) {
        return groupCreditReviewService.getRemainCreditAmount(req.getGroupCreditReviewId());
    }

    @Override
    public GroupCreditReviewInfoUpdateRatingRSP updateRating(GroupCreditReviewBaseInfoUpdateRatingREQ req) {
        return groupCreditReviewBaseInfoService.updateRating(req);
    }

    @Override
    public List<GroupCreditEstablishVagueListRSP> vague(GroupCreditEstablishVagueListREQ req) {
        List<GroupCreditEstablishVagueListRSP> groupCreditEstablishList = groupCreditEstablishBaseInfoService.vagueQuery(req);
        if (CollectionUtils.isEmpty(groupCreditEstablishList)) {
            return new ArrayList<>();
        }
        Set<Long> existReviewEstablishIdSet = groupCreditReviewBaseInfoService.getBaseMapper().selectList(Wrappers.<GroupCreditReviewBaseInfo>lambdaQuery()
                        .in(GroupCreditReviewBaseInfo::getGroupCreditEstablishId, groupCreditEstablishList.stream().map(GroupCreditEstablishVagueListRSP::getId).collect(Collectors.toList())))
                .stream().map(GroupCreditReviewBaseInfo::getGroupCreditEstablishId)
                .collect(Collectors.toSet());
        groupCreditEstablishList.removeIf(e -> existReviewEstablishIdSet.contains(e.getId()));
        return groupCreditEstablishList;
    }
}
