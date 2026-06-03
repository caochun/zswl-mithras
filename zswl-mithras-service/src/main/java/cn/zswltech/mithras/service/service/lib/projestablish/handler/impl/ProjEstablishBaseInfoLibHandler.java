package cn.zswltech.mithras.service.service.lib.projestablish.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoListRSP;
import cn.zswltech.mithras.dto.projestablish.baseinfo.jsonbean.ProjEstablishPersonInfo;
import cn.zswltech.mithras.service.convert.projestablish.ProjEstablishBaseInfoConverter;
import cn.zswltech.mithras.service.enums.projestablish.ProjEstablishInfoModule;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfoLib;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.lib.projestablish.handler.ProjEstablishLibAbstractHandler;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishBaseInfoService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.util.ObjectUtil.isNotNull;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Service
public class ProjEstablishBaseInfoLibHandler
        extends ProjEstablishLibAbstractHandler<ProjEstablishBaseInfoLib, ProjEstablishBaseInfo, ProjEstablishBaseInfoListRSP> {
    @Resource
    private ProjEstablishBaseInfoConverter baseInfoConverter;
    @Resource
    private ProjEstablishBaseInfoService baseInfoService;

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
        ProjEstablishBaseInfoListRSP rsp = baseInfoConverter.entityToDetailRSP(f);
        // fill names
        baseInfoService.join(rsp);
        rsp.setId(f.getOriginId());
        return rsp;
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
