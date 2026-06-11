package cn.zswltech.mithras.credit.controller.groupcredit.establish;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.groupcreditestablish.GroupCreditEstablishListREQ;
import cn.zswltech.mithras.dto.groupcreditestablish.GroupCreditEstablishListRSP;
import cn.zswltech.mithras.dto.groupcreditestablish.baseinfo.GroupCreditEstablishBaseInfoAddREQ;
import cn.zswltech.mithras.dto.groupcreditestablish.baseinfo.GroupCreditEstablishBaseInfoAddRSP;
import cn.zswltech.mithras.dto.groupcreditestablish.baseinfo.GroupCreditEstablishBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.groupcreditestablish.baseinfo.GroupCreditEstablishBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.groupcreditestablish.baseinfo.GroupCreditEstablishBaseInfoModifyREQ;
import cn.zswltech.mithras.dto.groupcreditestablish.baseinfo.GroupCreditEstalishBaseInfoUpdateRatingREQ;
import cn.zswltech.mithras.dto.groupcreditestablish.baseinfo.GroupCreditEstalishInfoUpdateRatingRSP;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ClientIdREQ;
import cn.zswltech.mithras.api.groupcreditestablish.GroupCreditEstablishBaseInfoApi;
import cn.zswltech.mithras.credit.application.groupcredit.establish.GroupCreditEstablishBaseInfoApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class GroupCreditEstablishBaseInfoController implements GroupCreditEstablishBaseInfoApi {
    @Resource
    private GroupCreditEstablishBaseInfoApplicationService groupCreditEstablishBaseInfoApplicationService;

    @Override
    public R<GroupCreditEstablishBaseInfoAddRSP> add(GroupCreditEstablishBaseInfoAddREQ req) {
        return groupCreditEstablishBaseInfoApplicationService.add(req);
    }

    @Override
    public R<Void> modify(GroupCreditEstablishBaseInfoModifyREQ req) {
        return groupCreditEstablishBaseInfoApplicationService.modify(req);
    }

    @Override
    public R<PageR<GroupCreditEstablishListRSP>> list(GroupCreditEstablishListREQ req) {
        return groupCreditEstablishBaseInfoApplicationService.list(req);
    }

    @Override
    public R<GroupCreditEstablishBaseInfoDetailRSP> detail(GroupCreditEstablishBaseInfoDetailREQ req) {
        return groupCreditEstablishBaseInfoApplicationService.detail(req);
    }

    @Override
    public R<Long> getClientStockRiskExposure(ClientIdREQ req) {
        return groupCreditEstablishBaseInfoApplicationService.getClientStockRiskExposure(req);
    }

    @Override
    public R<GroupCreditEstalishInfoUpdateRatingRSP> updateRating(GroupCreditEstalishBaseInfoUpdateRatingREQ req) {
        return groupCreditEstablishBaseInfoApplicationService.updateRating(req);
    }
}
