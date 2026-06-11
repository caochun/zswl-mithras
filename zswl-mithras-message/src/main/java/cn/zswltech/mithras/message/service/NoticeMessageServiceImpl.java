package cn.zswltech.mithras.message.service;


import cn.hutool.core.util.ObjectUtil;
import cn.zswl.notice.model.NoticeMsg;
import cn.zswl.notice.service.ExportService;
import cn.zswltech.mithras.dto.message.MessageCountRSP;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.message.enums.MessageType;
import cn.zswltech.mithras.message.model.MessageModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @ClassName MessageServiceImpl
 * @Description
 * @Author jackerhe
 * @Date 2022/7/26 5:02 下午
 * @Version 1.0
 **/
@Service
@Slf4j
public class NoticeMessageServiceImpl extends AbstractMessageService {

    @Autowired(required = false)
    private ExportService exportService;

    @Autowired
    private MessageConver messageConver;

    @Override
    public String getType() {
        return MessageType.NOTICE.getType();
    }

    @Override
    public Boolean saveMessage(MessageModel messageModel) {
        log.info("this is NoticeMessage save {}", messageModel);
        return super.saveMessage(messageModel);
    }

    @Override
    public Long pushMessage(MessageModel messageModel) {
       //userService.
        NoticeMsg noticeMsg = messageConver.buildNoticeMsg(messageModel);
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
