package cn.zswltech.mithras.service.service.lib.groupcreditestablish.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.groupcreditestablish.baseinfo.GroupCreditEstablishBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoListRSP;
import cn.zswltech.mithras.service.convert.groupcreditestablish.GroupCreditEstablishBaseInfoConverter;
import cn.zswltech.mithras.credit.domain.groupcredit.establish.enums.GroupCreditEstablishInfoModule;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.establish.model.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.establish.model.GroupCreditEstablishBaseInfoLib;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.groupcreditestablish.GroupCreditEstablishBaseInfoService;
import cn.zswltech.mithras.credit.application.groupcredit.establish.handler.GroupCreditEstablishLibAbstractHandler;
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
    private GroupCreditEstablishBaseInfoConverter baseInfoConverter;
    @Resource
    private GroupCreditEstablishBaseInfoService baseInfoService;
    @Resource
    private SysUserService sysUserService;

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
        return BeanUtil.copyProperties(f, GroupCreditEstablishBaseInfoLib.class);
    }

    @Override
    protected GroupCreditEstablishBaseInfo lib2Entity(GroupCreditEstablishBaseInfoLib t) {
        return BeanUtil.copyProperties(t, GroupCreditEstablishBaseInfo.class);
    }

    @Override
    protected GroupCreditEstablishBaseInfoDetailRSP lib2Rsp(GroupCreditEstablishBaseInfoLib f) {
        GroupCreditEstablishBaseInfoDetailRSP rsp = baseInfoConverter.entityToDetailRSP(f);
        // fill names
        baseInfoService.join(rsp);
        rsp.setId(f.getOriginId());
        // 帮前端兼容下
        rsp.setIsBizDept(sysUserService.currentUserIsBizDept());
        return rsp;
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
