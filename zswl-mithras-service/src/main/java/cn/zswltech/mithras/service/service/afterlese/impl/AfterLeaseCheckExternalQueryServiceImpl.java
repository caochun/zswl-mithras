package cn.zswltech.mithras.service.service.afterlese.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Assert;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckExternalQueryDto;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckExternalQueryListReq;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckExternalQueryListStatisticsRsp;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.VersionTypeEnum;
import cn.zswltech.mithras.afterlease.domain.enums.ExternalQueryStatus;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.NewAfterLeaseCheckExternalQueryMapper;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckExternalQuery;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckExternalQueryClientInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.BizProcessDataService;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckExternalQueryClientInfoService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckExternalQueryService;
import cn.zswltech.mithras.afterlease.application.lib.AfterLeaseCheckExternalQueryVersionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author zhaozhengkang
 * @description 租后检查外部查询任务
 * @date 2022-11-17
 */
@Service
public class AfterLeaseCheckExternalQueryServiceImpl
        extends ServiceImpl<NewAfterLeaseCheckExternalQueryMapper, NewAfterLeaseCheckExternalQuery>
        implements AfterLeaseCheckExternalQueryService {
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private AfterLeaseCheckExternalQueryVersionService afterLeaseCheckExternalQueryVersionService;
    @Resource
    private AfterLeaseCheckExternalQueryClientInfoService clientInfoService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void submit(Long id) {
        NewAfterLeaseCheckExternalQuery query = getById(id);
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(ProcessModelTypeEnum.NewAfterLeaseCheckExternalQueryFlow.name());
        Map<String, Object> varMap = new HashMap<>();
        varMap.put("bizDeptLeader", Objects.nonNull(query.getBizDeptLeader()) ?
                ListUtil.toList(String.valueOf(query.getBizDeptLeader())) : new ArrayList<>());
        varMap.put("bizDivisionLeader", Objects.nonNull(query.getBizDivisionLeader()) ?
                ListUtil.toList(String.valueOf(query.getBizDivisionLeader())) : new ArrayList<>());
        startProcessReq.setVariables(varMap);

        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setBusinessKey(String.valueOf(id));
        startProcessReq.setProcessInstanceName(query.getClientName() + "租后检查外部信息查询");
        startProcessReq.setStartUserDeptId(Optional.ofNullable(query.getDeptId())
                .map(String::valueOf).orElse(null));
        String processInstanceId = processApiService.start(startProcessReq);
        // 记录客户id
        bizProcessDataService.recordBizData(processInstanceId, query.getClientId());
        query.setProcessInstanceId(processInstanceId);
        query.setApprovalStatus(ExternalQueryStatus.UNDER_APPROVAL.name());
        updateById(query);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long add(NewAfterLeaseCheckExternalQuery req) {
        baseMapper.insert(req);
        return req.getId();
    }

    @Override
    public Page<NewAfterLeaseCheckExternalQuery> list(AfterLeaseCheckExternalQueryListReq req) {
        AfterLeaseCheckExternalQueryDto dto = new AfterLeaseCheckExternalQueryDto();
        dto.setInspectionMonth(req.getTargetMonth().atStartOfDay());
        dto.setClientName(req.getClientName());
        dto.setApprovalStatus(req.getApprovalStatus());
        dto.setSponsorUserId(req.getSponsorId());
        List<Long> canViewDeptIds = sysUserService.canViewDeptIds();
        boolean isBizUser = null != canViewDeptIds;
        if (isBizUser && canViewDeptIds.isEmpty()) {
            canViewDeptIds.add(Long.MIN_VALUE);
        }
        dto.setIsBizUser(isBizUser);
        dto.setDeptIdList(canViewDeptIds);
        dto.setCurrentUserId(AccountUtil.getLoginInfo().getId());
        return baseMapper.myList(new Page<>(req.getPage(), req.getPageSize()), dto);
    }

    @Override
    public AfterLeaseCheckExternalQueryListStatisticsRsp listStatistics(AfterLeaseCheckExternalQueryListReq req) {
        AfterLeaseCheckExternalQueryListStatisticsRsp rsp = new AfterLeaseCheckExternalQueryListStatisticsRsp();
        AfterLeaseCheckExternalQueryDto dto = new AfterLeaseCheckExternalQueryDto();
        List<Long> canViewDeptIds = sysUserService.canViewDeptIds();
        boolean isBizUser = null != canViewDeptIds;
        if (isBizUser && canViewDeptIds.isEmpty()) {
            canViewDeptIds.add(Long.MIN_VALUE);
        }
        dto.setIsBizUser(isBizUser);
        dto.setDeptIdList(canViewDeptIds);
        dto.setCurrentUserId(AccountUtil.getLoginInfo().getId());

        dto.setInspectionMonth(req.getTargetMonth().atStartOfDay());
        rsp.setTotalCount(baseMapper.myListStatistics(dto));
        dto.setApprovalStatus(ExternalQueryStatus.APPROVAL_PASS.name());
        rsp.setDoneCount(baseMapper.myListStatistics(dto));
        return rsp;
    }

    @Override
    public List<NewAfterLeaseCheckExternalQuery> listByClientId(Long clientId) {
        LambdaQueryWrapper<NewAfterLeaseCheckExternalQuery> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckExternalQuery::getClientId, clientId);
        return this.list(query);
    }

    @Override
    public void submitCheck(Long id) {
        NewAfterLeaseCheckExternalQuery query = getById(id);
        submitCheck(query);
    }

    @Override
    public void submitCheck(NewAfterLeaseCheckExternalQuery query) {
        LocalDateTime today = LocalDateTime.now();
        if (!query.getInspectionMonth().getMonth().equals(today.getMonth())) {
            throw new MithrasException("任务已跨月，不可提交");
        }
        Assert.notNull(query.getInspectionDate(), () -> MithrasException.newException("必填项'检查日期'为空"));
        List<NewAfterLeaseCheckExternalQueryClientInfo> clientInfos =
                clientInfoService.list(Wrappers.<NewAfterLeaseCheckExternalQueryClientInfo>lambdaQuery()
                        .eq(NewAfterLeaseCheckExternalQueryClientInfo::getQueryId, query.getId()));
        for (NewAfterLeaseCheckExternalQueryClientInfo clientInfo : clientInfos) {
            Assert.notNull(clientInfo.getQueryTimeFrom(), () -> MithrasException.newException("必填项'查询区间'为空"));
            Assert.notNull(clientInfo.getQueryTimeTo(), () -> MithrasException.newException("必填项'查询区间'为空"));
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void processEnd(Long id, Integer endType, Long startUserId, String processInstanceId, String modelKey) {
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        ProcessBusinessStatusEnum processBusinessStatusEnum = ProcessBusinessStatusEnum.getByType(endType);
        // 考虑到还有取消流程操作，因此将数据库操作放在if内
        if (processPass) {
            NewAfterLeaseCheckExternalQuery query = getById(id);
            query.setApprovalPassTime(LocalDateTime.now());
            query.setApprovalStatus(ExternalQueryStatus.APPROVAL_PASS.name());
            baseMapper.updateById(query);
            // 生成有效版本
            afterLeaseCheckExternalQueryVersionService.recordVersion(
                    id,
                    VersionTypeEnum.APPROVAL,
                    AccountUtil.getLoginInfo().getId(),
                    processInstanceId,
                    VersionTypeConstants.NORMAL
            );
        } else {
            NewAfterLeaseCheckExternalQuery query = getById(id);
            if (ProcessBusinessStatusEnum.REJECT.equals(processBusinessStatusEnum) || ProcessBusinessStatusEnum.REJECT_ALL.equals(processBusinessStatusEnum)) {
                query.setApprovalStatus(ExternalQueryStatus.APPROVAL_REJECT.name());
            }
            if (processBusinessStatusEnum == ProcessBusinessStatusEnum.CANCEL) {
                query.setApprovalStatus(ExternalQueryStatus.TO_BE_QUERY.name());
            }
            query.setProcessInstanceId(null);
            baseMapper.updateById(query);
            // 生成无效版本
            afterLeaseCheckExternalQueryVersionService.recordVersion(
                    id,
                    VersionTypeEnum.APPROVAL,
                    startUserId,
                    processInstanceId,
                    VersionTypeConstants.INVALID
            );
        }
    }
}