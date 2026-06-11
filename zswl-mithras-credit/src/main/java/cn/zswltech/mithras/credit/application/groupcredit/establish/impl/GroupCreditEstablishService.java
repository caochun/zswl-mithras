package cn.zswltech.mithras.credit.application.groupcredit.establish.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.workflow.flow.constant.FlowConstants;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.VersionTypeEnum;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.credit.application.groupcredit.establish.GroupCreditEstablishContractPort;
import cn.zswltech.mithras.credit.groupcredit.establish.enums.GroupCreditEstablishProcessStatus;
import cn.zswltech.mithras.credit.groupcredit.establish.mapper.GroupCreditEstablishBaseInfoMapper;
import cn.zswltech.mithras.credit.groupcredit.establish.mapper.GroupCreditEstablishBaseInfoLibMapper;
import cn.zswltech.mithras.credit.groupcredit.establish.model.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.credit.groupcredit.establish.model.GroupCreditEstablishBaseInfoLib;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.workflow.process.BizProcessDataService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.credit.groupcredit.establish.versioning.GroupCreditEstablishVersionServiceImpl;
import cn.zswltech.mithras.validation.ControllerMissParamException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static cn.hutool.core.util.ObjectUtil.isNull;

/**
 * @description 集团授信立项基本信息表
 * @author wangchuanhao
 * @date 2022-11-11
 */
@Service
public class GroupCreditEstablishService {
    @Resource
    private GroupCreditEstablishBaseInfoMapper groupCreditEstablishBaseInfoMapper;
    @Resource
    private GroupCreditEstablishBaseInfoLibMapper groupCreditEstablishBaseInfoLibMapper;
    @Resource
    private GroupCreditEstablishVersionServiceImpl groupCreditEstablishVersionService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private FlowTaskApiService taskApiService;

    @Resource
    private GroupCreditEstablishContractPort groupCreditEstablishContractPort;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private SysUserService sysUserService;

    @Transactional(rollbackFor = Throwable.class)
    public void effect(@NotNull Long groupCreditEstablishId) {
        GroupCreditEstablishBaseInfo baseInfo = groupCreditEstablishBaseInfoMapper.selectById(groupCreditEstablishId);
        StartProcessReq startProcessReq = new StartProcessReq();
        // 判断使用创建流程还是修改流程
        if (RecordStatus.NEW.name().equals(baseInfo.getGroupCreditEstablishStatus())) {
            startProcessReq.setModelKey(ProcessModelTypeEnum.GroupCreditEstablishCreateFlow.name());
        } else {
            startProcessReq.setModelKey(ProcessModelTypeEnum.GroupCreditEstablishModifyFlow.name());
        }

        List<String> riskControlManagerIds = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(baseInfo.getRiskControlManagerId())) {
            riskControlManagerIds.addAll(JSON.parseObject(baseInfo.getRiskControlManagerId(), new TypeReference<List<String>>() {
            }));
        }
        startProcessReq.setVariables(MapUtil.of(
                Pair.of("projEstablishApprovalType", baseInfo.getApprovalType()),
                Pair.of("bizDeptLeader", Objects.nonNull(baseInfo.getBizDeptLeaderId()) ? ListUtil.toList(String.valueOf(baseInfo.getBizDeptLeaderId())) : new ArrayList<>()),
                Pair.of("bizDivisionLeader", Objects.nonNull(baseInfo.getBizDivisionLeaderId()) ? ListUtil.toList(String.valueOf(baseInfo.getBizDivisionLeaderId())) : new ArrayList<>()),
                Pair.of("riskControlManager", riskControlManagerIds)
        ));
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setBusinessKey(String.valueOf(groupCreditEstablishId));
        startProcessReq.setSubModule("DEFAULT");
        startProcessReq.setProcessInstanceName(baseInfo.getProjName());
        startProcessReq.setCcUserIdList(StringUtils.isBlank(baseInfo.getProjCosponsorUserIds()) ? new ArrayList<>() : JSONUtil.parseArray(baseInfo.getProjCosponsorUserIds()).toList(String.class));
        startProcessReq.setStartUserDeptId(Optional.ofNullable(baseInfo.getBizDeptId()).map(String::valueOf).orElse(null));
        String processInstanceId = processApiService.start(startProcessReq);
        bizProcessDataService.recordBizData(processInstanceId, baseInfo.getClientId());

