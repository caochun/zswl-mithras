package cn.zswltech.mithras.dto.client.message;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName MessageInfoREQ
 * @Description
 * @Author jackerhe
 * @Date 2022/7/27 4:17 下午
 * @Version 1.0
 **/
@Data
public class MessageInfoREQ extends PageReq {

    //用户ID
    @ApiModelProperty(name = "clientId", value = "用户ID", required = true)
    private String clientId;

    //消息类型
    @ApiModelProperty(name = "messageType", value = "消息类型")
    private String messageType;

    //是否已读 0未读，1已读
    @ApiModelProperty(name = "read", value = "是否已读 0未读，1已读")
    private String messageRead;

    //标题
    @ApiModelProperty(name = "title", value = "标题")
    private String title;

}
