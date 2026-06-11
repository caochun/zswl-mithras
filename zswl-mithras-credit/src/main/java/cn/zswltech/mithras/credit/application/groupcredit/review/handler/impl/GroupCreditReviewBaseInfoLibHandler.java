package cn.zswltech.mithras.credit.application.groupcredit.review.handler.impl;

import cn.zswltech.mithras.dto.groupcreditreview.baseinfo.GroupCreditReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.credit.groupcredit.review.enums.GroupCreditReviewInfoModule;
import cn.zswltech.mithras.credit.groupcredit.review.model.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.credit.groupcredit.review.model.GroupCreditReviewBaseInfoLib;
import cn.zswltech.mithras.credit.application.groupcredit.GroupCreditBaseInfoAssembler;
import cn.zswltech.mithras.credit.application.groupcredit.review.handler.GroupCreditReviewLibAbstractHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

/**
 * 集团授信立项版本处理器
 *
 * @author wangchuanhao
 * @date 2022/11/11 16:49 PM
 */
@Component
public class GroupCreditReviewBaseInfoLibHandler
        extends GroupCreditReviewLibAbstractHandler<GroupCreditReviewBaseInfoLib, GroupCreditReviewBaseInfo, GroupCreditReviewBaseInfoDetailRSP> {

    @Resource
    private GroupCreditBaseInfoAssembler baseInfoAssembler;

    @Override
    public Set<String> compareIgnoreFieldNames() {
        HashSet<String> fields = new HashSet<>();
        fields.add("groupCreditReviewStatus");
        fields.add("groupCreditReviewProcessStatus");
        fields.add("createTime");
        fields.add("createBy");
        fields.add("updateTime");
        fields.add("updateBy");
        return fields;
    }

    @Override
    protected GroupCreditReviewBaseInfoLib entity2Lib(GroupCreditReviewBaseInfo f) {
        GroupCreditReviewBaseInfoLib lib = new GroupCreditReviewBaseInfoLib();
        BeanUtils.copyProperties(f, lib);
        return lib;
    }

    @Override
    protected GroupCreditReviewBaseInfo lib2Entity(GroupCreditReviewBaseInfoLib t) {
        GroupCreditReviewBaseInfo entity = new GroupCreditReviewBaseInfo();
        BeanUtils.copyProperties(t, entity);
        return entity;
    }

    @Override
    protected GroupCreditReviewBaseInfoDetailRSP lib2Rsp(GroupCreditReviewBaseInfoLib f) {
        return baseInfoAssembler.reviewLib2Rsp(f);
    }

    @Override
    public GroupCreditReviewInfoModule getSubModule() {
        return GroupCreditReviewInfoModule.BASE_INFO;
    }

    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }

    @Override
    public String libMainIdFieldName() {
        return "origin_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "id";
    }

    @Override
    public boolean isMainTable() {
        return true;
    }
}
