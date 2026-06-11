package cn.zswltech.mithras.projectprocess.versioning.projestablish.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoListRSP;
import cn.zswltech.mithras.projectprocess.enums.projestablish.ProjEstablishInfoModule;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfoLib;
import cn.zswltech.mithras.projectprocess.versioning.projestablish.handler.ProjEstablishLibAbstractHandler;
import cn.zswltech.mithras.projectprocess.application.support.ProjectProcessBaseInfoAssembler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Service
public class ProjEstablishBaseInfoLibHandler
        extends ProjEstablishLibAbstractHandler<ProjEstablishBaseInfoLib, ProjEstablishBaseInfo, ProjEstablishBaseInfoListRSP> {
    @Resource
    private ProjectProcessBaseInfoAssembler baseInfoAssembler;

    @Override
    public Set<String> compareIgnoreFieldNames() {
        HashSet<String> fields = new HashSet<>();
        fields.add("projEstablishStatus");
        fields.add("projEstablishProcessStatus");
        fields.add("createTime");
        fields.add("updateTime");
        return fields;
    }

    @Override
    protected ProjEstablishBaseInfoLib entity2Lib(ProjEstablishBaseInfo f) {
        return BeanUtil.copyProperties(f, ProjEstablishBaseInfoLib.class);
    }

    @Override
    protected ProjEstablishBaseInfo lib2Entity(ProjEstablishBaseInfoLib t) {
        return BeanUtil.copyProperties(t, ProjEstablishBaseInfo.class);
    }

    @Override
    protected ProjEstablishBaseInfoListRSP lib2Rsp(ProjEstablishBaseInfoLib f) {
        return baseInfoAssembler.establishLib2Rsp(f);
    }

    @Override
    public ProjEstablishInfoModule getSubModule() {
        return ProjEstablishInfoModule.BASE_INFO;
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
