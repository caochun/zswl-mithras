package cn.zswltech.mithras.message.service;


import cn.zswl.notice.model.CommonQry;
import cn.zswl.notice.model.NoticeVo;
import cn.zswltech.mithras.dto.dashboard.DashboardTodoCountRSP;
import cn.zswltech.mithras.dto.message.MessageCountRSP;
import cn.zswltech.mithras.dto.message.MessageHandleREQ;
import cn.zswltech.mithras.dto.message.MessageReadREQ;
import cn.zswltech.mithras.message.mapper.message.MessageModel;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface MessageService {

    /**
     * 调用此方法通知前端有新消息到达，按功能需要自定义MessageBody，body内的内容不做处理,通过body.getType()选择处理类,默认通过websocket发送至前端
     * 返回消息ID，如不在线或消息发送失败返回0
     * 如需其他发送方式，可自实现AbstractMessageService.sendMessage()，注意getType值需与MessageBody保持一致
     * @author: jackerhe
     * @date: 2022/7/28 10:24 上午
     * 通知消息示例
     *         NoticeMessageBody noticeMessageBody = new NoticeMessageBody();
     *         noticeMessageBody.setTitle("这是标题");
     *         MessageModel messageModel = new MessageModel(noticeMessageBody);
     *         messageModel.setTo(req.getId());
     *         Long aLong = messageService.sendMessage(messageModel);
     *         if(aLong > 0){
     *             log.info("success msgId = {}", aLong);
     *         }
     **/

    List<Long> sendMessage(MessageModel messageModel);

    void sendMessageAsync(MessageModel messageModel);

    String getType();

    Boolean saveMessage(MessageModel messageModel);

    Long pushMessage(MessageModel messageModel);

    /**
     *发送待办完成通知
     **/
    void handle(MessageHandleREQ req);

    void handleAsync(MessageHandleREQ req);

    /**
     *发送已读通知
     **/
    void makeReaded(MessageReadREQ req);

    void makeReadedAsync(MessageReadREQ req);

    void readAll(Long userId);

    /**
     * 通用查询
     **/
    PageInfo<NoticeVo> list(CommonQry commonQry);

    MessageCountRSP myTabCount();
}
