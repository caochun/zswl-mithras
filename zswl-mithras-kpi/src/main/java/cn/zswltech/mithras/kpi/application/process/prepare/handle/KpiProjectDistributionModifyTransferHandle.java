package cn.zswltech.mithras.kpi.application.process.prepare.handle;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.customer.mapper.client.ClientTransferMapper;
import cn.zswltech.mithras.customer.model.client.ClientTransfer;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionDeptWeightInfo;
import cn.zswltech.mithras.kpi.application.distribution.KpiProjectDistributionDeptWeightLibService;
import cn.zswltech.mithras.kpi.enums.KpiProjectWeightTypeEnum;
import cn.zswltech.mithras.kpi.mapper.KpiProjectDistributionBaseInfoMapper;
import cn.zswltech.mithras.kpi.mapper.KpiProjectDistributionDeptWeightMapper;
import cn.zswltech.mithras.kpi.mapper.KpiProjectDistributionMapper;
import cn.zswltech.mithras.kpi.mapper.KpiProjectDistributionWeightMapper;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistribution;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionBaseInfo;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionDeptWeight;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionDeptWeightLib;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionWeight;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.enums.common.ProcessStatus;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.StringUtil;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.workflow.process.prepare.handle.AbstractFlowCommitHandle;
import cn.zswltech.mithras.workflow.model.CommonProcessPrepare;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class KpiProjectDistributionModifyTransferHandle extends AbstractFlowCommitHandle {

    @Resource
    private KpiProjectDistributionMapper kpiProjectDistributionMapper;
    @Resource
    private KpiProjectDistributionBaseInfoMapper kpiProjectDistributionBaseInfoMapper;
    @Resource
    private KpiProjectDistributionWeightMapper kpiProjectDistributionWeightMapper;
    @Resource
    private KpiProjectDistributionDeptWeightMapper kpiProjectDistributionDeptWeightMapper;
    @Resource
    private KpiProjectDistributionDeptWeightLibService kpiProjectDistributionDeptWeightLibService;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ClientTransferMapper clientTransferMapper;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private FlowProcessApiService flowProcessApiService;

    @Override
    public boolean needHandle(String processType) {
        return StrUtil.equals(processType, ProcessModelTypeEnum.KpiProjectDistributionTransferFlow.name());
    }

    @Override
    public String commit(CommonProcessPrepare prepare) {
        Long projectDistributionId = Long.valueOf(prepare.getBusinessId());
        Long clientTransferId = Long.valueOf(prepare.getBusinessData());
        KpiProjectDistribution projectDistribution = kpiProjectDistributionMapper.selectById(projectDistributionId);
        if (Objects.isNull(projectDistribution)) {
            throw new MithrasException("项目分配信息不存在");
        }
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(projectDistribution.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException("合同信息不存在");
        }
        ClientTransfer clientTransfer = clientTransferMapper.selectById(clientTransferId);
        if (Objects.isNull(clientTransfer)) {
            throw new MithrasException("客户转移不存在");
        }

        KpiProjectDistributionDeptWeightLib latestDeptWeightLib = kpiProjectDistributionDeptWeightLibService.getOne(Wrappers.<KpiProjectDistributionDeptWeightLib>lambdaQuery()
                .eq(KpiProjectDistributionDeptWeightLib::getProjectDistributionId, projectDistributionId)
                .eq(KpiProjectDistributionDeptWeightLib::getVersionType, VersionTypeConstants.NORMAL)
                .orderByDesc(KpiProjectDistributionDeptWeightLib::getVersion)
                .last(StringUtil.mysqlLimitOne()));
        if (Objects.isNull(latestDeptWeightLib)) {
            throw new MithrasException("不存在部门分润版本信息");
        }
        List<KpiProjectDistributionDeptWeightInfo> deptWeightInfoLibs = kpiProjectDistributionDeptWeightLibService.buildDeptWeightInfoByDistributionId(projectDistributionId, latestDeptWeightLib.getVersion());
        List<KpiProjectDistributionDeptWeightInfo> deptWeightInfos = queryDeptWeightList(projectDistributionId);
        if (CollectionUtil.isEmpty(deptWeightInfos) || CollectionUtil.isEmpty(deptWeightInfoLibs)) {
            throw new MithrasException("请维护部门分配信息");
        }

        KpiProjectDistributionBaseInfo baseInfo = kpiProjectDistributionBaseInfoMapper.selectOne(Wrappers.<KpiProjectDistributionBaseInfo>lambdaQuery()
                .eq(KpiProjectDistributionBaseInfo::getProjectDistributionId, projectDistributionId)
                .last(StringUtil.mysqlLimitOne()));
        List<KpiProjectDistributionWeight> weightList = kpiProjectDistributionWeightMapper.selectList(Wrappers.<KpiProjectDistributionWeight>lambdaQuery()
                .eq(KpiProjectDistributionWeight::getProjectDistributionId, projectDistributionId));
        checkBeforeSubmit(baseInfo, weightList);

        Set<String> sponsorUserIds = new HashSet<>();
        Set<String> cosponsorUserIds = new HashSet<>();
        Set<String> otherDeptRecommend = new HashSet<>();
        Set<String> tempMemberUserIds = new HashSet<>();
        List<String> curLeaderIds = new LinkedList<>();
        List<String> prevLeaderIds = new LinkedList<>();
        List<String> curBusinessHeaderIds = new LinkedList<>();
        List<String> prevBusinessHeaderIds = new LinkedList<>();
        List<String> curTeamLeaderIds = new LinkedList<>();
        List<String> prevTeamLeaderIds = new LinkedList<>();

        if (Objects.nonNull(baseInfo.getTeamLeaderId())) {
            prevTeamLeaderIds.add(String.valueOf(baseInfo.getTeamLeaderId()));
        }
        if (Objects.nonNull(clientTransfer.getToDeptId())) {
            curTeamLeaderIds.addAll(listSpecificOrgJobUserIds(clientTransfer.getToDeptId(), JobEnum.teamleader.name()));
        }

        List<Long> newDeptIds = deptWeightInfos.stream().map(KpiProjectDistributionDeptWeightInfo::getWeightTarget)
                .filter(Objects::nonNull).distinct().collect(Collectors.toList());
        newDeptIds.add(clientTransfer.getToDeptId());
        newDeptIds = newDeptIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(newDeptIds)) {
            throw new MithrasException("请维护新的部门分配信息");
        }
        List<Long> oldDeptIds = deptWeightInfoLibs.stream().map(KpiProjectDistributionDeptWeightInfo::getWeightTarget)
                .filter(Objects::nonNull).distinct().collect(Collectors.toList());
        oldDeptIds.add(clientTransfer.getBelongDeptId());
        oldDeptIds = oldDeptIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(oldDeptIds)) {
            throw new MithrasException("请维护老的部门分配信息");
        }

        for (KpiProjectDistributionWeight weight : weightList) {
            if (StrUtil.isBlank(weight.getWeightTarget())) {
                continue;
            }
            if (Objects.equals(KpiProjectWeightTypeEnum.PROJECT_SPONSOR.name(), weight.getWeightType())) {
                sponsorUserIds.add(weight.getWeightTarget());
            } else if (Objects.equals(KpiProjectWeightTypeEnum.PROJECT_COSPONSOR.name(), weight.getWeightType())) {
                cosponsorUserIds.add(weight.getWeightTarget());
            } else if (Objects.equals(KpiProjectWeightTypeEnum.OTHER_DEPT_RECOMMEND.name(), weight.getWeightType())) {
                otherDeptRecommend.add(weight.getWeightTarget());
            }
        }
        tempMemberUserIds.addAll(sponsorUserIds);
        tempMemberUserIds.addAll(cosponsorUserIds);
        tempMemberUserIds.addAll(otherDeptRecommend);
        List<String> memberUserIds = new ArrayList<>(tempMemberUserIds);

        for (Long deptId : oldDeptIds) {
            prevBusinessHeaderIds.addAll(listSpecificOrgJobUserIds(deptId, JobEnum.businesshead.name()));
            prevLeaderIds.addAll(listSpecificOrgJobUserIds(deptId, JobEnum.leaderincharge.name()));
        }
        for (Long deptId : newDeptIds) {
            curBusinessHeaderIds.addAll(listSpecificOrgJobUserIds(deptId, JobEnum.businesshead.name()));
            curLeaderIds.addAll(listSpecificOrgJobUserIds(deptId, JobEnum.leaderincharge.name()));
        }

        curLeaderIds = curLeaderIds.stream().distinct().collect(Collectors.toList());
        curBusinessHeaderIds = curBusinessHeaderIds.stream().distinct().collect(Collectors.toList());
        prevLeaderIds = prevLeaderIds.stream().distinct().collect(Collectors.toList());
        prevBusinessHeaderIds = prevBusinessHeaderIds.stream().distinct().collect(Collectors.toList());

        Map<String, Object> varMap = new HashMap<>();
        varMap.put("teamLeaderNew", curTeamLeaderIds);
        varMap.put("teamLeaderPrev", prevTeamLeaderIds);
        varMap.put("teamMember", memberUserIds);
        varMap.put("bizDeptLeaderNew", curBusinessHeaderIds);
        varMap.put("bizDeptLeaderPrev", prevBusinessHeaderIds);
        varMap.put("leaderinchargeNew", curLeaderIds);
        varMap.put("leaderinchargePrev", prevLeaderIds);

        projectDistribution.setApprovalStatus(ProcessStatus.UNDER_APPROVAL.name());
        kpiProjectDistributionMapper.updateById(projectDistribution);

        Long currentUserId = AccountUtil.getLoginInfo().getId();
        if (Objects.isNull(currentUserId)) {
            throw new MithrasException(ResultMsg.USER_NOT_LOGIN);
        }
        String currentUserName = sysUserService.getUserName(currentUserId);
        ProcessModelTypeEnum processModelTypeEnum = Objects.equals(projectDistribution.getDistributionStatus(), YesOrNoNumberEnum.NO.getCode())
                ? ProcessModelTypeEnum.KpiProjectDistributionCreateFlow : ProcessModelTypeEnum.KpiProjectDistributionTransferFlow;
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(processModelTypeEnum.name());
        startProcessReq.setVariables(varMap);
        startProcessReq.setBusinessKey(String.valueOf(projectDistributionId));
        if (processModelTypeEnum == ProcessModelTypeEnum.KpiProjectDistributionCreateFlow) {
            startProcessReq.setProcessInstanceName(String.format("%s发起的关于【%s】合同的绩效考核项目分配创建新流程", currentUserName, contractBaseInfo.getContractCode()));
        } else {
            startProcessReq.setProcessInstanceName(String.format("%s发起的关于【%s】合同的绩效考核项目分配移交流程", currentUserName, contractBaseInfo.getContractCode()));
        }
        startProcessReq.setStartUserId(String.valueOf(currentUserId));
        startProcessReq.setStartUserDeptId(String.valueOf(contractBaseInfo.getBizDeptId()));
        return flowProcessApiService.start(startProcessReq);
    }

    public void checkBeforeSubmit(KpiProjectDistributionBaseInfo baseInfo, List<KpiProjectDistributionWeight> weightList) {
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException("基本信息不能为空");
        }
        if (Objects.isNull(baseInfo.getTeamLeaderId())) {
            throw new MithrasException("团队长不能为空");
        }
        if (StrUtil.isBlank(baseInfo.getRemark())) {
            throw new MithrasException("项目交接备注不能为空");
        }
        if (CollectionUtil.isEmpty(weightList)) {
            throw new MithrasException("分配比重不能为空");
        }
        int sum = weightList.stream().filter(e -> Objects.nonNull(e.getWeightValue())).mapToInt(KpiProjectDistributionWeight::getWeightValue).sum();
        if (sum != 1000000) {
            throw new MithrasException("分配比重加总必须等于100%");
        }
    }

    private List<KpiProjectDistributionDeptWeightInfo> queryDeptWeightList(Long projectDistributionId) {
        List<KpiProjectDistributionDeptWeight> list = kpiProjectDistributionDeptWeightMapper.selectList(Wrappers.<KpiProjectDistributionDeptWeight>lambdaQuery()
                .eq(KpiProjectDistributionDeptWeight::getProjectDistributionId, projectDistributionId));
        if (CollectionUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<KpiProjectDistributionDeptWeightInfo> collect = list.stream().map(item -> {
            KpiProjectDistributionDeptWeightInfo info = new KpiProjectDistributionDeptWeightInfo();
            info.setId(item.getId());
            info.setIsBusinessDept(false);
            info.setWeightValue(item.getWeightValue());
            info.setWeightTarget(item.getWeightTarget());
            return info;
        }).collect(Collectors.toList());
        collect.get(0).setIsBusinessDept(true);
        return collect;
    }

    private List<String> listSpecificOrgJobUserIds(Long orgId, String jobCode) {
        List<UserDO> users = sysUserService.listSpecificOrgJobUser(orgId, jobCode);
        if (CollectionUtil.isEmpty(users)) {
            return new ArrayList<>();
        }
        return users.stream().map(UserDO::getId).filter(Objects::nonNull).map(String::valueOf).collect(Collectors.toList());
    }
}
