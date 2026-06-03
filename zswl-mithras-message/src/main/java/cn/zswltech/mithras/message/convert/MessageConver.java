package cn.zswltech.mithras.message.convert;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswl.notice.constans.BizConstans;
import cn.zswl.notice.constans.enums.NoticeTypeEnum;
import cn.zswl.notice.model.BizInfo;
import cn.zswl.notice.model.DataScope;
import cn.zswl.notice.model.NoticeMsg;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.common.util.StringUtil;
import cn.zswltech.gruul.common.util.UUIDUtil;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.message.mapper.message.MessageModel;
import cn.zswltech.mithras.message.mapper.message.NoticeMessageBody;
import cn.zswltech.mithras.message.mapper.message.PopUpNotificationBody;
import cn.zswltech.mithras.message.mapper.message.TodoMessageBody;
import cn.zswltech.mithras.service.others.MithrasException;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @ClassName MessageConver
 * @Description
 * @Author jackerhe
 * @Date 2022/9/13 10:39 上午
 * @Version 1.0
 **/
@Component
@Slf4j
public class MessageConver {

    private static final String MSG_ID = "mithrasMsgId";

    private static final String MITHRAS_CLIENT_ID = "mithrasClientId";

    private static final String NOTICESOURCE = "noticeSource";

    private static final String MESSAGE_TYPE = "messageType";

    private static final String BUSINESS_URL = "businessUrl";

    @Value(value = "${common.notice.mithras.baseUrl}")
    private String baseUrl;

    @Value(value = "${common.notice.mithras.appBaseUrl}")
    private String appBaseUrl;

    @Value(value = "${remote.publishNotice.authPcUrl}")
    private String authPcUrl;

    @Value(value = "${remote.publishNotice.authAppUrl}")
    private String authAppUrl;


    public NoticeMsg buildNoticeMsg(MessageModel messageModel){
        NoticeTypeEnum code = NoticeTypeEnum.MESSAGE;
        //拼接url
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(MSG_ID, String.valueOf(messageModel.getMsgId()));
        params.add(MITHRAS_CLIENT_ID, String.valueOf(messageModel.getTo().get(0)));

        NoticeMessageBody bodie = (NoticeMessageBody) messageModel.getBodie();
        if(ObjectUtil.isEmpty(bodie.getFlowid())){
            bodie.setFlowid(UUIDUtil.genUuid());
        }
        Map<String, Object> map = new HashMap<>();
        BizInfo bizInfo = new BizInfo();
        if(ObjectUtil.isEmpty(bodie)){
            return null;
        }
        Date date = messageModel.getTimestamp() == null ? new Date() : messageModel.getTimestamp();
        NoticeMsg noticeMsg = BeanUtil.copyProperties(bodie, NoticeMsg.class);
        noticeMsg.setType(code.getCode());
        noticeMsg.setStatus((short) 0);
        DataScope dataScope = new DataScope();
        dataScope.setUsers(messageModel.getToTel());
        noticeMsg.setDataScope(JSONObject.toJSONString(dataScope));
        noticeMsg.setCreateBy(messageModel.getFrom());//创建人
        noticeMsg.setGmtCreate(date);
        noticeMsg.setGmtUpdate(date);
        noticeMsg.setUpdateBy(messageModel.getFrom());
        noticeMsg.setTitle(bodie.getTitle());//标题
        noticeMsg.setType(code.getCode());//类型
        //远端参数
        String pcUrl = getUrl(bodie.getPcurl(), params, baseUrl, bodie.getMessageTypeEnum());
        bizInfo.setBizUrl(StringUtils.join(authPcUrl, pcUrl));//pcUrl
        bizInfo.setBizCode(bodie.getNodename());//放通知类型搜索用
        map.put(BizConstans.APP_URL_KEY, StringUtils.join(authAppUrl, getUrl(bodie.getAppurl(), params, appBaseUrl, bodie.getMessageTypeEnum())));//appUrl
        map.put(BizConstans.FLOW_ID,  bodie.getFlowid());//流程实例id
        map.put(BizConstans.WORK_FLOW_NAME,  bodie.getWorkflowname());//流程类型名称
        map.put(BizConstans.NODE_NAME, bodie.getNodename());//流程实例id
        map.put(NOTICESOURCE, bodie.getWorkflowname());
        map.put(MESSAGE_TYPE, bodie.getNodename());
        map.put(BUSINESS_URL, pcUrl);
        //其他，透传参数
        bizInfo.setOther(map);

        noticeMsg.setBizInfo(JSONObject.toJSONString(bizInfo));
        noticeMsg.setDealUser(StringUtils.join(messageModel.getToTel(), ","));//接收人
        //noticeMsg.setDealUser(JSONUtils.toJSONString(messageModel.getTo()));
        noticeMsg.setNeedOa(ObjectUtil.isNull(messageModel.getNeedOa()) ? Boolean.TRUE : messageModel.getNeedOa());
        noticeMsg.setNeedQueue(Boolean.FALSE);
        noticeMsg.setNeedWebSocket(Boolean.TRUE);
        log.info("消息参数-发送通知 接收人 {}  appUrl {}, requestname {}, flowid {}", messageModel.getTo(), map.get(BizConstans.APP_URL_KEY), noticeMsg.getTitle(), map.get(BizConstans.FLOW_ID));
        return noticeMsg;
    }

