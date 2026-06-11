package cn.zswltech.mithras.application.orchestration.adapter.groupcredit;

import cn.zswltech.mithras.credit.application.groupcredit.GroupCreditBaseInfoAssembler;
import cn.zswltech.mithras.credit.application.groupcredit.convert.establish.GroupCreditEstablishBaseInfoConverter;
import cn.zswltech.mithras.credit.application.groupcredit.convert.review.GroupCreditReviewBaseInfoConverter;
import cn.zswltech.mithras.credit.groupcredit.establish.mapper.model.GroupCreditEstablishBaseInfoLib;
import cn.zswltech.mithras.credit.groupcredit.review.mapper.model.GroupCreditReviewBaseInfoLib;
import cn.zswltech.mithras.dto.groupcreditestablish.baseinfo.GroupCreditEstablishBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.groupcreditreview.baseinfo.GroupCreditReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.application.orchestration.groupcredit.establish.GroupCreditEstablishBaseInfoService;
import cn.zswltech.mithras.application.orchestration.groupcredit.review.GroupCreditReviewBaseInfoService;
import cn.zswltech.mithras.system.user.SysUserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class GroupCreditBaseInfoAssemblerAdapter implements GroupCreditBaseInfoAssembler {

    @Resource
    private GroupCreditEstablishBaseInfoConverter establishBaseInfoConverter;
    @Resource
    private GroupCreditEstablishBaseInfoService establishBaseInfoService;
    @Resource
    private GroupCreditReviewBaseInfoConverter reviewBaseInfoConverter;
    @Resource
    private GroupCreditReviewBaseInfoService reviewBaseInfoService;
    @Resource
    private SysUserService sysUserService;

    @Override
    public GroupCreditEstablishBaseInfoDetailRSP establishLib2Rsp(GroupCreditEstablishBaseInfoLib lib) {
        GroupCreditEstablishBaseInfoDetailRSP rsp = establishBaseInfoConverter.entityToDetailRSP(lib);
        establishBaseInfoService.join(rsp);
        rsp.setId(lib.getOriginId());
        rsp.setIsBizDept(sysUserService.currentUserIsBizDept());
        return rsp;
    }

    @Override
    public GroupCreditReviewBaseInfoDetailRSP reviewLib2Rsp(GroupCreditReviewBaseInfoLib lib) {
        GroupCreditReviewBaseInfoDetailRSP rsp = reviewBaseInfoConverter.entityToDetailRSP(lib);
        reviewBaseInfoService.join(rsp);
        rsp.setApprovedAmount(lib.getProjectApprovalAmount());
        rsp.setId(lib.getOriginId());
        rsp.setIsBizDept(sysUserService.currentUserIsBizDept());
        return rsp;
    }
}
