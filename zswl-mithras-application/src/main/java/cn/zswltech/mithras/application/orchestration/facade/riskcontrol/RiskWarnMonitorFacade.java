package cn.zswltech.mithras.application.orchestration.facade.riskcontrol;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.TaskSystemPageReq;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.riskcontrol.application.RiskWarnMonitorApplicationService;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.riskcontrol.opinion.*;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.riskcontrol.common.RiskDataSourceEnum;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionMonitor;
import cn.zswltech.mithras.riskcontrol.warning.RiskControlWarnMonitor;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.application.orchestration.riskcontrol.opinion.RiskControlOpinionMonitorService;
import cn.zswltech.mithras.riskcontrol.warning.RiskControlWarnMonitorService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description 风控管理-监控预警管理
 * @date 2023-02-08
 */
@Service
public class RiskWarnMonitorFacade implements RiskWarnMonitorApplicationService {

    @Resource
    private RiskControlOpinionMonitorService riskControlOpinionMonitorService;
    @Resource
    private RiskControlWarnMonitorService riskControlWarnMonitorService;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private SysUserService sysUserService;
    @Value("${xinsight.ips.primary}")
    private String XinsightIp;

    @Override
    public R<PageR<RiskWarnMonitorOpinionListRSP>> opinionList(@Valid RiskWarnMonitorOpinionListREQ req) {
        cn.zswltech.flow.core.util.Page<TaskResp> flow = this.getFlow(BusinessModuleEnum.RISK_OPINION.getModelKeyList());
        if (ObjectUtil.isEmpty(flow) || ObjectUtil.isEmpty(flow.getContents())) {
            return R.ok();
        }
        req.setIds(flow.getContents().stream().map(TaskResp::getBusinessKey).map(Long::parseLong).collect(Collectors.toList()));
        Page<RiskControlOpinionMonitor> riskControlOpinionMonitorPage = riskControlOpinionMonitorService.opinionList(req);
        List<RiskWarnMonitorOpinionListRSP> rsps = null;
        if (ObjectUtil.isNotEmpty(riskControlOpinionMonitorPage)) {
            Long accountId = AccountUtil.getLoginInfo().getId();
            rsps = BeanUtil.copyToList(riskControlOpinionMonitorPage.getRecords(), RiskWarnMonitorOpinionListRSP.class);
            // 获取创建人名称
            Set<Long> creatorIdList = riskControlOpinionMonitorPage.getRecords().stream().map(RiskControlOpinionMonitor::getCreateBy).collect(Collectors.toSet());
            Map<Long, String> nameMap = id2NameService.sysUserId2Name(creatorIdList);
            //补充流程信息
            if (ObjectUtil.isNotEmpty(flow) || ObjectUtil.isNotEmpty(flow.getContents())) {
                Map<String, String> businessKey2Task = flow.getContents().stream().collect(Collectors.toMap(TaskResp::getBusinessKey, TaskResp::getTaskId, (a, b) -> a));
                Map<String, String> businessKey2AgentId= flow.getContents().stream().collect(Collectors.toMap(TaskResp::getBusinessKey, TaskResp::getAssignee, (a, b) -> a));
                rsps.forEach(e -> {
                    e.setTaskId(businessKey2Task.get(String.valueOf(e.getId())));
                    if (ObjectUtil.equals(accountId, Long.parseLong(businessKey2AgentId.get(String.valueOf(e.getId()))))) {
                        e.setOperableFlag(YesOrNoNumberEnum.YES.getCode());
                    } else {
                        e.setOperableFlag(YesOrNoNumberEnum.NO.getCode());
                    }
                    // 人工新增舆情需求：增加返回字段
                    e.setOpinionType(source2Type(e.getDataSource()));
                    e.setCreateName(nameMap.get(e.getCreateBy()));
                });
            }
            return R.ok(PageR.of(rsps, riskControlOpinionMonitorPage.getTotal()));
        }
        return R.ok();
    }

    private String source2Type(String dataSource){
        if (dataSource.equals(RiskDataSourceEnum.MANUAL.name())){
            return RiskDataSourceEnum.MANUAL_ENTRY.display();
        }else {
            return RiskDataSourceEnum.AUTOMATIC_IMPORT.display();
        }
    }

