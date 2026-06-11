package cn.zswltech.mithras.associationreport.application.process.prepare.handle;

import cn.zswltech.mithras.workflow.process.prepare.handle.AbstractFlowCommitHandle;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.associationreport.mapper.AssociationReportMapper;
import cn.zswltech.mithras.associationreport.service.AssociationReportApplyService;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.associationreport.enums.AssociationProcessStatusEnum;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationReport;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationReportApply;
import cn.zswltech.mithras.workflow.model.CommonProcessPrepare;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.SysUserService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2025/9/26
 * @description
 */
@Slf4j
@Component
public class AssociationReportCommitHandle extends AbstractFlowCommitHandle {
    @Resource
    private AssociationReportMapper associationReportMapper;
    @Resource
    private AssociationReportApplyService associationReportApplyService;
    @Resource
    private FlowProcessApiService flowProcessApiService;
    @Resource
    private SysUserService sysUserService;

    @Override
    public boolean needHandle(String processType) {
        return StrUtil.equalsAny(processType, ProcessModelTypeEnum.AssociationReportMainBusinessFlow.name(), ProcessModelTypeEnum.AssociationReportQuarterMonthFlow.name());
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public String commit(CommonProcessPrepare prepare) {
        AssociationReportApply associationReportApply = associationReportApplyService.getById(Long.parseLong(prepare.getBusinessId()));
        if (Objects.isNull(associationReportApply)) {
            throw new MithrasException("批次数据不存在");
        }
        List<String> reportInstancesIds = StrUtil.split(associationReportApply.getReportInstanceIds(), ",");
        if (CollectionUtil.isEmpty(reportInstancesIds)) {
            throw new MithrasException("不存在待提交的报表记录");
        }
        // 创建流程
        ProcessModelTypeEnum processModelTypeEnum = ProcessModelTypeEnum.getByName(prepare.getProcessType());
        if (Objects.isNull(processModelTypeEnum)) {
            throw new MithrasException("流程模型类型不存在");
        }
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(prepare.getProcessType());
        startProcessReq.setBusinessKey(prepare.getBusinessId());
        startProcessReq.setProcessInstanceName(processModelTypeEnum.getDisplay());
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        startProcessReq.setStartUserId(currentUserId.toString());
        List<OrgDO> orgList = sysUserService.getSpecificUserDeptList(currentUserId);
        if (CollectionUtil.isNotEmpty(orgList)) {
            startProcessReq.setStartUserDeptId(orgList.get(0).getId().toString());
        }
        String processInstanceId = flowProcessApiService.start(startProcessReq);
        // 变更数据状态及可见性
        LambdaUpdateWrapper<AssociationReport> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(AssociationReport::getProcessStatus, AssociationProcessStatusEnum.UNDER_APPROVAL.name());
        updateWrapper.set(AssociationReport::getIsShow, YesOrNoNumberEnum.YES.getCode());
        updateWrapper.in(AssociationReport::getReportInstanceId, reportInstancesIds);
        associationReportMapper.update(null, updateWrapper);
        return processInstanceId;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void afterDiscard(CommonProcessPrepare prepare) {
        // 关闭待办则删除待办关联的数据
        AssociationReportApply associationReportApply = associationReportApplyService.getById(Long.parseLong(prepare.getBusinessId()));
        if (Objects.isNull(associationReportApply)) {
            throw new MithrasException("批次数据不存在");
        }
        List<String> reportInstancesIds = StrUtil.split(associationReportApply.getReportInstanceIds(), ",");
        if (CollectionUtil.isEmpty(reportInstancesIds)) {
            return;
        }
        List<AssociationReport> associationReportList = associationReportMapper.selectList(
                Wrappers.<AssociationReport>lambdaQuery().in(AssociationReport::getReportInstanceId, reportInstancesIds)
        );
        if (CollectionUtil.isEmpty(associationReportList)) {
            return;
        }
        // 批量删除
        for (AssociationReport associationReport : associationReportList) {
            associationReportMapper.deleteById(associationReport.getId());
        }
    }
}
