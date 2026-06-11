package cn.zswltech.mithras.dashboard.application;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.dashboard.DashboardAfterLeaseCheckListREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardAfterLeaseCheckRSP;
import cn.zswltech.mithras.dto.dashboard.DashboardAfterLeaseStatisticsRSP;
import cn.zswltech.mithras.dto.dashboard.DashboardClientAfterLeaseCheckRSP;
import cn.zswltech.mithras.dto.process.prepare.ProcessPrepareListREQ;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseCheckPlanProcessStatusEnum;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseCheckPlanStatusEnum;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseCheckPlanTypeEnum;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseCheckWayEnum;
import cn.zswltech.mithras.dashboard.enums.DashboardAfterLeaseCheckStatueEnum;
import cn.zswltech.mithras.dashboard.enums.DashboardCardGroupEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessState;
import cn.zswltech.mithras.afterlease.mapper.NewAfterLeaseCheckPlanBaseMapper;
import cn.zswltech.mithras.afterlease.mapper.NewAfterLeaseCheckPlanClientMapper;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.customer.mapper.model.client.Client;
import cn.zswltech.mithras.afterlease.mapper.model.dashboard.DashboardClientAfterLeaseCheckQuery;
import cn.zswltech.mithras.workflow.model.CommonProcessPrepare;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.workflow.process.prepare.CommonProcessPrepareService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.foundation.context.SpringContextHolder.getBean;


@Slf4j
@Service
public class DashboardAfterLeaseService implements cn.zswltech.mithras.dashboard.application.DashboardAfterLeaseApplicationService {

    @Resource
    private NewAfterLeaseCheckPlanBaseMapper afterLeaseCheckPlanBaseMapper;
    @Resource
    private CommonProcessPrepareService commonProcessPrepareService;
    @Resource
    private NewAfterLeaseCheckPlanClientMapper newAfterLeaseCheckPlanClientMapper;

    public List<DashboardAfterLeaseStatisticsRSP> statisticsList() {
        DashboardAfterLeaseCheckListREQ pageReq = new DashboardAfterLeaseCheckListREQ();
        pageReq.setPage(1);
        pageReq.setPageSize(5000);
        List<DashboardAfterLeaseCheckRSP> list = this.afterLeaseCheckList(pageReq);
        List<DashboardAfterLeaseCheckRSP> prepareList = this.afterLeaseCheckPrepareList(pageReq);
        DashboardAfterLeaseStatisticsRSP rsp = new DashboardAfterLeaseStatisticsRSP();
        rsp.setGroup(DashboardCardGroupEnum.CLIENT_AFTER_LEASE.getDisplay());
        rsp.setGroupCode(DashboardCardGroupEnum.CLIENT_AFTER_LEASE.name());
        if (ObjectUtil.isEmpty(list) && ObjectUtil.isEmpty(prepareList)) {
            rsp.setLeftCount(0);
            rsp.setRightCount(0);
            return Collections.singletonList(rsp);
        }

        List<DashboardAfterLeaseCheckRSP> left = list.stream().filter(o -> AfterLeaseCheckPlanStatusEnum.NEW.name().equals(o.getCheckPlanStatus()))
                .collect(Collectors.toList());
        left.addAll(prepareList);
        List<DashboardAfterLeaseCheckRSP> right = list.stream().filter(o -> AfterLeaseCheckPlanStatusEnum.CHECKING.name().equals(o.getCheckPlanStatus()))
                .collect(Collectors.toList());
        rsp.setLeftCount(left.size());
        rsp.setRightCount(right.size());
        return Collections.singletonList(rsp);
    }