    public NoticeMsg buildPopUpNotification(MessageModel messageModel) {
        PopUpNotificationBody bodie = (PopUpNotificationBody) messageModel.getBodie();

        Date date = messageModel.getTimestamp() == null ? new Date() : messageModel.getTimestamp();
        NoticeMsg noticeMsg = BeanUtil.copyProperties(bodie, NoticeMsg.class);
        noticeMsg.setType((short) 5);
        noticeMsg.setStatus((short) 0);
        DataScope dataScope = new DataScope();
        dataScope.setUsers(messageModel.getToTel());
        noticeMsg.setDataScope(JSONObject.toJSONString(dataScope));
        noticeMsg.setCreateBy(messageModel.getFrom());//创建人
        noticeMsg.setGmtCreate(date);
        noticeMsg.setGmtUpdate(date);
        noticeMsg.setId(messageModel.getMsgId());
        noticeMsg.setUpdateBy(messageModel.getFrom());
        noticeMsg.setTitle(bodie.getTitle());//标题
        noticeMsg.setType((short) 5);//类型
        noticeMsg.setContent(bodie.getContent());
        noticeMsg.setDealUser(StringUtils.join(messageModel.getToTel(), ","));//接收人
        //noticeMsg.setDealUser(JSONUtils.toJSONString(messageModel.getTo()));
        noticeMsg.setNeedOa(ObjectUtil.isNull(messageModel.getNeedOa()) ? Boolean.FALSE : messageModel.getNeedOa());
        noticeMsg.setNeedQueue(Boolean.FALSE);
        noticeMsg.setNeedWebSocket(Boolean.TRUE);

        BizInfo bizInfo = new BizInfo();
        bizInfo.setOther(bodie.getAttachment());
        noticeMsg.setBizInfo(JSONObject.toJSONString(bizInfo));
        return noticeMsg;
    }

    public NoticeMsg buildTodoMsg(MessageModel messageModel){
        NoticeTypeEnum code = NoticeTypeEnum.TASK;
        //url参数
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(MSG_ID, String.valueOf(messageModel.getMsgId()));
        params.add(MITHRAS_CLIENT_ID, String.valueOf(messageModel.getTo().get(0)));

        TodoMessageBody bodie = (TodoMessageBody) messageModel.getBodie();
        if(ObjectUtil.isEmpty(bodie.getFlowid())){
            bodie.setFlowid(UUIDUtil.genUuid());
        }
        Map<String, Object> map = new HashMap<>();
        BizInfo bizInfo = new BizInfo();
        if(ObjectUtil.isEmpty(bodie)){
            return null;
        }
        Date date = messageModel.getTimestamp() == null ? new Date() : messageModel.getTimestamp();
        NoticeMsg noticeMsg = BeanUtil.copyProperties(bodie, NoticeMsg.class);
        noticeMsg.setType(code.getCode());
        noticeMsg.setStatus((short) 0);
        DataScope dataScope = new DataScope();
        dataScope.setUsers(messageModel.getToTel());
        noticeMsg.setDataScope(JSONObject.toJSONString(dataScope));
        noticeMsg.setCreateBy(messageModel.getFrom());//创建人
        noticeMsg.setGmtCreate(date);
        noticeMsg.setGmtUpdate(date);
        noticeMsg.setUpdateBy(messageModel.getFrom());
        noticeMsg.setTitle(bodie.getTitle());//标题
        noticeMsg.setType(code.getCode());//类型
        //远端参数
        String pcUrl = getUrl(bodie.getPcurl(), params, baseUrl, bodie.getMessageTypeEnum());
        bizInfo.setBizUrl(StringUtils.join(authPcUrl, pcUrl));//pcUrl
        map.put(BizConstans.APP_URL_KEY, StringUtils.join(authAppUrl, getUrl(bodie.getAppurl(), params, appBaseUrl, bodie.getMessageTypeEnum())));//appUrl
        map.put(BizConstans.FLOW_ID,  bodie.getFlowid());//流程实例id
        map.put(BizConstans.WORK_FLOW_NAME,  bodie.getWorkflowname());//流程类型名称
        map.put(BizConstans.NODE_NAME, bodie.getNodename());//流程实例id
        map.put(NOTICESOURCE, bodie.getWorkflowname());
        map.put(MESSAGE_TYPE, bodie.getNodename());
        map.put(BUSINESS_URL, pcUrl);
        //其他，透传参数
        bizInfo.setOther(map);
        bizInfo.setBizCode(bodie.getNodename());//放通知类型搜索用
        noticeMsg.setBizInfo(JSONObject.toJSONString(bizInfo));
        noticeMsg.setDealUser(StringUtils.join(messageModel.getToTel(),","));//接收人
        //noticeMsg.setDealUser(JSONUtils.toJSONString(messageModel.getTo()));
        //默认不发
        noticeMsg.setNeedOa(ObjectUtil.isNull(messageModel.getNeedOa()) ? Boolean.FALSE : messageModel.getNeedOa());
        noticeMsg.setNeedQueue(Boolean.FALSE);
        noticeMsg.setNeedWebSocket(Boolean.TRUE);
        log.info("消息参数-发送待办 接收人 {}  appUrl {}, requestname {}, flowid {}",messageModel.getTo(), map.get(BizConstans.APP_URL_KEY), noticeMsg.getTitle(),  map.get(BizConstans.FLOW_ID));
        return noticeMsg;
    }


