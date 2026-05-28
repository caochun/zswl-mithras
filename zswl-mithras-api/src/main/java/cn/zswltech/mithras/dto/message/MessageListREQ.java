package cn.zswltech.mithras.dto.message;

import cn.zswltech.mithras.dto.PageReq;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;

/**
 * @ClassName MessageListREQ
 * @Description
 * @Author jackerhe
 * @Date 2022/9/14 3:35 下午
 * @Version 1.0
 **/
@Data
@ApiModel("消息通知-查询list请求体")
public class MessageListREQ extends PageReq {
    /**
     *  1则查询 已读
     *  2 查 未读
     */
    @ApiModelProperty("1则查询 已读, 2 查 未读")
    Integer needRead;

    @ApiModelProperty("开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    Date createStart;

    @ApiModelProperty("结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    Date createEnd;

    @ApiModelProperty("标题")
    String title;

    @ApiModelProperty("消息类型 messageTypeEnum")
    String messageType;

    /**
     * @see 1 通知, 2 公告,
     * 3 待办任务, 4 文章
     */
    @ApiModelProperty("1 通知, 2 公告,3 待办任务, 4 文章")
    Short type;

    /**
     * ture 1 查待办
     * false 2 查已办
     */
    @ApiModelProperty("1 查待办 2 查已办")
    Integer waitFlag;

    @ApiModelProperty("用户ID")
    Long userId;

}
