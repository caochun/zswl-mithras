package cn.zswltech.mithras.service.flow.listener;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowRuntimeApiService;
import cn.zswltech.flow.core.domain.resp.ProcessAttachDataResp;
import cn.zswltech.flow.core.enums.NotifyTypeEnum;
import cn.zswltech.flow.core.extension.event.NotifyEvent;
import cn.zswltech.flow.core.extension.event.context.NotifyContext;
import cn.zswltech.flow.core.service.impl.FlowCacheService;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.service.constant.FlowConstants;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.message.enums.MessageUrlEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.message.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.message.service.MessageService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.util.FlowUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 流程消息通知
 *
 * @author wangchuanhao
 * @date 2022/8/11 12:18 AM
 */
@Component
@Slf4j
public class ProcessNotifyListener implements ApplicationListener<NotifyEvent>  {

    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConvert;
    @Resource
    private HistoryService historyService;
    @Resource
    private FlowRuntimeApiService runtimeApiService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private FlowCacheService flowCacheService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    @Override
    public void onApplicationEvent(NotifyEvent event) {
        NotifyContext notifyContext = event.getNotifyContext();
        log.info("流程消息通知:{}", JSON.toJSONString(notifyContext));
        if (Objects.equals(NotifyTypeEnum.TODO.getType(), notifyContext.getType())) {

            // 待办消息 此处不能用自己的查询，会查不到
            // TaskResp taskResp = taskApiService.querySystemTaskById(notifyContext.getTaskId());
//            TaskEntityImpl task = (TaskEntityImpl) taskService.createTaskQuery().taskId(notifyContext.getTaskId()).singleResult();
            ProcessInstance processInstance = flowCacheService.queryRunningProcessInstanceWithCheck(notifyContext.getProcessInstanceId());
            String modelKey = processInstance.getProcessDefinitionKey();
            ProcessAttachDataResp attachDataResp = runtimeApiService.queryProcessAttachData(notifyContext.getProcessInstanceId());
            Task task = flowCacheService.queryRunningTaskWithCheck(notifyContext.getTaskId());

            MessageAddREQ messageAddREQ = new MessageAddREQ();
            messageAddREQ.setFrom("系统通知");
            messageAddREQ.setTo(notifyContext.getReceiverIdList().stream().filter(Objects::nonNull).map(Long::valueOf).collect(Collectors.toList()));
            messageAddREQ.setFlowid(notifyContext.getTaskId());
            //付款申请修改为项目名称
            if(Objects.equals(modelKey, ProcessModelTypeEnum.PaymentCreateFlow.name())){
                Long businessKey = Long.valueOf(notifyContext.getBusinessKey());
                PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(businessKey);
                ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(paymentBaseInfo.getContractId());
                messageAddREQ.setRelation(ObjectUtil.isNotEmpty(contractBaseInfo) ?
                        contractBaseInfo.getProjName() : processInstance.getName());
            } else if(Objects.equals(modelKey, ProcessModelTypeEnum.ContractEarlySettleFlow.name()) || Objects.equals(modelKey,
                    ProcessModelTypeEnum.ContractNormalSettleFlow.name())){
                Long businessKey = Long.valueOf(notifyContext.getBusinessKey());
                ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(businessKey);
                messageAddREQ.setRelation(ObjectUtil.isNotEmpty(contractBaseInfo) ?
                        contractBaseInfo.getProjName() : processInstance.getName());
            }else {
                messageAddREQ.setRelation(processInstance.getName());
            }
            messageAddREQ.setRelation(messageAddREQ.getRelation() + FlowUtil.convertModelName(processInstance.getProcessDefinitionKey()));
            // 审批待办：万达科技有限公司 客户变更流程 流程编号：LC22020219
            //messageAddREQ.setContent(String.format("审批待办：%s %s 流程编号：%s", taskResp.getProcessInstanceName(), taskResp.getModelName(), taskResp.getProcessInstanceId()));
            // 只有特定流程产生的消息才发到oa系统
            messageAddREQ.setNeedOa(Optional.ofNullable(ProcessModelTypeEnum.getByName(processInstance.getProcessDefinitionKey())).map(ProcessModelTypeEnum::getSendOa).orElse(false));
            messageAddREQ.setNoticeSource(NoticeSourceENUM.APPROVAL_PROCESS.name());
            messageAddREQ.setMessageType(MessageTypeEnum.UNDER_APPROVAL.name());
            // 前端还要区分我收到的、我发起的界面路径
            MessageUrlEnum noticeContextEnum = FlowConstants.START_USER_TASK.equals(task.getTaskDefinitionKey()) ? MessageUrlEnum.MY_APPLY_NOTICE_CONTEXT : MessageUrlEnum.NOTICE_CONTEXT;
            messageAddREQ.setAppurl(StringUtils.format(noticeContextEnum.appUrl, notifyContext.getTaskId()));
            messageAddREQ.setPcurl(StringUtils.format(noticeContextEnum.pcUrl, notifyContext.getTaskId(), notifyContext.getBusinessKey(), attachDataResp.getSubModule()));
            messageAddREQ.setTaskId(notifyContext.getTaskId());
            messageAddREQ.setContent(notifyContext.getProcessInstanceId());
            messageService.sendMessageAsync(messageConvert.reqToTodoMessage(messageAddREQ));
        } else if (Objects.equals(NotifyTypeEnum.CC.getType(), notifyContext.getType())) {
            // 抄送
            HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(notifyContext.getProcessInstanceId()).singleResult();
            String modelKey = processInstance.getProcessDefinitionKey();
            ProcessAttachDataResp attachDataResp = runtimeApiService.queryProcessAttachData(notifyContext.getProcessInstanceId());
            MessageAddREQ messageAddREQ = new MessageAddREQ();
            messageAddREQ.setFrom(sysUserService.getUserName(Long.valueOf(processInstance.getStartUserId())));
            messageAddREQ.setFlowid(processInstance.getId());
            //付款申请修改为项目名称
            String noticeName;
            if(Objects.equals(modelKey, ProcessModelTypeEnum.PaymentCreateFlow.name())){
                Long businessKey = Long.valueOf(notifyContext.getBusinessKey());
                PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(businessKey);
                ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(paymentBaseInfo.getContractId());
                noticeName = ObjectUtil.isNotEmpty(contractBaseInfo) ?
                        contractBaseInfo.getProjName() : processInstance.getName();
            } else if(Objects.equals(modelKey, ProcessModelTypeEnum.ContractEarlySettleFlow.name()) || Objects.equals(modelKey,
                    ProcessModelTypeEnum.ContractNormalSettleFlow.name())){
                Long businessKey = Long.valueOf(notifyContext.getBusinessKey());
                ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(businessKey);
                noticeName = ObjectUtil.isNotEmpty(contractBaseInfo) ?
                        contractBaseInfo.getProjName() : processInstance.getName();
            }else {
                noticeName = processInstance.getName();
            }
            messageAddREQ.setRelation(String.format("%s%s 来自：%s", noticeName, FlowUtil.convertModelName(processInstance.getProcessDefinitionKey()),
                    messageAddREQ.getFrom()));
            // 只有特定流程产生的消息才发到oa系统
            messageAddREQ.setNeedOa(Optional.ofNullable(ProcessModelTypeEnum.getByName(processInstance.getProcessDefinitionKey())).map(ProcessModelTypeEnum::getSendOa).orElse(false));
            messageAddREQ.setContent(processInstance.getId());
            messageAddREQ.setNoticeSource(NoticeSourceENUM.APPROVAL_PROCESS.name());
            messageAddREQ.setMessageType(MessageTypeEnum.CC.name());
            messageAddREQ.setTo(notifyContext.getReceiverIdList().stream().map(Long::valueOf).collect(Collectors.toList()));
            // 拼装url
            MessageUrlEnum processEndEnum = MessageUrlEnum.PROCESS_CC;
            messageAddREQ.setAppurl(StringUtils.format(processEndEnum.appUrl, processInstance.getId()));
            messageAddREQ.setPcurl(StringUtils.format(processEndEnum.pcUrl, processInstance.getId(), processInstance.getBusinessKey(), attachDataResp.getSubModule()));
            messageService.sendMessageAsync(messageConvert.reqToMessage(messageAddREQ));
        }
    }

}
