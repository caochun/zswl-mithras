package cn.zswltech.mithras.service.service.process.prepare.handle;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionDeptWeightInfo;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.service.enums.kpi.KpiProjectWeightTypeEnum;
import cn.zswltech.mithras.service.mapper.model.client.ClientTransfer;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.kpi.*;
import cn.zswltech.mithras.service.mapper.model.process.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.client.ClientTransferService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.kpi.*;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.collection.ListUtil.toList;
import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;
import static java.lang.String.valueOf;

/**
 * @ClassName RentPaymentNotifyFlowHandle
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/4/7 5:18 下午
 * @Version 1.0
 **/
@Slf4j
@Component
public class KpiProjectDistributionModifyTransferHandle extends AbstractFlowCommitHandle {

    @Resource
    private KpiProjectDistributionBaseInfoService kpiProjectDistributionBaseInfoService;
    @Resource
    private KpiProjectDistributionWeightService kpiProjectDistributionWeightService;
    @Resource
    private KpiProjectDistributionService kpiProjectDistributionService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private FlowProcessApiService flowProcessApiService;
    @Resource
    private ClientTransferService clientTransferService;
    @Resource
    private KpiProjectDistributionDeptWeightLibService kpiProjectDistributionDeptWeightLibService;
    @Resource
    private KpiProjectDistributionDeptWeightService kpiProjectDistributionDeptWeightService;

    @Override
    public boolean needHandle(String processType) {
        return StrUtil.equals(processType, ProcessModelTypeEnum.KpiProjectDistributionTransferFlow.name());
    }

    @Override
    public String commit(CommonProcessPrepare prepare) {
        String processInstanceId = null;
        Long projectDistributionId = Long.valueOf(prepare.getBusinessId());
        Long clientTransferId = Long.valueOf(prepare.getBusinessData());
        KpiProjectDistribution projectDistribution = kpiProjectDistributionService.getById(projectDistributionId);
        if (Objects.isNull(projectDistribution)) {
            throw new MithrasException("项目分配信息不存在");
        }
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(projectDistribution.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException("合同信息不存在");
        }
        ClientTransfer clientTransfer = clientTransferService.getById(clientTransferId);
        if (Objects.isNull(clientTransfer)) {
            throw new MithrasException("客户转移不存在");
        }


        // 找到最新的版本数据
        KpiProjectDistributionDeptWeightLib serviceOne = kpiProjectDistributionDeptWeightLibService.getOne(Wrappers.<KpiProjectDistributionDeptWeightLib>lambdaQuery()
                .eq(KpiProjectDistributionDeptWeight::getProjectDistributionId, projectDistributionId)
                .eq(KpiProjectDistributionDeptWeightLib::getVersionType, VersionTypeConstants.NORMAL)
                .orderByDesc(KpiProjectDistributionDeptWeightLib::getVersion)
                .last(StringUtil.mysqlLimitOne()));
        if (Objects.isNull(serviceOne)) {
            throw new MithrasException("不存在部门分润版本信息");
        }
        List<KpiProjectDistributionDeptWeightInfo> deptWeightInfoLibs = kpiProjectDistributionDeptWeightLibService.buildDeptWeightInfoByDistributionId(projectDistributionId, serviceOne.getVersion());
        List<KpiProjectDistributionDeptWeightInfo> deptWeightInfos = kpiProjectDistributionDeptWeightService.queryList(projectDistributionId);
        if (CollectionUtil.isEmpty(deptWeightInfos) || CollectionUtil.isEmpty(deptWeightInfoLibs)) {
            throw new MithrasException("请维护部门分配信息");
        }
        // 基本信息
        KpiProjectDistributionBaseInfo baseInfo = kpiProjectDistributionBaseInfoService.getOneByProjectDistributionId(projectDistributionId);
        // 项目分配比重
        List<KpiProjectDistributionWeight> weightList = kpiProjectDistributionWeightService.listByProjectDistributionId(projectDistributionId);
        // 前置校验
        this.checkBeforeSubmit(baseInfo, weightList);
        // 保存审批相关人员
        Set<String> sponsorUserIds = new HashSet<>();
        Set<String> cosponsorUserIds = new HashSet<>();
        Set<String> otherDeptRecommend = new HashSet<>();
        Set<String> tempMemberUserIds = new HashSet<>();
        List<String> memberUserIds = new LinkedList<>();
        List<String> curLeaderIds = new LinkedList<>();
        List<String> prevLeaderIds = new LinkedList<>();
        List<String> curBusinessHeaderIds = new LinkedList<>();
        List<String> prevBusinessHeaderIds = new LinkedList<>();
        List<String> curTeamLeaderIds = new LinkedList<>();
        List<String> prevTeamLeaderIds = new LinkedList<>();
        // 老团队长
        if (Objects.nonNull(baseInfo.getTeamLeaderId())) {
            prevTeamLeaderIds.add(String.valueOf(baseInfo.getTeamLeaderId()));
        }
        // 新团队长
        if (Objects.nonNull(clientTransfer.getToDeptId())) {
            List<UserDO> teamLeaderList = sysUserService.listSpecificOrgJobUser(clientTransfer.getToDeptId(), JobEnum.teamleader.name());
            if (CollectionUtil.isNotEmpty(teamLeaderList)) {
                for (UserDO userDO : teamLeaderList) {
                    curTeamLeaderIds.add(String.valueOf(userDO.getId()));
                }
            }
        }

        List<Long> newDeptIds = deptWeightInfos.stream().map(KpiProjectDistributionDeptWeightInfo::getWeightTarget).filter(Objects::nonNull)
                .distinct().collect(Collectors.toList());
        // 尝试拿到合同主办的业务部门
        newDeptIds.add(clientTransfer.getToDeptId());
        newDeptIds = newDeptIds.stream().distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(newDeptIds)) {
            throw new MithrasException("请维护新的部门分配信息");
        }
        List<Long> oldDeptIds = deptWeightInfoLibs.stream().map(KpiProjectDistributionDeptWeightInfo::getWeightTarget)
                .filter(Objects::nonNull).distinct().collect(Collectors.toList());
        oldDeptIds.add(clientTransfer.getBelongDeptId());
        oldDeptIds = oldDeptIds.stream().distinct().collect(Collectors.toList());
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
        if (!sponsorUserIds.isEmpty()) {
            tempMemberUserIds.addAll(sponsorUserIds);
        }
        if (!cosponsorUserIds.isEmpty()) {
            tempMemberUserIds.addAll(cosponsorUserIds);
        }
        if (!otherDeptRecommend.isEmpty()) {
            tempMemberUserIds.addAll(otherDeptRecommend);
        }
        memberUserIds = new ArrayList<>(tempMemberUserIds);
        for (Long deptId : oldDeptIds) {
            // 利润归属老部门负责人
            List<UserDO> businessHeaderList = sysUserService.listSpecificOrgJobUser(deptId, JobEnum.businesshead.name());
            if (CollectionUtil.isNotEmpty(businessHeaderList)) {
                for (UserDO userDO : businessHeaderList) {
                    prevBusinessHeaderIds.add(String.valueOf(userDO.getId()));
                }
            }
            // 分管领导老部门
            List<UserDO> leaderList = sysUserService.listSpecificOrgJobUser(deptId, JobEnum.leaderincharge.name());
            if (CollectionUtil.isNotEmpty(leaderList)) {
                for (UserDO userDO : leaderList) {
                    prevLeaderIds.add(String.valueOf(userDO.getId()));
                }
            }
        }

