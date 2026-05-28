package cn.zswltech.mithras.dto.client.message;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName MessageInfoRSP
 * @Description
 * @Author jackerhe
 * @Date 2022/7/27 4:30 下午
 * @Version 1.0
 **/
@Data
public class MessageInfoRSP {

    //id
    @ApiModelProperty("id")
    private Long id;

    //客户ID
    @ApiModelProperty("客户ID")
    private String clientId;

    //是否已读 0未读，1已读
    @ApiModelProperty("是否已读 0未读，1已读")
    private String messageRead;

    //消息时间
    @ApiModelProperty("消息时间")
    private Date messageTime;

    //消息类型
    @ApiModelProperty("消息类型")
    private String messageType;

    //消息体
    @ApiModelProperty("消息体")
    private String messageBody;

}