    private String getUrl(String url, MultiValueMap<String, String> params, String pre, MessageTypeEnum messageTypeEnum){
        log.info("发送消息, url {}, messageTypeEnum {}, pre {}", url, messageTypeEnum, pre);
        if(StringUtil.isBlank(url)){
            return null;
        }
        String owerUrl;
        if(MessageTypeEnum.isIgnore(messageTypeEnum)){
            owerUrl = UriComponentsBuilder.fromUriString(url)
                    .queryParams(params).toUriString();
        }else {
            owerUrl = UriComponentsBuilder.fromUriString(pre + url)
                    .queryParams(params).toUriString();
        }

        try {
            owerUrl = URLEncoder.encode(owerUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("MessageConver getUrl error", e);
        }
        return owerUrl;
    }

    public MessageModel reqToMessage(MessageAddREQ req){
        MessageModel messageModel = new MessageModel();
        NoticeMessageBody bodie = new NoticeMessageBody();
        if(ObjectUtil.isNull(req.getFrom())){
            if(ObjectUtil.isEmpty(AccountUtil.getLoginInfo())){
                throw new MithrasException("无发送人信息");
            }
            messageModel.setFrom(AccountUtil.getLoginInfo().getAccount());
        }else {
            messageModel.setFrom(req.getFrom());
        }
        messageModel.setTo(req.getTo());
        messageModel.setTimestamp(new Date());
        messageModel.setNeedOa(req.isNeedOa());
        MessageTypeEnum messageTypeEnum = Optional.ofNullable(MessageTypeEnum.of(req.getMessageType()))
                .orElseThrow(() -> new MithrasException("消息类型不合法"));
        bodie.setTitle(StringUtils.join( req.getRelation() + messageTypeEnum.message));
        bodie.setFlowid(req.getFlowid());
        bodie.setContent(req.getContent());//名称
        bodie.setWorkflowname(req.getNoticeSource());
        bodie.setNodename(messageTypeEnum.display);
        bodie.setMessageTypeEnum(messageTypeEnum);
        bodie.setAppurl(req.getAppurl());
        bodie.setPcurl(req.getPcurl());
        messageModel.setBodie(bodie);
        return messageModel;
    }


    public MessageModel reqToTodoMessage(MessageAddREQ req){
        MessageModel messageModel = new MessageModel();
        TodoMessageBody bodie = new TodoMessageBody();
        if(ObjectUtil.isNull(req.getFrom())){
            if(ObjectUtil.isEmpty(AccountUtil.getLoginInfo())){
                throw new MithrasException("无发送人信息");
            }
            messageModel.setFrom(AccountUtil.getLoginInfo().getAccount());
        }else {
            messageModel.setFrom(req.getFrom());
        }
        messageModel.setTo(req.getTo());
        messageModel.setTimestamp(new Date());
        messageModel.setNeedOa(req.isNeedOa());
        MessageTypeEnum messageTypeEnum = Optional.ofNullable(MessageTypeEnum.of(req.getMessageType()))
                .orElseThrow(() -> new MithrasException("消息类型不合法"));
        bodie.setTitle(StringUtils.join(  req.getRelation() + messageTypeEnum.message));
        bodie.setFlowid(req.getFlowid());
        bodie.setContent(req.getContent());//名称
        bodie.setWorkflowname(req.getNoticeSource());
        bodie.setNodename(messageTypeEnum.display);
        bodie.setMessageTypeEnum(messageTypeEnum);
        bodie.setAppurl(req.getAppurl());
        bodie.setPcurl(req.getPcurl());
        bodie.setMithrasId(req.getTaskId());
        messageModel.setBodie(bodie);
        return messageModel;
    }

}
