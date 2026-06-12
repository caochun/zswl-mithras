package cn.zswltech.mithras.message.persistence.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName MessageBaseInfo
 * @Description 消息发送实体类
 * @Author jackerhe
 * @Date 2022/7/27 10:27 上午
 * @Version 1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("message_base_info")
public class MessageBaseInfo extends BaseModel implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("client_id")
    private String clientId;//消息所有者，即to

    @TableField("is_read")
    private String messageRead;//是否已读 0未读，1已读

    @TableField("message_time")
    private Date messageTime;//消息时间

    @TableField("message_body")
    private String messageBody;//消息体

    @TableField("message_type")
    private String messageType;//消息类型

}
