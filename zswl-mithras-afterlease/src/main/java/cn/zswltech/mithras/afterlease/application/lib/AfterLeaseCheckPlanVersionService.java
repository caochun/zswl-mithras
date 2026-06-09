package cn.zswltech.mithras.afterlease.application.lib;

import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.req.task.TaskSystemPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.mithras.dto.flow.search.ReceiveTaskListRSP;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.message.enums.MessageUrlEnum;
import cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.message.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckPlanBase;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.lib.CommonVersionService;
import cn.zswltech.mithras.afterlease.application.lib.handler.AfterLeaseCheckPlanLibAbstractHandler;
import cn.zswltech.mithras.message.service.MessageService;
import cn.zswltech.mithras.basedata.util.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/12/15
 * @description
 */
@Component
public class AfterLeaseCheckPlanVersionService extends CommonVersionService<NewAfterLeaseCheckPlanBase> {
    @Autowired
    private List<AfterLeaseCheckPlanLibAbstractHandler> libHandlerList;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private AfterLeaseFlowTaskConvertPort flowTaskConvert;
    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConvert;

    @Override
    public void customFlushData(NewAfterLeaseCheckPlanBase newAfterLeaseCheckPlanBase, String version, boolean needClearLastFlag, Integer versionType) {
        // 处理抄表逻辑
        for (AfterLeaseCheckPlanLibAbstractHandler libHandler : libHandlerList) {
            libHandler.flushData(version, newAfterLeaseCheckPlanBase.getId(), needClearLastFlag, versionType);
        }
    }

    @Override
    public ChangeDTO checkActualChange(Long mainId) {
        throw new MithrasException("暂不支持的功能");
    }

    @Override
    public void customReset(NewAfterLeaseCheckPlanBase newAfterLeaseCheckPlanBase, CommonVersion commonVersion) {
        // 处理抄表逻辑
        for (AfterLeaseCheckPlanLibAbstractHandler libHandler : libHandlerList) {
            libHandler.reset(newAfterLeaseCheckPlanBase.getId(), commonVersion.getVersion());
        }
    }

    @Override
    public CommonVersionDiffRSP doCompare(CommonVersion newVersion, CommonVersion oldVersion) {
        throw new MithrasException("暂不支持的功能");
    }

    @Override
    protected CommonVersionListRSP convertPageRsp(CommonVersion cv, NewAfterLeaseCheckPlanBase baseModel, Map<Long, String> userNameMap) {
        throw new MithrasException("暂不支持的功能");
    }

    @Override
    protected String getBusinessModuleName() {
        return "NEW_AFTER_LEASE_CHECK_PLAN";
    }

    /**
     * 2个工作日未提交提醒
     * @author: luyujie
     * @date: 2025/11/27
     **/
    public void afterLeaseCheckRemind(String id) {
        TaskSystemPageReq flowReq = new TaskSystemPageReq();
        flowReq.setDynamicFilterParam(new HashMap<>());
        flowReq.setModelKeyList(Arrays.asList(ProcessModelTypeEnum.NewAfterLeaseCheckReportFlow.name(), ProcessModelTypeEnum.NewAfterLeaseCheckReportCommonlyFlow.name()));
        flowReq.setIsRunning(1);
        flowReq.setSortType(1);
        //查询租后检查报告（一般检查）与租后检查报告的在途流程
        Page<TaskResp> flowTaskPage = taskApiService.querySystemTask(flowReq);
        //存在在途流程并且在工作日发起提醒
        if (CollectionUtils.isNotEmpty(flowTaskPage.getContents())&&DateUtil.isWorkday(LocalDate.now())) {
            // 填充流程数据
            List<String> processInstanceIdList = flowTaskPage.getContents().stream().map(TaskResp::getProcessInstanceId).distinct().collect(Collectors.toList());
            Map<String, ProcessResp> processRespMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(processInstanceIdList)) {
                ProcessPageReq processFlowReq = new ProcessPageReq();
                processFlowReq.setProcessInstanceIdList(processInstanceIdList);
                processFlowReq.setPageSize(Integer.MAX_VALUE);
                processRespMap.putAll(taskApiService.queryProcess(processFlowReq).getContents().stream().collect(Collectors.toMap(ProcessResp::getProcessInstanceId, p -> p)));
            }
            List<ReceiveTaskListRSP> rspList = flowTaskPage.getContents().stream().map(resp -> flowTaskConvert.flowResp2ReceiveRSP(resp, processRespMap.get(resp.getProcessInstanceId()))).collect(Collectors.toList());
            flowTaskConvert.receiveTaskListRSPFillName(rspList);
            for (ReceiveTaskListRSP rsp : rspList) {
                //针对超过2个工作日的处理人发起消息提醒
                if (DateUtil.countWorkdayNumber(rsp.getTaskCreateTime().toLocalDate(), LocalDate.now()) == 3 && rsp.getAssignee() != null) {
                    MessageAddREQ messageAddREQ = new MessageAddREQ();
                    messageAddREQ.setFrom("系统通知");
                    messageAddREQ.setTo(Collections.singletonList(rsp.getAssignee()));
                    messageAddREQ.setFlowid(rsp.getTaskId());
                    messageAddREQ.setRelation("【" + rsp.getClientName() + "】的租后检查报告审批流程"+rsp.getModelName());
                    // 只有特定流程产生的消息才发到oa系统
                    messageAddREQ.setNeedOa(false);
                    messageAddREQ.setNoticeSource(NoticeSourceENUM.APPROVAL_PROCESS.name());
                    messageAddREQ.setMessageType(MessageTypeEnum.UNDER_APPROVAL.name());
                    // 前端我收到的界面路径
                    MessageUrlEnum noticeContextEnum = MessageUrlEnum.NOTICE_CONTEXT;
                    messageAddREQ.setAppurl(StringUtils.format(noticeContextEnum.appUrl, rsp.getTaskId()));
                    messageAddREQ.setPcurl(StringUtils.format(noticeContextEnum.pcUrl, rsp.getTaskId(), rsp.getBusinessKey(), rsp.getSubModule()));
                    messageAddREQ.setTaskId(rsp.getTaskId());
                    messageAddREQ.setContent(rsp.getProcessInstanceId());
                    messageService.sendMessageAsync(messageConvert.reqToTodoMessage(messageAddREQ));
                }
            }

        }
    }
}
