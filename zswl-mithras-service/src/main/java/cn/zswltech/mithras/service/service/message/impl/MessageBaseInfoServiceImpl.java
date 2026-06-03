/*
package cn.zswltech.mithras.message.service.impl;


import cn.zswltech.mithras.dto.client.message.MessageInfoREQ;
import cn.zswltech.mithras.dto.client.message.MessageReadREQ;
import cn.zswltech.mithras.service.enums.JudgeEnum;
import cn.zswltech.mithras.message.mapper.MessageBaseInfoMapper;
import cn.zswltech.mithras.message.mapper.message.MessageModel;
import cn.zswltech.mithras.message.mapper.model.MessageBaseInfo;
import cn.zswltech.mithras.message.service.MessageBaseInfoService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

*/
/**
 * @ClassName MessageBaseInfoServiceImpl
 * @Description
 * @Author jackerhe
 * @Date 2022/7/27 2:01 下午
 * @Version 1.0
 **//*

@Service
public class MessageBaseInfoServiceImpl extends ServiceImpl<MessageBaseInfoMapper, MessageBaseInfo> implements MessageBaseInfoService {

    @Autowired
    private MessageBaseInfoMapper mapper;

    @Override
    public Boolean saveMessage(MessageModel messageModel) {
        MessageBaseInfo messageBaseInfo = buildMessage(messageModel);
        mapper.insert(messageBaseInfo);
        messageModel.setMsgId(messageBaseInfo.getId());
        return true;
    }

    @Override
    public Page<MessageBaseInfo> getMessageList(MessageInfoREQ messageInfoREQ) {
        QueryWrapper<MessageBaseInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("client_id", messageInfoREQ.getClientId());
        if (!StringUtils.isBlank(messageInfoREQ.getMessageType())) {
            wrapper.eq("message_type", messageInfoREQ.getMessageType());
        }
        if (!StringUtils.isBlank(messageInfoREQ.getMessageRead())) {
            wrapper.eq("is_read", messageInfoREQ.getMessageRead());
        }
        wrapper.orderByDesc("message_time");
        return mapper.selectPage(new Page<>(messageInfoREQ.getPage(), messageInfoREQ.getPageSize()), wrapper);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Boolean readMessage(MessageReadREQ messageReadREQ) {
        UpdateWrapper<MessageBaseInfo> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", messageReadREQ.getMsgId());
        wrapper.eq("is_read", JudgeEnum.FALSE.getType());
        wrapper.set("is_read", JudgeEnum.TRUE.getType());
        return mapper.update(null, wrapper) > 0 ? Boolean.TRUE : Boolean.FALSE;
    }


    private MessageBaseInfo buildMessage(MessageModel messageModel) {
        MessageBaseInfo messageBaseInfo = new MessageBaseInfo();
        LocalDateTime date = LocalDateTime.now();
        messageBaseInfo.setClientId(JSONObject.toJSONString(messageModel.getTo()));
        messageBaseInfo.setMessageRead(JudgeEnum.FALSE.getType());
        messageBaseInfo.setMessageTime(messageModel.getTimestamp());
        messageBaseInfo.setMessageType(messageModel.getBodie().getSendType());
        messageBaseInfo.setMessageBody(JSONObject.toJSONString(messageModel.getBodie()));
        //通用参数
        messageBaseInfo.setCreateTime(date);
        messageBaseInfo.setUpdateTime(date);
        return messageBaseInfo;
    }

}
*/