        recordEstablishStatus(groupCreditEstablishId, null, RecordStatus.NEW.name().equals(baseInfo.getGroupCreditEstablishStatus())
                ? GroupCreditEstablishProcessStatus.NEW_UNDER_APPROVAL : GroupCreditEstablishProcessStatus.CHANGING_UNDER_APPROVAL);
    }

    public ProcessResp findRelatedProcess(Long groupCreditEstablishId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setBusinessKey(String.valueOf(groupCreditEstablishId));
        processPageReq.setModelKeyList(Arrays.asList(
                ProcessModelTypeEnum.GroupCreditEstablishCreateFlow.name(),
                ProcessModelTypeEnum.GroupCreditEstablishModifyFlow.name()
        ));
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        return processRespPage.getContents().stream().findFirst().orElse(null);
    }

    @Transactional(rollbackFor = Exception.class)
    public void processEnd(String modelKey, Long groupCreditEstablishId, Integer endType, Long startUserId, String processInstanceId) {
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        GroupCreditEstablishBaseInfo baseInfo = groupCreditEstablishBaseInfoMapper.selectById(groupCreditEstablishId);
        // 填充风险敞口
        baseInfo.setClientRiskExposure(groupCreditEstablishContractPort.getGroupCreditStockRiskExposure(baseInfo.getClientId()));
        // 更新风险敞口
        groupCreditEstablishBaseInfoMapper.updateById(baseInfo);
        // 记录版本前要先更新状态
        GroupCreditEstablishProcessStatus establishProcessStatus = null;
        RecordStatus establishStatus = null;
        if (processPass) {
            // 审批通过
            establishProcessStatus = RecordStatus.TAKE_EFFECT.name().equals(baseInfo.getGroupCreditEstablishStatus())
                    ? GroupCreditEstablishProcessStatus.CHANGING_APPROVAL_PASS : GroupCreditEstablishProcessStatus.NEW_APPROVAL_PASS;
            establishStatus = RecordStatus.TAKE_EFFECT;
        } else {
            if (RecordStatus.TAKE_EFFECT.name().equals(baseInfo.getGroupCreditEstablishStatus())) {
                // 变更审批
                establishProcessStatus = GroupCreditEstablishProcessStatus.CANCEL_CHANGE;
            } else {
                // 新建审批
                establishProcessStatus = GroupCreditEstablishProcessStatus.CANCEL_NEW;
            }
        }
        recordEstablishStatus(groupCreditEstablishId, establishStatus, establishProcessStatus);
        // 记录版本
        int versionType = processPass ? VersionTypeConstants.NORMAL : VersionTypeConstants.INVALID;
        groupCreditEstablishVersionService.recordVersion(groupCreditEstablishId, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, versionType);

        if (!processPass && RecordStatus.TAKE_EFFECT.name().equals(baseInfo.getGroupCreditEstablishStatus())) {
            // 变更审批 且 未审批通过 回滚
            groupCreditEstablishVersionService.reset(groupCreditEstablishId);
            recordEstablishStatus(groupCreditEstablishId, null, GroupCreditEstablishProcessStatus.CANCEL_CHANGE);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void recordEstablishStatus(Long establishId, RecordStatus establishStatus,
                                      GroupCreditEstablishProcessStatus establishProcessStatus) {
        if (establishId == null || (establishStatus == null && establishProcessStatus == null)) {
            return;
        }
        LambdaUpdateWrapper<GroupCreditEstablishBaseInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(GroupCreditEstablishBaseInfo::getId, establishId);
        if (establishStatus != null) {
            updateWrapper.set(GroupCreditEstablishBaseInfo::getGroupCreditEstablishStatus, establishStatus.name());
        }
        if (establishProcessStatus != null) {
            updateWrapper.set(GroupCreditEstablishBaseInfo::getGroupCreditEstablishProcessStatus, establishProcessStatus.name());
        }
        updateWrapper.set(GroupCreditEstablishBaseInfo::getUpdateTime, LocalDateTime.now());
        groupCreditEstablishBaseInfoMapper.update(null, updateWrapper);
    }

    public boolean canSave(Long groupCreditEstablishId) {
        AccountVO loginUser = AccountUtil.getLoginInfo();
        if (groupCreditEstablishId == null) {
            return false;
        }
        if (Objects.isNull(loginUser)) {
            return false;
        }
        ProcessResp processResp = findRelatedProcess(groupCreditEstablishId);
        if (processResp == null) {
            // 运行中流程为空 可以保存
            return true;
        }
        if (!FlowConstants.START_USER_TASK.equals(processResp.getCurTaskActivityIds())) {
            // 有运行中流程 不在发起人节点 不能保存
            return false;
        }
        if (!Objects.equals(String.valueOf(loginUser.getId()), processResp.getStartUserId())) {
            // 在发起人节点 不是发起人 不能保存
            return false;
        }
        return true;
    }

    public boolean clientRelatedGroupCreditEstablish(Long clientId) {
        int tmpRelatedCount = groupCreditEstablishBaseInfoMapper.selectCount(Wrappers.<GroupCreditEstablishBaseInfo>lambdaQuery().eq(GroupCreditEstablishBaseInfo::getClientId, clientId));
        if (tmpRelatedCount > 0) {
            return true;
        }
        return groupCreditEstablishBaseInfoLibMapper.selectCount(Wrappers.<GroupCreditEstablishBaseInfoLib>lambdaQuery().eq(GroupCreditEstablishBaseInfoLib::getClientId, clientId)) > 0;
    }

    public void effectCheck(Long groupCreditEstablishId) {
        GroupCreditEstablishBaseInfo baseInfo = groupCreditEstablishBaseInfoMapper.selectById(groupCreditEstablishId);
        effectCheck(baseInfo);
    }

    public void effectCheck(GroupCreditEstablishBaseInfo baseInfo) {
        // 此条判断的作用是判断下有没有编辑过 如果只新增没编辑，有些必填字段在接口层得不到校验
        if (isNull(baseInfo.getApplyCreditAmount())) {
            throw new ControllerMissParamException("授信额度");
        }
    }

}