    public List<DashboardAfterLeaseCheckRSP> afterLeaseCheckList(DashboardAfterLeaseCheckListREQ req) {
        AccountVO currentUser = AccountUtil.getLoginInfo();
        if (ObjectUtil.isNull(currentUser)) {
            return ListUtil.empty();
        }

        //展示计划状态为【检查中】&审批状态为【变更审批通过】或【新建审批通过】的数据
        Page<DashboardClientAfterLeaseCheckRSP> objectPage = new Page<>();
        objectPage.setCurrent(1);
        objectPage.setSize(5000);
        DashboardClientAfterLeaseCheckQuery query = BeanUtil.copyProperties(req, DashboardClientAfterLeaseCheckQuery.class);
        fillAuthQuery(query, currentUser);
        query.setProjSponsorUserId(currentUser.getId());
        Page<DashboardClientAfterLeaseCheckRSP> checkPlanBasePage = afterLeaseCheckPlanBaseMapper.pageList(objectPage, query);
        if (ObjectUtil.isEmpty(checkPlanBasePage.getRecords())) {
            return ListUtil.empty();
        }

        //如果存在数据，需要填充对应的display
        Map<Long, String> deptId2NameMap = getBean(Id2NameService.class).deptId2Name(checkPlanBasePage.getRecords().stream().map(DashboardClientAfterLeaseCheckRSP::getBizDeptId).collect(Collectors.toList()));
        Map<Long, String> user2NameMap = getBean(Id2NameService.class).sysUserId2Name(checkPlanBasePage.getRecords().stream().map(DashboardClientAfterLeaseCheckRSP::getProjSponsorUserId).collect(Collectors.toList()));
        checkPlanBasePage.getRecords().forEach(one -> {
            one.setReportProcessStatusDisplay(Optional.ofNullable(AfterLeaseCheckPlanProcessStatusEnum.of(one.getReportProcessStatusCode())).map(AfterLeaseCheckPlanProcessStatusEnum::getDisplay).orElse(""));
            one.setCheckWayDisplay(Optional.ofNullable(AfterLeaseCheckWayEnum.find(one.getCheckWayCode())).map(AfterLeaseCheckWayEnum::getDisplay).orElse(""));
            one.setCheckPlanTypeDisplay(Optional.ofNullable(AfterLeaseCheckPlanTypeEnum.of(one.getPlanType())).map(AfterLeaseCheckPlanTypeEnum::getDisplay).orElse(""));
            one.setCheckPlanStatusDisplay(Optional.ofNullable(AfterLeaseCheckPlanStatusEnum.find(one.getCheckPlanStatus())).map(AfterLeaseCheckPlanStatusEnum::getDisplay).orElse(""));
            one.setProjSponsorUserName(user2NameMap.get(one.getProjSponsorUserId()));
            one.setBizDeptName(deptId2NameMap.get(one.getBizDeptId()));
        });
        return BeanUtil.copyToList(checkPlanBasePage.getRecords(), DashboardAfterLeaseCheckRSP.class);
    }

    private void fillAuthQuery(DashboardClientAfterLeaseCheckQuery query, AccountVO currentUser) {
        List<Long> viewDeptIds = getBean(SysUserService.class).canViewDeptIds(currentUser);
        if (viewDeptIds == null) {
            return;
        }
        if (viewDeptIds.isEmpty()) {
            query.setAuthCurrentUserId(currentUser.getId());
        } else {
            query.setAuthBizDeptIds(viewDeptIds);
        }
    }

