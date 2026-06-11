package cn.zswltech.mithras.credit.controller.groupcredit.establish;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.groupcreditestablish.GroupCreditEstablishRatingCheckRSP;
import cn.zswltech.mithras.dto.groupcreditestablish.version.GroupCreditEstablishEffectREQ;
import cn.zswltech.mithras.dto.groupcreditestablish.version.GroupCreditEstablishVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.api.groupcreditestablish.GroupCreditEstablishVersionApi;
import cn.zswltech.mithras.credit.application.groupcredit.establish.GroupCreditEstablishVersionApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class GroupCreditEstablishVersionController implements GroupCreditEstablishVersionApi {
    @Resource
    private GroupCreditEstablishVersionApplicationService groupCreditEstablishVersionApplicationService;

    @Override
    public R<Void> effect(GroupCreditEstablishEffectREQ req) {
        return groupCreditEstablishVersionApplicationService.effect(req);
    }

    @Override
    public R<PageR<CommonVersionListRSP>> list(CommonVersionListREQ req) {
        return groupCreditEstablishVersionApplicationService.list(req);
    }

    @Override
    public R<CommonVersionDiffRSP> comparePreVersion(GroupCreditEstablishVersionDiffREQ req) {
        return groupCreditEstablishVersionApplicationService.comparePreVersion(req);
    }

    @Override
    public R<GroupCreditEstablishRatingCheckRSP> checkRatingInfo(SinglePkREQ req) {
        return groupCreditEstablishVersionApplicationService.checkRatingInfo(req);
    }
}