        for (Long deptId : newDeptIds) {
            // 利润归属新部门负责人
            List<UserDO> businessHeaderIds = sysUserService.listSpecificOrgJobUser(deptId, JobEnum.businesshead.name());
            if (CollectionUtil.isNotEmpty(businessHeaderIds)) {
                for (UserDO userDO : businessHeaderIds) {
                    curBusinessHeaderIds.add(String.valueOf(userDO.getId()));
                }
            }

            // 分管领导新部门
            List<UserDO> leaderIds = sysUserService.listSpecificOrgJobUser(deptId, JobEnum.leaderincharge.name());
            if (CollectionUtil.isNotEmpty(leaderIds)) {
                for (UserDO userDO : leaderIds) {
                    curLeaderIds.add(String.valueOf(userDO.getId()));
                }
            }
        }

        // 对新老部门负责人和分管领导去重
        curLeaderIds = curLeaderIds.stream().distinct().collect(Collectors.toList());
        curBusinessHeaderIds = curBusinessHeaderIds.stream().distinct().collect(Collectors.toList());
        prevLeaderIds = prevLeaderIds.stream().distinct().collect(Collectors.toList());
        prevBusinessHeaderIds = prevBusinessHeaderIds.stream().distinct().collect(Collectors.toList());

        // 审批流参数
        Map<String, Object> varMap = new HashMap<>();
        varMap.put("teamLeaderNew", curTeamLeaderIds);
        varMap.put("teamLeaderPrev", prevTeamLeaderIds);
        varMap.put("teamMember", memberUserIds);
        varMap.put("bizDeptLeaderNew", curBusinessHeaderIds);
        varMap.put("bizDeptLeaderPrev", prevBusinessHeaderIds);
        varMap.put("leaderinchargeNew", curLeaderIds);
        varMap.put("leaderinchargePrev", prevLeaderIds);

        // 变更审批状态
        projectDistribution.setApprovalStatus(ProcessStatus.UNDER_APPROVAL.name());
        kpiProjectDistributionService.updateById(projectDistribution);
        // 生成审批流
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        if (Objects.isNull(currentUserId)) {
            throw new MithrasException(ResultMsg.USER_NOT_LOGIN);
        }
        String currentUserName = sysUserService.getUserName(currentUserId);
        StartProcessReq startProcessReq = new StartProcessReq();
        ProcessModelTypeEnum processModelTypeEnum = Objects.equals(projectDistribution.getDistributionStatus(), YesOrNoNumberEnum.NO.getCode()) ? ProcessModelTypeEnum.KpiProjectDistributionCreateFlow : ProcessModelTypeEnum.KpiProjectDistributionTransferFlow;
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
        processInstanceId = flowProcessApiService.start(startProcessReq);
        return processInstanceId;
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


}
