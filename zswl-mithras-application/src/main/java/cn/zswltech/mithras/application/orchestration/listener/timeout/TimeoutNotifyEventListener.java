package cn.zswltech.mithras.application.orchestration.listener.timeout;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswl.notice.message.impl.WebSocketServer;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.contract.enums.TimeoutTypeEnum;
import cn.zswltech.mithras.contract.event.timeout.TimeoutNotifyEvent;
import cn.zswltech.mithras.contract.event.timeout.TimeoutStartEvent;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.message.model.MessageModel;
import cn.zswltech.mithras.message.model.PopUpNotificationBody;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractRemindRecord;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractRemindRecordService;
import cn.zswltech.mithras.contract.core.delayqueue.DelayQueueService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName TimeoutStartEventListener
 * @Description 超时事件监听
 * @Author jackerhe
 * @Date 2023/4/25 5:36 下午
 * @Version 1.0
 **/
@Component
@Slf4j
@DependsOn("contractRemindRecordServiceImpl")
public class TimeoutNotifyEventListener implements ApplicationRunner, ApplicationListener<TimeoutNotifyEvent> {

    @Value("${common.contractStartRentRemind}")
    private Boolean isContractStartRentRemind;
    @Resource
    private ContractRemindRecordService contractRemindRecordService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private DelayQueueService delayQueueService;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    @Resource
    private UserService userService;
    @Resource
    private MessageConver messageConver;

    @Override
    public void run(ApplicationArguments args) {
        flashStartRentNotice();
    }

    public void flashStartRentNotice() {
        //查询所有未起租
        List<ContractRemindRecord> list = contractRemindRecordService.list();
        if (ObjectUtil.isEmpty(list)) {
            log.info("no contract need start rent !!!!!");
        }
        list.forEach(contractRemindRecord -> {
            // 如果合同已经处于起租流程中则忽略
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractRemindRecord.getContractId());
            if(ObjectUtil.isNotEmpty(contractBaseInfo)) {
                if (!Objects.equals(contractBaseInfo.getContractProcessStatus(), ContractProcessStatusEnum.START_RENT_COMMIT.name())) {
                    TimeoutStartEvent timeoutStartEvent = new TimeoutStartEvent(contractRemindRecord, String.valueOf(contractRemindRecord.getContractCreatorId()),
                            System.currentTimeMillis(), ObjectUtil.isNotEmpty(contractRemindRecord.getIntervals()) ?
                            Math.max(LongUtil.null2zero(contractRemindRecord.getIntervals()), 60L) : 60, TimeoutTypeEnum.CONTRACT_RENT.name(), false);
                    applicationEventPublisher.publishEvent(timeoutStartEvent);
                }
            }
        });
    }


    /**
     * 监听起租信息
     **/
    @Override
    public void onApplicationEvent(TimeoutNotifyEvent event) {
        if (TimeoutTypeEnum.CONTRACT_RENT.name().equals(event.getType())) {
            try {
                log.info("TimeoutNotifyEventListener  onApplicationEvent event {}", event.getKey());
                List<ContractRemindRecord> contractRemindRecords = contractRemindRecordService.list(Wrappers.<ContractRemindRecord>lambdaQuery()
                        .eq(ContractRemindRecord::getContractCreatorId, event.getKey()));
                if (ObjectUtil.isEmpty(contractRemindRecords)) {
                    return;
                }
                List<String> texts = new ArrayList<String>();
                // 如果合同已经处于起租流程中则忽略
                contractRemindRecords.forEach(contractRemindRecord -> {
                    ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractRemindRecord.getContractId());
                    if (!Objects.equals(contractBaseInfo.getContractProcessStatus(), ContractProcessStatusEnum.START_RENT_COMMIT.name())) {
                        texts.add(String.format("%s：%s", contractBaseInfo.getProjName(), contractRemindRecord.getContractCode()));
                    }
                });
                if (ObjectUtil.isEmpty(texts)) {
                    return;
                }
                MessageModel messageModel = new MessageModel();
                messageModel.setNeedOa(false);
                messageModel.setFrom("系统通知");
                messageModel.setTo(ListUtil.toList(Long.valueOf(event.getKey())));
                PopUpNotificationBody startRentMessageBody = new PopUpNotificationBody();
                startRentMessageBody.setContent(String.join("$", texts));
                startRentMessageBody.setTitle("合同起租提醒");
                Map<String, Object> attach = new HashMap<>();
                attach.put("popUpType", "CONTRACT_RENT");
                startRentMessageBody.setAttachment(attach);
                messageModel.setMsgId(Long.valueOf(event.getKey()));
                messageModel.setBodie(startRentMessageBody);
                messageModel.setToTel(ListUtil.toList(userService.getRealPhone(Long.valueOf(event.getKey()))));
                //直接使用
                if (Objects.nonNull(isContractStartRentRemind) && Objects.equals(Boolean.TRUE, isContractStartRentRemind)) {
                    WebSocketServer.sendAsyncInfo(messageConver.buildPopUpNotification(messageModel));
                } else {
                    log.info("当前环境不需要通过websocket推送合同起租提醒，忽略不处理");
                }
                TimeoutStartEvent timeoutStartEvent = new TimeoutStartEvent(contractRemindRecords, event.getKey(),
                        System.currentTimeMillis(), contractRemindRecords.get(0).getIntervals(), TimeoutTypeEnum.CONTRACT_RENT.name(), false);
                applicationEventPublisher.publishEvent(timeoutStartEvent);
            } catch (Exception e) {
                log.info("sent contract rent error", e);
            } finally {
                checkSize();
            }

        }
    }

    private void checkSize() {
        if (ObjectUtil.notEqual(contractRemindRecordService.list().stream().map(ContractRemindRecord::getContractCreatorId).collect(Collectors.toSet()).size(),
                delayQueueService.getTaskCount(TimeoutTypeEnum.CONTRACT_RENT.name()))) {
            flashStartRentNotice();
        }
    }

}
