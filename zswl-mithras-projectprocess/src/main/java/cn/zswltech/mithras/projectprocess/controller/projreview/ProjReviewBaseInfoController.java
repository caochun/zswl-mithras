package cn.zswltech.mithras.projectprocess.controller.projreview;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.groupcreditreview.GroupCreditReviewVagueListREQ;
import cn.zswltech.mithras.dto.groupcreditreview.GroupCreditReviewVagueListRSP;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListRSP;
import cn.zswltech.mithras.dto.projreview.CorpSubjectItemCheckReasonREQ;
import cn.zswltech.mithras.dto.projreview.CorpSubjectItemCheckResult;
import cn.zswltech.mithras.dto.projreview.CorpSubjectItemCheckResultDetail;
import cn.zswltech.mithras.dto.projreview.ProjReviewButtonStatusRsp;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoAddByGroupCreditREQ;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoAddREQ;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoAddRSP;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoListREQ;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoListRSP;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoModifyREQ;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoRemoveREQ;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoUpdateRatingREQ;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoUpdateRatingRSP;
import java.util.List;
import cn.zswltech.mithras.api.projreview.ProjReviewBaseInfoApi;
import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewBaseInfoApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class ProjReviewBaseInfoController implements ProjReviewBaseInfoApi {
    @Resource
    private ProjReviewBaseInfoApplicationService projReviewBaseInfoApplicationService;

    @Override
    public R<List<ProjEstablishVagueListRSP>> vague(ProjEstablishVagueListREQ req) {
        return projReviewBaseInfoApplicationService.vague(req);
    }

    @Override
    public R<List<ProjEstablishVagueListRSP>> vagueEffect(ProjEstablishVagueListREQ req) {
        return projReviewBaseInfoApplicationService.vagueEffect(req);
    }

    @Override
    public R<List<GroupCreditReviewVagueListRSP>> groupCreditReviewVague(GroupCreditReviewVagueListREQ req) {
        return projReviewBaseInfoApplicationService.groupCreditReviewVague(req);
    }

    @Override
    public R<ProjReviewBaseInfoAddRSP> add(ProjReviewBaseInfoAddREQ req) {
        return projReviewBaseInfoApplicationService.add(req);
    }

    @Override
    public R<ProjReviewBaseInfoAddRSP> addByGroupCredit(ProjReviewBaseInfoAddByGroupCreditREQ req) {
        return projReviewBaseInfoApplicationService.addByGroupCredit(req);
    }

    @Override
    public R<Void> modify(ProjReviewBaseInfoModifyREQ req) {
        return projReviewBaseInfoApplicationService.modify(req);
    }

    @Override
    public R<ProjReviewBaseInfoUpdateRatingRSP> updateRating(ProjReviewBaseInfoUpdateRatingREQ req) {
        return projReviewBaseInfoApplicationService.updateRating(req);
    }

    @Override
    public R<PageR<ProjReviewBaseInfoListRSP>> list(ProjReviewBaseInfoListREQ req) {
        return projReviewBaseInfoApplicationService.list(req);
    }

    @Override
    public R<ProjReviewBaseInfoDetailRSP> detail(ProjReviewBaseInfoDetailREQ req) {
        return projReviewBaseInfoApplicationService.detail(req);
    }

    @Override
    public R<Void> disable(ProjReviewBaseInfoRemoveREQ req) {
        return projReviewBaseInfoApplicationService.disable(req);
    }

    @Override
    public R<ProjReviewButtonStatusRsp> buttonStatus(ProjReviewBaseInfoDetailREQ req) {
        return projReviewBaseInfoApplicationService.buttonStatus(req);
    }

    @Override
    public R<CorpSubjectItemCheckResult> preCheckSubjectCorpItem(SinglePkREQ req) {
        return projReviewBaseInfoApplicationService.preCheckSubjectCorpItem(req);
    }

    @Override
    public R<CorpSubjectItemCheckResult> preCheckReviewMaterialComments(SinglePkREQ req) {
        return projReviewBaseInfoApplicationService.preCheckReviewMaterialComments(req);
    }

    @Override
    public R<List<CorpSubjectItemCheckResultDetail>> listSubjectItemCheckResult(SinglePkREQ req) {
        return projReviewBaseInfoApplicationService.listSubjectItemCheckResult(req);
    }

    @Override
    public R<Void> saveSubjectItemReason(CorpSubjectItemCheckReasonREQ req) {
        return projReviewBaseInfoApplicationService.saveSubjectItemReason(req);
    }
}
