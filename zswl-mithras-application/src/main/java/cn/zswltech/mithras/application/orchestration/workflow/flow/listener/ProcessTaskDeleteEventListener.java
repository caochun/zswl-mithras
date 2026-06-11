package cn.zswltech.mithras.application.orchestration.workflow.flow.listener;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.flow.core.extension.event.TaskDeleteEvent;
import cn.zswltech.flow.core.extension.event.context.TaskDeleteContext;
import cn.zswltech.flow.core.service.impl.FlowCacheService;
import cn.zswltech.mithras.dto.message.MessageHandleREQ;
import cn.zswltech.mithras.dto.message.MessageReadREQ;
import cn.zswltech.mithras.message.enums.MessageChannelEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.message.model.ZhfkNoticeRelation;
import cn.zswltech.mithras.message.mapper.ZhfkNoticeRelationMapper;
import cn.zswltech.mithras.message.service.MessageService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Optional;

/**
 * 任务删除（不再可办）
 *
 * @author wangchuanhao
 * @date 2022/9/27 5:08 PM
 */
@Component
@Slf4j
public class ProcessTaskDeleteEventListener implements ApplicationListener<TaskDeleteEvent> {

    @Autowired
    private MessageService messageService;
    @Resource
    private ZhfkNoticeRelationMapper zhfkNoticeRelationMapper;
    @Resource
    private FlowCacheService flowCacheService;

    @Override
    public void onApplicationEvent(TaskDeleteEvent event) {
        TaskDeleteContext taskDeleteContext =  event.getTaskDeleteContext();
        log.info("任务已不可办:{}", JSON.toJSONString(taskDeleteContext));
        ProcessInstance processInstance = flowCacheService.queryRunningProcessInstanceWithCheck(taskDeleteContext.getProcessInstanceId());
        // 完成任务 消息已读
        if (StringUtils.isNotBlank(taskDeleteContext.getAssigneeId()) && !"null".equals(taskDeleteContext.getAssigneeId())) {
            ZhfkNoticeRelation relation = zhfkNoticeRelationMapper.selectOne(Wrappers.<ZhfkNoticeRelation>lambdaQuery()
                    .eq(ZhfkNoticeRelation::getMithrasId, taskDeleteContext.getTaskId())
                    .last("LIMIT 1")
            );
            if (Objects.nonNull(relation)) {
                MessageReadREQ readREQ = new MessageReadREQ();
                readREQ.setMessageChannel(MessageChannelEnum.APP.name());
                readREQ.setMithrasUserId(Long.parseLong(taskDeleteContext.getAssigneeId()));
                readREQ.setNeedQA(Optional.ofNullable(ProcessModelTypeEnum.getByName(processInstance.getProcessDefinitionKey())).map(ProcessModelTypeEnum::getSendOa).orElse(false));
                readREQ.setNoticeIds(ListUtil.toList(relation.getId()));
                messageService.makeReaded(readREQ);
            }
            MessageHandleREQ req = new MessageHandleREQ();
            req.setTaskId(taskDeleteContext.getTaskId());
            req.setMessageChannel(MessageChannelEnum.PC.name());
            req.setNeedQA(Optional.ofNullable(ProcessModelTypeEnum.getByName(processInstance.getProcessDefinitionKey())).map(ProcessModelTypeEnum::getSendOa).orElse(false));
            req.setMithrasUserId(Long.parseLong(taskDeleteContext.getAssigneeId()));//admin
            messageService.handle(req);
        }
    }

}
