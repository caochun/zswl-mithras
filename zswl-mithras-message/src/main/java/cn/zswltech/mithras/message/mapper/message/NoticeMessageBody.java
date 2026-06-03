package cn.zswltech.mithras.message.mapper.message;

import cn.zswltech.mithras.message.enums.MessageType;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName NoticeMessage
 * @Description
 * @Author jackerhe
 * @Date 2022/7/26 4:20 下午
 * @Version 1.0
 **/
@Data
public class NoticeMessageBody implements MessageBody {

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 备注
     */
    private String remark;

    /**
     * 数据范围（deptId,roleId,userID)
     */
    private String dataScope;

    /**
     * 创建用户，发送者
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改用户
     */
    private String updateBy;

    /**
     * 修改时间
     */
    private Date gmtUpdate;

    /**
     * APP地址
     */
    String appurl;

    /**
     * PC地址
     */
    String pcurl;

    /**
     * 流程实例id
     */
    String flowid;

    /**
     * 标题，标题中出现单引号、双引号等特殊字符时需转义，否则无法处理
     */
    //String requestname;

    /**
     * 流程类型名称 NoticeSourceENUM
     */
    String workflowname;

    /**
     * 步骤名称（节点名称）NoticeTypeEnum
     */
    String nodename;

    private MessageTypeEnum messageTypeEnum;


    @Override
    public String getSendType() {
        return MessageType.NOTICE.getType();
    }

}