    @Override
    public R<PageR<RiskWarnMonitorWarnListRSP>> warnList(@Valid RiskWarnMonitorWarnListREQ req) {
        cn.zswltech.flow.core.util.Page<TaskResp> flow = this.getFlow(BusinessModuleEnum.RISK_WARN.getModelKeyList());
        if (ObjectUtil.isEmpty(flow) || ObjectUtil.isEmpty(flow.getContents())) {
            return R.ok();
        }
        req.setIds(flow.getContents().stream().map(TaskResp::getBusinessKey).map(Long::parseLong).collect(Collectors.toList()));
        Page<RiskControlWarnMonitor> riskControlWarnMonitorPage = riskControlWarnMonitorService.warnList(req);
        List<RiskWarnMonitorWarnListRSP> rsps = null;
        if (ObjectUtil.isNotEmpty(riskControlWarnMonitorPage)) {
            Long accountId = AccountUtil.getLoginInfo().getId();
            rsps = BeanUtil.copyToList(riskControlWarnMonitorPage.getRecords(), RiskWarnMonitorWarnListRSP.class);
            //补充流程信息
            if (ObjectUtil.isNotEmpty(flow) || ObjectUtil.isNotEmpty(flow.getContents())) {
                Map<String, String> businessKey2Task = flow.getContents().stream().collect(Collectors.toMap(TaskResp::getBusinessKey, TaskResp::getTaskId, (a, b) -> a));
                rsps.forEach(e -> {
                    e.setTaskId(businessKey2Task.get(String.valueOf(e.getId())));
                });
            }
            Map<Long, String> deptId2Name = id2NameService.deptId2Name(rsps.stream().map(RiskWarnMonitorWarnListRSP::getBelongDeptId).collect(Collectors.toList()));
            Map<String, String> businessKey2AgentId= flow.getContents().stream().collect(Collectors.toMap(TaskResp::getBusinessKey, TaskResp::getAssignee, (a, b) -> a));
            rsps.forEach(e -> {
                e.setBelongDeptName(deptId2Name.get(e.getBelongDeptId()));
                if (ObjectUtil.equals(accountId, Long.parseLong(businessKey2AgentId.get(String.valueOf(e.getId()))))) {
                    e.setOperableFlag(YesOrNoNumberEnum.YES.getCode());
                } else {
                    e.setOperableFlag(YesOrNoNumberEnum.NO.getCode());
                }
                //慧眼数据需要拼接IP地址
                if(e.getDataSource().equals(RiskDataSourceEnum.XINSIGHT.name())){
                    e.setLinkAddress(XinsightIp + e.getLinkAddress());
                }
            });
            return R.ok(PageR.of(rsps, riskControlWarnMonitorPage.getTotal()));
        }
        return R.ok();
    }

    private cn.zswltech.flow.core.util.Page<TaskResp> getFlow(List<String> modelKeyList) {
        TaskSystemPageReq flowReq = new TaskSystemPageReq();
        flowReq.setIsRunning(1);
        flowReq.setModelKeyList(modelKeyList);
        //区分是否为后台
        if (sysUserService.currentUserIsBizDept()) {
            flowReq.setAssignee(String.valueOf(AccountUtil.getLoginInfo().getId()));
        }
        //flowReq.setNotEqualsActivityId(FlowConstants.START_USER_TASK);
        flowReq.setSortType(1);
        flowReq.setPageIndex(1);
        flowReq.setPageSize(5000);
        return taskApiService.querySystemTask(flowReq);
    }

    @Override
    public R<RiskWarnMonitorWarnDetailRSP> warnDetail(@Valid SinglePkREQ req) {
        return R.ok(riskControlWarnMonitorService.warnDetail(req));
    }

    @Override
    public R<RiskWarnMonitorStatisticsRSP> statistics() {
        RiskWarnMonitorStatisticsRSP rsp = new RiskWarnMonitorStatisticsRSP();
        // 统计监控客户数
        riskControlOpinionMonitorService.statisticsOpinion(rsp);
        // 统计预警客户数
        riskControlWarnMonitorService.statisticsWarn(rsp);
        return R.ok(rsp);
    }

    @Override
    public R<List<RiskWarnMonitorQuantityChangeRSP>> quantityChange() {
        List<RiskWarnMonitorQuantityChangeRSP> rsps = new ArrayList<>();
        LocalDate localDate = LocalDate.now().minusDays(4);
        // 统计监控客户数
        List<RiskWarnMonitorQuantityChangeRSP> opinionList = riskControlOpinionMonitorService.quantityChange(localDate);
        if (ObjectUtil.isNotEmpty(opinionList)) {
            rsps.addAll(opinionList);
        }
        // 统计预警客户数
        List<RiskWarnMonitorQuantityChangeRSP> warnList = riskControlWarnMonitorService.quantityChange(localDate);
        if (ObjectUtil.isNotEmpty(warnList)) {
            rsps.addAll(warnList);
        }
        return R.ok(rsps);
    }

    @Override
    public R<List<RiskWarnMonitorTypeChangeRSP>> typeChange() {
        return R.ok(riskControlWarnMonitorService.typeChange());
    }

    @Override
    public R<Void> warnModify(@Valid RiskControlWarnModifyREQ req) {
        riskControlWarnMonitorService.warnModify(req);
        return R.ok();
    }
}