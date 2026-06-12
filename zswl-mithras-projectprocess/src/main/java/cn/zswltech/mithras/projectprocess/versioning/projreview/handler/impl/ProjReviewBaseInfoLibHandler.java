package cn.zswltech.mithras.projectprocess.versioning.projreview.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjReviewInfoModule;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjectType;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfoLib;
import cn.zswltech.mithras.projectprocess.application.support.ProjectProcessDictionaryPort;
import cn.zswltech.mithras.projectprocess.application.support.ProjectProcessNameResolver;
import cn.zswltech.mithras.projectprocess.versioning.projreview.handler.ProjReviewLibAbstractHandler;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.hutool.core.util.ObjectUtil.isNotNull;

/**
 * @author
 * @description
 * @since
 */
@Service
public class ProjReviewBaseInfoLibHandler
        extends ProjReviewLibAbstractHandler<ProjReviewBaseInfoLib, ProjReviewBaseInfo, ProjReviewBaseInfoDetailRSP> {

    @Resource
    private ProjectProcessNameResolver id2NameService;

    @Resource
    private ProjectProcessDictionaryPort projectProcessDictionaryPort;

    @Override
    public Set<String> compareIgnoreFieldNames() {
        HashSet<String> fields = new HashSet<>();
        fields.add("projReviewStatus");
        fields.add("projReviewProcessStatus");
        return fields;
    }

    @Override
    protected ProjReviewBaseInfoLib entity2Lib(ProjReviewBaseInfo f) {
        return BeanUtil.copyProperties(f, ProjReviewBaseInfoLib.class);
    }

    @Override
    protected ProjReviewBaseInfo lib2Entity(ProjReviewBaseInfoLib t) {
        return BeanUtil.copyProperties(t, ProjReviewBaseInfo.class);
    }

    @Override
    protected ProjReviewBaseInfoDetailRSP lib2Rsp(ProjReviewBaseInfoLib f) {
        String[] ignoreProperties = new String[]{"creditorInfo", "debtorInfo", "guaranteeInfo", "lesseeInfo", "mortgagorInfo",
                "pledgorInfo", "leaseTypes", "factoringTypes", "zrTypes", "projCosponsorUserIds", "projCosponsorUserNames"};
        ProjReviewBaseInfoDetailRSP rsp = BeanUtil.copyProperties(f, ProjReviewBaseInfoDetailRSP.class, ignoreProperties);
        rsp.setCreditorInfo(Optional.ofNullable(JSON.parseObject(f.getCreditorInfo(),
                new TypeReference<List<ClientInfo>>() {
                }))
                .orElse(Collections.emptyList()));
//                .stream()
//                .sorted()
//                .collect(Collectors.toList()));
        rsp.setDebtorInfo(Optional.ofNullable(JSON.parseObject(f.getDebtorInfo(),
                new TypeReference<List<ClientInfo>>() {
                }))
                .orElse(Collections.emptyList()));
//                .stream()
//                .sorted()
//                .collect(Collectors.toList()));
        rsp.setGuaranteeInfo(Optional.ofNullable(JSON.parseObject(f.getGuaranteeInfo(),
                new TypeReference<List<ClientInfo>>() {
                }))
                .orElse(Collections.emptyList()));
//                .stream()
//                .sorted()
//                .collect(Collectors.toList()));
        rsp.setLesseeInfo(Optional.ofNullable(JSON.parseObject(f.getLesseeInfo(),
                new TypeReference<List<ClientInfo>>() {
                }))
                .orElse(Collections.emptyList()));
//                .stream()
//                .sorted()
//                .collect(Collectors.toList()));
        rsp.setMortgagorInfo(Optional.ofNullable(JSON.parseObject(f.getMortgagorInfo(),
                new TypeReference<List<ClientInfo>>() {
                }))
                .orElse(Collections.emptyList()));
//                .stream()
//                .sorted()
//                .collect(Collectors.toList()));
        rsp.setPledgorInfo(Optional.ofNullable(JSON.parseObject(f.getPledgorInfo(),
                new TypeReference<List<ClientInfo>>() {
                }))
                .orElse(Collections.emptyList()));
//                .stream()
//                .sorted()
//                .collect(Collectors.toList()));
        rsp.setLeaseTypes(Optional.ofNullable(JSON.parseObject(f.getLeaseTypes(),
                new TypeReference<List<String>>() {
                }))
                .orElse(Collections.emptyList()));
//                .stream()
//                .sorted()
//                .collect(Collectors.toList()));
        rsp.setFactoringTypes(Optional.ofNullable(JSON.parseObject(f.getFactoringTypes(),
                new TypeReference<List<String>>() {
                }))
                .orElse(Collections.emptyList()));
//                .stream()
//                .sorted()
//                .collect(Collectors.toList()));
        rsp.setZrTypes(Optional.ofNullable(JSON.parseObject(f.getZrTypes(),
                new TypeReference<List<String>>() {
                }))
                .orElse(Collections.emptyList()));
//                .stream()
//                .sorted()
//                .collect(Collectors.toList()));
        rsp.setProjCosponsorUserIds(Optional.ofNullable(JSON.parseObject(f.getProjCosponsorUserIds(),
                new TypeReference<List<Long>>() {
                }))
                .orElse(Collections.emptyList()));
//                .stream()
//                .sorted()
//                .collect(Collectors.toList()));
        // fill names
        Set<Long> sysUserIds = new HashSet<>();
        Set<Long> clientIds = new HashSet<>();
        Set<Long> deptIds = new HashSet<>();
        sysUserIds.add(rsp.getProjSponsorUserId());
        if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
            sysUserIds.addAll(rsp.getProjCosponsorUserIds());
        }
        sysUserIds.add(rsp.getBizDeptLeaderId());
        sysUserIds.add(rsp.getBizDivisionLeaderId());
        sysUserIds.add(rsp.getRiskControlManagerId());
        sysUserIds.add(rsp.getLegalManagerUserId());
        deptIds.add(rsp.getBizDeptId());
        if (CollUtil.isNotEmpty(rsp.getLesseeInfo())) {
            clientIds.addAll(rsp.getLesseeInfo().stream().map(ClientInfo::getClientId).collect(Collectors.toList()));
        }
        if (CollUtil.isNotEmpty(rsp.getDebtorInfo())) {
            clientIds.addAll(rsp.getDebtorInfo().stream().map(ClientInfo::getClientId).collect(Collectors.toList()));
        }
        if (CollUtil.isNotEmpty(rsp.getGuaranteeInfo())) {
            clientIds.addAll(rsp.getGuaranteeInfo().stream().map(ClientInfo::getClientId).collect(Collectors.toList()));
        }
        if (CollUtil.isNotEmpty(rsp.getMortgagorInfo())) {
            clientIds.addAll(rsp.getMortgagorInfo().stream().map(ClientInfo::getClientId).collect(Collectors.toList()));
        }
        if (CollUtil.isNotEmpty(rsp.getPledgorInfo())) {
            clientIds.addAll(rsp.getPledgorInfo().stream().map(ClientInfo::getClientId).collect(Collectors.toList()));
        }
        if (CollUtil.isNotEmpty(rsp.getCreditorInfo())) {
            clientIds.addAll(rsp.getCreditorInfo().stream().map(ClientInfo::getClientId).collect(Collectors.toList()));
        }
        clientIds.add(rsp.getClientId());
        Map<Long, String> clientMap = id2NameService.clientId2Name(clientIds);
        Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(sysUserIds);
        Map<Long, String> deptMap = id2NameService.deptId2Name(deptIds);
        rsp.setClientName(clientMap.get(rsp.getClientId()));
        rsp.setAssignor(f.getAssignor());
        rsp.setBizDeptName(deptMap.get(rsp.getBizDeptId()));
        rsp.setProjSponsorUserName(sysUserMap.get(rsp.getProjSponsorUserId()));
        if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
            rsp.setProjCosponsorUserNames(rsp.getProjCosponsorUserIds().stream().map(sysUserMap::get).collect(Collectors.toList()));
        }
        rsp.setBizDeptLeaderName(sysUserMap.get(rsp.getBizDeptLeaderId()));
        rsp.setBizDivisionLeaderName(sysUserMap.get(rsp.getBizDivisionLeaderId()));
        rsp.setRiskControlManagerName(sysUserMap.get(rsp.getRiskControlManagerId()));
        if (CollUtil.isNotEmpty(rsp.getLesseeInfo())) {
            rsp.getLesseeInfo().stream().filter(e -> isNotNull(e.getClientId())).forEach(e -> e.setClientName(clientMap.get(e.getClientId())));
        }
        if (CollUtil.isNotEmpty(rsp.getDebtorInfo())) {
            rsp.getDebtorInfo().stream().filter(e -> isNotNull(e.getClientId())).forEach(e -> e.setClientName(clientMap.get(e.getClientId())));
        }
        if (CollUtil.isNotEmpty(rsp.getGuaranteeInfo())) {
            rsp.getGuaranteeInfo().stream().filter(e -> isNotNull(e.getClientId())).forEach(e -> e.setClientName(clientMap.get(e.getClientId())));
        }
        if (CollUtil.isNotEmpty(rsp.getMortgagorInfo())) {
            rsp.getMortgagorInfo().stream().filter(e -> isNotNull(e.getClientId())).forEach(e -> e.setClientName(clientMap.get(e.getClientId())));
        }
        if (CollUtil.isNotEmpty(rsp.getPledgorInfo())) {
            rsp.getPledgorInfo().stream().filter(e -> isNotNull(e.getClientId())).forEach(e -> e.setClientName(clientMap.get(e.getClientId())));
        }
        if (CollUtil.isNotEmpty(rsp.getCreditorInfo())) {
            rsp.getCreditorInfo().stream().filter(e -> isNotNull(e.getClientId())).forEach(e -> e.setClientName(clientMap.get(e.getClientId())));
        }
        rsp.setLegalManagerName(sysUserMap.get(rsp.getLegalManagerUserId()));
        ProjectType of = ProjectType.of(f.getProjectType());
        rsp.setProjectType( of == null ? null :of.name());
        rsp.setId(f.getOriginId());
        Map<String, String> nameMap = projectProcessDictionaryPort.addressCode2Display(ListUtil.toList(rsp.getProvince(), rsp.getCity(), rsp.getDistrict()));
        if(ObjectUtil.isNotEmpty(nameMap)){
            StringBuilder st = new StringBuilder();
            if(ObjectUtil.isNotNull(nameMap.get(rsp.getProvince()))){
                st.append(nameMap.get(rsp.getProvince()));
            }
            if(ObjectUtil.isNotNull(nameMap.get(rsp.getCity()))){
                st.append(nameMap.get(rsp.getCity()));
            }
            if(ObjectUtil.isNotNull(nameMap.get(rsp.getDistrict()))){
                st.append(nameMap.get(rsp.getDistrict()));
            }
            rsp.setAreaName(st.toString());
        }
        if(ObjectUtil.isNotEmpty(rsp.getEvaluationSubjectId())){
            rsp.setEvaluationSubjectName(id2NameService.clientId2NameSingle(rsp.getEvaluationSubjectId()));
        }
        return rsp;
    }


    @Override
    public ProjReviewInfoModule getSubModule() {
        return ProjReviewInfoModule.BASE_INFO;
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
