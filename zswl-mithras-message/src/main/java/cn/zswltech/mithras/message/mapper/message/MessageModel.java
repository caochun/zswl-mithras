package cn.zswltech.mithras.message.mapper.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName NoticeMessage
 * @Description
 * @Author jackerhe
 * @Date 2022/7/26 3:04 下午
 * @Version 1.0
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize
public class MessageModel implements Serializable {
    private Long msgId;//消息id
    private MessageBody bodie;//自定义消息体
    private String from;//来自谁
    private List<Long> to;//消息传递给谁(被通知对象) 注意
    private List<String> toTel;//消息传递给谁(被通知对象电话)
    private String read;//是否已读 0未读，1已读
    private Date timestamp;//
    private Boolean needOa;//是否需要发送至qa, 默认发送

    public MessageModel(){
    }

    public MessageModel(MessageBody messageBody) {
        if (messageBody != null) {
            bodie = messageBody;
        }
    }

}
