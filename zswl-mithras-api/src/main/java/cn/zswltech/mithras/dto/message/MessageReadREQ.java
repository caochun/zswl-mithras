package cn.zswltech.mithras.dto.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName MessageReadREQ
 * @Description
 * @Author jackerhe
 * @Date 2022/9/17 11:27 上午
 * @Version 1.0
 **/
@Data
@ApiModel("消息通知-读消息请求体")
public class MessageReadREQ {

    @ApiModelProperty(value = "来源途径 MessageChannelEnum")
    private String messageChannel;

    @ApiModelProperty(value = "消息ID")
    private List<Long> noticeIds;

    @ApiModelProperty(value = "租赁用户ID")
    private Long mithrasUserId;

    @ApiModelProperty(value = "是否发送集团通知")
    private Boolean needQA;

}
