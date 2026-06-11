package cn.zswltech.mithras.credit.application.groupcredit.establish.handler.impl;

import cn.zswltech.mithras.dto.groupcreditestablish.baseinfo.GroupCreditEstablishBaseInfoDetailRSP;
import cn.zswltech.mithras.credit.groupcredit.establish.enums.GroupCreditEstablishInfoModule;
import cn.zswltech.mithras.credit.groupcredit.establish.model.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.credit.groupcredit.establish.model.GroupCreditEstablishBaseInfoLib;
import cn.zswltech.mithras.credit.application.groupcredit.GroupCreditBaseInfoAssembler;
import cn.zswltech.mithras.credit.application.groupcredit.establish.handler.GroupCreditEstablishLibAbstractHandler;
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
public class GroupCreditEstablishBaseInfoLibHandler
        extends GroupCreditEstablishLibAbstractHandler<GroupCreditEstablishBaseInfoLib, GroupCreditEstablishBaseInfo, GroupCreditEstablishBaseInfoDetailRSP> {

    @Resource
    private GroupCreditBaseInfoAssembler baseInfoAssembler;

    @Override
    public Set<String> compareIgnoreFieldNames() {
        HashSet<String> fields = new HashSet<>();
        fields.add("groupCreditEstablishStatus");
        fields.add("groupCreditEstablishProcessStatus");
        fields.add("createTime");
        fields.add("createBy");
        fields.add("updateTime");
        fields.add("updateBy");
        return fields;
    }

    @Override
    protected GroupCreditEstablishBaseInfoLib entity2Lib(GroupCreditEstablishBaseInfo f) {
        GroupCreditEstablishBaseInfoLib lib = new GroupCreditEstablishBaseInfoLib();
        BeanUtils.copyProperties(f, lib);
        return lib;
    }

    @Override
    protected GroupCreditEstablishBaseInfo lib2Entity(GroupCreditEstablishBaseInfoLib t) {
        GroupCreditEstablishBaseInfo entity = new GroupCreditEstablishBaseInfo();
        BeanUtils.copyProperties(t, entity);
        return entity;
    }

    @Override
    protected GroupCreditEstablishBaseInfoDetailRSP lib2Rsp(GroupCreditEstablishBaseInfoLib f) {
        return baseInfoAssembler.establishLib2Rsp(f);
    }

    @Override
    public GroupCreditEstablishInfoModule getSubModule() {
        return GroupCreditEstablishInfoModule.BASE_INFO;
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
