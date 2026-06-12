package cn.zswltech.mithras.policy.versioning.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.TypeReference;
import cn.zswltech.mithras.dto.policy.PolicyInfoDetailRSP;
import cn.zswltech.mithras.policy.application.port.PolicyProjectClientInfoPort;
import cn.zswltech.mithras.policy.application.port.model.PolicyProjectClientInfo;
import cn.zswltech.mithras.policy.persistence.model.PolicyInfo;
import cn.zswltech.mithras.policy.persistence.model.PolicyInfoLib;
import cn.zswltech.mithras.policy.versioning.handler.PolicyAbstractHandler;
import cn.zswltech.mithras.policy.versioning.handler.PolicyInfoModule;
import cn.zswltech.mithras.foundation.port.UserNameResolver;
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
    private PolicyProjectClientInfoPort policyProjectClientInfoPort;
    @Resource
    private UserNameResolver userNameResolver;

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
        PolicyProjectClientInfo projInfo = policyProjectClientInfoPort.getByProjectId(f.getProjId());
        PolicyInfoDetailRSP rsp = new PolicyInfoDetailRSP();
        rsp.setId(f.getOriginId());
        rsp.setPolicyCode(f.getPolicyCode());
        rsp.setProjId(f.getProjId());
        rsp.setProjName(projInfo.getProjectName());
        rsp.setProjCode(projInfo.getProjectCode());
        rsp.setClientId(projInfo.getClientId());
        rsp.setInsuranceStartDate(f.getInsuranceStartDate());
        rsp.setInsuranceEndDate(f.getInsuranceEndDate());
        rsp.setInsuranceCompany(f.getInsuranceCompany());
        rsp.setPolicyAmount(f.getPolicyAmount());
        rsp.setProjSponsorUserId(projInfo.getProjectSponsorUserId());
        Set<Long> sysUserIds = new HashSet<>();
        rsp.setProjCosponsorUserIds(isBlank(projInfo.getProjectCosponsorUserIds()) ? null : toBean(projInfo.getProjectCosponsorUserIds(), new TypeReference<List<Long>>() {
        }, true));
        sysUserIds.add(rsp.getProjSponsorUserId());
        if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
            sysUserIds.addAll(rsp.getProjCosponsorUserIds());
        }
        Map<Long, String> sysUserMap = userNameResolver.sysUserId2Name(sysUserIds);
        rsp.setProjSponsorUserName(sysUserMap.get(rsp.getProjSponsorUserId()));
        if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
            rsp.setProjCosponsorUserNames(rsp.getProjCosponsorUserIds().stream().map(sysUserMap::get).collect(Collectors.toList()));
        }
        rsp.setClientName(projInfo.getClientName());
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
