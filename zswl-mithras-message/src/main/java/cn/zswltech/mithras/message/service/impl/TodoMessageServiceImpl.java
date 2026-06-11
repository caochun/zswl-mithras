package cn.zswltech.mithras.message.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.zswl.notice.model.NoticeMsg;
import cn.zswl.notice.service.ExportService;
import cn.zswltech.mithras.dto.message.MessageCountRSP;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.message.enums.MessageType;
import cn.zswltech.mithras.message.model.MessageModel;
import cn.zswltech.mithras.message.service.AbstractMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 *
 * @author: jackerhe
 * @date: 2022/7/27 2:18 下午
 **/
@Service
@Slf4j
public class TodoMessageServiceImpl extends AbstractMessageService {

    @Autowired(required = false)
    private ExportService exportService;

    @Autowired
    private MessageConver messageConver;

    @Override
    public String getType() {
        return MessageType.TODO.getType();
    }

    @Override
    public Boolean saveMessage(MessageModel messageModel) {
        log.info("this is TodoMessageServiceImpl save {}", messageModel);
        return super.saveMessage(messageModel);
    }

    @Override
    public Long pushMessage(MessageModel messageModel) {
        NoticeMsg noticeMsg = messageConver.buildTodoMsg(messageModel);
        if(ObjectUtil.isNull(exportService)){
            log.info("消息通知功能未开启");
            return -1L;
        }
        return exportService.send(noticeMsg);
    }

    @Override
    public MessageCountRSP myTabCount() {
        return null;
    }

}
