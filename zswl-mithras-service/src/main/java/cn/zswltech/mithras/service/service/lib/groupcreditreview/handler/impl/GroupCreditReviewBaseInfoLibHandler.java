package cn.zswltech.mithras.service.service.lib.groupcreditreview.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.groupcreditreview.baseinfo.GroupCreditReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.service.convert.groupcreditreview.GroupCreditReviewBaseInfoConverter;
import cn.zswltech.mithras.service.enums.groupcreditreview.GroupCreditReviewInfoModule;
import cn.zswltech.mithras.service.mapper.model.groupcreditreview.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.model.groupcreditreview.GroupCreditReviewBaseInfoLib;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.groupcreditreview.GroupCreditReviewBaseInfoService;
import cn.zswltech.mithras.service.service.lib.groupcreditreview.handler.GroupCreditReviewLibAbstractHandler;
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
    private GroupCreditReviewBaseInfoConverter baseInfoConverter;
    @Resource
    private GroupCreditReviewBaseInfoService baseInfoService;
    @Resource
    private SysUserService sysUserService;

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
        return BeanUtil.copyProperties(f, GroupCreditReviewBaseInfoLib.class);
    }

    @Override
    protected GroupCreditReviewBaseInfo lib2Entity(GroupCreditReviewBaseInfoLib t) {
        return BeanUtil.copyProperties(t, GroupCreditReviewBaseInfo.class);
    }

    @Override
    protected GroupCreditReviewBaseInfoDetailRSP lib2Rsp(GroupCreditReviewBaseInfoLib f) {
        GroupCreditReviewBaseInfoDetailRSP rsp = baseInfoConverter.entityToDetailRSP(f);
        // fill names
        baseInfoService.join(rsp);
        rsp.setApprovedAmount(f.getProjectApprovalAmount());
        rsp.setId(f.getOriginId());
        // 帮前端兼容下
        rsp.setIsBizDept(sysUserService.currentUserIsBizDept());
        return rsp;
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
