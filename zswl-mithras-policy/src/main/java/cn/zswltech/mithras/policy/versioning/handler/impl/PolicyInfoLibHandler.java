package cn.zswltech.mithras.policy.versioning.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.TypeReference;
import cn.zswltech.mithras.dto.policy.PolicyInfoDetailRSP;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.policy.model.PolicyInfo;
import cn.zswltech.mithras.policy.model.PolicyInfoLib;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.policy.versioning.handler.PolicyAbstractHandler;
import cn.zswltech.mithras.policy.versioning.handler.PolicyInfoModule;
import cn.zswltech.mithras.foundation.port.UserNameResolver;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
    private ClientMapper clientMapper;
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
        Map<Long, String> clientMap = clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                        .in(Client::getId, clientIds))
                .stream()
                .collect(Collectors.toMap(Client::getId, Client::getClientName));
        Map<Long, String> sysUserMap = userNameResolver.sysUserId2Name(sysUserIds);
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
