package cn.zswltech.mithras.application.orchestration.facade.credit.groupcredit.establish;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.groupcreditestablish.GroupCreditEstablishListREQ;
import cn.zswltech.mithras.dto.groupcreditestablish.GroupCreditEstablishListRSP;
import cn.zswltech.mithras.dto.groupcreditestablish.baseinfo.*;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ClientIdREQ;
import cn.zswltech.mithras.credit.groupcredit.establish.model.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.application.orchestration.credit.groupcredit.establish.GroupCreditEstablishBaseInfoService;
import org.springframework.stereotype.Service;

import cn.zswltech.mithras.credit.application.groupcredit.establish.GroupCreditEstablishBaseInfoApplicationService;
import javax.annotation.Resource;

import static cn.hutool.core.bean.BeanUtil.copyProperties;

/**
* @description 集团授信立项基本信息表
* @author wangchuanhao
* @date 2022-11-11
*/
@Service
public class GroupCreditEstablishBaseInfoFacade implements GroupCreditEstablishBaseInfoApplicationService {

    @Resource
    private GroupCreditEstablishBaseInfoService groupCreditEstablishBaseInfoService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    @Override
    public R<GroupCreditEstablishBaseInfoAddRSP> add(GroupCreditEstablishBaseInfoAddREQ req) {
        GroupCreditEstablishBaseInfo info = groupCreditEstablishBaseInfoService.add(req);
        return R.ok(copyProperties(info, GroupCreditEstablishBaseInfoAddRSP.class));
    }

    @Override
    public R<Void> modify(GroupCreditEstablishBaseInfoModifyREQ req){
        groupCreditEstablishBaseInfoService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<GroupCreditEstablishListRSP>> list(GroupCreditEstablishListREQ req){
        PageR<GroupCreditEstablishListRSP> data = groupCreditEstablishBaseInfoService.list(req);
        return R.ok(data);
    }

    @Override
    public R<GroupCreditEstablishBaseInfoDetailRSP> detail(GroupCreditEstablishBaseInfoDetailREQ req) {
        return R.ok(groupCreditEstablishBaseInfoService.detail(req.getGroupCreditEstablishId(), req.getVersion()));
    }

    @Override
    public R<Long> getClientStockRiskExposure(ClientIdREQ req) {
        return R.ok(contractBaseInfoService.getGroupCreditStockRiskExposure(req.getClientId()));
    }

    @Override
    public R<GroupCreditEstalishInfoUpdateRatingRSP> updateRating(GroupCreditEstalishBaseInfoUpdateRatingREQ req) {
        return R.ok(groupCreditEstablishBaseInfoService.updateRating(req));
    }

}