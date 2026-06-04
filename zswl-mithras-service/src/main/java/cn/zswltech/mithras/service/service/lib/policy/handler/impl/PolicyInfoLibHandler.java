package cn.zswltech.mithras.policy.application.lib.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.TypeReference;
import cn.zswltech.mithras.dto.policy.PolicyInfoDetailRSP;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoListRSP;
import cn.zswltech.mithras.service.convert.projestablish.ProjEstablishBaseInfoConverter;
import cn.zswltech.mithras.projectprocess.enums.projestablish.ProjEstablishInfoModule;
import cn.zswltech.mithras.policy.infrastructure.persistence.model.PolicyInfo;
import cn.zswltech.mithras.policy.infrastructure.persistence.model.PolicyInfoLib;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfoLib;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.policy.application.lib.handler.PolicyAbstractHandler;
import cn.zswltech.mithras.policy.application.lib.handler.PolicyInfoModule;
import cn.zswltech.mithras.projectprocess.service.lib.projestablish.handler.ProjEstablishLibAbstractHandler;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishBaseInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.hutool.core.text.CharSequenceUtil.isBlank;
import static cn.hutool.json.JSONUtil.toBean;


@Service
public class PolicyInfoLibHandler
        extends PolicyAbstractHandler<PolicyInfoLib, PolicyInfo,
        PolicyInfoDetailRSP> {

    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private Id2NameService id2NameService;

    @Override
    public Set<String> compareIgnoreFieldNames() {
        HashSet<String> fields = new HashSet<>();
        fields.add("approvalStatus");
        fields.add("createTime");
        fields.add("updateTime");
        return fields;
    }

    @Override
    protected PolicyInfoLib entity2Lib(PolicyInfo f) {
        return BeanUtil.copyProperties(f, PolicyInfoLib.class);
    }

    @Override
    protected PolicyInfo lib2Entity(PolicyInfoLib t) {
        return BeanUtil.copyProperties(t, PolicyInfo.class);
    }

    @Override
    protected PolicyInfoDetailRSP lib2Rsp(PolicyInfoLib f) {
        ProjReviewBaseInfo projInfo = projReviewBaseInfoMapper.selectById(f.getProjId());
        PolicyInfoDetailRSP rsp = new PolicyInfoDetailRSP();
        rsp.setId(f.getOriginId());
        rsp.setPolicyCode(f.getPolicyCode());
        rsp.setProjId(f.getProjId());
        rsp.setProjName(projInfo.getProjName());
        rsp.setProjCode(projInfo.getProjCode());
        rsp.setClientId(projInfo.getClientId());
        rsp.setInsuranceStartDate(f.getInsuranceStartDate());
        rsp.setInsuranceEndDate(f.getInsuranceEndDate());
        rsp.setInsuranceCompany(f.getInsuranceCompany());
        rsp.setPolicyAmount(f.getPolicyAmount());
        rsp.setProjSponsorUserId(projInfo.getProjSponsorUserId());
        Set<Long> sysUserIds = new HashSet<>();
        Set<Long> clientIds = new HashSet<>();
        rsp.setProjCosponsorUserIds(isBlank(projInfo.getProjCosponsorUserIds()) ? null : toBean(projInfo.getProjCosponsorUserIds(), new TypeReference<List<Long>>() {
        }, true));
        sysUserIds.add(rsp.getProjSponsorUserId());
        if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
            sysUserIds.addAll(rsp.getProjCosponsorUserIds());
        }
        clientIds.add(rsp.getClientId());
        Map<Long, String> clientMap = id2NameService.clientId2Name(clientIds);
        Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(sysUserIds);
        rsp.setProjSponsorUserName(sysUserMap.get(rsp.getProjSponsorUserId()));
        if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
            rsp.setProjCosponsorUserNames(rsp.getProjCosponsorUserIds().stream().map(sysUserMap::get).collect(Collectors.toList()));
        }
        rsp.setClientName(clientMap.get(rsp.getClientId()));
        return rsp;
    }

    @Override
    public PolicyInfoModule getSubModule() {
        return PolicyInfoModule.BASE_INFO;
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
