package cn.zswltech.mithras.message.service;

import cn.zswl.notice.model.CommonQry;
import cn.zswl.notice.model.NoticeVo;
import cn.zswltech.mithras.dto.message.MessageHandleREQ;
import cn.zswltech.mithras.dto.message.MessageReadREQ;
import cn.zswltech.mithras.message.model.MessageModel;
import com.github.pagehelper.PageInfo;

import java.util.Collections;
import java.util.List;
//import cn.zswltech.mithras.message.service.impl.WebSocketServer;

/**
 * @ClassName AbstractMessageService
 * @Description
 * @Author jackerhe
 * @Date 2022/7/26 5:10 下午
 * @Version 1.0
 **/
public abstract class AbstractMessageService implements MessageService {
    @Override
    public List<Long> sendMessage(MessageModel messageModel) {
        return Collections.emptyList();
    }

    /**
     * 消息体保存接口，如需要需自实现保存逻辑
     * @author: jackerhe
     * @date: 2022/7/27 10:23 上午
     **/
    public Boolean saveMessage(MessageModel messageModel) {
        return Boolean.TRUE;
    }

    /**
     * 消息发送方式，默认采用websocket的异步发送消息接口
     * @author: jackerhe
     * @date: 2022/7/27 10:11 上午
     **/
    public Long pushMessage(MessageModel messageModel) {
        //WebSocketServer.sendAsyncInfo(messageModel);
       return 0L;
    }

    @Override
    public void handle(MessageHandleREQ req) {
    }

    @Override
    public void makeReaded(MessageReadREQ req) {
    }

    @Override
    public void readAll(Long userId) {
    }

    @Override
    public void sendMessageAsync(MessageModel messageModel) {

    }

    @Override
    public void handleAsync(MessageHandleREQ req) {

    }

    @Override
    public void makeReadedAsync(MessageReadREQ req) {

    }

    @Override
    public PageInfo<NoticeVo> list(CommonQry commonQry) {
        return new PageInfo<>();
    }
}
