package cn.zswltech.mithras.service.flow.listener;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowRuntimeApiService;
import cn.zswltech.flow.core.dao.BusinessStatusMapper;
import cn.zswltech.flow.core.domain.resp.ProcessAttachDataResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.ProcessEndEvent;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.service.convert.MessageConver;
import cn.zswltech.mithras.service.enums.MessageUrlEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.service.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.service.flow.listener.endhandler.AbstractProcessEndHandler;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.service.ProcAttentionRecordService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.message.MessageService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.util.FlowUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.flowable.engine.HistoryService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;


/**
 * 流程结束
 *
 * @author wangchuanhao
 * @date 2022/6/17 6:11 PM
 */
@Slf4j
@Component
public class ProcessEndEventListener implements ApplicationListener<ProcessEndEvent> {

    @Resource
    private List<AbstractProcessEndHandler> processEndHandlerList;
    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConvert;
    @Resource
    private BusinessStatusMapper businessStatusMapper;
    @Resource
    private HistoryService historyService;
    @Resource
    private FlowRuntimeApiService runtimeApiService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ProcAttentionRecordService procAttentionRecordService;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void onApplicationEvent(ProcessEndEvent event) {
        log.info("监听到流程结束, 数据:{}", JSON.toJSONString(event.getProcessEndContext()));
        ProcessEndContext endContext = event.getProcessEndContext();
        // 业务层处理
        processEndHandlerList.forEach(handler -> {
            if (handler.needHandle(endContext)) {
                handler.handle(endContext);
            }
        });
        // 流程结束消息通知
        sendFlowMsg(endContext);
    }

    private void sendFlowMsg(ProcessEndContext endContext) {
//         消息通知 已处理任务的审批人 TODO 此处的处理方式有问题 之后有空处理下
//        List<HistoricTaskInstance> historyTaskList = historyService.createHistoricTaskInstanceQuery().processInstanceId(endContext.getProcessInstanceId()).list();
//        // 找到所有任务的操作记录
//        Example example = new Example(BusinessStatus.class);
//        Example.Criteria criteria = example.createCriteria();
//        criteria.andIn("sourceId", historyTaskList.stream().map(HistoricTaskInstance::getId).collect(Collectors.toList()))
//                .andEqualTo("sourceType", BusinessDataEnum.TASK.getType());
//        Set<String> approveTaskIdSet = businessStatusMapper.selectByCondition(example).stream().map(BusinessStatus::getSourceId).collect(Collectors.toSet());
//        Set<Long> doneAssigneeIdSet = historyTaskList.stream()
//                .filter(t -> approveTaskIdSet.contains(t.getId()))
//                .map(HistoricTaskInstance::getAssignee)
//                .filter(Objects::nonNull)
//                .distinct()
//                .map(Long::valueOf)
//                .collect(Collectors.toSet());

        // 2023-04-10 不再通知所有人 找到关注流程的用户列表
        Set<Long> attentionUserIdSet = new HashSet<>(procAttentionRecordService.listAttentionUserIdList(endContext.getProcessInstanceId()));
        ProcessAttachDataResp attachDataResp = runtimeApiService.queryProcessAttachData(endContext.getProcessInstanceId());

        MessageAddREQ messageAddREQ = new MessageAddREQ();
        messageAddREQ.setFrom("系统通知");
        messageAddREQ.setFlowid(endContext.getProcessInstanceId());
        //付款申请修改为项目名称
        if(Objects.equals(endContext.getModelKey(), ProcessModelTypeEnum.PaymentCreateFlow.name())){
            Long businessKey = Long.valueOf(endContext.getBusinessKey());
            PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(businessKey);
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(paymentBaseInfo.getContractId());
            messageAddREQ.setRelation(ObjectUtil.isNotEmpty(contractBaseInfo) ?
                    contractBaseInfo.getProjName() : endContext.getProcessInstanceName());
        } else if(Objects.equals(endContext.getModelKey(), ProcessModelTypeEnum.ContractEarlySettleFlow.name()) || Objects.equals(endContext.getModelKey(),
                ProcessModelTypeEnum.ContractNormalSettleFlow.name())){
            Long businessKey = Long.valueOf(endContext.getBusinessKey());
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(businessKey);
            messageAddREQ.setRelation(ObjectUtil.isNotEmpty(contractBaseInfo) ?
                    contractBaseInfo.getProjName() : endContext.getProcessInstanceName());
        }else {
            messageAddREQ.setRelation(endContext.getProcessInstanceName());
        }
        messageAddREQ.setRelation(messageAddREQ.getRelation() + FlowUtil.convertModelName(endContext.getModelKey()));

        // 只有特定流程产生的消息才发到oa系统
        messageAddREQ.setNeedOa(Optional.ofNullable(ProcessModelTypeEnum.getByName(endContext.getModelKey())).map(ProcessModelTypeEnum::getSendOa).orElse(false));
        messageAddREQ.setContent(endContext.getProcessInstanceId());
        messageAddREQ.setNoticeSource(NoticeSourceENUM.APPROVAL_PROCESS.name());
        // 拼装url
        MessageUrlEnum processEndEnum = MessageUrlEnum.PROCESS_END;
        messageAddREQ.setAppurl(StringUtils.format(processEndEnum.appUrl, endContext.getProcessInstanceId()));
        messageAddREQ.setPcurl(StringUtils.format(processEndEnum.pcUrl, endContext.getProcessInstanceId(), endContext.getBusinessKey(), attachDataResp.getSubModule()));
        // 通过、拒绝、取消
        if (ProcessBusinessStatusEnum.success(endContext.getEndType())) {
            messageAddREQ.setMessageType(MessageTypeEnum.APPROVAL_PASS.name());
            // 加发起人
            attentionUserIdSet.add(Long.valueOf(endContext.getStartUserId()));
            messageAddREQ.setTo(new ArrayList<>(attentionUserIdSet));
            messageService.sendMessageAsync(messageConvert.reqToMessage(messageAddREQ));
        } else if (ProcessBusinessStatusEnum.REJECT.getType().equals(endContext.getEndType()) || ProcessBusinessStatusEnum.REJECT_ALL.getType().equals(endContext.getEndType())) {
            messageAddREQ.setMessageType(MessageTypeEnum.APPROVAL_REJECT.name());
            // 加发起人
            attentionUserIdSet.add(Long.valueOf(endContext.getStartUserId()));
            messageAddREQ.setTo(new ArrayList<>(attentionUserIdSet));
            messageService.sendMessage(messageConvert.reqToMessage(messageAddREQ));
        } else if (ProcessBusinessStatusEnum.CANCEL.getType().equals(endContext.getEndType())) {
            messageAddREQ.setMessageType(MessageTypeEnum.CANCELED.name());
            messageAddREQ.setTo(new ArrayList<>(attentionUserIdSet));
            if (CollectionUtils.isNotEmpty(messageAddREQ.getTo())) {
                messageService.sendMessageAsync(messageConvert.reqToMessage(messageAddREQ));
            }
        }
    }

}