    //待发起数据
    public List<DashboardAfterLeaseCheckRSP> afterLeaseCheckPrepareList(DashboardAfterLeaseCheckListREQ req) {
        AccountVO currentUser = AccountUtil.getLoginInfo();
        if (ObjectUtil.isNull(currentUser)) {
            return ListUtil.empty();
        }
        //展示计划状态为【检查中】&审批状态为【变更审批通过】或【新建审批通过】的数据
        ProcessPrepareListREQ preparReq = new ProcessPrepareListREQ();
        preparReq.setPage(1);
        preparReq.setPageSize(5000);
        preparReq.setStartUserId(String.valueOf(currentUser.getId()));
        preparReq.setProcessTypeList(Collections.singletonList(ProcessModelTypeEnum.NewAfterLeaseCheckReportCommonlyFlow.name()));
        if (ObjectUtil.isNotEmpty(req.getClientId())) {
            preparReq.setBusinessId(String.valueOf(req.getClientId()));
        }
        Page<CommonProcessPrepare> processPreparePage = commonProcessPrepareService.list(preparReq);
        if (ObjectUtil.isEmpty(processPreparePage) || ObjectUtil.isEmpty(processPreparePage.getRecords())) {
            return ListUtil.empty();
        }
        List<CommonProcessPrepare> commonProcessPrepares = processPreparePage.getRecords();
        commonProcessPrepares.removeIf(e -> ObjectUtil.isNotEmpty(e.getBusinessData()));
        if (ObjectUtil.isEmpty(commonProcessPrepares)) {
            return ListUtil.empty();
        }
        /*List<Long> newAfterLeaseCheckPlanBaseIds = commonProcessPrepares.stream().map(CommonProcessPrepare::getBusinessData).filter(ObjectUtil::isNotEmpty).map(Long::parseLong).collect(Collectors.toList());
        Map<Long, NewAfterLeaseCheckPlanBase> newAfterLeaseCheckPlanBaseMap = new HashMap<>();
        if (ObjectUtil.isNotEmpty(newAfterLeaseCheckPlanBaseIds)) {
            newAfterLeaseCheckPlanBaseMap.putAll(afterLeaseCheckPlanBaseMapper.selectBatchIds(newAfterLeaseCheckPlanBaseIds).stream()
                    .collect(Collectors.toMap(NewAfterLeaseCheckPlanBase::getId, e -> e, (a, b) -> a)));
        }*/
        //如果存在数据，需要填充对应的display
        List<Long> newAfterLeaseCheckPlanClientIds = commonProcessPrepares.stream().map(CommonProcessPrepare::getBusinessId).map(Long::parseLong).collect(Collectors.toList());
        List<NewAfterLeaseCheckPlanClient> newAfterLeaseCheckPlanClients = newAfterLeaseCheckPlanClientMapper.selectBatchIds(newAfterLeaseCheckPlanClientIds);
        if (ObjectUtil.isEmpty(newAfterLeaseCheckPlanClients)) {
            return ListUtil.empty();
        }
        Map<Long, Long> checkPlanClientId2ClientId = newAfterLeaseCheckPlanClients.stream().collect(Collectors.toMap(NewAfterLeaseCheckPlanClient::getId, NewAfterLeaseCheckPlanClient::getClientId, (a, b) -> a));
        List<Long> clientIds = newAfterLeaseCheckPlanClients.stream().map(NewAfterLeaseCheckPlanClient::getClientId).collect(Collectors.toList());
        Map<Long, Client> clientId2Client = getBean(ClientMapper.class).selectBatchIds(clientIds).stream().collect(Collectors.toMap(Client::getId, e -> e,(a, b) -> a));
        Map<Long, String> deptId2Name = getBean(Id2NameService.class).deptId2Name(clientId2Client.values().stream().map(Client::getBelongDeptId).collect(Collectors.toList()));
        Map<Long, String> systemId2Name = getBean(Id2NameService.class).sysUserId2Name(clientId2Client.values().stream().map(Client::getBelongSponsorId).collect(Collectors.toList()));

        List<DashboardAfterLeaseCheckRSP> rsps = new ArrayList<>();
        commonProcessPrepares.forEach(commonProcess -> {
            Client client = clientId2Client.get(checkPlanClientId2ClientId.get(Long.parseLong(commonProcess.getBusinessId())));
            if (ObjectUtil.isNotEmpty(client)) {
                DashboardAfterLeaseCheckRSP rsp = new DashboardAfterLeaseCheckRSP();
                rsp.setIdKey(commonProcess.getId());
                rsp.setClientId(client.getId());
                rsp.setClientName(client.getClientName());
                rsp.setCheckPlanName(commonProcess.getFormName());
                /*NewAfterLeaseCheckPlanBase newAfterLeaseCheckPlanBase = newAfterLeaseCheckPlanBaseMap.get(Long.parseLong(commonProcess.getBusinessData()));
                if (ObjectUtil.isNotEmpty(newAfterLeaseCheckPlanBase)) {
                    rsp.setCheckPlanName(newAfterLeaseCheckPlanBase.getPlanName());
                    rsp.setPlanType(newAfterLeaseCheckPlanBase.getPlanType());
                    rsp.setCheckPlanTypeDisplay(Optional.ofNullable(AfterLeaseCheckPlanTypeEnum.of(newAfterLeaseCheckPlanBase.getPlanType())).map(AfterLeaseCheckPlanTypeEnum::getDisplay).orElse(null));
                    rsp.setCheckWayCode(newAfterLeaseCheckPlanBase.getCheckWay());
                    rsp.setCheckWayDisplay(Optional.ofNullable(AfterLeaseCheckWayEnum.find(newAfterLeaseCheckPlanBase.getCheckWay())).map(AfterLeaseCheckWayEnum::getDisplay).orElse(null));
                    //todo
                    rsp.setCheckDate(newAfterLeaseCheckPlanBase.getDeadLine());

                }*/
                rsp.setBizDeptId(client.getBelongDeptId());
                rsp.setBizDeptName(deptId2Name.get(client.getBelongDeptId()));
                rsp.setProjSponsorUserId(client.getBelongSponsorId());
                rsp.setProjSponsorUserName(systemId2Name.get(client.getBelongSponsorId()));
                rsp.setReportProcessStatusCode(ProcessState.UN_SUBMIT.name());
                rsp.setReportProcessStatusDisplay(ProcessState.UN_SUBMIT.display());
                rsp.setCheckPlanStatus(DashboardAfterLeaseCheckStatueEnum.NEW.name());
                rsp.setCheckPlanStatusDisplay(DashboardAfterLeaseCheckStatueEnum.NEW.display());
                rsp.setCreateTime(commonProcess.getCreateTime());
                rsp.setUpdateTime(commonProcess.getUpdateTime());
                rsps.add(rsp);
            }
        });
        if (ObjectUtil.isNotEmpty(req.getClientId())) {
            rsps.removeIf(e -> !ObjectUtil.equals(e.getClientId(), e.getClientId()));
        }
        if (ObjectUtil.isNotEmpty(req.getCheckWayCode())) {
            rsps.removeIf(e -> !ObjectUtil.equals(e.getCheckWayCode(), req.getCheckWayCode()));
        }
        return rsps;
    }

}
